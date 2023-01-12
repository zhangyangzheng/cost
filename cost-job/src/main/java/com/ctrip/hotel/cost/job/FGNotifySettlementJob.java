package com.ctrip.hotel.cost.job;

import com.ctrip.hotel.cost.application.handler.HandlerApi;
import com.ctrip.hotel.cost.application.model.vo.AuditOrderFgReqDTO;
import com.ctrip.hotel.cost.caller.BaseHandlerCaller;
import com.ctrip.hotel.cost.helper.ConvertHelper;
import com.ctrip.hotel.cost.model.bizenum.JobStatus;
import com.ctrip.hotel.cost.model.bizenum.OpType;
import com.ctrip.hotel.cost.model.bizenum.ProcessJobMethod;
import com.ctrip.hotel.cost.model.bo.FgOrderAuditMqDataBo;
import com.ctrip.hotel.cost.model.inner.Identify;
import com.ctrip.hotel.cost.model.inner.WJobMergeItem;
import com.ctrip.hotel.cost.repository.OrderAuditFgMqRepository;
import com.ctrip.hotel.cost.repository.SettleCallbackInfoRepository;
import com.ctrip.hotel.cost.common.util.I18NMessageUtil;
import com.ctrip.hotel.cost.strategy.FgThrowStrategy;
import hotel.settlement.common.ListHelper;
import hotel.settlement.common.LogHelper;
import hotel.settlement.common.QConfigHelper;
import hotel.settlement.common.json.JsonUtils;
import hotel.settlement.common.tuples.Tuple;
import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.OrderAuditFgMqTiDBGen;
import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.SettleCallbackInfoTiDBGen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class FGNotifySettlementJob extends BaseNotifySettlementJob<OrderAuditFgMqTiDBGen> {

  @Autowired OrderAuditFgMqRepository orderAuditFgMqRepository;

  @Autowired SettleCallbackInfoRepository settleCallbackInfoRepository;

  @Autowired
  BaseHandlerCaller handlerCaller;


  protected void processDoneAllJobList(List<FgOrderAuditMqDataBo> jobList) throws Exception {
     List<OrderAuditFgMqTiDBGen> orderAuditFgMqTiDBGenList = ConvertHelper.fgOrderAuditMqDataBoListToOrderAuditFgMqTiDBGenList(jobList);
     orderAuditFgMqTiDBGenList.stream()
        .forEach(
            p -> {
              p.setExecCount(Optional.ofNullable(p.getExecCount()).orElse(0) + 1);
              p.setJobStatus(JobStatus.Success.getValue());
              p.setRemark(I18NMessageUtil.getMessage("FGNotifySettlementJob.Remark.3"));
            });
    orderAuditFgMqRepository.batchUpdate(orderAuditFgMqTiDBGenList);
  }

  protected void processDoNothingJobList(List<FgOrderAuditMqDataBo> jobList) throws Exception {
    List<OrderAuditFgMqTiDBGen> orderAuditFgMqTiDBGenList = ConvertHelper.fgOrderAuditMqDataBoListToOrderAuditFgMqTiDBGenList(jobList);
    orderAuditFgMqTiDBGenList.stream()
            .forEach(
                    p -> {
                      p.setExecCount(Optional.ofNullable(p.getExecCount()).orElse(0) + 1);
                      p.setRemark(I18NMessageUtil.getMessage("FGNotifySettlementJob.Remark.7"));
                    });
    orderAuditFgMqRepository.batchUpdate(orderAuditFgMqTiDBGenList);
  }

  protected void processSuccessJobList(
      List<FgOrderAuditMqDataBo> successJobList,
      Map<Identify, WJobMergeItem> identifyWJobMergeItemMap,
      Map<Identify, List<FgOrderAuditMqDataBo>> identifyJobListMap)
      throws Exception {
    List<OrderAuditFgMqTiDBGen> allSuccessOrderAuditFgMqTiDBGenList = new ArrayList<>();
    List<OrderAuditFgMqTiDBGen> successOrderAuditFgMqTiDBGenList = ConvertHelper.fgOrderAuditMqDataBoListToOrderAuditFgMqTiDBGenList(successJobList);
    for (OrderAuditFgMqTiDBGen orderAuditFgMqTiDBGen : successOrderAuditFgMqTiDBGenList) {
      Identify identity = new Identify(orderAuditFgMqTiDBGen.getOrderId(), orderAuditFgMqTiDBGen.getFgId());
      if (OpType.Delete.getValue().equals(orderAuditFgMqTiDBGen.getOpType())) {
        // 不会发生取不到的情况
        List<FgOrderAuditMqDataBo> identityJobList = identifyJobListMap.get(identity);
        // 设置所有单remark
        identityJobList.stream()
            .forEach(
                p -> p.getOrderAuditFgMqTiDBGen().setRemark(I18NMessageUtil.getMessage("FGNotifySettlementJob.Remark.4")));
        // 覆盖主单remark
        orderAuditFgMqTiDBGen.setRemark(I18NMessageUtil.getMessage("FGNotifySettlementJob.Remark.5"));
        allSuccessOrderAuditFgMqTiDBGenList.addAll(ConvertHelper.fgOrderAuditMqDataBoListToOrderAuditFgMqTiDBGenList(identityJobList));
      } else {
        // 不会发生取不到的情况
        WJobMergeItem wJobMergeItem = identifyWJobMergeItemMap.get(identity);
        wJobMergeItem.setLeaderRemark(I18NMessageUtil.getMessage("FGNotifySettlementJob.Remark.5"));
        wJobMergeItem.setFollowersRemark(
            I18NMessageUtil.getMessage("FGNotifySettlementJob.Remark.2"));
        // 主单和副单同生共死
        allSuccessOrderAuditFgMqTiDBGenList.add(wJobMergeItem.leader.getOrderAuditFgMqTiDBGen());
        allSuccessOrderAuditFgMqTiDBGenList.addAll(ConvertHelper.fgOrderAuditMqDataBoListToOrderAuditFgMqTiDBGenList(wJobMergeItem.followers));
      }
    }
    allSuccessOrderAuditFgMqTiDBGenList.stream()
        .forEach(
                p -> {
                  p.setExecCount(Optional.ofNullable(p.getExecCount()).orElse(0) + 1);
                  p.setJobStatus(JobStatus.Success.getValue());
            });
    orderAuditFgMqRepository.batchUpdate(allSuccessOrderAuditFgMqTiDBGenList);
  }



  protected void processFailJobList(
      List<FgOrderAuditMqDataBo> failJobList,
      Map<Identify, WJobMergeItem> identifyWJobMergeItemMap)
      throws Exception {
    List<OrderAuditFgMqTiDBGen> allFailOrderAuditFgMqTiDBGenList = new ArrayList<>();
    List<OrderAuditFgMqTiDBGen> failOrderAuditFgMqTiDBGenList = ConvertHelper.fgOrderAuditMqDataBoListToOrderAuditFgMqTiDBGenList(failJobList);
    for (OrderAuditFgMqTiDBGen orderAuditFgMqTiDBGen : failOrderAuditFgMqTiDBGenList) {
      Identify identity = new Identify(orderAuditFgMqTiDBGen.getOrderId(), orderAuditFgMqTiDBGen.getFgId());
      // 不会发生取不到的情况
      WJobMergeItem wJobMergeItem = identifyWJobMergeItemMap.get(identity);
      wJobMergeItem.setLeaderRemark(I18NMessageUtil.getMessage("FGNotifySettlementJob.Remark.6"));
      wJobMergeItem.setFollowersRemark(
          I18NMessageUtil.getMessage("FGNotifySettlementJob.Remark.1"));
      // 主单和副单同生共死
      allFailOrderAuditFgMqTiDBGenList.add(wJobMergeItem.leader.getOrderAuditFgMqTiDBGen());
      allFailOrderAuditFgMqTiDBGenList.addAll(ConvertHelper.fgOrderAuditMqDataBoListToOrderAuditFgMqTiDBGenList(wJobMergeItem.followers));
    }
    for (OrderAuditFgMqTiDBGen orderAuditFgMqTiDBGen : allFailOrderAuditFgMqTiDBGenList) {
      orderAuditFgMqTiDBGen.setExecCount(Optional.ofNullable(orderAuditFgMqTiDBGen.getExecCount()).orElse(0) + 1);
      if (orderAuditFgMqTiDBGen.getExecCount()
          > Integer.parseInt(
              QConfigHelper.getSwitchConfigByKey("fgNotifySettlementJobMaxExeCount", "5"))) {
        orderAuditFgMqTiDBGen.setJobStatus(JobStatus.Fail.getValue());
      }
    }
    orderAuditFgMqRepository.batchUpdate(allFailOrderAuditFgMqTiDBGenList);
  }



  protected List<FgOrderAuditMqDataBo> getFgOrderAuditMqDataBoList(
      List<OrderAuditFgMqTiDBGen> orderAuditFgMqTiDBGenList) throws Exception {
    List<FgOrderAuditMqDataBo> auditOrderFgReqDTOList = new ArrayList<>();
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

      FgOrderAuditMqDataBo fgOrderAuditMqDataBo =
          new FgOrderAuditMqDataBo(orderAuditFgMqTiDBGen, settleCallbackInfoTiDBGen);

      auditOrderFgReqDTOList.add(fgOrderAuditMqDataBo);
    }
    return auditOrderFgReqDTOList;
  }


  protected List<FgOrderAuditMqDataBo> getJobList(
          Identify identify) throws Exception {
    List<OrderAuditFgMqTiDBGen> orderAuditFgMqTiDBGenList =
            orderAuditFgMqRepository.getJobsByOrderIdAndFgId(identify.orderId, identify.fgId);
    List<FgOrderAuditMqDataBo> fgOrderAuditMqDataBoList = getFgOrderAuditMqDataBoList(orderAuditFgMqTiDBGenList);
    return fgOrderAuditMqDataBoList;
  }


  protected Set<Identify> getPendingIdentify(List<Integer> sliceIndexList) throws Exception {
    List<OrderAuditFgMqTiDBGen> pendingJobs = getPending(sliceIndexList);
    LogHelper.logInfo("FGNotifySettlementJobGetPendingJobs", JsonUtils.beanToJson(pendingJobs));
    Set<Identify> identifySet =
            pendingJobs.stream()
                    .map(p -> new Identify(p.getOrderId(), p.getFgId()))
                    .collect(Collectors.toSet());
    return identifySet;
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
    List<FgOrderAuditMqDataBo> throwSettleList = new ArrayList<>();
    // 批量设置成功执行状态列表
    List<FgOrderAuditMqDataBo> doneAllList = new ArrayList<>();
    // 等待列表
    List<FgOrderAuditMqDataBo> doNothingList = new ArrayList<>();
    // 获取待执行的单子标识
    Set<Identify> identifySet = getPendingIdentify(sliceIndexList);

    Map<Identify, WJobMergeItem> identifyWJobMergeItemMap = new HashMap<>();

    Map<Identify, List<FgOrderAuditMqDataBo>> identifyJobListMap = new HashMap<>();

    for (Identify identify : identifySet) {
      List<FgOrderAuditMqDataBo> identityJobList = getJobList(identify);
      FgThrowStrategy fgThrowStrategy = new FgThrowStrategy(identityJobList);
      WJobMergeItem wJobMergeItem = fgThrowStrategy.getWJobMergeItem();
      identifyWJobMergeItemMap.put(identify, wJobMergeItem);
      identifyJobListMap.put(identify, identityJobList);
      // 获取处理方式
      ProcessJobMethod processMethod = fgThrowStrategy.getProcessMethod();
      if (processMethod.equals(ProcessJobMethod.ThrowSettle)) {
        throwSettleList.add(wJobMergeItem.leader);
      } else if (processMethod.equals(ProcessJobMethod.DoneAll)) {
        doneAllList.addAll(identityJobList);
      } else if (processMethod.equals(ProcessJobMethod.DoNothing)) {
        doNothingList.addAll(identityJobList);
      }
    }


    LogHelper.logInfo("FGNotifySettlementJobThrowSettleDtoList", JsonUtils.beanToJson(throwSettleList));

    LogHelper.logInfo("FGNotifySettlementJobDoneAllList", JsonUtils.beanToJson(doneAllList));

    LogHelper.logInfo("FGNotifySettlementJobDoNothingList", JsonUtils.beanToJson(doNothingList));

    Tuple<List<FgOrderAuditMqDataBo>, List<FgOrderAuditMqDataBo>> successAndFailList = handlerCaller.batchCallAndGetSuccessAndFailList(throwSettleList);

    // 批量设置不需要处理的Job为执行成功
    processDoneAllJobList(doneAllList);
    // 批量设置 remark： 等待其他单到达后执行
    processDoNothingJobList(doNothingList);
    // 处理成功列表
    processSuccessJobList(successAndFailList.getFirst(), identifyWJobMergeItemMap, identifyJobListMap);
    // 处理失败列表
    processFailJobList(successAndFailList.getSecond(), identifyWJobMergeItemMap);
  }
}
