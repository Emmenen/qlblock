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
public class PeerContext {

    @Autowired
    private AddrYou addrYou;

    @Autowired
    private Peer addrMe;

    private List<Peer> connectList = new ArrayList<>();

    private HashSet<MySocket> socketHashSet = new HashSet<>();
//    private List<MySocket> socketList = new ArrayList<>();


    public List<Peer> getConnectList() {
        return connectList;
    }

    public ArrayList<MySocket> getSocketList(){
        return new ArrayList<>(socketHashSet);
    }

    public ArrayList<Peer> getUnConnectList() {
        ArrayList<Peer> peers = addrYou.toArrayListCopy();
        peers.removeAll(getConnectList());
        peers.remove(addrMe);
        return peers;
    }

    public PeerContext() {
    }

    public AddrYou getAddrYou() {
        return addrYou;
    }

    public boolean addToAddrYou(Peer peer){
       return this.addrYou.add(peer);
    }

    public boolean addToAddrYou(HashSet<Peer> peerList){
       return this.addrYou.addAll(peerList);
    }

    public void addSocketToList(MySocket socket){
        if (socketHashSet.add(socket)) {
            connectList.add(socket.getPeer());
        }
    }
    public void deleteSocketToList(MySocket socket){
        if (socketHashSet.remove(socket)) {
            connectList.remove(socket.getPeer());
        }
    }
}
