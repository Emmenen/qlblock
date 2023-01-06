package org.ql.block.peer.thread;

import lombok.extern.slf4j.Slf4j;
import org.ql.block.ledger.model.block.Block;
import org.ql.block.ledger.model.blockchain.BlockChain;
import org.ql.block.ledger.model.blockdata.BlockData;
import org.ql.block.ledger.model.blockdata.Transaction;
import org.ql.block.peer.communication.message.peer.enums.MessageType;
import org.ql.block.peer.communication.message.peer.pojo.SetBlock;
import org.ql.block.peer.communication.message.thread.ThreadMessageVO;
import org.ql.block.peer.communication.message.thread.enums.ThreadMessageType;
import org.ql.block.peer.communication.message.thread.pojo.MintedBlock;
import org.ql.block.peer.communication.message.thread.pojo.ThreadMessage;
import org.ql.block.peer.context.PeerContext;
import org.ql.block.peer.service.GossipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Created at 2022/11/7 10:20
 * Author: @Qi Long
 * email: 592918942@qq.com
 * 不断从交易池中取出交易并打包的线程；
 */
@Component
@Slf4j
public class PowThread implements Callable {

  @Autowired
  private PeerContext peerContext;

  @Autowired
  private BlockChain blockChain;

  @Autowired
  private GossipService gossipService;

  @Override
  public Object call() throws Exception {
    while (true){
      FutureTask<ArrayList<Transaction>> arrayListFutureTask = new FutureTask<ArrayList<Transaction>>(new MintRunnable());
      Thread minter = new Thread(arrayListFutureTask);
      minter.start();
      //监听是否有线程消息
      ThreadMessageVO messageVO = peerContext.blockingMsgQueue.take();

      ThreadMessageType messageType = messageVO.getMessageType();
      ThreadMessage message = messageVO.getMessage();
      switch (messageType){
        case MINTED_BLOCK:
          //收到此消息表示网络中有新的块被挖出来了;
          log.info("收到新的合法区块");
          MintedBlock mintedBlock = (MintedBlock) message;
          log.info("终止当前正在执行的挖矿操作");
          minter.stop();
          TreeSet<Transaction> transactionPool = peerContext.getTransactionPool();
          transactionPool.addAll(peerContext.transactions);
//          将新加入的区块中的交易从本地交易池中去除
          Arrays.asList(mintedBlock.getBlock().getData().getTransactions()).forEach(transactionPool::remove);
          break;
      }
    }
  }

  /**
   * 一个进行POW挖矿的线程
   */
  class MintRunnable implements Callable{
    @Override
    public ArrayList<Transaction> call() {
      TreeSet<Transaction> transactionPool = peerContext.getTransactionPool();
      ArrayList<Transaction> transactions = new ArrayList<>(transactionPool.size());
      boolean isInterrupted = true;
      while (true){
        try {
          Transaction tc =  transactionPool.pollFirst();
          log.info("开始从交易池中取出交易...");
          int size = 0;
          //在挖矿的同时，当有新的区块进来的时候，要打断当前进度，并重新挖掘
          // 需要将之前从交易池中取到的数据全部放回去
          while (tc != null) {
            size += tc.getSize();
            transactions.add(tc);
            if (size <= 386377746) {
              log.info("取出了足够多的交易");
              break;
            }
            tc = transactionPool.pollFirst();
          }
          peerContext.transactions = transactions;
          log.info("交易取出完成,开始挖掘新的区块...");
          BlockData blockData = new BlockData(transactions.toArray(new Transaction[0]));
          //将挖出的区块发送到网络中,只有更多的节点确认了该区块，挖矿才是有意义的
          log.info("开始挖掘新的区块...");

          Block block = blockChain.addBlock(blockData);
          /**
           * 发送区块
           * 对应的就是关于接受到新区块时的处理{@link ConnectedThreadPool#startAClient}
           */
          log.info("挖出新的区块,开始广播区块");
          gossipService.gossipSpread(new SetBlock(block), MessageType.SET_BLOCK);
          isInterrupted = false;
          //为了区别正常结束和异常终止
        }finally {
          if (isInterrupted){
            log.info("挖矿操作被中断了！");
            //不论什么情况下，程序结束都要进将尝试打包进块的交易返回；
            return transactions;
          }
        }
      }
    }
  }

  static class CallableTest implements Callable{

    @Override
    public Object call()  {
      int i = 0;
      while (!Thread.currentThread().isInterrupted()){
        i++;
      }
      return i;
    }
  }

//  public static void main(String[] args) throws ExecutionException, InterruptedException {
//    FutureTask<Integer> integerFutureTask = new FutureTask<Integer>(new CallableTest());
//    Thread thread = new Thread(integerFutureTask);
//    thread.start();
//    Thread.sleep(100);
//    thread.stop();
//    System.out.println(integerFutureTask.get());
//  }
}
