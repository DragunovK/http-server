package com.github.iitdevelopment.subscriber.impl;

import com.github.iitdevelopment.ContentType;
import com.github.iitdevelopment.HttpCode;
import com.github.iitdevelopment.HttpMethod;
import com.github.iitdevelopment.subscriber.ISubscriber;

import java.io.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OptionsSubscriber implements ISubscriber {

    private static final Logger logger = Logger.getLogger(OptionsSubscriber.class.getName());

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
                logger.log(Level.WARNING, "InputStream closing error: " + e.getMessage());
            }
        }
        logger.log(Level.INFO, "Read " + fileData.length + " bytes from InputStream");
        return fileData;
    }

    @Override
    public void delegateInput(String inputHeader, byte[] inputBody, OutputStream response) {
        PrintWriter forHead = new PrintWriter(response);
        BufferedOutputStream forData = new BufferedOutputStream(response);

        byte[] fileData = readFile(getClass().getResourceAsStream("/service/OPTIONS.txt"));

        forHead.println(
                "HTTP/1.1 " + HttpCode.OK.getCode() + " " +
                        HttpCode.OK.getDescription() + "\n" +
                "Server: Server" + "\n" +
                "Date: " + new Date() + "\n" +
                "Content-type: " + ContentType.PLAIN_TEXT.getType() + "\n" +
                "Content-length: " + fileData.length + "\n" +
                "Access-Control-Allow-Origin: " + "localhost" + "\n" +
                "Access-Control-Allow-Methods: " + "GET, POST, OPTIONS" + "\n"
        );
        forHead.flush();

        try {
            logger.log(Level.INFO, "Sending /OPTIONS.txt");
            forData.write(fileData, 0, fileData.length);
            forData.flush();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Data write error: " + e.getMessage());
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
        return HttpMethod.OPTIONS;
    }
}
