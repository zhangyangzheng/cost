package com.ctrip.hotel.cost.caller;

import com.ctrip.hotel.cost.application.handler.HandlerApi;
import com.ctrip.hotel.cost.application.model.vo.AuditOrderFgReqDTO;
import com.ctrip.hotel.cost.helper.ConvertHelper;
import com.ctrip.hotel.cost.model.bo.FgOrderAuditMqDataBo;
import hotel.settlement.common.ListHelper;
import hotel.settlement.common.LogHelper;
import hotel.settlement.common.json.JsonUtils;
import hotel.settlement.common.tuples.Tuple;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component("fkBatchHandlerCaller")
public class FakeBatchHandlerCaller extends BaseHandlerCaller {

  @Autowired HandlerApi handlerApi;

  @Override
  public Tuple<List<FgOrderAuditMqDataBo>, List<FgOrderAuditMqDataBo>>
      batchCallAndGetSuccessAndFailList(List<FgOrderAuditMqDataBo> throwSettleList) {
    Set<String> successReferenceIdSet = new HashSet<>();
    List<AuditOrderFgReqDTO> throwSettleDtoList =
        ConvertHelper.fgOrderAuditMqDataBoListToAuditOrderFgReqDTOList(throwSettleList);
    if (CollectionUtils.isNotEmpty(throwSettleDtoList)) {
      for (AuditOrderFgReqDTO auditOrderFgReqDTO : throwSettleDtoList) {
        List<AuditOrderFgReqDTO> singleList = new ArrayList<>();
        singleList.add(auditOrderFgReqDTO);
        successReferenceIdSet.addAll(handlerApi.auditOrderFg(singleList));
      }
    }
    // 成功列表
    List<FgOrderAuditMqDataBo> successJobList =
        throwSettleList.stream()
            .filter(
                p -> successReferenceIdSet.contains(p.getOrderAuditFgMqTiDBGen().getReferenceId()))
            .collect(Collectors.toList());

    // 失败列表
    List<FgOrderAuditMqDataBo> failJobList =
        throwSettleList.stream()
            .filter(
                p -> !successReferenceIdSet.contains(p.getOrderAuditFgMqTiDBGen().getReferenceId()))
            .collect(Collectors.toList());

    LogHelper.logInfo("FGNotifySettlementJobSuccess", JsonUtils.beanToJson(successJobList));

    if (!ListHelper.isEmpty(failJobList)) {
      LogHelper.logWarn("FGNotifySettlementJobFail", JsonUtils.beanToJson(failJobList));
    }

    return new Tuple<>(successJobList, failJobList);
  }
}
