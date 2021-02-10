package com.github.iitdevelopment.subscriber.impl;

import com.github.iitdevelopment.ContentType;
import com.github.iitdevelopment.HttpCode;
import com.github.iitdevelopment.HttpMethod;
import com.github.iitdevelopment.subscriber.ISubscriber;

import java.io.*;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GetSubscriber implements ISubscriber {

    private static final Logger logger = Logger.getLogger(GetSubscriber.class.getName());

    private void createResponse(OutputStream response,
                                HttpCode code,
                                ContentType type,
                                byte[] fileData) {
        PrintWriter forHead = new PrintWriter(response);
        BufferedOutputStream forData = new BufferedOutputStream(response);

        forHead.println(
                "HTTP/1.1 " + code.getCode() + " " +
                        code.getDescription() + "\n" +
                "Server: Server" + "\n" +
                "Date: " + new Date() + "\n" +
                "Content-type: " + type.getType() + "\n" +
                "Content-length: " + fileData.length + "\n" +
                "Access-Control-Allow-Origin: " + "localhost" + "\n" +
                "Access-Control-Allow-Methods: " + "GET, POST, OPTIONS" + "\n"
        );
        forHead.flush();

        try {
            forData.write(fileData, 0, fileData.length);
            forData.flush();
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.WARNING, "Data write error: " + e.getMessage());
        }

        logger.log(Level.INFO, "Created response of code " + code.getCode());
    }

    private String getPath(String input) {
        StringTokenizer tokenizer = new StringTokenizer(input);
        tokenizer.nextToken(); // skip method token
        String path = tokenizer.nextToken();
        if (path.endsWith("/"))
            path = "/index.html";
        return path;
    }

    private byte[] readFile(InputStream inputStream) {
        byte[] fileData = new byte[0];
        try {
            fileData = new byte[inputStream.available()];
            inputStream.read(fileData);
        } catch (IOException e) {
            logger.log(Level.WARNING, "File reading error: " + e.getMessage());
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                logger.log(Level.WARNING, inputStream + " closing error: " + e.getMessage());
            }
        }
        logger.log(Level.INFO, "Read " + fileData.length + " bytes from " + inputStream);
        return fileData;
    }

    @Override
    public void delegateInput(String inputHeader, byte[] inputBody, OutputStream response) {
        String path = getPath(inputHeader);
        InputStream inputStream = getClass().getResourceAsStream(path);

        logger.log(Level.INFO, "Requested file: " + path);

        if (inputStream == null) {
            logger.log(Level.INFO, "Couldn't find requested file. Sending back /404.html");
            createResponse(
                    response,
                    HttpCode.NOT_FOUND,
                    ContentType.HTML,
                    readFile(getClass().getResourceAsStream("/service/404.html"))
            );
        } else {
            logger.log(Level.INFO, "Requested file found. About to read it with " + inputStream);
            createResponse(
                    response,
                    HttpCode.OK,
                    ContentType.determine(path),
                    readFile(inputStream)
            );
        }

        try {
            logger.log(Level.INFO, "Closing response [" + response + "]");
            response.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Response [" + response + "] close error: " + e.getMessage());
        }
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }
}
