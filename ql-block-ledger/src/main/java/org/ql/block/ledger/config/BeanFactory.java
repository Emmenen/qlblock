package org.ql.block.ledger.config;

import lombok.extern.slf4j.Slf4j;
import org.ql.block.ledger.exceptions.WalletInformationError;
import org.ql.block.ledger.wallet.Identify;
import org.ql.block.ledger.wallet.Wallet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created at 2022/7/16 1:51
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Configuration
@Slf4j
public class BeanFactory {

  @Value("${qlblock.ledger.wallet.key}")
  private String currentUserKey;
  @Bean
  public Wallet createWallet(){
    Wallet wallet = new Wallet();
    try {
      wallet.connectUser(currentUserKey);
    } catch (WalletInformationError e) {
      log.error(e.getMessage());
    }
    if (wallet.isConnected()){
    }else {
      log.warn("尚未连接钱包, 为避免损失, 请在连接其他节点前导入您的账户地址！");
      log.warn("我们会为你创建一个新的钱包地址");
      Identify identify = wallet.createIdentify("qlblock" + System.currentTimeMillis());
      wallet.connectUser(identify);
    }
    log.info("钱包连接成功，{}",wallet);
    return wallet;
  }

}
