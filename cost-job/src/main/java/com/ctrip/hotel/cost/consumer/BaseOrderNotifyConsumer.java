package com.ctrip.hotel.cost.consumer;

import qunar.tc.qmq.Message;

// 消费通知消息的消费者的父类（预付现付）
public abstract class BaseOrderNotifyConsumer<T> {

  // job状态 T: 成功 F: 失败 W: 待执行 I: 非法输入
  final String[] jobStatusArr = new String[] {"T", "F", "W", "I"};

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

  protected abstract Integer getSliceIndex(Object... values);

  protected abstract T convertTo(Message message) throws Exception;

  protected abstract boolean legalCheck(T item) throws Exception;

  public abstract void insertInto(Message message) throws Exception;
}
