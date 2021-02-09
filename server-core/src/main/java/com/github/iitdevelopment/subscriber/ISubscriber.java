package com.github.iitdevelopment.subscriber;

import com.github.iitdevelopment.HttpMethod;

import java.io.OutputStream;

public interface ISubscriber {

    void delegateInput(String input, OutputStream response);

    HttpMethod getMethod();
}
