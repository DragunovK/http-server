package com.github.iitdevelopment;

import com.github.iitdevelopment.core.Server;
import com.github.iitdevelopment.subscriber.impl.GetSubscriber;
import com.github.iitdevelopment.subscriber.impl.OptionsSubscriber;
import com.github.iitdevelopment.subscriber.impl.PostSubscriber;
import org.apache.commons.cli.*;

import java.io.*;

public class ServerApplication {

    public static void main(String[] args) throws IOException, InterruptedException {
        Options options = new Options();

        Option logFileName
                = new Option("l", "log", true, "log file name");
        logFileName.setRequired(false);
        options.addOption(logFileName);

        Option resourceDirectoryPath
                = new Option("r", "resource-dir", true, "resource dir path");
        resourceDirectoryPath.setRequired(false);
        options.addOption(resourceDirectoryPath);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }

        String logfile = "log.txt";
        if (cmd.getOptionValue("log") != null) {
            logfile = cmd.getOptionValue("log");
        }

        String resourceDir = "";
        if (cmd.getOptionValue("resource-dir") != null) {
            resourceDir = cmd.getOptionValue("resource-dir");
        }

        System.out.println(resourceDir);

        File file = new File(logfile);
        if (!file.exists()) {
            file.createNewFile();
        }
        PrintStream logout = new PrintStream(file);
        System.setErr(logout);

        Server server = new Server(8080, 20);
        Server.resourcePath = resourceDir;
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
