package com.ctrip.hotel.cost.strategy;

import com.ctrip.hotel.cost.model.bizenum.ProcessJobMethod;

public abstract class BaseThrowStrategy<T> {
  public abstract T getMergeItem();

  public abstract ProcessJobMethod getProcessMethod();
}
