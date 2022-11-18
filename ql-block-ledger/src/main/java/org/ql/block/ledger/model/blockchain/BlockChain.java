package org.ql.block.ledger.model.blockchain;

import lombok.extern.slf4j.Slf4j;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.jetbrains.annotations.NotNull;
import org.ql.block.common.annotation.AddBlock;
import org.ql.block.ledger.db.Database;
import org.ql.block.ledger.exceptions.BlockOrderError;
import org.ql.block.ledger.exceptions.DataBaseIsNotExistError;
import org.ql.block.ledger.exceptions.GetBlockError;
import org.ql.block.ledger.model.block.Block;
import org.ql.block.ledger.model.block.MasterBlock;
import org.ql.block.ledger.model.blockdata.BlockData;
import org.ql.block.ledger.model.blockdata.TXOutput;
import org.ql.block.ledger.model.blockdata.Transaction;
import org.ql.block.ledger.model.utxo.UTXO;
import org.ql.block.ledger.model.utxo.UnSpentOutput;
import org.ql.block.ledger.util.MathUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.ql.block.ledger.util.ObjectUtil.ObjectToByteArray;
import static org.ql.block.ledger.util.ObjectUtil.byteArrayToObject;

/**
 * Created at 2022/6/29 20:09
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Slf4j
public abstract class BlockChain {


  protected static final String BLOCK_BUCKET = "blockBucket";
  protected static final String CHAIN_STATE = "chainState";
  protected DB bucketBucketDb;

  /**
   * 存放区块链数据的bucket名称
   */
  public String ChainName;
  /**
   * 区块链的最上层区块的hash
   */
  public String tip;

  /**
   * 区块深度
   */
  public int deep;

  public Database database;


  @Autowired
  public BlockChain(Database staticDatabase) {
    this.database = staticDatabase;
  }

  protected void init(Database staticDatabase){
    try {
      this.database = staticDatabase.connect(ChainName);
    } catch (DataBaseIsNotExistError e) {
      staticDatabase.createBucket(ChainName);
      this.database = staticDatabase.createDatabase(ChainName);
    }

    this.bucketBucketDb = this.database.getBucket(BLOCK_BUCKET);
    if (null==bucketBucketDb) {
      this.bucketBucketDb = database.createBucket(BLOCK_BUCKET);
      Block block = this.newGenesisBlock();
      this.deep = -1;
      addBlock(block);
    } else {
      this.tip = new String(bucketBucketDb.get(Iq80DBFactory.bytes("l")));
      log.info("最新区块Hash："+this.tip);
      this.deep = this.getHeight();
      byte[] d = bucketBucketDb.get(Iq80DBFactory.bytes("d"));
      this.deep = MathUtils.byteArrayToInt(d);
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
    return MathUtils.byteArrayToInt(getData("d"));
  }

  public String getLastHash(){
    return new String(getData("l"));
  }

  @AddBlock
  public void addBlock(@NotNull Block block) {
    this.tip = block.currentHash;
    log.info("当前区块高度："+deep);
    this.deep++;
    database.update(BLOCK_BUCKET, bucket ->{
      if (!block.previousHash.equals(tip)){
        return;
      }
      bucket.put(Iq80DBFactory.bytes(block.currentHash),ObjectToByteArray(block));
      log.info("添加新的区块到链上(perHash: {})",block.previousHash);
      log.info("添加新的区块到链上(currentHash: {})",block.currentHash);
      log.info("矿工(minter: {})",block.miner);
      bucket.put(Iq80DBFactory.bytes("l"),Iq80DBFactory.bytes(tip));
      bucket.put(Iq80DBFactory.bytes("d"),MathUtils.intToByteArray(deep));
    });
    //放后面的作用是先存放区块高度再+1，因为创始区块的高度为0;
    log.info("当前区块高度："+getHeight());
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

  public byte[] getData(String key){
    byte[] bytes = bucketBucketDb.get(Iq80DBFactory.bytes(key));
    return bytes;
  }

  public Block getBlock(String blockHash){
    byte[] data = getData(blockHash);
    return (Block) byteArrayToObject(data);
  }

  public DB getBlockDB(){
    return this.bucketBucketDb;
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
    BLockChainIterator bLockChainIterator = new BLockChainIterator(tip, getBlockDB());
    return bLockChainIterator;
  }
}
