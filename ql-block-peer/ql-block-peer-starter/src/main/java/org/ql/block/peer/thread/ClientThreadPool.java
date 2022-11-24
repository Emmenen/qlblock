package org.ql.block.peer.thread;

import lombok.extern.slf4j.Slf4j;
import org.ql.block.common.config.properties.QlBlockConfiguration;
import org.ql.block.common.exceptions.GetBlockError;
import org.ql.block.ledger.model.block.Block;
import org.ql.block.ledger.model.blockchain.BlockChain;
import org.ql.block.ledger.model.blockdata.Transaction;
import org.ql.block.ledger.service.MasterChainService;
import org.ql.block.ledger.util.ObjectUtil;
import org.ql.block.peer.communication.message.peer.pojo.*;
import org.ql.block.peer.communication.message.peer.MessageVO;
import org.ql.block.peer.communication.message.thread.ThreadMessageVO;
import org.ql.block.peer.communication.message.thread.enums.ThreadMessageType;
import org.ql.block.peer.communication.message.thread.pojo.MintedBlock;
import org.ql.block.peer.model.MyOutputStream;
import org.ql.block.peer.model.MySocket;
import org.ql.block.common.beans.pojo.Peer;
import org.ql.block.peer.context.PeerContext;
import org.ql.block.peer.communication.message.peer.enums.MessageType;
import org.ql.block.peer.service.GossipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

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

    @Autowired
    private BlockChain blockChain;

    @Autowired
    private QlBlockConfiguration qlBlockConfiguration;

    public void startAClient(Socket socket) throws IOException {
        this.startAClient(new MySocket(socket));
    }

    public void startAClient(final MySocket client){
        //将连接到的节点的socket加入到已经连接的上下文中
        Callable callable = ()->{
            try {
                while (true){
                    InputStream in = client.getInputStream();
                    MessageVO messageVO = (MessageVO) ObjectUtil.inputSteamToObject(in);
                    MessageType header = messageVO.getMessageType();
                    Object message = messageVO.getMessage();
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
                            log.info("GET_BLOCKS");
                            log.info("请求区块信息来自:{}",client);
                            GetBLock getBLock = (GetBLock) message;
                            getBlockMsg(client,getBLock.getBestHeight());
                            break;
                        case BLOCKS_LIST:
                            log.info("BLOCKS_LIST");
                            Inv inv = (Inv) message;
                            log.info("收到{}的区块清单,{}个区块",client,inv.getBlockList().size());
                            invMsg(inv);
                            break;
                        case GOSSIP_TRANSACTION:
                            log.info("GOSSIP_TRANSACTION");
                            Transaction transaction = (Transaction) message;
                            TransactionMessage transactionMessage = new TransactionMessage(transaction);
                            break;
                        case SET_BLOCK:
                            log.info("SET_BLOCK");
                            SetBlock setBlock = (SetBlock) message;
                            Block block = setBlock.getBlock();
                            log.info("收到新挖出的区块{}",block);

                            //todo 区块验证
                            if (block.validate()) {
                                log.info("区块合法");
                                if (block.previousHash.equals(masterChainService.getLastHash())){
                                    //区块验证通过之后，
                                    //1. 中断当前正在工作的挖矿工作，开启下一轮竞争
                                    //2. 将新加入的区块中的交易从本地交易池中去除
                                    //问题：
                                    //当有多个节点挖出块，后挖出的块先到达了该节点，如何确定将最新挖出的块存放到链上。
                                    log.info("区块合法，且区块顺序合法！");
                                    if (qlBlockConfiguration.getIsMinter()){
                                        //如果当前节点是矿工节点，则需要将收到新块的消息告知给powThread
                                        peerContext.blockingMsgQueue.put(new ThreadMessageVO(new MintedBlock(block),ThreadMessageType.MINTED_BLOCK));
                                    }
                                    log.info("将新块加入到链上");
                                    masterChainService.addBlock(block);
                                }
                            }
                            break;
                        case GOSSIP_TEXTDATA:
                            String textData = (String) message;
                            log.info("GOSSIP_TEXTDATA:{}",textData);
                            peerContext.addData(textData);
                            break;
                    }
                }
            } catch (IOException e) {
                if (e instanceof SocketException){
                    if (!client.isConnected()){
                        log.info("断开连接:{}",client);
                        peerContext.removeOnePeer(client);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace(System.out);
            } catch (Exception e){
                e.printStackTrace(System.out);
            } finally {
                return "end";
            }
        };
        //当监听线程意外终止时
        FutureTask<String> futureTask = new FutureTask<String>(callable);
        ThreadFactory.execute(futureTask);
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
        peerContext.addOnePeer(socket);
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
