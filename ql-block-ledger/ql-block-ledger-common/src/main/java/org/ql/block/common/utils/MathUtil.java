package org.ql.block.common.utils;

/**
 * Created at 2022/11/22 15:24
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class MathUtil {
  /**
   * 百分数转小数
   * @param percentNum
   * @return
   */
  public static Float percentToFloat(String percentNum){
    percentNum=percentNum.replace("%","");
    return Float.parseFloat(percentNum) / 100;
  }

  /**
   * 将百分数转为 1：N
   * x/100 = 1/N
   * N = 100/x;
   * @param percentNum
   * @return N 取整
   */
  public static int percentToInt(String percentNum){
    percentNum=percentNum.replace("%","");
    float x = Float.parseFloat(percentNum);
    return Math.round(100/x);
  }
}
