package org.ql.block.peer.service;

import lombok.extern.slf4j.Slf4j;
import org.ql.block.peer.communication.message.messageModel.GetBLock;
import org.ql.block.peer.communication.message.messageModel.Message;
import org.ql.block.peer.communication.message.MessageType;
import org.ql.block.peer.communication.message.MessageVO;
import org.ql.block.peer.context.PeerContext;
import org.ql.block.peer.model.MyOutputStream;
import org.ql.block.peer.model.MySocket;
import org.ql.block.peer.thread.ThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

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

  public void gossipSpread(Message message, MessageType messageType) {
    Runnable runnable = () -> {
      try {
        Set<Integer> spreadIndex = new HashSet<Integer>();
        List<MySocket> socketList = peerContext.getSocketList();
        // 获取当前节点已经连接到的节点
        int connectedCount = socketList.size();
        int count = 0;
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
              return;
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
            if (mySocket.getMessageFlag(message)) {
              continue;
            }
            MyOutputStream out = new MyOutputStream(mySocket.getOutputStream());
            out.write(new MessageVO(messageType,message));
            mySocket.putMessage(message);
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    };
    ThreadFactory.cachedThreadPool.execute(runnable);
  }

  /**
   * 广播GET_BLOCKS消息
   */
  public void gossipGetBlocks(){
    log.info("gossip spread:GET_BLOCKS");
    GetBLock getBLock = new GetBLock(peerContext.getBestHeight());
    gossipSpread(getBLock,MessageType.GET_BLOCKS);
  }


}
