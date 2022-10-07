package org.ql.block.peer.service;

import lombok.extern.slf4j.Slf4j;
import org.ql.block.peer.thread.ClientThread;
import org.ql.block.peer.thread.ServerThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA at 2022/5/18 13:56
 * User: @Qi Long
 */
@Service
@Slf4j
public class PeerService {

    @Autowired
    private ServerThread serverRunnable;

    @Autowired
    private ClientThread clientRunnable;


    public void start(){
        log.info("节点启动...");
        //1、使用DatagramSocket 指定端口，创建接受端
        Thread serverThread = new Thread(serverRunnable);
        serverThread.start();
        Thread clientThread = new Thread(clientRunnable);
        clientThread.start();
    }
}
