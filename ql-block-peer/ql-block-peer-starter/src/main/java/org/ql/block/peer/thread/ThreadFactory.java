package org.ql.block.peer.thread;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created at 2022/10/6 12:16
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class ThreadFactory {
  public static ThreadPoolExecutor cachedThreadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), 30,60L,TimeUnit.SECONDS,new SynchronousQueue<Runnable>());
  private static Map<String,Runnable> tasks = Collections.synchronizedMap(new HashMap<>());

  public static void execute(Runnable runnable,String name){
    cachedThreadPool.execute(runnable);
    tasks.put(name,runnable);

  }public static void execute(Runnable runnable){
    cachedThreadPool.execute(runnable);
  }

  /**
   *
   * @param callable
   * @param name
   */
//  public static void executeForever(Callable callable,String name){
//    try {
//      FutureTask<String> futureTask = new FutureTask<String>(callable);
//      tasks.put(name,futureTask);
//      cachedThreadPool.execute(futureTask);
//      String s = futureTask.get();
//      System.out.println(s);
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    } catch (ExecutionException e) {
//      e.printStackTrace();
//    } finally {
//      executeForever(callable,name);
//    }
//  }
}
