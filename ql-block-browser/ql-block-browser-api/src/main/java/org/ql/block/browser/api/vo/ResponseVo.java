package org.ql.block.browser.api.vo;

import lombok.Data;

/**
 * Created at 2022/8/28 17:40
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Data
public class ResponseVo<T>  {
  private int status;
  private String message;
  private T data;

  public ResponseVo(int status, String message, T data) {
    this.status = status;
    this.message = message;
    this.data = data;
  }

  public static <T> ResponseVo<T> ok(T data){
   return new ResponseVo<>(200,"success",data);
  }
  public static <T> ResponseVo<T> error(T data){
   return new ResponseVo<>(400,"failed",data);
  }
  public static <T> ResponseVo<T> status(int status,T data){
   return new ResponseVo<>(400,"failed",data);
  }
}
