package org.ql.block.peer.thread;

import lombok.extern.slf4j.Slf4j;
import org.ql.block.ledger.exceptions.BlockOrderError;
import org.ql.block.ledger.exceptions.GetBlockError;
import org.ql.block.ledger.model.block.Block;
import org.ql.block.ledger.service.MasterChainService;
import org.ql.block.ledger.util.ObjectUtil;
import org.ql.block.peer.communication.message.messageModel.*;
import org.ql.block.peer.communication.message.MessageVO;
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
import java.net.SocketException;
import java.util.*;

/**
 * Created with IntelliJ IDEA at 2022/5/18 16:20
 * User: @Qi Long
 * 从别的节点接收到的信息的处理都在这个类中
 * 处理接收到的信息的线程
 */
@Service
@Slf4j
public class ClientThreadPool {
    @Autowired
    private PeerContext peerContext;

    @Autowired
    private GossipService gossipService;

    @Autowired
    private MasterChainService masterChainService;

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
                    MessageType header = messageVO.getMessageType();
                    Message message = messageVO.getMessage();
                    switch (header) {
                        case VERSION:
                            Version version = (Version) message;
                            log.info("VERSION:{}", version);
                            client.setPeer(version.getAddrMe());
                            log.info("启动了一个ClientSocket: {}",client.getPeer());
                            this.versionMsg(client,version);
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
                            peerContext.addOnePeer(client);
                            gossipService.gossipSpread(gossipVersion,MessageType.GOSSIP_VERSION);
                            break;
                        case GET_BLOCKS:
                            //
                            log.info("GET_BLOCKS");
                            log.info("请求区块信息来自:{}",client);
                            GetBLock getBLock = (GetBLock) message;
                            getBlockMsg(client,getBLock.getBestHeight());
                        case BLOCKS_LIST:
                            log.info("BLOCKS_LIST");
                            Inv inv = (Inv) message;
                            log.info("收到{}的区块清单,{}个区块",client,inv.getBlockList().size());
                            invMsg(inv);
                    }
                }
            } catch (IOException e) {
                if (e instanceof SocketException){
                    if (!client.isConnected()){
                        log.info("断开连接:{}",client);
                        peerContext.removeOnePeer(client);
                    }
                }
            }
        };
        ThreadFactory.cachedThreadPool.execute(runnable);
    }

    public void addNewAddrYou(HashSet<Peer> newAddrYouSet){
        peerContext.addToAddrYou(newAddrYouSet);
    }

    public void versionMsg(MySocket socket,Version version) throws IOException {
        if (!peerContext.getNVersion().equals(this.peerContext.getNVersion())) {

        }
        //1. 将新节点的地址广播给其他节点
        gossipService.gossipSpread(version,MessageType.GOSSIP_VERSION);
        //2. 将网络中的节点列表发送给新节点
            //获取接受到的Version的addrYou
        HashSet<Peer> addrYou = version.getAddrYou();
            //获取本节点的AddrYou
        HashSet<Peer> contextAddrYou = peerContext.getAddrYou().getAddrYouCopy();
        contextAddrYou.removeAll(addrYou);
        MyOutputStream mout = socket.getMyOutputStream();
        mout.write(new MessageVO(MessageType.NEW_ADDR_YOU, new HashSetMessage(contextAddrYou)));
        mout.flush();
        //3. 将新节点的信息写入网络节点列表
        peerContext.addToAddrYou(version.getAddrMe());
        peerContext.addSocketToList(socket);

        //4. 交换区块清单
        int bestHeight = version.getBestHeight();
        getBlockMsg(socket,bestHeight);
    }

    public void getBlockMsg(MySocket socket,Integer bestHeight) throws IOException {
        int contextBestHeight = peerContext.getBestHeight();
        MyOutputStream mout = socket.getMyOutputStream();
        int d = contextBestHeight - bestHeight;
        if (d==0){
            log.info("{}的区块高度与当前节点相同:{}，向其发送BLOCKS_OK回复",socket,contextBestHeight);
            return;
        }
        if (d>0){
            //当前节点的区块较高
            //将区块清单发给请求的节点
            log.info("发现{}的区块高度低于当前节点，向其发送BLOCKS_LIST回复",socket);
            List<Block> blocks = null;
            try {
                // contextBestHeight - d + 1 为了从较低链之后的第一个区块开始取
                blocks = masterChainService.getBlocks(contextBestHeight - d + 1, d);
            } catch (GetBlockError e) {
                log.error(e.getMessage());
            }
            mout.write(new MessageVO(MessageType.BLOCKS_LIST,new Inv(blocks)));
            mout.flush();
        }else{
            //当前节点的区块较低
            //向请求的节点发送getBlocks请求
            log.info("发现{}的区块高度高于当前节点，向其发送GET_BLOCKS请求",socket);
            mout.write(new MessageVO(MessageType.GET_BLOCKS,new GetBLock(contextBestHeight)));
            mout.flush();
        }
    }

    public void invMsg(Inv inv) {
        List<Block> blockList = inv.getBlockList();
        masterChainService.addBlockAll(blockList);
    }
}
