package com.github.iitdevelopment.core;

import com.github.iitdevelopment.delegator.DelegatorImpl;
import com.github.iitdevelopment.delegator.IDelegator;
import com.github.iitdevelopment.subscriber.ISubscriber;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable{

    private static final Logger logger = Logger.getLogger(Server.class.getName());

    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private IDelegator delegator;

    public Server(int port, int threadPoolSize) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.executorService = Executors.newFixedThreadPool(threadPoolSize, (p) -> {
            Thread t = new Thread(p);
            t.setDaemon(true);
            t.setPriority(10);
            return t;
        });
        this.delegator = new DelegatorImpl();
    }

    @Override
    public void run() {
        logger.log(Level.INFO, "Server started. Connections expected on port " + serverSocket.getLocalPort());

        while (true) {
            try {
                Socket socket = serverSocket.accept();
                logger.log(Level.INFO, "New connection via " + socket);
                executorService.execute(() -> {
                    delegator.delegate(socket);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void subscribe(ISubscriber subscriber) {
        delegator.subscribe(subscriber);
    }

    public void subscribe(ISubscriber... subscribers) {
        delegator.subscribe(subscribers);
    }
}
