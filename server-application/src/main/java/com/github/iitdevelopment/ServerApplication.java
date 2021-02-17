package com.github.iitdevelopment;

import com.github.iitdevelopment.core.Server;
import com.github.iitdevelopment.subscriber.impl.GetSubscriber;
import com.github.iitdevelopment.subscriber.impl.OptionsSubscriber;
import com.github.iitdevelopment.subscriber.impl.PostSubscriber;

import java.io.*;

public class ServerApplication {

    public static void main(String[] args) throws IOException, InterruptedException {
        String logfile = "log.txt";
        if (args.length > 0) {
            logfile = args[0];
        }

        File file = new File(logfile);
        if (!file.exists()) {
            file.createNewFile();
        }
        PrintStream logout = new PrintStream(file);
        System.setErr(logout);

        Server server = new Server(8080, 20);
        server.subscribe(
                new GetSubscriber(),
                new PostSubscriber(),
                new OptionsSubscriber()
        );
        Thread serverThread = new Thread(server);
        serverThread.start();
        serverThread.join();
    }
}
