package org.ql.block.db.share.message;

import lombok.Data;
import org.ql.block.db.share.enums.CommandEnum;
import org.ql.block.db.share.enums.TypeEnum;
import org.ql.block.db.share.utils.Iq80Factory;

import java.io.Serializable;
import java.util.Random;

/**
 * Created at 2022/12/19 21:36
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Data
public class Operation implements Message, Serializable {
  private String uid;
  private CommandEnum command;
  private TypeEnum type;
  private String target;
  private String database;
  private byte[] key;
  private byte[] value;
  private Limit limit;

  public Operation() {
    this.uid = generateUID();
  }
  public static String generateUID(){
    Random random = new Random();
    String result="";
    for(int i=0;i<8;i++){
      //首字母不能为0
      result += (random.nextInt(9)+1);
    }
    return result;
  }

  @Override
  public String toString() {
    return this.getCommand()+" "+this.getDatabase()+" "+this.getType()+" "+this.getTarget()+" "+ Iq80Factory.asString(this.getKey()) +" "+ new String(this.getValue());
  }



}
