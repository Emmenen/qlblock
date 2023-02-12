package org.ql.block.ledger.service;

import org.ql.block.ledger.model.block.SalveBlock;
import org.ql.block.ledger.model.blockchain.SalveBlockChain;

/**
 * Created at 2022/12/10 15:54
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public interface SalveChainService {
  public SalveBlock applyForNewSalve(SalveBlockChain salveBlockChain);
}