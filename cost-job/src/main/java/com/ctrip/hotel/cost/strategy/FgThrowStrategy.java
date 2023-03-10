package com.ctrip.hotel.cost.strategy;

import com.ctrip.hotel.cost.common.LongHelper;
import com.ctrip.hotel.cost.helper.ConvertHelper;
import com.ctrip.hotel.cost.model.bizenum.JobStatus;
import com.ctrip.hotel.cost.model.bizenum.OpType;
import com.ctrip.hotel.cost.model.bizenum.ProcessJobMethod;
import com.ctrip.hotel.cost.model.bo.FgOrderAuditMqDataBo;
import com.ctrip.hotel.cost.model.inner.JobStatusStatistics;
import com.ctrip.hotel.cost.model.inner.WJobMergeItem;
import hotel.settlement.common.ListHelper;
import hotel.settlement.common.LogHelper;
import hotel.settlement.common.beans.BeanHelper;
import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.OrderAuditFgMqTiDBGen;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FgThrowStrategy extends BaseThrowStrategy<WJobMergeItem> {

  static final Map<String, Integer> opTypePriority;

  static {
    opTypePriority = new HashMap<>();
    opTypePriority.put(OpType.Delete.getValue(), 3);
    opTypePriority.put(OpType.Create.getValue(), 2);
    opTypePriority.put(OpType.Update.getValue(), 1);
  }

  WJobMergeItem wJobMergeItem;

  ProcessJobMethod processJobMethod;

  // 收集订单下各单执行情况
  private JobStatusStatistics getJobStatusStatistics(List<FgOrderAuditMqDataBo> jobList) {
    JobStatusStatistics jobStatusStatistics = new JobStatusStatistics();
    if (!ListHelper.isEmpty(jobList)) {
      boolean isSettled =
          jobList.stream()
              .map(
                  job ->
                      LongHelper.isEffectData(job.getSettleCallbackInfoTiDBGen().getSettlementId())
                          || LongHelper.isEffectData(
                              job.getSettleCallbackInfoTiDBGen().getHwpSettlementId()))
              .reduce(false, (a, b) -> a || b);
      jobStatusStatistics.isSettled = isSettled;
      List<OrderAuditFgMqTiDBGen> orderAuditFgMqTiDBGenList =
          ConvertHelper.fgOrderAuditMqDataBoListToOrderAuditFgMqTiDBGenList(jobList);
      for (OrderAuditFgMqTiDBGen orderAuditFgMqTiDBGen : orderAuditFgMqTiDBGenList) {
        if (StringUtils.equals("T", orderAuditFgMqTiDBGen.getIsThrow())) {
          JobStatus jobStatus = JobStatus.getJobStatus(orderAuditFgMqTiDBGen.getJobStatus());
          String opType = orderAuditFgMqTiDBGen.getOpType();
          if (JobStatus.Pending.equals(jobStatus) && OpType.Create.getValue().equals(opType)) {
            jobStatusStatistics.wCreateCount++;
          } else if (JobStatus.Pending.equals(jobStatus)
              && OpType.Update.getValue().equals(opType)) {
            jobStatusStatistics.wUpdateCount++;
          } else if (JobStatus.Pending.equals(jobStatus)
              && OpType.Delete.getValue().equals(opType)) {
            jobStatusStatistics.wDeleteCount++;
          } else if (JobStatus.Success.equals(jobStatus)
              && OpType.Create.getValue().equals(opType)) {
            jobStatusStatistics.tCreateCount++;
          } else if (JobStatus.Success.equals(jobStatus)
              && OpType.Update.getValue().equals(opType)) {
            jobStatusStatistics.tUpdateCount++;
          } else if (JobStatus.Success.equals(jobStatus)
              && OpType.Delete.getValue().equals(opType)) {
            jobStatusStatistics.tDeleteCount++;
          } else if (JobStatus.Fail.equals(jobStatus) && OpType.Create.getValue().equals(opType)) {
            jobStatusStatistics.fCreateCount++;
          } else if (JobStatus.Fail.equals(jobStatus) && OpType.Update.getValue().equals(opType)) {
            jobStatusStatistics.fUpdateCount++;
          } else if (JobStatus.Fail.equals(jobStatus) && OpType.Delete.getValue().equals(opType)) {
            jobStatusStatistics.fDeleteCount++;
          } else {
            LogHelper.logError(
                "jobStatusStatisticsInit",
                "Unexpected jobStatus or opType referenceId : "
                    + orderAuditFgMqTiDBGen.getReferenceId());
          }
        }
      }
    }
    return jobStatusStatistics;
  }

  // 生成合并单项 主单和副单为一个整体 共同成功失败
  private void initWJobMergeItem(List<FgOrderAuditMqDataBo> jobList) {
    List<FgOrderAuditMqDataBo> wJobList =
        jobList.stream()
            .filter(
                p ->
                    JobStatus.Pending.getValue()
                        .equals(p.getOrderAuditFgMqTiDBGen().getJobStatus()))
            .sorted(
                (o1, o2) ->
                    opTypePriority.get(o2.getOrderAuditFgMqTiDBGen().getOpType())
                        - opTypePriority.get(o1.getOrderAuditFgMqTiDBGen().getOpType()))
            .collect(Collectors.toList());
    FgOrderAuditMqDataBo mergedJob = wJobList.remove(0);
    wJobMergeItem = new WJobMergeItem(mergedJob, wJobList);
  }

  private void initProcessJobMethod(List<FgOrderAuditMqDataBo> jobList) {
    JobStatusStatistics jobStatusStatistics = getJobStatusStatistics(jobList);
    FgOrderAuditMqDataBo leaderJob = wJobMergeItem.leader;
    // 已经有成功执行的删除单 不管啥单子 都设置全部完成
    // 或
    // 为删除单 没有已经成功抛出的创建单修改单 就设置全部成功
    if (jobStatusStatistics.tDeleteCount > 0
        || (OpType.Delete.getValue().equals(leaderJob.getOrderAuditFgMqTiDBGen().getOpType())
            && jobStatusStatistics.getTCreateAndUpdateCount() == 0
            && !jobStatusStatistics.isSettled)) {
      processJobMethod = ProcessJobMethod.DoneAll;
    }
    // 为修改单 且创建单还没执行
    else if (OpType.Update.getValue().equals(leaderJob.getOrderAuditFgMqTiDBGen().getOpType())
        && jobStatusStatistics.tCreateCount == 0
        && !jobStatusStatistics.isSettled) {
      processJobMethod = ProcessJobMethod.DoNothing;
    }
    // 其他情况抛结算
    else {
      processJobMethod = ProcessJobMethod.ThrowSettle;
    }
  }

  public FgThrowStrategy(List<FgOrderAuditMqDataBo> jobList) {
    initWJobMergeItem(jobList);
    initProcessJobMethod(jobList);
  }

  @Override
  public WJobMergeItem getMergeItem() {
    return wJobMergeItem;
  }

  @Override
  public ProcessJobMethod getProcessMethod() {
    return processJobMethod;
  }
}
