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

public class PostSubscriber implements ISubscriber {

    private static final Logger logger = Logger.getLogger(PostSubscriber.class.getName());

    private ContentType getContentType(String input) {
        StringTokenizer tokenizer = new StringTokenizer(input);
        while (!tokenizer.nextToken().equalsIgnoreCase("Content-Type:")); //skipping tokens up to content-type
        String token = tokenizer.nextToken();
        return ContentType.getContentType(token.replace(";", ""));
    }

    @Override
    public void delegateInput(String inputHeader, byte[] inputBody, OutputStream response) {
        ContentType contentType = getContentType(inputHeader);

        File file = null;
        try {
            file = new File("post_" + response.toString().substring(27) + "." + contentType.getExtension());
            file.createNewFile();
            OutputStream os = new FileOutputStream(file);
            os.write(inputBody);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintWriter forHead = new PrintWriter(response);
        forHead.println(
                "HTTP/1.1 " + HttpCode.CREATED.getCode() +
                        " " + HttpCode.CREATED.getDescription() + "\n" +
                "File name: " + (file != null ? file.getName() : "") + "\n" +
                "Server: Server" + "\n" +
                "Date: " + new Date() + "\n" +
                "Access-Control-Allow-Origin: " + "localhost" + "\n" +
                "Access-Control-Allow-Methods: " + "GET, POST, OPTIONS" + "\n"
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
        return HttpMethod.POST;
    }
}
