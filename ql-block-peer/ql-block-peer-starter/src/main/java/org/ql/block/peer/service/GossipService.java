package org.ql.block.peer.service;

import lombok.extern.slf4j.Slf4j;
import org.ql.block.ledger.model.block.Block;
import org.ql.block.peer.communication.message.peer.pojo.PeerMessage;
import org.ql.block.peer.communication.message.peer.enums.MessageType;
import org.ql.block.peer.communication.message.peer.MessageVO;
import org.ql.block.peer.communication.message.peer.pojo.SetBlock;
import org.ql.block.peer.context.PeerContext;
import org.ql.block.peer.model.MyOutputStream;
import org.ql.block.peer.model.MySocket;
import org.ql.block.peer.thread.ThreadConfig.ThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Created at 2022/10/6 16:27
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Service
@Slf4j
public class GossipService {

  @Autowired
  private PeerContext peerContext;

  /**
   * 调用此方法会将Message传播到已经连接的节点。
   * @param message 传播的内容
   * @param messageType 内容的类型
   * @return 返回FutureTask供调用这获取传播消息的反馈；
   */
  public FutureTask<String> gossipSpread(PeerMessage message, MessageType messageType) {
    GossipCallable callable = new GossipCallable(message, messageType);
    FutureTask<String> futureTask = new FutureTask<String>(callable);
    //todo 消息发送不成功。
    ThreadFactory.cachedThreadPool.execute(futureTask);
    return futureTask;
  }

  /**
   * 广播GET_BLOCKS消息
   */
  public void gossipAddBlock(Block block){
    log.info("gossip spread:SET_BLOCK");
    SetBlock getBLock = new SetBlock(block);
    gossipSpread(getBLock,MessageType.SET_BLOCK);
  }


  class GossipCallable implements Callable{
    private PeerMessage peerMessage;
    private MessageType messageType;

    public GossipCallable(PeerMessage peerMessage, MessageType messageType) {
      this.peerMessage = peerMessage;
      this.messageType = messageType;
    }

    @Override
    public Object call() throws Exception {
      int count = 0;
      try {
        Set<Integer> spreadIndex = new HashSet<Integer>();
        List<MySocket> socketList = peerContext.getSocketList();
        // 获取当前节点已经连接到的节点
        int connectedCount = socketList.size();
        //暂且设置为每个节点要把消息告诉自己连接的所有的节点
        while (count<connectedCount){
          Random random = new Random();
          random.setSeed(System.currentTimeMillis());
          //每个节点将信息传播给自己连接的三个节点
          // spreadIndex是一个记录要传播的节点索引的Set
          if (connectedCount<=3){
            //如果节点不够3个
            if (connectedCount==0){
              //如果没有连接到任何节点，不传播信息
              log.info("none of peer connected!");
              return "none of peer connected!";
            }else {
              // 在不足3个节点是，有多少放多少个节点的索引
              for (int i = 0; i < connectedCount; i++) {
                spreadIndex.add(i);
                count++;
              }
            }
          }else {
            //如果已经连接的节点大于3个；需要随机出3个传播的信息
            while (spreadIndex.size() < 3) {//获取3个
              //随机再集合里取出元素，添加到新哈希集合
              spreadIndex.add(random.nextInt(connectedCount));
              count++;
            }
          }

          Iterator<Integer> spreadIterator = spreadIndex.iterator();
          while (spreadIterator.hasNext()){
            MySocket mySocket = socketList.get(spreadIterator.next());
            if (mySocket.getMessageFlag(peerMessage)) {
              continue;
            }
            MyOutputStream out = new MyOutputStream(mySocket.getOutputStream());
            out.write(new MessageVO(messageType,peerMessage));
            mySocket.putMessage(peerMessage);
            log.info(messageType+"spread 'No."+count+"' peer success！");
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      log.info(messageType+"spread '"+count+"' peer success！");
      return messageType+"spread '"+count+"' peer success！";
    }
  }


}
