package org.ql.block.peer.thread;

import lombok.extern.slf4j.Slf4j;
import org.ql.block.peer.model.MyServerSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA at 2022/5/17 12:06
 * User: @Qi Long
 */
@Service
@Slf4j
public class ServerThread implements Runnable{

    private MyServerSocket server;

    @Autowired
    private ClientThreadPool clientThreadPool;

    @Autowired
    public ServerThread(MyServerSocket server) {
        this.server = server;
    }

    @Override
    public void run() {
        try {
            while (true){
                log.info("等待新连接...");
                Socket accept = server.accept();
                clientThreadPool.startAClient(accept);
                System.out.println("来自"+accept.getPort()+"的连接成功");
            }
        } catch (IOException e) {
            if (server.isClosed()){
                log.info("节点停止");
            }
        }
    }
}
