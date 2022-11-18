package org.ql.block.browser.controller;

import org.ql.block.browser.vo.ResponseVo;
import org.ql.block.ledger.exceptions.GetBlockError;
import org.ql.block.ledger.model.block.Block;
import org.ql.block.ledger.model.block.MasterBlock;
import org.ql.block.ledger.model.blockdata.BlockData;
import org.ql.block.ledger.service.MasterChainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created at 2022/10/5 20:36
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@RestController
@RequestMapping("/browser/v1")
public class MasterChainController {

  @Autowired
  private MasterChainService masterChainService;

  @GetMapping("/getBlockHeight")
  public ResponseVo<Integer> getBlockHeight(){
    return ResponseVo.ok(masterChainService.getBlockHeight());
  }

  @PostMapping("/addBlock")
  public ResponseVo<MasterBlock> addBlock(){
    MasterBlock test = new MasterBlock(masterChainService.getLastHash(), new BlockData("test"));
    masterChainService.addBlock(test);
    return ResponseVo.ok(test);
  }
  @PostMapping("/connectWallet")
  public ResponseVo<MasterBlock> connectWallet(){
    MasterBlock test = new MasterBlock(masterChainService.getLastHash(), new BlockData("test"));
    masterChainService.addBlock(test);
    return ResponseVo.ok(test);
  }
  @GetMapping("/masterChain/getBlockByHeight")
  public ResponseVo<Block> getBlockByHeight(@RequestParam int height){
    List<Block> blocks = new ArrayList<>();
    try {
      blocks = masterChainService.getBlocks(height, 1);
    } catch (GetBlockError e) {
      e.printStackTrace();
    }
    return ResponseVo.ok(blocks.get(0));
  }
  @GetMapping("/masterChain/getBlockByHash")
  public ResponseVo<Block> getBlockByHash(@RequestParam String hash){
    Block block = null;
    try {
      block = masterChainService.getBlock(hash);
    } catch (GetBlockError e) {
      e.printStackTrace();
    }
    return ResponseVo.ok(block);
  }


}
