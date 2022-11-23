package com.ctrip.hotel.cost.job;

import java.util.List;

// 现付预付的拉取计算并抛结算的job都继承这个抽象类
public abstract class BaseNotifySettlementJob<T> {

    protected enum ProcessPendingJobMethod{
        ThrowSettle,
        DoneAll,
        DoNothing;
    }

    protected abstract List<T> getPending(List<Integer> sliceIndexList) throws Exception;

    public abstract void execute(List<Integer> sliceIndexList) throws Exception;
}
