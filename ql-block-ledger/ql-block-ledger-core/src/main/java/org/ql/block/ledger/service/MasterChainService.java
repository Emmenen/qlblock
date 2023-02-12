package org.ql.block.ledger.service;

import org.ql.block.common.exceptions.GetBlockError;
import org.ql.block.ledger.model.block.Block;
import org.ql.block.ledger.model.block.SalveBlock;

import java.util.Collection;
import java.util.List;

/**
 * Created at 2022/10/5 20:40
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public interface MasterChainService {

  public int getBlockHeight();

  public String getLastHash();

  /**
   *
   * @param offset
   * @param number
   * @return
   */
  public List<Block> getBlocks(int offset,int number) throws GetBlockError;

  public Block getBlock(String hash) throws GetBlockError;

  public void addBlock(Block block);

  public boolean addBlockAll(Collection<Block> c);

  public SalveBlock newSalveBlockChain(String chainName);
}
