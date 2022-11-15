package org.ql.block.peer.thread.ThreadConfig;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created at 2022/11/7 11:23
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class QLThreadPool {
  // 1:任务队列   集合  需要控制线程安全问题
  private List<Runnable> tasks = Collections.synchronizedList(new LinkedList<>());
  //2:当前线程数量
  private int num;
  //3:核心线程数量
  private int corePoolSize;
  //4:最大线程数量
  private int maxSize;
  //5:任务队列的长度
  private int workSize;

  public QLThreadPool(int maxCore) {
  }

  //1:提交任务;
  public void submit(Runnable r){
    //判断当前集合中任务的数量,是否超出了最大任务数量
    if(tasks.size()>=workSize){
      System.out.println("任务:"+r+"被丢弃了...");
    }else {
      tasks.add(r);
      //执行任务
      execTask(r);
    }
  }

  //2:执行任务;
  private void execTask(Runnable r) {
    //判断当前线程池中的线程总数量,是否超出了核心数,
    if(num < corePoolSize){
      num++;
    }else if(num < maxSize){
      num++;
    }else {
      System.out.println("任务:"+r+" 被缓存了...");
    }
  }
}
