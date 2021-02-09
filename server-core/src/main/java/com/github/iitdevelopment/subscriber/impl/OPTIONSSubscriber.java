package com.github.iitdevelopment.subscriber.impl;

import com.github.iitdevelopment.ContentType;
import com.github.iitdevelopment.HttpCode;
import com.github.iitdevelopment.HttpMethod;
import com.github.iitdevelopment.subscriber.ISubscriber;

import java.io.*;
import java.util.Date;

public class OPTIONSSubscriber implements ISubscriber {
    private byte[] readFile(String path) {
        InputStream inputStream = getClass().getResourceAsStream(path);
        byte[] fileData = new byte[0];
        try {
            fileData = new byte[inputStream.available()];
            inputStream.read(fileData);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileData;
    }

    @Override
    public void delegateInput(String input, OutputStream response) {
        PrintWriter forHead = new PrintWriter(response);
        BufferedOutputStream forData = new BufferedOutputStream(response);

        byte[] fileData = readFile("/OPTIONS.txt");

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
            forData.write(fileData, 0, fileData.length);
            forData.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.OPTIONS;
    }
}
