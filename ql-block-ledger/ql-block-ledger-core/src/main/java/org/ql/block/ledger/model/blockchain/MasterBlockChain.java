package org.ql.block.ledger.model.blockchain;

import org.jetbrains.annotations.NotNull;
import org.ql.block.common.beans.annotation.AddBlock;
import org.ql.block.common.beans.annotation.RequireWallet;
import org.ql.block.common.exceptions.GetBlockError;
import org.ql.block.ledger.model.block.MasterBlock;
import org.ql.block.ledger.model.block.MasterGenesisBlock;
import org.ql.block.ledger.model.blockdata.BlockData;
import org.ql.block.ledger.db.Database;
import org.ql.block.ledger.model.block.Block;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;


/**
 * Created at 2022/7/9 0:14
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Repository("masterBlockChain")
public class MasterBlockChain extends BlockChain{

  @Autowired
  public MasterBlockChain(Database staticDatabase) {
    super(staticDatabase);
    ChainName = "Master";
    this.init(staticDatabase);
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
