package org.ql.block.common.config.properties;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created at 2022/10/5 21:36
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Component
@ConfigurationProperties(prefix = "qlblock")
@Data
public class QlBlockConfiguration {

  @Value("${qlblock.peer.seednode.ip}")
  private String seedNodeIP;

  @Value("${qlblock.peer.seednode.port}")
  private int seedNodePort;

  @Value("${qlblock.peer.port}")
  private int port;

  @Value("${qlblock.peer.ip}")
  private String ip;

  @Value("${qlblock.peer.version}")
  private String version;

  @Value("${qlblock.ledger.wallet.key}")
  private String key;

  @Value("${qlblock.sys.log.ismintprocess}")
  private String isMintProcess;
  public boolean getIsMintProcess() {
    if (isMintProcess.equals("true")||isMintProcess.equals("yes")) return true;
    return false;
  }

  @Value("${qlblock.consensus.performanceOccupy}")
  private String performanceOccupy;

  @Value("${qlblock.consensus.isMinter}")
  private String isMinter;
  public boolean getIsMinter() {
    if (isMinter.equals("true")||isMinter.equals("yes")) return true;
    return false;
  }
}
