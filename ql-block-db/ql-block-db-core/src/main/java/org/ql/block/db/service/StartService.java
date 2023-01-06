package org.ql.block.db.service;

import org.ql.block.db.config.ThreadFactory;
import org.ql.block.db.connect.Conn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created at 2022/12/24 12:08
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Service
public class StartService {
  @Autowired
  private Conn conn;

  public void start(){
    ThreadFactory.execute(conn);
  }
}
