package com.ctrip.hotel.cost.domain.job;

import java.sql.SQLException;
import java.util.List;

// 现付预付的job都继承这个抽象类
public abstract class BaseNotifySettlementJob<T> {

    protected enum ProcessPendingJobMethod{
        ThrowSettle,
        DoneAll,
        WaitForOther,
        SetFail;
    }

    protected abstract List<T> getPending(List<Integer> sliceIndexList) throws Exception;


    protected abstract void processFailJobList(List<T> failJobList) throws Exception;

    public abstract void execute(List<Integer> sliceIndexList) throws Exception;
}
