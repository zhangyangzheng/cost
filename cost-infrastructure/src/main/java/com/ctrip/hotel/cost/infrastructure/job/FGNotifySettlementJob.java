package com.ctrip.hotel.cost.infrastructure.job;

import com.ctrip.hotel.cost.domain.job.BaseNotifySettlementJob;
import com.ctrip.hotel.cost.infrastructure.repository.OrderAuditFgMqRepositoryImpl;
import com.ctrip.platform.dal.dao.DalHints;
import hotel.settlement.common.ListHelper;
import hotel.settlement.common.tuples.Tuple;
import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.OrderAuditFgMqTiDBGen;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class FGNotifySettlementJob extends BaseNotifySettlementJob<OrderAuditFgMqTiDBGen> {

  class DataStatus {
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


  // 合并后的Job对象 处理后同生共死
  class WJobMergeItem {
    // 合并结果job
    OrderAuditFgMqTiDBGen leader;
    // 被合并job集合
    List<OrderAuditFgMqTiDBGen> followers;

    public WJobMergeItem(OrderAuditFgMqTiDBGen leader, List<OrderAuditFgMqTiDBGen> followers) {
      this.leader = leader;
      this.followers = followers;
    }
  }

  final int maxExeCount = 8;

  @Autowired OrderAuditFgMqRepositoryImpl orderAuditFgMqRepository;

  // 1：当前job为同一个订单的所有Job  2：最新的当前job 3：当前job为同一个订单的所有Job的执行状态
  protected Tuple<List<OrderAuditFgMqTiDBGen>, DataStatus> getDataAndStatus(
      Long dataId) throws Exception {
    List<OrderAuditFgMqTiDBGen> orderAuditFgMqTiDBGenList =
        orderAuditFgMqRepository.getJobsByDataId(dataId);
    DataStatus dataStatus = new DataStatus();
    if (!ListHelper.isEmpty(orderAuditFgMqTiDBGenList)) {
      for (OrderAuditFgMqTiDBGen orderAuditFgMqTiDBGen : orderAuditFgMqTiDBGenList) {
        String jobStatus = orderAuditFgMqTiDBGen.getJobStatus();
        String opType = orderAuditFgMqTiDBGen.getOpType();
        if ("W".equals(jobStatus) && "C".equals(opType)) {
          dataStatus.wCreateCount++;
        } else if ("W".equals(jobStatus) && "U".equals(opType)) {
          dataStatus.wUpdateCount++;
        } else if ("W".equals(jobStatus) && "D".equals(opType)) {
          dataStatus.wDeleteCount++;
        } else if ("T".equals(jobStatus) && "C".equals(opType)) {
          dataStatus.tCreateCount++;
        } else if ("T".equals(jobStatus) && "U".equals(opType)) {
          dataStatus.tUpdateCount++;
        } else if ("T".equals(jobStatus) && "D".equals(opType)) {
          dataStatus.tDeleteCount++;
        } else if ("F".equals(jobStatus) && "C".equals(opType)) {
          dataStatus.fCreateCount++;
        } else if ("F".equals(jobStatus) && "U".equals(opType)) {
          dataStatus.fUpdateCount++;
        } else if ("F".equals(jobStatus) && "D".equals(opType)) {
          dataStatus.fDeleteCount++;
        } else {
          throw new Exception("Unexpected jobStatus or opType");
        }
      }
    } else {
      throw new Exception("PendingJob Not exist");
    }
    return new Tuple<>(orderAuditFgMqTiDBGenList, dataStatus);
  }

  // 传进来的job是最新的
  protected ProcessPendingJobMethod getProcessMethodByDataStatus(
      DataStatus dataStatus, OrderAuditFgMqTiDBGen tmpJob) {
    // 已经有成功执行的删除单 不管啥单子 都设置全部完成
    if (dataStatus.tDeleteCount > 0) {
      return ProcessPendingJobMethod.DoneAll;
    }
    // 为删除单
    if ("D".equals(tmpJob.getOpType())) {
      // 已经成功抛出创建修改单
      if (dataStatus.getTCreateAndUpdateCount() > 0) {
        return ProcessPendingJobMethod.ThrowSettle;
      }
      // 没有成功抛出的创建修改单 并且有等待执行失败执行的单子 那就设置全部完成
      if (dataStatus.getWFCreateAndUpdateCount() > 0) {
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
    orderAuditFgMqRepository.batchUpdate(new DalHints(), jobList);
  }

  // 获取合并后的job 被合并的job 拼为一个对象返回
  protected WJobMergeItem getWJobMergeItem(
      List<OrderAuditFgMqTiDBGen> dataJobList) {
    dataJobList = dataJobList.stream().filter(p -> "W".equals(p.getJobStatus())).collect(Collectors.toList());
    // 如果说没有删除单 取最后一单 如果说有删除单 取最后一个删除单 （优先后面的是因为删除移动少）
    int selectedJobInd = dataJobList.size() - 1;
    for (int i = 0; i < dataJobList.size(); i++) {
      if ("D".equals(dataJobList.get(i).getOpType())) {
        selectedJobInd = i;
      }
    }
    OrderAuditFgMqTiDBGen mergedJob = dataJobList.remove(selectedJobInd);
    return new WJobMergeItem(mergedJob, dataJobList);
  }

  protected Set<Long> getDataIdSet(List<OrderAuditFgMqTiDBGen> jobList){
    Set<Long> dataIdSet = new HashSet<>();
    for (OrderAuditFgMqTiDBGen job : jobList) {
        dataIdSet.add(job.getDataId());
    }
    return dataIdSet;
  }

  protected List<OrderAuditFgMqTiDBGen> getAllFollowerJob(List<OrderAuditFgMqTiDBGen> leaderJobList, Map<Long, WJobMergeItem> dataIdWJobMergeItemMap){
    List<OrderAuditFgMqTiDBGen> allFollowerJobList = new ArrayList<>();
    for (OrderAuditFgMqTiDBGen leaderJob : leaderJobList) {
      allFollowerJobList.addAll(dataIdWJobMergeItemMap.get(leaderJob.getDataId()).followers);
    }
    return allFollowerJobList;
  }

  protected void processSuccessJobList(List<OrderAuditFgMqTiDBGen> successJobList, Map<Long, WJobMergeItem> dataIdWJobMergeItemMap)
      throws Exception {
    successJobList.addAll(getAllFollowerJob(successJobList, dataIdWJobMergeItemMap));
    for (OrderAuditFgMqTiDBGen tmpJob : successJobList) {
      tmpJob.setExecCount(tmpJob.getExecCount() + 1);
      tmpJob.setJobStatus("T");
    }
    orderAuditFgMqRepository.batchUpdate(new DalHints(), successJobList);
  }


  protected void processFailJobList(List<OrderAuditFgMqTiDBGen> failJobList, Map<Long, WJobMergeItem> dataIdWJobMergeItemMap)
          throws Exception {
    failJobList.addAll(getAllFollowerJob(failJobList, dataIdWJobMergeItemMap));
    for (OrderAuditFgMqTiDBGen tmpJob : failJobList) {
      tmpJob.setExecCount(tmpJob.getExecCount() + 1);
      if (tmpJob.getExecCount() > maxExeCount) {
        tmpJob.setJobStatus("F");
      }
    }
    orderAuditFgMqRepository.batchUpdate(new DalHints(), failJobList);
  }

  @Override
  protected List<OrderAuditFgMqTiDBGen> getPending(List<Integer> sliceIndexList)
          throws SQLException {
    Integer count = 100; // 后续从qconfig拿
    List<OrderAuditFgMqTiDBGen> orderAuditFgMqTiDBGenList =
            orderAuditFgMqRepository.getPendingJobs(sliceIndexList, count);
    return orderAuditFgMqTiDBGenList;
  }

  @Override
  public void execute(List<Integer> sliceIndexList) throws Exception {
    // 抛结算列表
    List<OrderAuditFgMqTiDBGen> throwSettleList = new ArrayList<>();
    // 批量设置成功执行状态列表
    List<OrderAuditFgMqTiDBGen> setSuccessStatusList = new ArrayList<>();
    // 获取待执行的job列表
    List<OrderAuditFgMqTiDBGen> pendingJobList = getPending(sliceIndexList);

    Set<Long> dataIdSet = getDataIdSet(pendingJobList);

    Map<Long, WJobMergeItem> dataIdWJobMergeItemMap = new HashMap<>();

    for (Long dataId : dataIdSet) {
      Tuple<List<OrderAuditFgMqTiDBGen>, DataStatus> dataAndStatus = getDataAndStatus(dataId);
      List<OrderAuditFgMqTiDBGen> tmpDataAllJobList = dataAndStatus.getFirst();
      DataStatus dataStatus = dataAndStatus.getSecond();
      WJobMergeItem wJobMergeItem = getWJobMergeItem(tmpDataAllJobList);
      dataIdWJobMergeItemMap.put(dataId, wJobMergeItem);
      OrderAuditFgMqTiDBGen leaderJob = wJobMergeItem.leader;
      // 获取处理方式
      ProcessPendingJobMethod processMethod = getProcessMethodByDataStatus(dataStatus, leaderJob);
      if (processMethod.equals(ProcessPendingJobMethod.ThrowSettle)) {
        throwSettleList.add(leaderJob);
      } else if (processMethod.equals(ProcessPendingJobMethod.DoneAll)) {
        setSuccessStatusList.addAll(tmpDataAllJobList);
      }
    }

    // 批量设置不需要处理的Job为执行成功
    updateJobListStatus(setSuccessStatusList, "T");

    // 后续改为调用计费函数 返回成功结果集;
    List<OrderAuditFgMqTiDBGen> successJobList = new ArrayList<>();
    // 不在失败列表里的为成功的
    List<OrderAuditFgMqTiDBGen> failJobList =
        throwSettleList.stream().filter(p -> !successJobList.contains(p)).collect(Collectors.toList());
    // 处理成功列表
    processSuccessJobList(successJobList, dataIdWJobMergeItemMap);
    // 处理失败列表
    processFailJobList(failJobList, dataIdWJobMergeItemMap);
  }
}
