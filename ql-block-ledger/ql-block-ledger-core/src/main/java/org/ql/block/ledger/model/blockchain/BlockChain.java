package org.ql.block.ledger.model.blockchain;

import lombok.extern.slf4j.Slf4j;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.jetbrains.annotations.NotNull;
import org.ql.block.common.beans.annotation.AddBlock;
import org.ql.block.common.exceptions.BlockOrderError;
import org.ql.block.common.exceptions.GetBlockError;
import org.ql.block.db.sdk.message.ResponseVo;
import org.ql.block.ledger.model.block.Block;
import org.ql.block.ledger.model.blockdata.BlockData;
import org.ql.block.ledger.model.blockdata.Transaction;
import org.ql.block.ledger.model.utxo.UnSpentOutput;
import org.ql.block.ledger.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.ql.block.ledger.config.LedgerConfig.BLOCK_BUCKET;

/**
 * Created at 2022/6/29 20:09
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Slf4j
public abstract class BlockChain {

  /**
   * 存放区块链数据的bucket名称
   */
  public String ChainName;
  /**
   * 区块链的最上层区块的hash
   */
  public String tip = "genesis";

  /**
   * 区块深度
   */
  public int deep;

  private DatabaseService databaseService;

  @Autowired
  public BlockChain(DatabaseService databaseService) {
    this.databaseService = databaseService;
  }

  protected void init(){
    databaseService.useDatabase(ChainName);
    ResponseVo creatRes = databaseService.createBucket(BLOCK_BUCKET);
    if (creatRes.getStatus()==200) {
      //创建成功
      Block block = this.newGenesisBlock();
      this.deep = -1;
      addBlock(block);
    } else {
      //创建失败-》已经存在
      this.tip = getData("l");
      log.info("最新区块Hash："+this.tip);
      this.deep = this.getHeight();
      log.info("区块高度："+this.deep);
    }
  }

  public abstract Block newGenesisBlock();


  public abstract Block addBlock(BlockData data);

  /**
   * 区块的高度的下标是从1开始的;
   * @param offset 从offset高度开始取
   * @param number 取offset之后的number个
   * @return
   * @throws GetBlockError
   */
  public LinkedList<Block> getBlocks(int offset, int number) throws GetBlockError {
    if (offset<0){
      log.error("取出区块异常, offset 的值最小为0");
      throw new GetBlockError("取出区块异常, offset 的值最小为0");
    }
    if (number<1){
      log.error("取出区块异常, number 的值最小为1");
      throw new GetBlockError("取出区块异常, number 的值最小为1");
    }
    Integer height = getHeight();
    LinkedList<Block> blockList = new LinkedList<>();
    int var1 = height - offset;
    if (var1<0){
      log.error("取出区块异常, offset 大于当前区块高度");
      throw new GetBlockError("取出区块异常, offset 大于当前区块高度");
    }else {
      //从账本顶部开始检索，找到offset处，从offset往后取出number个
      int count = 0;
      Block block = getBlock(tip);
      blockList.addFirst(block);
      count++;
      int var2 = var1 + 1;
      while (count<var2){
        block = getBlock(block.previousHash);
        blockList.addFirst(block);
        count++;
      }
    }

    for (int i = 0; i < blockList.size() - number; i++) {
      blockList.removeLast();
    }
    return blockList;
  }

  public Integer getHeight(){
    return Integer.parseInt(getData("d"));
  }

  public String getLastHash(){
    return new String(getData("l"));
  }

  @AddBlock
  public Block addBlock(@NotNull Block block) {
    log.info("当前区块高度："+deep);
    int deepPre = this.deep;
    //因为区块在挖掘的时候只需要知道prehash无须知道当前区块高度，所以将区块高度的赋值放在了blockChain中；
    if (block.height == -1){
      //高度为-1时，则说明区块高度没有被初始化
      block.height = deepPre;
    }
    if (!block.previousHash.equals(tip)){
      throw new BlockOrderError("区块hash错误");
    }
    databaseService.insertOrUpdate(BLOCK_BUCKET,block.currentHash,block.toByte());
    log.info("添加新的区块到链上(perHash: {})",block.previousHash);
    log.info("添加新的区块到链上(currentHash: {})",block.currentHash);
    log.info("矿工(minter: {})",block.miner);
    this.deep++;
    this.tip = block.currentHash;
    databaseService.insertOrUpdate(BLOCK_BUCKET,"l",tip);
    databaseService.insertOrUpdate(BLOCK_BUCKET,"d", String.valueOf(deep));
    //放后面的作用是先存放区块高度再+1，因为创始区块的高度为0;
    log.info("当前区块高度："+getHeight());
    if (deepPre<deep){
      return block;
    }else return null;
  }

  /**
   * 批量更新账本
   * 如何保证清单中的区块是按顺序添加到账本中的？
   * 账本清单严格的讲应该是一个顺序列表，从区块高度较低->区块高度较高的区块；
   * 有低到高取出区块nextNode，每次添加区块之前验证节点中存放的lastHash与 nextNode.previousHash是否相等
   * 若不相等，说明区块清单被篡改，拒绝添加该出错区块之后的所有区块
   * 如何保证每个区块都是正确的？https://www.jianshu.com/p/64dfd24599e5
   * @param c 账本清单
   * @return
   */
  public boolean addBlockAllStrict(Collection<Block> c)  {
    // 保证最后添加到账本中的区块是最新的就可以；
    Iterator<Block> iterator = c.iterator();

    while (iterator.hasNext()){
      Block next = iterator.next();
      if (!getLastHash().equals(next.getPreviousHash())){
        throw new BlockOrderError("区块清单顺序有误！");
      }
      addBlock(next);
    }
    return true;
  }

  public String getData(String key){
    return (String) databaseService.select(BLOCK_BUCKET, key).getData().iterator().next();
  }

  public Block getBlock(String blockHash){
    String data = getData(blockHash);
    return Block.formByte(data.getBytes());
  }


  public ConcurrentHashMap<String, ArrayList<UnSpentOutput>> FindUTXO(){
    ConcurrentHashMap<String, ArrayList<Integer>> spentTXOs = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, ArrayList<UnSpentOutput>> UTXO = new ConcurrentHashMap<>();
    BLockChainIterator iterator = Iterator();
    while (iterator.hashNext()){
      Block block = iterator.next();
      Transaction[] transactions = block.data.getTransactions();
      for (Transaction transaction : transactions) {
        boolean isSpend = false;
        for (int i = 0; i < transaction.vOut.length; i++) {
          //判断交易是否被引用过
          if (spentTXOs.get(transaction.id) != null) {
            //被引用
            //判断引用的交易中的输出是否是当前遍历的交易输出
            for (int index : spentTXOs.get(transaction.id)) {
              if (i==index){
                isSpend = true;
              }
            }
          }
          if (!isSpend){
            ArrayList<UnSpentOutput> outs = UTXO.getOrDefault(transaction.id,new ArrayList<>());
            outs.add(new UnSpentOutput(transaction.vOut[i],i,transaction.id));
            UTXO.put(transaction.id,outs);
          }
        }
        if (!transaction.isBaseCoin()){
          for (int i = 0; i < transaction.vIn.length; i++) {
            ArrayList<Integer> orDefault = spentTXOs.getOrDefault(transaction.id, new ArrayList<>());
            orDefault.add(i);
            spentTXOs.put(transaction.id,orDefault);
          }
        }
      }
    }
    return UTXO;
  }

  public BLockChainIterator Iterator(){
    BLockChainIterator bLockChainIterator = new BLockChainIterator(tip, databaseService);
    return bLockChainIterator;
  }

}
