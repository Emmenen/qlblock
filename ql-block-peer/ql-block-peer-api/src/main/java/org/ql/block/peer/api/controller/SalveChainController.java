package org.ql.block.peer.api.controller;

import org.ql.block.browser.api.vo.ResponseVo;
import org.ql.block.ledger.communication.salve.SalveApply;
import org.ql.block.ledger.model.block.SalveBlock;
import org.ql.block.ledger.model.blockchain.SalveBlockChain;
import org.ql.block.ledger.service.MasterChainService;
import org.ql.block.ledger.service.SalveChainService;
import org.ql.block.peer.api.model.form.SalveApplyForm;
import org.ql.block.peer.service.GossipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created at 2022/12/10 17:06
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@RestController
public class SalveChainController {

  @Autowired
  private SalveChainService salveChainService;

  @Autowired
  private MasterChainService masterChainService;


  @PostMapping("/applyNewSalveChain")
  public ResponseVo applyNewSalveChain(@RequestBody SalveApplyForm salveApplyForm){
    SalveBlock salveBlock = masterChainService.newSalveBlockChain(salveApplyForm.getChainName());

    //todo  将salveBlock信息广播出去
    return ResponseVo.ok(salveBlock);
  }
}
