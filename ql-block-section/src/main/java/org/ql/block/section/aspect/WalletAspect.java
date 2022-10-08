package org.ql.block.section.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.ql.block.ledger.exceptions.WalletInformationError;
import org.ql.block.ledger.wallet.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created at 2022/10/8 14:09
 * Author: @Qi Long
 * email: 592918942@qq.com
 * 管理钱包的切面
 */
@Aspect
@Component
@Slf4j
public class WalletAspect {

  @Autowired
  private Wallet wallet;

  @Pointcut("@annotation(org.ql.block.common.annotation.RequireWallet)")
  private void pointcut() {
  }


  @Around("pointcut()")
  public Object before(ProceedingJoinPoint joinPoint) throws Throwable {
    if (!wallet.isConnected()) {
      log.error("请先连接钱包地址！");
      throw new WalletInformationError("请先连接钱包地址！");
    }else {
     return joinPoint.proceed();
    }
  }
}
