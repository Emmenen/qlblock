package org.ql.block.sys.log;

import java.text.DecimalFormat;

/**
 * Created at 2022/11/14 15:08
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class LogUtils {
  /**
   * 进度条长度
   */
  private int barLen;

  /**
   * 总数
   */
  private long total;

  /**
   * 用于进度条显示的字符
   */
  private char showChar;

  private DecimalFormat formater = new DecimalFormat("#.##%");

  /**
   * 使用系统标准输出，显示字符进度条及其百分比
   */
  public LogUtils(int barLen,long total, char showChar) {
    this.barLen = barLen;
    this.showChar = showChar;
    this.total=total;
  }

  /**
   * 显示进度条
   */
  public void show(int value) {
    //if (value < 0 || value > 100) {
    //    return;
    //}

    reset();

    // 比例
    float rate = (float) (value*1.0 / total);
    // 比例*进度条总长度=当前长度
    draw(barLen, rate);
    if (value == total) {
      afterComplete();
    }
  }

  /**
   * 画指定长度个showChar
   */
  private void draw(int barLen, float rate) {
    int len = (int) (rate * barLen);
    System.out.print("Progress: ");
    for (int i = 0; i < len; i++) {
      System.out.print(showChar);
    }
    for (int i = 0; i < barLen-len; i++) {
      System.out.print(" ");
    }
    System.out.print(" |" + format(rate));
  }


  /**
   * 光标移动到行首
   */
  private void reset() {
    System.out.print('\r');
  }

  /**
   * 完成后换行
   */
  private void afterComplete() {
    System.out.print('\n');
  }

  private String format(float num) {
    return formater.format(num);
  }



  public static void main(String[] args) throws InterruptedException {
    LogUtils cpb = new LogUtils(50,300, '#');
    for (int i = 1; i <= 300; i++) {
      cpb.show(i);
      Thread.sleep(20);
    }
    System.out.print("1");
    System.out.print('\r');
    System.out.print("12");
    System.out.print('\r');

  }
}
