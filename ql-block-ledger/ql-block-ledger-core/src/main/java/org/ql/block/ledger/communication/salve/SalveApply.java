package org.ql.block.ledger.communication.salve;

import lombok.Data;
import org.ql.block.ledger.model.block.SalveBlock;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created at 2022/12/10 16:05
 * Author: @Qi Long
 * email: 592918942@qq.com
 */
@Data
public class SalveApply {
  private Set<SalveBlock> salveBlocks = new HashSet<>(2);

  public void addApply(SalveBlock salveBlock){
    salveBlocks.add(salveBlock);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SalveApply)) return false;

    SalveApply that = (SalveApply) o;

    Set<SalveBlock> thatSalveBlocks = that.getSalveBlocks();
    Set<SalveBlock> thisSalveBlocks = getSalveBlocks();

    if (thatSalveBlocks == null && thisSalveBlocks == null) return true;

    if (thatSalveBlocks == null || thisSalveBlocks == null || thatSalveBlocks.size() != thisSalveBlocks.size()
    || thatSalveBlocks.size()== 0 || thisSalveBlocks.size() == 0){
      return false;
    }
    boolean equal = true;
    Iterator<SalveBlock> iterator = thatSalveBlocks.stream().iterator();
    while (iterator.hasNext()) {
      if (!thisSalveBlocks.contains(iterator.next())) {
        equal = false;
      }
    }
    return equal;
  }

  @Override
  public int hashCode() {
    Set<SalveBlock> salveBlocks = getSalveBlocks();
    int hash = 0;
    for (SalveBlock salveBlock : salveBlocks) {
      hash += salveBlock.hashCode();
    }
    return getSalveBlocks() != null ? hash : 0;
  }
}
