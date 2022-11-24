package org.ql.block.common.beans.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created at 2022/10/8 14:18
 * Author: @Qi Long
 * email: 592918942@qq.com
 * 添加此注释在方法名上，则执行此方法必须要绑定钱包；
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequireWallet {
}
