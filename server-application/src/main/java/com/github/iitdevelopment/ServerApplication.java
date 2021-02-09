package com.github.iitdevelopment;

import com.github.iitdevelopment.core.Server;
import com.github.iitdevelopment.subscriber.impl.GETSubscriber;
import com.github.iitdevelopment.subscriber.impl.OPTIONSSubscriber;
import com.github.iitdevelopment.subscriber.impl.POSTSubscriber;

import java.io.IOException;

public class ServerApplication {

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = new Server(8080, 10);
        server.subscribe(
                new GETSubscriber(),
                new POSTSubscriber(),
                new OPTIONSSubscriber()
        );
        Thread serverThread = new Thread(server);
        serverThread.start();
        serverThread.join();
    }
}
