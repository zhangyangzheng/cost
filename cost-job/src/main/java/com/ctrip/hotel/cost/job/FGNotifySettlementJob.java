package com.ctrip.hotel.cost.job;

import com.ctrip.hotel.cost.infrastructure.repository.OrderAuditFgMqRepository;
import hotel.settlement.common.ListHelper;
import hotel.settlement.common.beans.BeanHelper;
import hotel.settlement.common.tuples.Tuple;
import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.OrderAuditFgMqTiDBGen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FGNotifySettlementJob extends BaseNotifySettlementJob<OrderAuditFgMqTiDBGen> {

  class JobStatusStatistics {
    int wCreateCount = 0;
    int wUpdateCount = 0;
    int wDeleteCount = 0;
    int tCreateCount = 0;
    int tUpdateCount = 0;
    int tDeleteCount = 0;
    int fCreateCount = 0;
    int fUpdateCount = 0;
    int fDeleteCount = 0;

    int getTCreateAndUpdateCount() {
      return tCreateCount + tUpdateCount;
    }

    int getWFCreateAndUpdateCount() {
      return wCreateCount + wUpdateCount + fCreateCount + fUpdateCount;
    }
  }

  // 合并后的Job对象 同生共死
  class WJobMergeItem {
    // 主单
    OrderAuditFgMqTiDBGen leader;
    // 副单
    List<OrderAuditFgMqTiDBGen> followers;

    public WJobMergeItem(OrderAuditFgMqTiDBGen leader, List<OrderAuditFgMqTiDBGen> followers) {
      this.leader = leader;
      this.followers = followers;
    }
  }

  class Identify {
    Long orderId;
    Integer fgId;

    public Identify(Long orderId, Integer fgId) {
      this.orderId = orderId;
      this.fgId = fgId;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Identify identify = (Identify) o;
      return Objects.equals(orderId, identify.orderId) && Objects.equals(fgId, identify.fgId);
    }

    @Override
    public int hashCode() {
      return Objects.hash(orderId, fgId);
    }
  }

  final int maxExeCount = 8;

  @Autowired
  OrderAuditFgMqRepository orderAuditFgMqRepository;

  protected Tuple<JobStatusStatistics, List<OrderAuditFgMqTiDBGen>> getStatusAndJobList(
      Identify identify) throws Exception {
    List<OrderAuditFgMqTiDBGen> orderAuditFgMqTiDBGenList =
        orderAuditFgMqRepository.getJobsByOrderIdAndFgId(identify.orderId, identify.fgId);
    JobStatusStatistics jobStatusStatistics = new JobStatusStatistics();
    if (!ListHelper.isEmpty(orderAuditFgMqTiDBGenList)) {
      for (OrderAuditFgMqTiDBGen orderAuditFgMqTiDBGen : orderAuditFgMqTiDBGenList) {
        String jobStatus = orderAuditFgMqTiDBGen.getJobStatus();
        String opType = orderAuditFgMqTiDBGen.getOpType();
        if ("W".equals(jobStatus) && "C".equals(opType)) {
          jobStatusStatistics.wCreateCount++;
        } else if ("W".equals(jobStatus) && "U".equals(opType)) {
          jobStatusStatistics.wUpdateCount++;
        } else if ("W".equals(jobStatus) && "D".equals(opType)) {
          jobStatusStatistics.wDeleteCount++;
        } else if ("T".equals(jobStatus) && "C".equals(opType)) {
          jobStatusStatistics.tCreateCount++;
        } else if ("T".equals(jobStatus) && "U".equals(opType)) {
          jobStatusStatistics.tUpdateCount++;
        } else if ("T".equals(jobStatus) && "D".equals(opType)) {
          jobStatusStatistics.tDeleteCount++;
        } else if ("F".equals(jobStatus) && "C".equals(opType)) {
          jobStatusStatistics.fCreateCount++;
        } else if ("F".equals(jobStatus) && "U".equals(opType)) {
          jobStatusStatistics.fUpdateCount++;
        } else if ("F".equals(jobStatus) && "D".equals(opType)) {
          jobStatusStatistics.fDeleteCount++;
        } else {
          throw new Exception("Unexpected jobStatus or opType");
        }
      }
    } else {
      throw new Exception("PendingJob Not exist");
    }
    return new Tuple<>(jobStatusStatistics, orderAuditFgMqTiDBGenList);
  }

  protected ProcessPendingJobMethod getProcessMethodByJobStatusStatistics(
      JobStatusStatistics jobStatusStatistics, OrderAuditFgMqTiDBGen tmpJob) {
    // 已经有成功执行的删除单 不管啥单子 都设置全部完成
    if (jobStatusStatistics.tDeleteCount > 0) {
      return ProcessPendingJobMethod.DoneAll;
    }
    // 为删除单
    if ("D".equals(tmpJob.getOpType())) {
      // 已经成功抛出创建修改单
      if (jobStatusStatistics.getTCreateAndUpdateCount() > 0) {
        return ProcessPendingJobMethod.ThrowSettle;
      }
      // 没有成功抛出的创建修改单 并且有等待执行失败执行的单子 那就设置全部完成
      if (jobStatusStatistics.getWFCreateAndUpdateCount() > 0) {
        return ProcessPendingJobMethod.DoneAll;
      }
      // 没有任何创建修改单子 就不处理（等待其他单子）
      return ProcessPendingJobMethod.DoNothing;
    }
    // 其他情况抛结算(当前为创建修改单 且之前没有成功的删除单)
    return ProcessPendingJobMethod.ThrowSettle;
  }

  protected void updateJobListStatus(List<OrderAuditFgMqTiDBGen> jobList, String jobStatus)
      throws SQLException {
    jobList.stream().forEach(job -> job.setJobStatus(jobStatus));
    orderAuditFgMqRepository.batchUpdate(jobList);
  }

  // 获取合并后的job 被合并的job 拼为一个对象返回
  protected WJobMergeItem getWJobMergeItem(List<OrderAuditFgMqTiDBGen> jobList) {
    List<OrderAuditFgMqTiDBGen> wJobList =
        jobList.stream()
            .filter(p -> "W".equals(p.getJobStatus()))
            .map(p -> BeanHelper.convert(p, OrderAuditFgMqTiDBGen.class))
            .collect(Collectors.toList());
    // 如果说没有删除单 取最后一单 如果说有删除单 取最后一个删除单 （优先后面的是因为删除移动少）
    int selectedJobInd = wJobList.size() - 1;
    for (int i = 0; i < wJobList.size(); i++) {
      if ("D".equals(wJobList.get(i).getOpType())) {
        selectedJobInd = i;
      }
    }
    OrderAuditFgMqTiDBGen mergedJob = wJobList.remove(selectedJobInd);
    return new WJobMergeItem(mergedJob, wJobList);
  }

  protected void processSuccessJobList(
      List<OrderAuditFgMqTiDBGen> successJobList,
      Map<Identify, WJobMergeItem> identifyWJobMergeItemMap,
      Map<Identify, List<OrderAuditFgMqTiDBGen>> identifyJobListMap)
      throws Exception {
    List<OrderAuditFgMqTiDBGen> allSuccessJobList = new ArrayList<>();
    for (OrderAuditFgMqTiDBGen job : successJobList) {
      Identify identity = new Identify(job.getOrderId(), job.getFgId());
      if ("D".equals(job.getOpType())) {
        // 不会发生取不到的情况
        List<OrderAuditFgMqTiDBGen> identityJobList = identifyJobListMap.get(identity);
        allSuccessJobList.addAll(identityJobList);
      } else {
        // 不会发生取不到的情况
        WJobMergeItem wJobMergeItem = identifyWJobMergeItemMap.get(identity);
        // 主单和副单同生共死
        allSuccessJobList.add(wJobMergeItem.leader);
        allSuccessJobList.addAll(wJobMergeItem.followers);
      }
    }
    for (OrderAuditFgMqTiDBGen job : allSuccessJobList) {
      job.setExecCount(job.getExecCount() + 1);
      job.setJobStatus("T");
    }
    orderAuditFgMqRepository.batchUpdate(allSuccessJobList);
  }

  protected void processFailJobList(
      List<OrderAuditFgMqTiDBGen> failJobList,
      Map<Identify, WJobMergeItem> identifyWJobMergeItemMap)
      throws Exception {
    List<OrderAuditFgMqTiDBGen> allFailJobList = new ArrayList<>();
    for (OrderAuditFgMqTiDBGen job : failJobList) {
      Identify identity = new Identify(job.getOrderId(), job.getFgId());
      // 不会发生取不到的情况
      WJobMergeItem wJobMergeItem = identifyWJobMergeItemMap.get(identity);
      // 主单和副单同生共死
      allFailJobList.add(wJobMergeItem.leader);
      allFailJobList.addAll(wJobMergeItem.followers);
    }
    for (OrderAuditFgMqTiDBGen job : allFailJobList) {
      job.setExecCount(job.getExecCount() + 1);
      if (job.getExecCount() > maxExeCount) {
        job.setJobStatus("F");
      }
    }
    orderAuditFgMqRepository.batchUpdate(allFailJobList);
  }

  protected Set<Identify> getPendingIdentify(List<Integer> sliceIndexList) throws Exception {
    List<OrderAuditFgMqTiDBGen> pendingJobs = getPending(sliceIndexList);
    Set<Identify> identifySet =
        pendingJobs.stream()
            .map(p -> new Identify(p.getOrderId(), p.getFgId()))
            .collect(Collectors.toSet());
    return identifySet;
  }

  @Override
  protected List<OrderAuditFgMqTiDBGen> getPending(List<Integer> sliceIndexList) throws Exception {
    Integer count = 100; // 后续从qconfig拿
    List<OrderAuditFgMqTiDBGen> pendingJobs =
        orderAuditFgMqRepository.getPendingJobs(sliceIndexList, count);
    return pendingJobs;
  }

  @Override
  public void execute(List<Integer> sliceIndexList) throws Exception {
    // 抛结算列表
    List<OrderAuditFgMqTiDBGen> throwSettleList = new ArrayList<>();
    // 批量设置成功执行状态列表
    List<OrderAuditFgMqTiDBGen> setSuccessStatusList = new ArrayList<>();
    // 获取待执行的单子标识
    Set<Identify> identifySet = getPendingIdentify(sliceIndexList);

    Map<Identify, WJobMergeItem> identifyWJobMergeItemMap = new HashMap<>();

    Map<Identify, List<OrderAuditFgMqTiDBGen>> identifyJobListMap = new HashMap<>();

    for (Identify identify : identifySet) {
      Tuple<JobStatusStatistics, List<OrderAuditFgMqTiDBGen>> jobStatusStatisticsAndJobList =
          getStatusAndJobList(identify);
      JobStatusStatistics jobStatusStatistics = jobStatusStatisticsAndJobList.getFirst();
      List<OrderAuditFgMqTiDBGen> identityJobList = jobStatusStatisticsAndJobList.getSecond();
      WJobMergeItem wJobMergeItem = getWJobMergeItem(identityJobList);
      identifyWJobMergeItemMap.put(identify, wJobMergeItem);
      identifyJobListMap.put(identify, identityJobList);
      OrderAuditFgMqTiDBGen leaderJob = wJobMergeItem.leader;
      // 获取处理方式
      ProcessPendingJobMethod processMethod =
          getProcessMethodByJobStatusStatistics(jobStatusStatistics, leaderJob);
      if (processMethod.equals(ProcessPendingJobMethod.ThrowSettle)) {
        throwSettleList.add(leaderJob);
      } else if (processMethod.equals(ProcessPendingJobMethod.DoneAll)) {
        setSuccessStatusList.addAll(identityJobList);
      }
    }

    // 批量设置不需要处理的Job为执行成功
    updateJobListStatus(setSuccessStatusList, "T");

    // 后续改为调用计费函数 返回成功结果集;
    List<OrderAuditFgMqTiDBGen> successJobList = new ArrayList<>();
    // 不在失败列表里的为成功的
    List<OrderAuditFgMqTiDBGen> failJobList =
        throwSettleList.stream()
            .filter(p -> !successJobList.contains(p))
            .map(p -> BeanHelper.convert(p, OrderAuditFgMqTiDBGen.class))
            .collect(Collectors.toList());
    // 处理成功列表
    processSuccessJobList(successJobList, identifyWJobMergeItemMap, identifyJobListMap);
    // 处理失败列表
    processFailJobList(failJobList, identifyWJobMergeItemMap);
  }
}
