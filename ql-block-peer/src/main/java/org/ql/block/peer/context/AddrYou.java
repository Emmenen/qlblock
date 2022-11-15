package org.ql.block.peer.context;

import org.ql.block.peer.model.Peer;
import org.ql.block.peer.thread.ClientThread;
import org.ql.block.peer.thread.ThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created at 2022/10/6 15:27
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Component
public class AddrYou {


  @Autowired
  private ClientThread clientRunnable;

  private HashSet<Peer> addrYouSet = new HashSet<>();

  @Autowired
  private AddrYou(Peer seedNode,Peer addrMe){
    addrYouSet.add(seedNode);
//    addrYouSet.remove(addrMe);
  }

  HashSet<Peer> getAddrYouSet(){
    return addrYouSet;
  }

  public ArrayList<Peer> toArrayListCopy(){
    return  new ArrayList<>(addrYouSet);
  }
  public HashSet<Peer> getAddrYouCopy(){
    return new HashSet<>(addrYouSet);
  }
  /**
   * 新增加可见的节点是，启动连接节点的线程
   * @param peer
   */
  public boolean add(Peer peer){
    boolean add = addrYouSet.add(peer);
    if (add)
    ThreadFactory.cachedThreadPool.execute(clientRunnable);
    return add;
  }
  public boolean addAll(Collection<Peer> c){
    boolean add = addrYouSet.addAll(c);
    if (add)
      ThreadFactory.cachedThreadPool.execute(clientRunnable);
    return add;
  }

  public boolean remove(Peer peer){
    boolean remove = addrYouSet.remove(peer);
    if (remove)
      ThreadFactory.cachedThreadPool.execute(clientRunnable);
    return remove;
  }
}
