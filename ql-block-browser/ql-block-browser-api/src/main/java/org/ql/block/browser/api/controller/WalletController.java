package org.ql.block.browser.api.controller;

import org.ql.block.browser.api.vo.ResponseVo;
import org.ql.block.browser.service.WalletService;
import org.ql.block.common.exceptions.WalletInformationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created at 2022/12/9 12:24
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@RestController
public class WalletController {

  @Autowired
  private WalletService walletService;

  @GetMapping("/bindWallet")
  public ResponseVo bindWallet(@RequestParam String priKey){
    try {
      walletService.bindWallet(priKey);
    } catch (WalletInformationError e) {
      e.printStackTrace();
    }
    return ResponseVo.ok(walletService.getAddress());
  }
}
