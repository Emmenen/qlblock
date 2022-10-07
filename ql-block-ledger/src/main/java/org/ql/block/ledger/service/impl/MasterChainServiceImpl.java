package org.ql.block.ledger.service.impl;

import org.ql.block.ledger.model.blockchain.MasterBlockChain;
import org.ql.block.ledger.service.MasterChainService;
import org.ql.block.ledger.util.MathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created at 2022/10/5 20:41
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Service
public class MasterChainServiceImpl implements MasterChainService {

  @Autowired
  private MasterBlockChain masterBlockChain;

  @Override
  public int getBlockHeight() {
    byte[] ds = masterBlockChain.getData("d");
    return MathUtils.byteArrayToInt(ds);
  }
}
