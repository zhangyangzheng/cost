package com.ctrip.hotel.cost.domain.consumer;

import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.OrderAuditFgMqTiDBGen;
import qunar.tc.qmq.Message;

public abstract class BaseOrderNotifyConsumer<T> {

  final protected <A> boolean isInLegalArr(A item, A[] legalArr) {
    if (legalArr != null) {
      for (A legalItem : legalArr) {
        if (legalItem.equals(item)) {
          return true;
        }
      }
    }
    return false;
  }

  protected abstract Integer getSliceIndex(Long data);

  protected abstract T convertTo(Message message) throws Exception;

  protected abstract void legalCheck(T item) throws Exception;

  public abstract void insertInto(Message message) throws Exception;
}
