package org.ql.block.peer.communication.message.peer.pojo;

import org.ql.block.peer.communication.message.peer.enums.MessageType;
import org.ql.block.peer.model.Peer;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;

/**
 * Created at 2022/10/6 15:50
 * Author: @Qi Long
 * email: 592918942@qq.com
 * 用来与网络中其他节点交流的Version对象，
 * 对应的{@link MessageType#VERSION}
 */
public class Version implements PeerMessage, Serializable {

  private String nVersion;
  //当前时间
  private Date nTime;

  private HashSet<Peer> addrYou;

  private Peer addrMe;

  private int BestHeight;

  public HashSet<Peer> getAddrYou() {
    return addrYou;
  }

  public String getnVersion() {
    return nVersion;
  }

  public Date getnTime() {
    return nTime;
  }

  public Peer getAddrMe() {
    return addrMe;
  }

  public int getBestHeight() {
    return BestHeight;
  }

  public Version(String nVersion, Date nTime, HashSet<Peer> addrYou, Peer addrMe, int bestHeight) {
    this.nVersion = nVersion;
    this.nTime = nTime;
    this.addrYou = addrYou;
    this.addrMe = addrMe;
    BestHeight = bestHeight;
  }

  @Override
  public String toString() {
    return "Version{" +
            "nVersion='" + nVersion + '\'' +
            ", nTime=" + nTime +
            ", addrMe=" + addrMe +
            ", BestHeight=" + BestHeight +
            '}';
  }

  @Override
  public long UID() {
    return hashCode()+System.currentTimeMillis();
  }
}
