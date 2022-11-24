package org.ql.block.section.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Created at 2022/10/9 20:47
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Aspect
@Component
@Slf4j
public class BlockAspect {

  @Pointcut("@annotation(org.ql.block.common.beans.annotation.AddBlock)")
  private void pointcut() {
  }

  @Around("pointcut()")
  public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    log.info("AddBLock");
    Object proceed = joinPoint.proceed();
    /**
     * 通过反射的方法调用GossipService方法
     */
//    Object gossipService = SpringContextUtil.getBean("gossipService");
//    Class<?> context = Class.forName("org.ql.block.peer.service.GossipService");
//    Method gossipGetBlocks = context.getMethod("gossipAddBlock");
//    gossipGetBlocks.invoke(gossipService,proceed);
    return proceed;
  }
}
