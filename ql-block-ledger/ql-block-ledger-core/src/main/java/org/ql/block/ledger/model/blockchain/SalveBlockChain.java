package org.ql.block.ledger.model.blockchain;

import org.jetbrains.annotations.NotNull;
import org.ql.block.common.beans.annotation.AddBlock;
import org.ql.block.common.exceptions.GetBlockError;
import org.ql.block.ledger.model.block.SalveBlock;
import org.ql.block.ledger.model.blockdata.BlockData;
import org.ql.block.ledger.model.block.Block;
import org.ql.block.ledger.service.DatabaseService;

import java.util.LinkedList;


/**
 * Created at 2022/7/9 0:14
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class SalveBlockChain extends BlockChain{

  private SalveBlockChainStatus status;

  public SalveBlockChain(DatabaseService DatabaseService, String chainName) {
    super(DatabaseService);
    ChainName = chainName;
  }

  public void setStatus(SalveBlockChainStatus status) {
    this.status = status;
  }

  @Override
  public SalveBlock newGenesisBlock(){
    return new SalveBlock("genesis", new BlockData("genesis"));
  }

  @Override
  public SalveBlock addBlock(BlockData data) {
    SalveBlock block;
    block = new SalveBlock(tip, data);
    addBlock(block);
    return block;
  }


  @Override
  public LinkedList<Block> getBlocks(int offset, int number) throws GetBlockError {
    return super.getBlocks(offset,number);
  }

  @Override
  @AddBlock
  public SalveBlock addBlock(@NotNull Block taskBlock) {
    if (taskBlock instanceof SalveBlock){
     return (SalveBlock) super.addBlock(taskBlock);
    }else {
      try {
        throw new Exception("不是匹配的区块类型");
      } catch (Exception e) {
        e.printStackTrace();
      }finally {
        return null;
      }
    }
  }
}
