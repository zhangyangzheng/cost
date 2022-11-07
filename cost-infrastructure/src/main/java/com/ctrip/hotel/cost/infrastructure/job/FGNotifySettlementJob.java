package com.ctrip.hotel.cost.infrastructure.job;

import com.ctrip.hotel.cost.domain.job.BaseNotifySettlementJob;
import com.ctrip.hotel.cost.infrastructure.repository.OrderAuditFgMqRepositoryImpl;
import com.ctrip.platform.dal.dao.DalHints;
import hotel.settlement.common.ListHelper;
import hotel.settlement.common.LogHelper;
import hotel.settlement.common.tuples.Tuple;
import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.OrderAuditFgMqTiDBGen;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

  final int maxExeCount = 8;

  @Autowired OrderAuditFgMqRepositoryImpl orderAuditFgMqRepository;

  protected Tuple<List<OrderAuditFgMqTiDBGen>, DataStatus> getDataAndStatus(Long dataId)
      throws Exception {
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
    }
    return new Tuple<>(orderAuditFgMqTiDBGenList, dataStatus);
  }

  protected ProcessPendingJobMethod getProcessMethodByDataStatus(
      DataStatus dataStatus, OrderAuditFgMqTiDBGen orderAuditFgMqTiDBGen) {
    // 大于重试阈值 设置失败
    if (orderAuditFgMqTiDBGen.getExecCount() > maxExeCount) {
      return ProcessPendingJobMethod.SetFail;
    }
    // 已经有成功执行的删除单 不管啥单子 都设置全部完成
    if (dataStatus.tDeleteCount > 0) {
      return ProcessPendingJobMethod.DoneAll;
    }
    // 为删除单
    if ("D".equals(orderAuditFgMqTiDBGen.getOpType())) {
      // 已经成功抛出创建修改单
      if (dataStatus.getTCreateAndUpdateCount() > 0) {
        return ProcessPendingJobMethod.ThrowSettle;
      }
      // 没有成功抛出的创建修改单 并且有等待执行失败执行的单子 那就设置全部完成
      if (dataStatus.getWFCreateAndUpdateCount() > 0) {
        return ProcessPendingJobMethod.DoneAll;
      }
      // 没有任何创建修改单子 就不处理
      return ProcessPendingJobMethod.WaitForOther;
    }
    // 其他情况抛结算(创建修改单 且之前没有成功的删除单)
    return ProcessPendingJobMethod.ThrowSettle;
  }

  protected void setJobListDone(List<OrderAuditFgMqTiDBGen> jobList) throws SQLException {
    jobList.stream().forEach(job -> job.setJobStatus("T"));
    orderAuditFgMqRepository.batchUpdate(new DalHints(), jobList);
  }

  protected void setJobFailStatus(OrderAuditFgMqTiDBGen tmpJob) throws SQLException {
    tmpJob.setJobStatus("F");
    orderAuditFgMqRepository.update(new DalHints(), tmpJob);
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
  protected void processFailJobList(List<OrderAuditFgMqTiDBGen> failJobList) throws Exception {
    for (OrderAuditFgMqTiDBGen tmpJob : failJobList) {
      tmpJob.setExecCount(tmpJob.getExecCount() + 1);
      if (tmpJob.getExecCount() > maxExeCount) {
        tmpJob.setJobStatus("F");
      }
    }
    orderAuditFgMqRepository.batchUpdate(new DalHints(), failJobList);
  }

  @Override
  public void execute(List<Integer> sliceIndexList) throws Exception {
    List<OrderAuditFgMqTiDBGen> pendingJobList = getPending(sliceIndexList);
    List<OrderAuditFgMqTiDBGen> throwSettleList = new ArrayList<>(pendingJobList.size());
    for (OrderAuditFgMqTiDBGen tmpJob : pendingJobList) {
      Tuple<List<OrderAuditFgMqTiDBGen>, DataStatus> dataAndStatus =
          getDataAndStatus(tmpJob.getDataId());
      List<OrderAuditFgMqTiDBGen> tmpDataAllJobList = dataAndStatus.getFirst();
      DataStatus dataStatus = dataAndStatus.getSecond();
      ProcessPendingJobMethod processMethod = getProcessMethodByDataStatus(dataStatus, tmpJob);
      if (processMethod.equals(ProcessPendingJobMethod.ThrowSettle)) {
        throwSettleList.add(tmpJob);
      } else if (processMethod.equals(ProcessPendingJobMethod.DoneAll)) {
        setJobListDone(tmpDataAllJobList);
      } else if (processMethod.equals(ProcessPendingJobMethod.SetFail)) {
        setJobFailStatus(tmpJob);
      }
    }
    // 后续改为调用计费函数 返回失败结果集;
    List<OrderAuditFgMqTiDBGen> failJobList = new ArrayList<>();
    processFailJobList(failJobList);
  }
}
