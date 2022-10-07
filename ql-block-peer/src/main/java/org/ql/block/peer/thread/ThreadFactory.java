package org.ql.block.peer.thread;

import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created at 2022/10/6 12:16
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class ThreadFactory {
  public static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
}
