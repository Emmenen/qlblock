package org.ql.block.ledger.model;

import org.ql.block.ledger.model.block.Block;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Created at 2022/10/7 15:44
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class BlockLinkedList {
  public HashMap<String,Block> blockHashMap = new HashMap<>();

  /**
   *
   */
  public void add(Block block){
    String currentHash = block.currentHash;
  }


}
