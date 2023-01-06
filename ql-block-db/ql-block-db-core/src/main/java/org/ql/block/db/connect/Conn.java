package org.ql.block.db.connect;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created at 2022/12/18 14:36
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Service
@Slf4j
public class Conn implements Runnable{

  @Value("${qlblock.database.server.port}")
  public int port;

  @Autowired
  private ConnectionPool connectionPool;

  @Override
  public void run() {
    try {
      ServerSocket serverSocket = new ServerSocket(port);
      while (true){
        Socket accept = serverSocket.accept();
        log.info("一个新的链接：{}",accept.getLocalAddress());
        connectionPool.startAConnection(accept);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
