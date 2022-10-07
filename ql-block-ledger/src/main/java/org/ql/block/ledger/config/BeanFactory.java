package org.ql.block.ledger.config;

import org.ql.block.ledger.wallet.Wallet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created at 2022/7/16 1:51
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Configuration
public class BeanFactory {

  @Bean
  public Wallet createWallet(){
    Wallet wallet = new Wallet("ql-block-ledger","src", "main", "resources", "wallet");
    wallet.user("qilong");
    return wallet;
  }

}
