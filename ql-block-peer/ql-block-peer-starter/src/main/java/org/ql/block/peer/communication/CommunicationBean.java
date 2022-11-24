package org.ql.block.peer.communication;

import org.ql.block.common.beans.pojo.Peer;
import org.ql.block.common.config.properties.QlBlockConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created at 2022/10/5 21:49
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Configuration
public class CommunicationBean {
  @Autowired
  private QlBlockConfiguration qlBlockConfiguration;

  @Bean
  public Peer addrMe(){
    return new Peer(qlBlockConfiguration.getIp(),qlBlockConfiguration.getPort());
  }
  @Bean
  public Peer seedNode(){
    return new Peer(qlBlockConfiguration.getSeedNodeIP(), qlBlockConfiguration.getSeedNodePort());
  }


//  @Bean
//  public HashSet<Peer> addrYou(){
//    HashSet<Peer> addrYou = new HashSet<>();
//    return addrYou;
//  }
}
