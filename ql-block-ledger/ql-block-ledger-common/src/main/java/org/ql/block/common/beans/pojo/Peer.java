package org.ql.block.common.beans.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * Created at 2022/10/5 21:47
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Data
@AllArgsConstructor
public class Peer implements Serializable {
  private String ip;
  private int port;

  @Override
  public String toString() {
    return "Peer{" +
            "ip='" + ip + '\'' +
            ", port=" + port +
            '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Peer)) return false;
    Peer peer = (Peer) o;
    if (getPort() != peer.getPort()) return false;
    return getIp() != null ? getIp().equals(peer.getIp()) : peer.getIp() == null;
  }

  @Override
  public int hashCode() {
    int result = getIp() != null ? getIp().hashCode() : 0;
    result = 31 * result + getPort();
    return result;
  }
}
