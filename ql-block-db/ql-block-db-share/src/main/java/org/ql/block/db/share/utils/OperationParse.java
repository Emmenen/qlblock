package org.ql.block.db.share.utils;


import org.ql.block.db.share.enums.CommandEnum;
import org.ql.block.db.share.enums.LimitEnum;
import org.ql.block.db.share.enums.TypeEnum;
import org.ql.block.db.share.exceptions.OptionalSyntaxException;
import org.ql.block.db.share.message.Limit;
import org.ql.block.db.share.message.Operation;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;

/**
 * Created at 2022/12/24 13:02
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
public class OperationParse {

  //将字符串转为操作
  public static Operation toOperation(String str) throws OptionalSyntaxException {
    // "create database master"
    String trim = str.trim();
    char[] charArray = trim.toCharArray();
    ArrayDeque<String> queue = new ArrayDeque<>();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < charArray.length; i++) {
      switch (charArray[i]){
        case ';':
        case ' ':
          queue.add(sb.toString());
          sb = new StringBuilder();
          break;
        case '(':
          if (sb.toString().length()>0) {
            throw new OptionalSyntaxException("语法错误，在'('附近");
          }else {
            i++;
            while (charArray[i]!=')'){
              sb.append(charArray[i]);
            }
            if (charArray[++i] != ' ') {
              throw new OptionalSyntaxException("语法错误，在')'附近");
            }else {
              queue.add(sb.toString());
              sb = new StringBuilder();
            }
          }
          break;
        default:
          sb.append(charArray[i]);
          break;
      }
    }
    if (sb.toString().length()>0) {
      queue.add(sb.toString());
    }

    CommandEnum commandEnum = CommandEnum.fromString(queue.poll());
    Operation operation = new Operation();
    operation.setCommand(commandEnum);
    switch (commandEnum){
      case CREATE:
      case DROP:
        TypeEnum typeEnum = TypeEnum.fromString(queue.poll());
        operation.setType(typeEnum);
        operation.setTarget(queue.poll());
        break;
      case DELETE:
      case INSERT:
      case UPDATE:
        operation.setTarget(queue.poll());
        operation.setKey(Iq80Factory.bytes(queue.poll()));
        operation.setValue(Iq80Factory.bytes(queue.poll()));
        break;
      case SELECT:
        operation.setTarget(queue.poll());
        operation.setKey(Iq80Factory.bytes(queue.poll()));
        break;
      case USE:
        operation.setDatabase(queue.poll());
        break;
      case COUNT:
        operation.setTarget(queue.poll());
        break;
      default:
//        throw new OptionalSyntaxException("语法错误");
    }
    String poll = queue.poll();
    if (poll!=null) {
      LimitEnum limitEnum = LimitEnum.fromString(poll);
      switch (limitEnum) {
        case LIMIT:
          Limit limit = new Limit();
          limit.setOffset(Integer.valueOf(queue.poll()));//offset
          limit.setSize(Integer.valueOf(queue.poll()));//size
          operation.setLimit(limit);
          break;
      }
    }
    return operation;
  }
}
