package com.ctrip.hotel.cost.infrastructure.util.compare;

import cn.hutool.core.util.HashUtil;
import com.ctrip.hotel.cost.infrastructure.client.SoaHelper;
import com.ctrip.hotel.settlement.exchange.service.HotelSettlementExchangeServiceClient;
import com.ctrip.hotel.settlement.exchange.service.hbasesaveservice.HBaseKeyAndValue;
import com.ctrip.hotel.settlement.exchange.service.hbasesaveservice.HBaseSaveServiceRequestType;
import com.ctrip.hotel.settlement.exchange.service.hbasesaveservice.HBaseSaveServiceResponseType;
import com.ctriposs.baiji.rpc.common.types.AckCodeType;
import hotel.settlement.common.IntegerHelper;
import hotel.settlement.common.LogHelper;
import hotel.settlement.common.json.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class ClientCompareHelper {
  final String hBaseTableName = "ordernotifysettlement";

  final String comparePrefix = "FGNotifySettleCompare";

  final String hBaseColName = "costNotifySettleFG";

  @Autowired SoaHelper soaHelper;

//  @Autowired CompareSettleDataCommonDao compareSettleDataCommonDao;

  private String processReferenceId(String referenceId, String serviceName) {
    // 增加hash前缀 防止region数据倾斜
    String hashPrefix = String.valueOf(Math.abs(HashUtil.apHash(referenceId) % 10));
    String processedReferenceId =
        String.format("%s_%s_%s_%s", hashPrefix, referenceId, comparePrefix, serviceName);
    return processedReferenceId;
  }
//
//  protected void addCompareItemToDB(String processedReferenceId) throws SQLException {
//    CompareSettleDataCommonGen compareSettleDataCommonGen = new CompareSettleDataCommonGen();
//    compareSettleDataCommonGen.setReferenceId(processedReferenceId);
//    compareSettleDataCommonGen.setJobStatus("0");
//    compareSettleDataCommonGen.setExecCount(0);
//    compareSettleDataCommonDao.insert(compareSettleDataCommonGen);
//  }

  protected void addCompareValueToHBase(String processedReferenceId, String compareJson)
      throws Exception {
    HBaseSaveServiceRequestType request = new HBaseSaveServiceRequestType();
    request.setTableName(hBaseTableName);
    HBaseKeyAndValue hBaseKeyAndValue = new HBaseKeyAndValue();
    hBaseKeyAndValue.setRowKey(processedReferenceId);
    hBaseKeyAndValue.setColumnName(hBaseColName);
    hBaseKeyAndValue.setValue(compareJson);
    request.setHBaseKeyAndValueList(Arrays.asList(hBaseKeyAndValue));
    HBaseSaveServiceResponseType response =
        soaHelper.callSoa(
            request,
            HotelSettlementExchangeServiceClient.class,
            "HBaseSaveService",
            HBaseSaveServiceResponseType.class);
    if (response == null
        || response.getResponseStatus() == null
        || !AckCodeType.Success.equals(response.getResponseStatus().getAck())
        || response.getResult() == null
        || !IntegerHelper.equals(response.getResult().getCode(), 0)) {
      throw new Exception("response error");
    }
  }

  public void addComparing(
      String referenceId, String serviceName, Object compareObj, boolean addCompareItem) {
    try {
      String processedReferenceId = processReferenceId(referenceId, serviceName);
      String compareJson = JsonUtils.beanToJson(compareObj);
//      if (addCompareItem) {
//        addCompareItemToDB(processedReferenceId);
//      }
      addCompareValueToHBase(processedReferenceId, compareJson);
    } catch (Exception e) {
      LogHelper.logError(
          String.format("addComparing error %s_%s", comparePrefix, hBaseColName),
          String.format("referenceId : %s, message : %s", referenceId, e.getMessage()));
    }
  }
}
