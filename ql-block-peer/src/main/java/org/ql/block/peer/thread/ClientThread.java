package org.ql.block.peer.thread;

import lombok.extern.slf4j.Slf4j;
import org.ql.block.ledger.util.ObjectUtil;
import org.ql.block.peer.communication.message.MessageType;
import org.ql.block.peer.communication.message.MessageVO;
import org.ql.block.peer.context.AddrYou;
import org.ql.block.peer.model.MyOutputStream;
import org.ql.block.peer.model.MySocket;
import org.ql.block.peer.model.Peer;
import org.ql.block.peer.context.PeerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA at 2022/5/17 12:06
 * User: @Qi Long
 * 用来寻找网络中的节点的线程
 */
@Service
@Slf4j
public class ClientThread implements Runnable{

    @Autowired
    private PeerContext peerContext;

    private static final int retry = 3;

    @Autowired
    private ClientThreadPool clientThreadPool;

    public ClientThread() {
    }

    @Override
    public void run() {
        try {
            List<Peer> connectList = peerContext.getConnectList();
            log.info("寻找新的节点...");
            ArrayList<Peer> unConnectList = peerContext.getUnConnectList();
            log.info("已经连接{},有{}个已知但未连接的节点",connectList.size(), unConnectList.size());
            // todo 连接网路中的节点
            while (connectList.size()<10&& unConnectList.size()>0){
                log.info("连接第{}个节点...",connectList.size()+1);
                int size = unConnectList.size();
                int index = size-1;
                Peer peer = unConnectList.get(index);
                for (int i = 0; i < retry; i++) {
                    try {
                        log.info("Try to connect node {} {} times",peer,i+1);
                        MySocket client = new MySocket(peer);
                        log.info("连接第{}个节点:{}成功!",connectList.size()+1,peer.toString());
                        clientThreadPool.startAClient(client);
                        MyOutputStream myOutputStream = client.getMyOutputStream();
                        myOutputStream.write(new MessageVO(MessageType.VERSION,peerContext.getVersion()));
                        connectList.add(peer);
                        break;
                    } catch (IOException e) {
                        log.info("Failed to connect node {} for the {}th time",peer,i+1);
                    }
                    Thread.sleep(300);
                }
                unConnectList.remove(index);
                Thread.sleep(300);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
