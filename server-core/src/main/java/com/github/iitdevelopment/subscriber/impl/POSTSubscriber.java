package com.github.iitdevelopment.subscriber.impl;

import com.github.iitdevelopment.ContentType;
import com.github.iitdevelopment.HttpCode;
import com.github.iitdevelopment.HttpMethod;
import com.github.iitdevelopment.subscriber.ISubscriber;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;

public class POSTSubscriber implements ISubscriber {
    private final HttpMethod method = HttpMethod.POST;

    @Override
    public String delegateInput(String input, OutputStream response) {
        PrintWriter forHead = new PrintWriter(response);

        forHead.println(
                "HTTP/1.1 " + HttpCode.CREATED.getCode() +
                        " " + HttpCode.CREATED.getDescription() + "\n" +
                "Server: Server" + "\n" +
                "Date: " + new Date() + "\n" +
                "Content-type: " + ContentType.PLAIN_TEXT.getType() + "\n" +
                "Content-length: " + 0 + "\n" +
                "Access-Control-Allow-Origin: " + "localhost" + "\n" +
                "Access-Control-Allow-Methods: " + "GET, POST, OPTIONS" + "\n" +
                "\n"
        );
        forHead.flush();

        return null;
    }

    @Override
    public HttpMethod getMethod() {
        return method;
    }
}
