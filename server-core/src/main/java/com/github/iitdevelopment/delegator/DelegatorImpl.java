package com.github.iitdevelopment.delegator;

import com.github.iitdevelopment.HttpMethod;
import com.github.iitdevelopment.subscriber.ISubscriber;
import sun.misc.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DelegatorImpl implements IDelegator{

    private Map<HttpMethod, ISubscriber> subscribers = new ConcurrentHashMap<>();

    @Override
    public void subscribe(ISubscriber subscriber) {
        subscribers.putIfAbsent(subscriber.getMethod(), subscriber);
    }

    @Override
    public void subscribe(ISubscriber... subscribers) {
        Arrays.stream(subscribers).forEach(e -> {
            this.subscribers.putIfAbsent(e.getMethod(), e);
        });
    }

    public void delegate(Socket socket) {
        try {
            String input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())).readLine();
            StringTokenizer tokenizer = new StringTokenizer(input);
            String methodToken = tokenizer.nextToken();
            HttpMethod method = HttpMethod.valueOf(methodToken);
            ISubscriber subscriber = subscribers.get(method);
            if (subscriber == null) {
                socket.close();
            }
            subscriber.delegateInput(input, socket.getOutputStream());
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
