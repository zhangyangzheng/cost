package com.ctrip.hotel.cost.service;

import java.util.List;

// 现付预付的job都继承这个抽象类
public abstract class BaseNotifySettlementJob<T> {

    protected enum ProcessPendingJobMethod{
        ThrowSettle,
        DoneAll,
        DoNothing;
    }

    protected abstract List<T> getPending(List<Integer> sliceIndexList) throws Exception;

    public abstract void execute(List<Integer> sliceIndexList) throws Exception;
}
