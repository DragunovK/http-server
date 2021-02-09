package com.github.iitdevelopment.subscriber.impl;

import com.github.iitdevelopment.ContentType;
import com.github.iitdevelopment.HttpCode;
import com.github.iitdevelopment.HttpMethod;
import com.github.iitdevelopment.subscriber.ISubscriber;

import java.io.*;
import java.util.Date;
import java.util.StringTokenizer;

public class GETSubscriber implements ISubscriber {
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
        }
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
        String path = getPath(input);
        InputStream inputStream = getClass().getResourceAsStream(path);
        if (inputStream == null) {
            createResponse(
                    response,
                    HttpCode.NOT_FOUND,
                    ContentType.HTML,
                    readFile(
                            getClass().getResourceAsStream("/404.html")
                    )
            );
        } else {
            createResponse(
                    response,
                    HttpCode.OK,
                    ContentType.determine(path),
                    readFile(inputStream)
            );
        }

        try {
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }
}
