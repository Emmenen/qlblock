package org.ql.block.browser.service.impl;

import org.ql.block.browser.service.WalletService;
import org.ql.block.common.exceptions.WalletInformationError;
import org.ql.block.ledger.wallet.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created at 2022/12/9 12:51
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Service
public class WalletServiceImpl implements WalletService {

  @Autowired
  private Wallet wallet;

  @Override
  public void bindWallet(String priKey) throws WalletInformationError {
    wallet.connectUser(priKey);
  }

  @Override
  public String getAddress() {
    return wallet.getAddress();
  }
}
