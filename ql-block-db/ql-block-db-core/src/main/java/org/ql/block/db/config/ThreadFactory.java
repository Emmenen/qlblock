package org.ql.block.db.config;


import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created at 2022/10/6 12:16
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Slf4j
public class ThreadFactory {
  public static ThreadPoolExecutor cachedThreadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), 30,60L,TimeUnit.SECONDS,new SynchronousQueue<Runnable>());
  private static Map<String,Runnable> tasks = Collections.synchronizedMap(new HashMap<>());

  public static void execute(Runnable runnable,String name){
    cachedThreadPool.execute(runnable);
    tasks.put(name,runnable);
  }
  public static void execute(Runnable runnable){
    cachedThreadPool.execute(runnable);
  }

}
