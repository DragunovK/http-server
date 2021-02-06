package com.github.iitdevelopment.subscriber;

import java.io.InputStream;
import java.net.Socket;

public interface ISubscriber {

    void delegate(Socket socket);

    String delegateInput(InputStream is);
}
