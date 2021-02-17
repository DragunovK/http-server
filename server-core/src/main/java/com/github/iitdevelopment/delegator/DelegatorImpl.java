package com.github.iitdevelopment.delegator;

import com.github.iitdevelopment.HttpCode;
import com.github.iitdevelopment.HttpMethod;
import com.github.iitdevelopment.delegator.messageReader.MessageReader;
import com.github.iitdevelopment.subscriber.ISubscriber;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DelegatorImpl implements IDelegator{

    private static final Logger logger = Logger.getLogger(DelegatorImpl.class.getName());
    private Map<HttpMethod, ISubscriber> subscribers = new ConcurrentHashMap<>();

    @Override
    public void subscribe(ISubscriber subscriber) {
        subscribers.putIfAbsent(subscriber.getMethod(), subscriber);
    }

    @Override
    public void subscribe(ISubscriber... subscribers) {
        Arrays.stream(subscribers).forEach(e -> {
            this.subscribers.putIfAbsent(e.getMethod(), e);
        });
    }

    public void createResponse(OutputStream response, HttpCode code) {
        PrintWriter forHead = new PrintWriter(response);
        forHead.println(
                "HTTP/1.1 " + code.getCode() + " " +
                        code.getDescription() + "\n" +
                        "Server: Server" + "\n" +
                        "Date: " + new Date() + "\n" +
                        "Access-Control-Allow-Origin: " + "localhost" + "\n" +
                        "Access-Control-Allow-Methods: " + Arrays.toString(HttpMethod.values()) + "\n"
        );
        forHead.flush();
    }

    public void delegate(Socket socket) {
        try {
            InputStream inputStream = socket.getInputStream();
            List<byte[]> message = MessageReader.read(inputStream);
            while (message.size() == 0) {
                message = MessageReader.read(inputStream);
            } // message[0] = header, message[1] = body
            String header = new String(message.get(0));
            logger.log(Level.INFO, "Message header: " + header);
            StringTokenizer tokenizer = new StringTokenizer(header);
            String methodToken = tokenizer.nextToken();
            HttpMethod method = HttpMethod.valueOf(methodToken);
            ISubscriber subscriber = subscribers.get(method);
            if (subscriber == null) {
                logger.log(Level.INFO, "Couldn't find appropriate subscriber for method " +
                        method.name() + ". Closing " + socket);
            } else {
                subscriber.delegateInput(header, message.get(1), socket.getOutputStream());
                logger.log(Level.INFO, "Closing " + socket);
            }
            socket.close();
            socket.getInputStream().close();
        } catch (IllegalArgumentException illegalArgumentException) {
            logger.log(Level.INFO, "Creating 501 response [" + socket + "]");
            try {
                OutputStream response = socket.getOutputStream();
                createResponse(response, HttpCode.NOT_IMPLEMENTED);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException ioException) {
            logger.log(Level.INFO, "Creating 500 response [" + socket + "]");
            try {
                OutputStream response = socket.getOutputStream();
                createResponse(response, HttpCode.INTERNAL_SERVER_ERROR);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception exception) {
            logger.log(Level.SEVERE, "Delegating [" + socket + "] problem. Stack trace: ");
            exception.printStackTrace();
            try {
                OutputStream response = socket.getOutputStream();
                createResponse(response, HttpCode.BAD_REQUEST);
                socket.close();
            } catch (IOException ioException1) {
                ioException1.printStackTrace();
            }
        }
    }
}
