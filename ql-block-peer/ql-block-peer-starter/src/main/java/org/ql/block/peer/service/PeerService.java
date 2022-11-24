package org.ql.block.peer.service;

import lombok.extern.slf4j.Slf4j;
import org.ql.block.common.config.properties.QlBlockConfiguration;
import org.ql.block.peer.thread.ClientThread;
import org.ql.block.peer.thread.PowThread;
import org.ql.block.peer.thread.ServerThread;
import org.ql.block.peer.thread.ThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.FutureTask;

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

    @Autowired
    private PowThread powThread;

    @Autowired
    private QlBlockConfiguration qlBlockConfiguration;

    public void start(){
        log.info("节点启动...");
        ThreadFactory.cachedThreadPool.execute(serverRunnable);
        ThreadFactory.cachedThreadPool.execute(clientRunnable);
        if (qlBlockConfiguration.getIsMinter()) ThreadFactory.cachedThreadPool.execute(new FutureTask<Object>(powThread));
    }
}
