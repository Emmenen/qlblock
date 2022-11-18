package org.ql.block.ledger.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.ql.block.ledger.exceptions.GetBlockError;
import org.ql.block.ledger.model.block.Block;
import org.ql.block.ledger.model.blockchain.MasterBlockChain;
import org.ql.block.ledger.service.MasterChainService;
import org.ql.block.ledger.util.MathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * Created at 2022/10/5 20:41
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Service
@Slf4j
public class MasterChainServiceImpl implements MasterChainService {

  private MasterBlockChain masterBlockChain;

  @Autowired
  public MasterChainServiceImpl(MasterBlockChain masterBlockChain) {
    this.masterBlockChain = masterBlockChain;
  }


  @Override
  public int getBlockHeight() {
    byte[] ds = masterBlockChain.getData("d");
    return MathUtils.byteArrayToInt(ds);
  }

  @Override
  public String getLastHash() {
    return masterBlockChain.getLastHash();
  }


  @Override
  public List<Block> getBlocks(int offset, int number) throws GetBlockError {
    log.info("开启检索账本的第{}块到第{}块...",offset,offset+number-1);
    return masterBlockChain.getBlocks(offset,number);
  }

  @Override
  public Block getBlock(String hash) throws GetBlockError {
    return masterBlockChain.getBlock(hash);
  }

  @Override
  public void addBlock(Block block) {
    masterBlockChain.addBlock(block);
  }

  @Override
  public boolean addBlockAll(Collection<Block> c) {
    return masterBlockChain.addBlockAllStrict(c);
  }
}
