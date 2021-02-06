package com.github.iitdevelopment.subscriber;

import com.github.iitdevelopment.HttpMethod;

import java.io.InputStream;
import java.net.Socket;

public interface ISubscriber {

    void delegate(Socket socket);

    String delegateInput(InputStream is);

    String getPath();

    HttpMethod getMethod();
}
