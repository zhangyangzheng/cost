package com.ctrip.hotel.cost.strategy;

import com.ctrip.hotel.cost.model.bizenum.ProcessJobMethod;
import com.ctrip.hotel.cost.model.bo.FgOrderAuditMqDataBo;
import com.ctrip.hotel.cost.model.inner.Identify;
import com.ctrip.hotel.cost.model.inner.WJobMergeItem;
import hotel.settlement.common.LogHelper;
import hotel.settlement.common.json.JsonUtils;

import java.util.ArrayList;
import java.util.List;

public class FgThrowStrategyContext extends BaseThrowStrategyContext<FgOrderAuditMqDataBo, WJobMergeItem> {

  // 抛结算列表
  List<FgOrderAuditMqDataBo> throwSettleList = new ArrayList<>();
  // 批量设置成功执行状态列表
  List<FgOrderAuditMqDataBo> doneAllList = new ArrayList<>();
  // 等待列表
  List<FgOrderAuditMqDataBo> doNothingList = new ArrayList<>();

  public List<FgOrderAuditMqDataBo> getThrowSettleList() {
    return throwSettleList;
  }

  public List<FgOrderAuditMqDataBo> getDoneAllList() {
    return doneAllList;
  }

  public List<FgOrderAuditMqDataBo> getDoNothingList() {
    return doNothingList;
  }

  @Override
  public WJobMergeItem processAndGetMergeItem(List<FgOrderAuditMqDataBo> identifyJobList) {
    BaseThrowStrategy<WJobMergeItem> fgThrowStrategy = new FgThrowStrategy(identifyJobList);
    WJobMergeItem mergeItem = fgThrowStrategy.getMergeItem();
    // 获取处理方式
    ProcessJobMethod processMethod = fgThrowStrategy.getProcessMethod();
    if (processMethod.equals(ProcessJobMethod.ThrowSettle)) {
      throwSettleList.add(mergeItem.leader);
    } else if (processMethod.equals(ProcessJobMethod.DoneAll)) {
      doneAllList.addAll(identifyJobList);
    } else if (processMethod.equals(ProcessJobMethod.DoNothing)) {
      doNothingList.addAll(identifyJobList);
    }
    LogHelper.logInfo(
        "FgThrowStrategyContext",
        processMethod.name() + " : " + JsonUtils.beanToJson(identifyJobList));
    return mergeItem;
  }
}
