package org.ql.block.common.beans.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created at 2022/10/9 20:46
 * Author: @Qi Long
 * email: 592918942@qq.com
 * 当有新块加入到区块上时
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AddBlock {
}
