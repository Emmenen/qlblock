package org.ql.block.peer.context;

import org.ql.block.peer.model.MySocket;
import org.ql.block.peer.model.Peer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created with IntelliJ IDEA at 2022/5/17 16:22
 * User: @Qi Long
 */

@Component("peerContext")
public class PeerContext extends VersionContext {


    @Autowired
    private Peer addrMe;

    private List<Peer> connectList = new ArrayList<>();

    private HashSet<MySocket> socketHashSet = new HashSet<>();
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

    public PeerContext() {
    }


    public synchronized boolean addToAddrYou(Peer peer){
       return this.getAddrYou().add(peer);
    }
    public synchronized boolean removeAddrYou(Peer peer){
       return this.getAddrYou().remove(peer);
    }
    public synchronized boolean addOnePeer(MySocket socket){
       return addToAddrYou(socket.getPeer()) && addSocketToList(socket);
    }
    public synchronized boolean removeOnePeer(MySocket socket){
       return removeAddrYou(socket.getPeer()) && removeSocketToList(socket);
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
}
