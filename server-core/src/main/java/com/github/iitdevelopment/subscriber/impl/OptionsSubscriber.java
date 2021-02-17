package com.github.iitdevelopment.subscriber.impl;

import com.github.iitdevelopment.ContentType;
import com.github.iitdevelopment.HttpCode;
import com.github.iitdevelopment.HttpMethod;
import com.github.iitdevelopment.subscriber.ISubscriber;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OptionsSubscriber implements ISubscriber {

    private static final Logger logger = Logger.getLogger(OptionsSubscriber.class.getName());

    @Override
    public void delegateInput(String inputHeader, byte[] inputBody, OutputStream response) {
        PrintWriter forHead = new PrintWriter(response);

        forHead.println(
                "HTTP/1.1 " + HttpCode.OK.getCode() + " " +
                        HttpCode.OK.getDescription() + "\n" +
                "Server: Server" + "\n" +
                "Date: " + new Date() + "\n" +
                "Access-Control-Allow-Origin: " + "localhost" + "\n" +
                "Access-Control-Allow-Methods: " + Arrays.toString(HttpMethod.values()) + "\n"
        );
        forHead.flush();

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
