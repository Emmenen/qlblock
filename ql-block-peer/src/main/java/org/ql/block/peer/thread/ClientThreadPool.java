package org.ql.block.peer.thread;

import lombok.extern.slf4j.Slf4j;
import org.ql.block.ledger.util.ObjectUtil;
import org.ql.block.peer.communication.message.Message;
import org.ql.block.peer.communication.message.MessageVO;
import org.ql.block.peer.communication.message.child.HashSetMessage;
import org.ql.block.peer.communication.message.child.Version;
import org.ql.block.peer.context.VersionContext;
import org.ql.block.peer.model.MyOutputStream;
import org.ql.block.peer.model.MySocket;
import org.ql.block.peer.model.Peer;
import org.ql.block.peer.context.PeerContext;
import org.ql.block.peer.communication.message.MessageType;
import org.ql.block.peer.service.GossipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * Created with IntelliJ IDEA at 2022/5/18 16:20
 * User: @Qi Long
 * 处理接收到的信息的线程
 */
@Service
@Slf4j
public class ClientThreadPool {
    @Autowired
    private PeerContext peerContext;

    @Autowired
    private VersionContext versionContext;

    @Autowired
    private GossipService gossipService;


    public void startAClient(Socket socket) throws IOException {
        this.startAClient(new MySocket(socket));
    }

    public void startAClient(final MySocket client){
        //将连接到的节点的socket加入到已经连接的上下文中
        Runnable runnable = ()->{
            try {
                while (true){
                    InputStream in = client.getInputStream();
                    MessageVO messageVO = (MessageVO) ObjectUtil.inputSteamToObject(in);
                    try {
                        MessageType header = messageVO.getMessageType();
                        Message message = messageVO.getMessage();
                        switch (header) {
                            case VERSION:
                                Version version = (Version) message;
                                log.info("VERSION:{}", version);
                                client.setPeer(version.getAddrMe());
                                log.info("启动了一个ClientSocket: {}",client.getPeer());
                                this.versionMsg(client,version);
                                peerContext.addSocketToList(client);
                                break;
                            case NEW_ADDR_YOU:
                                //收到新的节点列表
                                HashSetMessage hashSetMessage = (HashSetMessage) message;
                                this.addNewAddrYou(hashSetMessage.getAddrYou());
                                break;
                            case GOSSIP_VERSION:
                                log.info("GOSSIP_VERSION");
                                //收到网络中新的节点信息
                                /**
                                 * 1. 判断当前节点是否已经链接了newNode
                                 *   yes: 1.1 直接广播 GOSSIP_VERSION 信息
                                 *   no: 1.2 链接到该节点并广播 GOSSIP_VERSION 信息
                                 */
                                Version gossipVersion = (Version) message;
                                peerContext.addToAddrYou(gossipVersion.getAddrMe());
                                gossipService.gossipSpread(gossipVersion,MessageType.GOSSIP_VERSION);
                                break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        new Thread(runnable).start();
    }

    public void addNewAddrYou(HashSet<Peer> newAddrYouSet){
        peerContext.addToAddrYou(newAddrYouSet);
    }

    public void versionMsg(MySocket socket,Version version) throws IOException {
        if (!versionContext.getNVersion().equals(this.versionContext.getNVersion())) {

        }
        //1. 将新节点的地址广播给其他节点
        gossipService.gossipSpread(version,MessageType.GOSSIP_VERSION);
        //2. 将网络中的节点列表发送给新节点
        //获取接受到的Version的addrYou
        HashSet<Peer> addrYou = version.getAddrYou();

        //获取本节点的AddrYou
        HashSet<Peer> contextAddrYou = versionContext.getAddrYou().getAddrYouCopy();

        contextAddrYou.removeAll(addrYou);
        MyOutputStream mout = socket.getMyOutputStream();

        mout.write(new MessageVO(MessageType.NEW_ADDR_YOU, new HashSetMessage(contextAddrYou)));
        mout.flush();

        //3. 将新节点的信息写入网络节点列表
        peerContext.addToAddrYou(version.getAddrMe());
    }

}
