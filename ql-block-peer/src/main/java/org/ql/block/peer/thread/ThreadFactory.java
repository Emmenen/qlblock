package org.ql.block.peer.thread;

import java.util.concurrent.*;

/**
 * Created at 2022/10/6 12:16
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class ThreadFactory {
  public static ThreadPoolExecutor cachedThreadPool = new ThreadPoolExecutor(13,30,60L,TimeUnit.SECONDS,new SynchronousQueue<Runnable>());
}
