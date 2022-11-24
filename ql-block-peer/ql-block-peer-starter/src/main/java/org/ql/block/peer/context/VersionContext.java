package org.ql.block.peer.context;

import lombok.Data;
import org.ql.block.common.beans.pojo.Peer;
import org.ql.block.common.config.properties.QlBlockConfiguration;
import org.ql.block.ledger.service.MasterChainService;
import org.ql.block.peer.communication.message.peer.pojo.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 *
 * Created at 2022/10/5 21:29
 * Author: @Qi Long
 * email: 592918942@qq.com
 * 保存当前节点的Version信息的上下文对象
 */
@Component("versionContext")
@Data
class VersionContext {

  protected QlBlockConfiguration qlBlockConfiguration;

  protected String nVersion;
  //当前时间
//  private Date nTime;

  @Autowired
  protected AddrYou addrYou;

  @Autowired
  protected Peer addrMe;

  @Autowired
  protected MasterChainService masterChainService;

  protected int BestHeight;


  @Autowired
  public VersionContext(QlBlockConfiguration qlBlockConfiguration) {
    this.qlBlockConfiguration = qlBlockConfiguration;
    this.nVersion = qlBlockConfiguration.getVersion();
  }


  public int getBestHeight() {
    return masterChainService.getBlockHeight();
  }

  //获取一个Version对象。
  public Version getVersion(){
    return new Version(nVersion,new Date(),addrYou.getAddrYouSet(),addrMe,getBestHeight());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof VersionContext)) return false;

    VersionContext versionContext = (VersionContext) o;

    if (getBestHeight() != versionContext.getBestHeight()) return false;
    if (!nVersion.equals(versionContext.nVersion)) return false;
//    if (!nTime.equals(versionContext.nTime)) return false;
    if (!getAddrYou().equals(versionContext.getAddrYou())) return false;
    return getAddrMe().equals(versionContext.getAddrMe());
  }

  @Override
  public int hashCode() {
    int result = nVersion.hashCode();
//    result = 31 * result + nTime.hashCode();
    result = 31 * result + getAddrYou().hashCode();
    result = 31 * result + getAddrMe().hashCode();
    result = 31 * result + getBestHeight();
    return result;
  }
}
