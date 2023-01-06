package org.ql.block.browser.service;

import org.ql.block.common.exceptions.WalletInformationError;
import org.springframework.stereotype.Service;

/**
 * Created at 2022/12/9 12:50
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Service
public interface WalletService {

  public void bindWallet(String priKey) throws WalletInformationError;

  public String getAddress();
}
