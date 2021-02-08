package com.github.iitdevelopment.subscriber;

import com.github.iitdevelopment.HttpMethod;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public interface ISubscriber {

    String delegateInput(String input, OutputStream response);

    HttpMethod getMethod();
}
