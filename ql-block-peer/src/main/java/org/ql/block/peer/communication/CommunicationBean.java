package org.ql.block.peer.communication;

import org.ql.block.peer.handler.PropertyHandler;
import org.ql.block.peer.model.Peer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.beans.PropertyChangeSupport;
import java.util.HashSet;

/**
 * Created at 2022/10/5 21:49
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Configuration
public class CommunicationBean {
  @Value("${qlblock.peer.port}")
  private int port;

  @Value("${qlblock.peer.ip}")
  private String ip;

  @Value("${qlblock.peer.seednode.ip}")
  private String seedNodeIP;

  @Value("${qlblock.peer.seednode.port}")
  private int seedNodePort;

  @Bean
  public Peer addrMe(){
    return new Peer(ip,port);
  }
  @Bean
  public Peer seedNode(){
    return new Peer(seedNodeIP,seedNodePort);
  }


//  @Bean
//  public HashSet<Peer> addrYou(){
//    HashSet<Peer> addrYou = new HashSet<>();
//    return addrYou;
//  }
}
