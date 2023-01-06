package org.ql.block.section.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.ql.block.common.config.SpringContextUtil;
import org.ql.block.common.exceptions.WalletInformationError;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

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


  @Pointcut("@annotation(org.ql.block.common.beans.annotation.RequireWallet)")
  private void pointcut() {
  }

  @Around("pointcut()")
  public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    Object wallet = SpringContextUtil.getBean("wallet");
    Class<?> walletClass = Class.forName("org.ql.block.ledger.wallet.Wallet");
    Method method = walletClass.getMethod("isConnected");
    boolean connected = (boolean) method.invoke(wallet);
    if (!connected) {
      log.error("请先连接钱包地址！");
      throw new WalletInformationError("请先连接钱包地址！");
    }else {
     return joinPoint.proceed();
    }
  }
}
