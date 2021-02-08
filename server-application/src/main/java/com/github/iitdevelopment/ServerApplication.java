package com.github.iitdevelopment;

import com.github.iitdevelopment.core.Server;

import java.io.IOException;

public class ServerApplication {

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = new Server(8080, 10);
        Thread serverThread = new Thread(server);
        serverThread.start();
        serverThread.join();
    }
}
