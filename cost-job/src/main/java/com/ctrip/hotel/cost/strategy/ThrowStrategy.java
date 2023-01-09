package com.ctrip.hotel.cost.strategy;

import com.ctrip.hotel.cost.model.bizenum.ProcessJobMethod;

public abstract class ThrowStrategy {
    public abstract ProcessJobMethod getProcessMethod();
}
