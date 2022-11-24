package org.ql.block.peer.context;

import org.ql.block.common.config.properties.QlBlockConfiguration;
import org.ql.block.ledger.model.blockdata.BlockData;
import org.ql.block.ledger.model.blockdata.Transaction;
import org.ql.block.peer.communication.message.thread.ThreadMessageVO;
import org.ql.block.peer.model.MySocket;
import org.ql.block.common.beans.pojo.Peer;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA at 2022/5/17 16:22
 * User: @Qi Long
 */

@Component("peerContext")
public class PeerContext extends VersionContext {

    public static final int threshold = 10;

    private TreeSet<Transaction> transactionPool = new TreeSet<>(new Comparator<Transaction>() {
        @Override
        public int compare(Transaction o1, Transaction o2) {
            return o1.reward - o2.reward;
        }
    });

    public List<Transaction> transactions = null;

    //创建阻塞队列：在取交易的时候如果为空，则等待
    private BlockingQueue<Transaction> blockingTransactionPool = new LinkedBlockingQueue<>();

    //创建阻塞队列：在取交易的时候如果为空，则等待
    public BlockingQueue<ThreadMessageVO> blockingMsgQueue = new LinkedBlockingQueue<>();

    private HashSet<String> testPool = new HashSet<>();

    private List<Peer> connectList = new ArrayList<>();

    private HashSet<MySocket> socketHashSet = new HashSet<>();

    public PeerContext(QlBlockConfiguration qlBlockConfiguration) {
        super(qlBlockConfiguration);
    }
//    private List<MySocket> socketList = new ArrayList<>();


    /**
     * 给获取已经连接的节点列表加同步锁，
     * @return
     */
    public synchronized List<Peer> getConnectList() {
        return connectList;
    }


    public synchronized ArrayList<MySocket> getSocketList(){
        return new ArrayList<>(socketHashSet);
    }

    public synchronized ArrayList<Peer> getUnConnectList() {
        ArrayList<Peer> peers = getAddrYou().toArrayListCopy();
        peers.removeAll(getConnectList());
        peers.remove(addrMe);
        return peers;
    }


    public synchronized boolean addToAddrYou(Peer peer){
       return this.getAddrYou().add(peer);
    }
    public synchronized boolean removeAddrYou(Peer peer){
       return this.getAddrYou().remove(peer);
    }
    public synchronized boolean addOnePeer(MySocket socket){
        boolean b1 = addToAddrYou(socket.getPeer());
        boolean b2 = addSocketToList(socket);
        return b1 && b2;
    }
    public synchronized boolean removeOnePeer(MySocket socket){
        boolean b1 = removeAddrYou(socket.getPeer());
        boolean b2 = removeSocketToList(socket);
        return b1 && b2;
    }

    public synchronized boolean addToAddrYou(HashSet<Peer> peerList){
       return this.getAddrYou().addAll(peerList);
    }

    public boolean addSocketToList(MySocket socket){
        boolean add = socketHashSet.add(socket);
        if (add) {
            getConnectList().add(socket.getPeer());
        }
        return add;
    }
    public boolean removeSocketToList(MySocket socket){
        boolean remove = socketHashSet.remove(socket);
        if (remove) {
            getConnectList().remove(socket.getPeer());
        }
        return remove;
    }

    public HashSet<String> getTestPool() {
        return testPool;
    }

    public TreeSet<Transaction> getTransactionPool() {
        return transactionPool;
    }

    public synchronized void addTransaction(Transaction transaction){
        getTransactionPool().add(transaction);
    }

    public synchronized void addData(String str){
        getTestPool().add(str);
        /**
         * todo 触发区块打包
         */
        if (getTestPool().size()>=threshold){
            Iterator<String> iterator = getTestPool().iterator();
            String data = "";
            while (iterator.hasNext()){
                data += iterator.next();
            }
            new BlockData(data);
        }
    }

}
