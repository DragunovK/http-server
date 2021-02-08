package com.github.iitdevelopment.delegator;

import com.github.iitdevelopment.subscriber.ISubscriber;

import java.net.Socket;

public interface IDelegator {

    void subscribe(ISubscriber subscriber);

    void subscribe(ISubscriber... subscribers);

    void delegate(Socket connection);
}
