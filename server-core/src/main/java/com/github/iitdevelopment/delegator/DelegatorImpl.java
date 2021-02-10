package com.github.iitdevelopment.delegator;

import com.github.iitdevelopment.HttpMethod;
import com.github.iitdevelopment.delegator.messageReader.MessageReader;
import com.github.iitdevelopment.subscriber.ISubscriber;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
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

    public void delegate(Socket socket) {
        try {
            InputStream inputStream = socket.getInputStream();
            List<byte[]> message = MessageReader.read(inputStream); // message[0] = header, message[1] = body

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
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Delegating [" + socket + "] problem. Stack trace: ");
            e.printStackTrace();
        }
    }
}
