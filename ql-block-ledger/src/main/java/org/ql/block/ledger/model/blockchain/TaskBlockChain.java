package org.ql.block.ledger.model.blockchain;

import org.jetbrains.annotations.NotNull;
import org.ql.block.ledger.model.block.TaskBlock;
import org.ql.block.ledger.model.blockdata.BlockData;
import org.ql.block.ledger.db.Database;
import org.ql.block.ledger.model.block.Block;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/**
 * Created at 2022/7/9 0:14
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class TaskBlockChain extends BlockChain{

  public TaskBlockChain(Database staticDatabase) {
    super(staticDatabase);
    ChainName = "Task";
    this.init(staticDatabase);
  }

  @Override
  public TaskBlock newGenesisBlock(){
    return new TaskBlock("genesis", new BlockData("genesis"));
  }

  @Override
  public void addBlock(BlockData data){
    TaskBlock block;
    block = new TaskBlock(tip, data);
    addBlock(block);
  }
  @Override
  public void addBlock(@NotNull Block taskBlock) {
    if (taskBlock instanceof TaskBlock){
      super.addBlock(taskBlock);
    }else {
      try {
        throw new Exception("不是匹配的区块类型");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
