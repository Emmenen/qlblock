package org.ql.block.peer.service;

import lombok.extern.slf4j.Slf4j;
import org.ql.block.common.config.properties.QlBlockConfiguration;
import org.ql.block.ledger.communication.wallet.WalletMessageVo;
import org.ql.block.ledger.wallet.Wallet;
import org.ql.block.peer.communication.message.thread.ThreadMessageVO;
import org.ql.block.peer.context.PeerContext;
import org.ql.block.peer.thread.ClientThread;
import org.ql.block.peer.thread.PowThread;
import org.ql.block.peer.thread.ServerThread;
import org.ql.block.peer.thread.ThreadConfig.ThreadFactory;
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

    @Autowired
    private Wallet wallet;

    @Autowired
    private PeerContext peerContext;

    public void start(){
        log.info("节点启动...");
        ThreadFactory.cachedThreadPool.execute(serverRunnable);
        ThreadFactory.cachedThreadPool.execute(clientRunnable);

        if (qlBlockConfiguration.getIsMinter()) {
            if (wallet.isConnected()) {
                ThreadFactory.cachedThreadPool.execute(new FutureTask<Object>(powThread));
            }else {
                log.info("钱包未绑定，暂不开始挖矿！");
                try {
                    /**
                     * 绑定了钱包之后才会开始挖矿；
                     */
                    while (wallet.waitConnected()) {
                        ThreadFactory.cachedThreadPool.execute(new FutureTask<Object>(powThread));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
