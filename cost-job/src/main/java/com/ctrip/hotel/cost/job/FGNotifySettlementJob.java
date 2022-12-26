package com.ctrip.hotel.cost.job;

import com.ctrip.hotel.cost.application.handler.HandlerApi;
import com.ctrip.hotel.cost.application.model.vo.AuditOrderFgReqDTO;
import com.ctrip.hotel.cost.domain.data.model.OrderAuditFgMqBO;
import com.ctrip.hotel.cost.domain.data.model.SettlementCallBackInfo;
import com.ctrip.hotel.cost.model.JobStatus;
import com.ctrip.hotel.cost.repository.OrderAuditFgMqRepository;
import com.ctrip.hotel.cost.repository.SettleCallbackInfoRepository;
import com.ctrip.hotel.cost.common.util.I18NMessageUtil;
import hotel.settlement.common.ListHelper;
import hotel.settlement.common.LogHelper;
import hotel.settlement.common.QConfigHelper;
import hotel.settlement.common.beans.BeanHelper;
import hotel.settlement.common.json.JsonUtils;
import hotel.settlement.common.tuples.Tuple;
import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.OrderAuditFgMqTiDBGen;
import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.SettleCallbackInfoTiDBGen;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class FGNotifySettlementJob extends BaseNotifySettlementJob<OrderAuditFgMqTiDBGen> {

  static class JobStatusStatistics {
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

  }



  // 合并后的Job对象 同生共死
  static class WJobMergeItem {
    // 主单
    OrderAuditFgMqTiDBGen leader;
    // 副单
    List<OrderAuditFgMqTiDBGen> followers;

    public WJobMergeItem(OrderAuditFgMqTiDBGen leader, List<OrderAuditFgMqTiDBGen> followers) {
      this.leader = leader;
      this.followers = followers;
    }

    public void setLeaderRemark(String remark) {
      leader.setRemark(remark);
    }

    public void setFollowersRemark(String remark) {
      followers.stream().forEach(job -> job.setRemark(remark));
    }
  }



  static class Identify {
    Long orderId;
    Long fgId;


    public Identify(Long orderId, Long fgId) {
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

  static final Map<String, Integer> opTypePriority;

  static {
    opTypePriority = new HashMap<>();
    opTypePriority.put("D", 3);
    opTypePriority.put("C", 2);
    opTypePriority.put("U", 1);
  }

  @Autowired OrderAuditFgMqRepository orderAuditFgMqRepository;

  @Autowired SettleCallbackInfoRepository settleCallbackInfoRepository;

  @Autowired HandlerApi handlerApi;


  // 获取一个订单内各单的执行状态
  protected JobStatusStatistics getJobStatusStatistics(List<OrderAuditFgMqTiDBGen> orderAuditFgMqTiDBGenList){
    JobStatusStatistics jobStatusStatistics = new JobStatusStatistics();
    if (!ListHelper.isEmpty(orderAuditFgMqTiDBGenList)) {
      for (OrderAuditFgMqTiDBGen orderAuditFgMqTiDBGen : orderAuditFgMqTiDBGenList) {
        JobStatus jobStatus = JobStatus.getJobStatus(orderAuditFgMqTiDBGen.getJobStatus());
        String opType = orderAuditFgMqTiDBGen.getOpType();
        if (JobStatus.Pending.equals(jobStatus) && "C".equals(opType)) {
          jobStatusStatistics.wCreateCount++;
        } else if (JobStatus.Pending.equals(jobStatus) && "U".equals(opType)) {
          jobStatusStatistics.wUpdateCount++;
        } else if (JobStatus.Pending.equals(jobStatus) && "D".equals(opType)) {
          jobStatusStatistics.wDeleteCount++;
        } else if (JobStatus.Success.equals(jobStatus) && "C".equals(opType)) {
          jobStatusStatistics.tCreateCount++;
        } else if (JobStatus.Success.equals(jobStatus) && "U".equals(opType)) {
          jobStatusStatistics.tUpdateCount++;
        } else if (JobStatus.Success.equals(jobStatus) && "D".equals(opType)) {
          jobStatusStatistics.tDeleteCount++;
        } else if (JobStatus.Fail.equals(jobStatus) && "C".equals(opType)) {
          jobStatusStatistics.fCreateCount++;
        } else if (JobStatus.Fail.equals(jobStatus) && "U".equals(opType)) {
          jobStatusStatistics.fUpdateCount++;
        } else if (JobStatus.Fail.equals(jobStatus) && "D".equals(opType)) {
          jobStatusStatistics.fDeleteCount++;
        } else {
          LogHelper.logError(
              "FGNotifySettlementJob",
              "Unexpected jobStatus or opType referenceId : "
                  + orderAuditFgMqTiDBGen.getReferenceId());
        }
      }
    }
    return jobStatusStatistics;
  }

  protected Tuple<JobStatusStatistics, List<OrderAuditFgMqTiDBGen>> getStatusAndJobList(
      Identify identify) throws Exception {
    List<OrderAuditFgMqTiDBGen> orderAuditFgMqTiDBGenList =
        orderAuditFgMqRepository.getJobsByOrderIdAndFgId(identify.orderId, identify.fgId);
    JobStatusStatistics jobStatusStatistics = getJobStatusStatistics(orderAuditFgMqTiDBGenList);
    return new Tuple<>(jobStatusStatistics, orderAuditFgMqTiDBGenList);
  }

  protected ProcessPendingJobMethod getProcessMethodByJobStatusStatistics(
      JobStatusStatistics jobStatusStatistics, OrderAuditFgMqTiDBGen tmpJob) {
    // 已经有成功执行的删除单 不管啥单子 都设置全部完成
    // 或
    // 为删除单 没有已经成功抛出的创建单修改单 就设置全部成功
    if (jobStatusStatistics.tDeleteCount > 0
        || ("D".equals(tmpJob.getOpType()) && jobStatusStatistics.getTCreateAndUpdateCount() == 0)) {
      return ProcessPendingJobMethod.DoneAll;
    }
    // 为修改单 且创建单还没执行
    if ("U".equals(tmpJob.getOpType()) && jobStatusStatistics.tCreateCount == 0) {
      return ProcessPendingJobMethod.DoNothing;
    }
    // 其他情况抛结算
    return ProcessPendingJobMethod.ThrowSettle;
  }

  protected void processDoneAllJobList(List<OrderAuditFgMqTiDBGen> jobList) throws Exception {
    jobList.stream()
        .forEach(
            job -> {
              job.setJobStatus(JobStatus.Success.getValue());
              job.setRemark(I18NMessageUtil.getMessage("FGNotifySettlementJob.Remark.3"));
            });
    orderAuditFgMqRepository.batchUpdate(jobList);
  }

  // 获取合并后的job 被合并的job 拼为一个对象返回
  public WJobMergeItem getWJobMergeItem(List<OrderAuditFgMqTiDBGen> jobList) {
    List<OrderAuditFgMqTiDBGen> wJobList =
        jobList.stream()
            .filter(p -> JobStatus.Pending.getValue().equals(p.getJobStatus()))
            .map(p -> BeanHelper.convert(p, OrderAuditFgMqTiDBGen.class))
            .sorted(
                (o1, o2) -> opTypePriority.get(o2.getOpType()) - opTypePriority.get(o1.getOpType()))
            .collect(Collectors.toList());
    OrderAuditFgMqTiDBGen mergedJob = wJobList.remove(0);
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
        // 设置所有单remark
        identityJobList.stream()
            .forEach(
                p -> p.setRemark(I18NMessageUtil.getMessage("FGNotifySettlementJob.Remark.4")));
        // 覆盖主单remark
        job.setRemark(I18NMessageUtil.getMessage("FGNotifySettlementJob.Remark.5"));
        allSuccessJobList.addAll(identityJobList);
      } else {
        // 不会发生取不到的情况
        WJobMergeItem wJobMergeItem = identifyWJobMergeItemMap.get(identity);
        wJobMergeItem.setLeaderRemark(I18NMessageUtil.getMessage("FGNotifySettlementJob.Remark.5"));
        wJobMergeItem.setFollowersRemark(
            I18NMessageUtil.getMessage("FGNotifySettlementJob.Remark.2"));
        // 主单和副单同生共死
        allSuccessJobList.add(wJobMergeItem.leader);
        allSuccessJobList.addAll(wJobMergeItem.followers);
      }
    }
    for (OrderAuditFgMqTiDBGen job : allSuccessJobList) {
      job.setExecCount(job.getExecCount() + 1);
      job.setJobStatus(JobStatus.Success.getValue());
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
      wJobMergeItem.setLeaderRemark(I18NMessageUtil.getMessage("FGNotifySettlementJob.Remark.6"));
      wJobMergeItem.setFollowersRemark(
          I18NMessageUtil.getMessage("FGNotifySettlementJob.Remark.1"));
      // 主单和副单同生共死
      allFailJobList.add(wJobMergeItem.leader);
      allFailJobList.addAll(wJobMergeItem.followers);
    }
    for (OrderAuditFgMqTiDBGen job : allFailJobList) {
      job.setExecCount(job.getExecCount() + 1);
      if (job.getExecCount()
          > Integer.parseInt(
              QConfigHelper.getSwitchConfigByKey("fgNotifySettlementJobMaxExeCount", "5"))) {
        job.setJobStatus(JobStatus.Fail.getValue());
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



  protected AuditOrderFgReqDTO getAuditOrderFgReqDTO(
      OrderAuditFgMqTiDBGen orderAuditFgMqTiDBGen,
      SettleCallbackInfoTiDBGen settleCallbackInfoTiDBGen) {
    OrderAuditFgMqBO orderAuditFgMqBO =
        BeanHelper.convert(orderAuditFgMqTiDBGen, OrderAuditFgMqBO.class);
    SettlementCallBackInfo settlementCallBackInfo =
        BeanHelper.convert(settleCallbackInfoTiDBGen, SettlementCallBackInfo.class);
    settlementCallBackInfo.setPushWalletPay(
        BooleanUtils.toBooleanObject(settleCallbackInfoTiDBGen.getPushWalletPay()));

    AuditOrderFgReqDTO auditOrderFgReqDTO =
        new AuditOrderFgReqDTO(orderAuditFgMqBO, settlementCallBackInfo);

    return auditOrderFgReqDTO;
  }



  protected List<AuditOrderFgReqDTO> getAuditOrderFgReqDTOList(
      List<OrderAuditFgMqTiDBGen> orderAuditFgMqTiDBGenList) throws Exception {
    List<AuditOrderFgReqDTO> auditOrderFgReqDTOList = new ArrayList<>();
    List<String> referenceIdList =
        orderAuditFgMqTiDBGenList.stream()
            .map(OrderAuditFgMqTiDBGen::getReferenceId)
            .collect(Collectors.toList());
    Map<String, SettleCallbackInfoTiDBGen> referenceIdSettleCallbackInfoMap =
        settleCallbackInfoRepository.getMapByReferenceIdList(referenceIdList);
    for (OrderAuditFgMqTiDBGen orderAuditFgMqTiDBGen : orderAuditFgMqTiDBGenList) {
      String referenceId = orderAuditFgMqTiDBGen.getReferenceId();
      SettleCallbackInfoTiDBGen settleCallbackInfoTiDBGen =
          referenceIdSettleCallbackInfoMap.get(referenceId);

      AuditOrderFgReqDTO auditOrderFgReqDTO =
          getAuditOrderFgReqDTO(orderAuditFgMqTiDBGen, settleCallbackInfoTiDBGen);

      auditOrderFgReqDTOList.add(auditOrderFgReqDTO);
    }
    return auditOrderFgReqDTOList;
  }



  @Override
  protected List<OrderAuditFgMqTiDBGen> getPending(List<Integer> sliceIndexList) throws Exception {
    Integer minBetween =
        Integer.parseInt(
            QConfigHelper.getSwitchConfigByKey("fgNotifySettlementJobMinuteBetween", "30"));
    Integer count =
        Integer.parseInt(
            QConfigHelper.getSwitchConfigByKey("fgNotifySettlementJobBatchSize", "100"));
    List<OrderAuditFgMqTiDBGen> pendingJobs =
        orderAuditFgMqRepository.getPendingJobs(sliceIndexList, minBetween, count);
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

    List<AuditOrderFgReqDTO> throwSettleDtoList = getAuditOrderFgReqDTOList(throwSettleList);

    List<String> successReferenceList = handlerApi.auditOrderFg(throwSettleDtoList);

    Set<String> successReferenceIdSet = new HashSet<>(successReferenceList);

    // 成功列表
    List<OrderAuditFgMqTiDBGen> successJobList =
        throwSettleList.stream()
            .filter(p -> successReferenceIdSet.contains(p.getReferenceId()))
            .collect(Collectors.toList());

    LogHelper.logInfo("FGNotifySettlementJobSuccess", JsonUtils.beanToJson(successJobList));

    // 失败列表
    List<OrderAuditFgMqTiDBGen> failJobList =
        throwSettleList.stream()
            .filter(p -> !successReferenceIdSet.contains(p.getReferenceId()))
            .collect(Collectors.toList());

    LogHelper.logError("FGNotifySettlementJobFail", JsonUtils.beanToJson(failJobList));

    // 批量设置不需要处理的Job为执行成功
    processDoneAllJobList(setSuccessStatusList);
    // 处理成功列表
    processSuccessJobList(successJobList, identifyWJobMergeItemMap, identifyJobListMap);
    // 处理失败列表
    processFailJobList(failJobList, identifyWJobMergeItemMap);
  }
}
