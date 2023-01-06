package org.ql.block.ledger.model.blockchain;

import org.jetbrains.annotations.NotNull;
import org.ql.block.common.exceptions.GetBlockError;
import org.ql.block.db.sdk.connect.Connection;
import org.ql.block.ledger.model.block.MasterBlock;
import org.ql.block.ledger.model.block.MasterGenesisBlock;
import org.ql.block.ledger.model.blockdata.BlockData;
import org.ql.block.ledger.model.block.Block;
import org.ql.block.ledger.service.DatabaseService;

import java.util.*;


/**
 * Created at 2022/7/9 0:14
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class MasterBlockChain extends BlockChain{

  public MasterBlockChain(DatabaseService databaseService) {
    super(databaseService);
    ChainName = "Master";
    this.init();
  }


  public MasterBlock newGenesisBlock(){
    MasterBlock masterBlock = new MasterGenesisBlock("genesis", new BlockData("genesis"));
    return masterBlock;
  }

  @Override
  public MasterBlock addBlock(BlockData data) {
    MasterBlock block;
    block = new MasterBlock(tip, data);
    if (block.validate()) {
      addBlock(block);
    }
    return block;
  }


  @Override
  public LinkedList<Block> getBlocks(int offset, int number) throws GetBlockError {
    return super.getBlocks(offset,number);
  }

  @Override
  public MasterBlock addBlock(@NotNull Block masterBlock) {
    if (masterBlock instanceof MasterBlock){
     return (MasterBlock) super.addBlock(masterBlock);
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

  public void findSpendableOutputs(){

  }







}
