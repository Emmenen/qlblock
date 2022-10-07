package org.ql.block.browser.controller;

import org.ql.block.browser.vo.ResponseVo;
import org.ql.block.ledger.service.MasterChainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created at 2022/10/5 20:36
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@RestController
@RequestMapping("/browser/v1")
public class MasterChainController {

  @Autowired
  private MasterChainService masterChainService;

  @GetMapping("/getBlockHeight")
  public ResponseVo<Integer> getBlockHeight(){
    return ResponseVo.ok(masterChainService.getBlockHeight());
  }


}
