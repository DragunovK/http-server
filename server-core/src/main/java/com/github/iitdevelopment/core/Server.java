package com.github.iitdevelopment.core;

import com.github.iitdevelopment.delegator.DelegatorImpl;
import com.github.iitdevelopment.delegator.IDelegator;
import com.github.iitdevelopment.subscriber.ISubscriber;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable{

    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private IDelegator delegator;

    public Server(int port, int threadPoolSize) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
        this.delegator = new DelegatorImpl();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
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
