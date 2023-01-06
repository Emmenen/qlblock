package org.ql.block.db.share.message;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created at 2022/8/28 17:40
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Data
public class ResponseVo<T> implements Serializable {
  private int status;
  private String message;
  private Collection<T> data;

  public ResponseVo(int status, String message, Collection<T> data) {
    this.message = message;
    this.status = status;
    this.data = data;
  }

  public static <T> ResponseVo<T> ok(String message, Collection<T> data){
    ResponseVo<T> success = new ResponseVo(200, message, data);
    return success;
  }
  public static <T> ResponseVo<T> okJson(String message,Collection<T> data){
    ResponseVo<T> ok = ok(message, data);
    return ok;
  }


  public static <T> ResponseVo<T> error(Collection<T> data){
   return new ResponseVo<>(400,"failed",data);
  }
  public static <T> ResponseVo<T> status(int status,Collection<T> data){
   return new ResponseVo<>(400,"failed",data);
  }
}
