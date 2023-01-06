package org.ql.block.db;

import org.ql.block.db.config.SpringContextUtil;
import org.ql.block.db.connect.Conn;
import org.ql.block.db.service.StartService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


/**
 * Created at 2022/12/10 17:27
 * Author: @Qi Long
 * email: 592918942@qq.com
 * 数据库的设计
 */
@SpringBootApplication
@EnableCaching
public class DBStartApplication {
  public static void main(String[] args) {
    SpringApplication.run(DBStartApplication.class,args);
    StartService startService = SpringContextUtil.getBean(StartService.class);
    startService.start();
  }
}
