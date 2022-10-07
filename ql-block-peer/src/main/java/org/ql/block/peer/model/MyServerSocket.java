package org.ql.block.peer.model;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

/**
 * Created with IntelliJ IDEA at 2022/5/18 14:45
 * User: @Qi Long
 */
@Slf4j
public class MyServerSocket extends ServerSocket {

    static {
        log.info("peer starting ...");
    }

    public MyServerSocket() throws IOException {
    }

    public MyServerSocket(int port) throws IOException {
        super(port);
    }

    public MyServerSocket(int port, int backlog) throws IOException {
        super(port, backlog);
    }

    public MyServerSocket(int port, int backlog, InetAddress bindAddr) throws IOException {
        super(port, backlog, bindAddr);
    }
}
