package org.ql.block.ledger.model.blockchain;

import org.jetbrains.annotations.NotNull;
import org.ql.block.common.beans.annotation.AddBlock;
import org.ql.block.common.exceptions.GetBlockError;
import org.ql.block.db.service.DataBase;
import org.ql.block.ledger.model.block.TaskBlock;
import org.ql.block.ledger.model.blockdata.BlockData;
import org.ql.block.ledger.model.block.Block;

import java.util.LinkedList;


/**
 * Created at 2022/7/9 0:14
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class TaskBlockChain extends BlockChain{

  public TaskBlockChain(DataBase staticDatabase) {
    super(staticDatabase);
    ChainName = "Task";
    this.init(staticDatabase);
  }

  @Override
  public TaskBlock newGenesisBlock(){
    return new TaskBlock("genesis", new BlockData("genesis"));
  }

  @Override
  public TaskBlock addBlock(BlockData data) {
    TaskBlock block;
    block = new TaskBlock(tip, data);
    addBlock(block);
    return block;
  }


  @Override
  public LinkedList<Block> getBlocks(int offset, int number) throws GetBlockError {
    return super.getBlocks(offset,number);
  }

  @Override
  @AddBlock
  public TaskBlock addBlock(@NotNull Block taskBlock) {
    if (taskBlock instanceof TaskBlock){
     return (TaskBlock) super.addBlock(taskBlock);
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
