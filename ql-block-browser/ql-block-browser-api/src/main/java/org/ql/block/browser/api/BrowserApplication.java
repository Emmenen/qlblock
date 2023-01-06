package org.ql.block.browser.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created at 2022/11/24 10:06
 * Author: @Qi Long
 * email: 592918942@qq.com
 */

@SpringBootApplication
@ComponentScan("org.ql.block")
public class BrowserApplication {
  public static void main(String[] args) {
    SpringApplication.run(BrowserApplication.class,args);
  }
}

