package org.ql.block.peer.thread;

import org.ql.block.peer.context.PeerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA at 2022/5/17 12:07
 * User: @Qi Long
 */
@Service
public class GossipClientThread implements Runnable{

    @Autowired
    private PeerContext peerContext;

    public GossipClientThread() {
    }

    @Override
    public void run() {
        while (true){
//            peerInfo.addNewUnConnectPeer(123);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
}
