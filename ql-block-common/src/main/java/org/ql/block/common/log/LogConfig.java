package org.ql.block.common.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

/**
 * Created at 2022/11/7 12:49
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Component
@Slf4j
public class LogConfig {
  private LinkedList<Log> logList = new LinkedList<>();
}
