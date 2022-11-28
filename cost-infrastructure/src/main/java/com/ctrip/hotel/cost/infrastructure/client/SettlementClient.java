package com.ctrip.hotel.cost.infrastructure.client;

import com.ctrip.framework.foundation.Foundation;
import com.ctrip.hotel.cost.infrastructure.client.compare.ClientCompareHelper;
import com.ctrip.hotel.cost.infrastructure.model.dto.CancelOrderDto;
import com.ctrip.hotel.cost.infrastructure.model.dto.SettlementApplyListDto;
import com.ctrip.hotel.cost.infrastructure.model.dto.SettlementCancelListDto;
import com.ctrip.hotel.cost.infrastructure.model.dto.SettlementPayDataReceiveDto;
import com.ctrip.soa.hotel.settlement.api.*;
import com.ctrip.soa.hotel.vendor.settlement.v2.SettlementCommonSOAV2Client;
import com.ctriposs.baiji.rpc.common.types.AckCodeType;
import hotel.settlement.common.ListHelper;
import hotel.settlement.common.LogHelper;
import hotel.settlement.common.QConfigHelper;
import hotel.settlement.common.helpers.DefaultValueHelper;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import soa.ctrip.com.hotel.vendor.settlement.v1.SettlementWsClient;
import soa.ctrip.com.hotel.vendor.settlement.v1.cancelorder.CancelorderResponseType;
import soa.ctrip.com.hotel.vendor.settlement.v1.settlementdata.SettlementPayData;
import soa.ctrip.com.hotel.vendor.settlement.v1.settlementdata.SettlementPayDataReceiveRequestType;
import soa.ctrip.com.hotel.vendor.settlement.v1.settlementdata.SettlementPayDataReceiveResponseType;

import java.util.ArrayList;
import java.util.List;

@Component
public class SettlementClient {


  @Autowired
  ClientCompareHelper clientCompareHelper;

  @Autowired
  SoaHelper soaHelper;

  // 现付订单抛到前置模块
  public boolean callSettlementPayDataReceive(SettlementPayDataReceiveDto settlementPayDataReceiveDto) {
    boolean result = true;
    try {
      SettlementPayDataReceiveRequestType prepayRequest = new SettlementPayDataReceiveRequestType();
      List<SettlementPayData> datalist = new ArrayList<>();
      datalist.add(settlementPayDataReceiveDto.getSettlementPayData());
      prepayRequest.setSettlementPayDataList(datalist);

      boolean isCall = BooleanUtils.toBoolean(QConfigHelper.getSwitchConfigByKey("FGCostNotifySettle", "F"));

      if(!isCall){
        clientCompareHelper.addComparing(settlementPayDataReceiveDto.getReferenceId(), "settlementPayDataReceive", settlementPayDataReceiveDto.getSettlementPayData(), false);
        return true;
      }

      SettlementPayDataReceiveResponseType response =
          soaHelper.callSoa(
              prepayRequest,
              SettlementWsClient.class,
              "settlementPayDataReceive",
              SettlementPayDataReceiveResponseType.class);

      if (response == null) {
        throw new Exception("interface settlementPayDataReceive error");
      }

      if (response != null
          && response.getResponseResult() != null
          && (response.getResponseResult().getStatus() == null
              || response.getResponseResult().getStatus().getValue()
                  == AckCodeType.Success.getValue())
          && !ListHelper.isEmpty(response.getSettlementPayDataResultList())) {
        if (response.getSettlementPayDataResultList().get(0) != null
            && DefaultValueHelper.getValue(
                    response.getSettlementPayDataResultList().get(0).getRetCode())
                != 0) {
          throw new Exception(response.getSettlementPayDataResultList().get(0).getMessage());
        }
      }
    } catch (Exception ex) {
      result = false;
      LogHelper.logWarn("Failed to transfer the cash order to the front module", ex.getMessage());
    }
    return result;
  }

  // 抛结算新增修改流程
  public boolean callSettlementApplyList(SettlementApplyListDto settlementApplyListDto) {
    boolean result = true;
    try {
      SettlementApplyListRequestType request = new SettlementApplyListRequestType();
      request.setSettleDatas(new ArrayList<>());
      request.getSettleDatas().add(settlementApplyListDto.getSettleDataRequest());

      RequestHead requestHead = new RequestHead();

      requestHead.setAsyncRequest("false");
      requestHead.setRequestType(
          "Account.Vendor.SettlementWebServiceV2.SettlementApplyListRequest");
      requestHead.setReCallType("Account.Vendor.SettlementStatusWSV2.UpdateStatus");
      requestHead.setReferenceID(settlementApplyListDto.getReferenceId());
      requestHead.setEnvironment("FWS");
      requestHead.setUserID(Foundation.app().getAppId());
      request.setHeader(requestHead);

      boolean isCall = BooleanUtils.toBoolean(QConfigHelper.getSwitchConfigByKey("FGCostNotifySettle", "F"));

      if(!isCall){
        clientCompareHelper.addComparing(settlementApplyListDto.getReferenceId(), "settlementApplyList", settlementApplyListDto.getSettleDataRequest(), false);
        return true;
      }

      SettlementApplyListResponseType response =
          soaHelper.callSoa(
              request,
              HotelSettlementApiServiceClient.class,
              "settlementApplyList",
              SettlementApplyListResponseType.class);

      if (response != null
          && !ListHelper.isEmpty(response.getSettleDatas())
          && response.getSettleDatas().get(0) != null) {
        if (DefaultValueHelper.getValue(response.getSettleDatas().get(0).getRetCode()) != 0) {
          throw new Exception(response.getSettleDatas().get(0).getMessage());
        }
      } else {
        throw new Exception("Failed to get Response, Response is null");
      }
    } catch (Exception ex) {
      result = false;
    }

    return result;
  }

  // 为了数据比对方便 现在都是假批量 一个一个调的（与原来一致） 后续比对完成会改成真批量
//  public List<Boolean> batchCallSettlementApplyList(List<SettlementApplyListUsedBo> settlementApplyListUsedBoList){
//    List<Boolean> resList = settlementApplyListUsedBoList.stream().map(p -> callSettlementApplyList(p)).collect(Collectors.toList());
//    return resList;
//  }

  public boolean callCancelOrder(CancelOrderDto cancelOrderDto) {
    boolean result = true;
    try {

      boolean isCall = BooleanUtils.toBoolean(QConfigHelper.getSwitchConfigByKey("FGCostNotifySettle", "F"));

      if(!isCall){
        clientCompareHelper.addComparing(cancelOrderDto.getReferenceId(), "cancelorder", cancelOrderDto.getCancelOrderRequest(), false);
        return true;
      }

      CancelorderResponseType response =
          soaHelper.callSoa(
              cancelOrderDto.getCancelOrderRequest(), SettlementWsClient.class, "cancelorder", CancelorderResponseType.class);

      if (response == null) {
        throw new Exception("cancelorder error");
      }

      if (response != null
          && response.getResponseStatus() != null
          && (response.getResponseStatus().getAck() == null
              || response.getResponseStatus().getAck().getValue() == AckCodeType.Success.getValue())
          && response.getResponseResult() != null) {
        if (response.getResponseResult().getStatus() != null
            && response.getResponseResult().getStatus().getValue()
                == AckCodeType.Failure.getValue()) {
          result = false;
        }
      }
    } catch (Exception ex) {
      result = false;
    }
    return result;
  }

  public boolean callSettlementCancelList(SettlementCancelListDto settlementCancelListDto) {
    boolean result = true;
    try {
      SettlementCancelListRequest requestBody = new SettlementCancelListRequest();
      requestBody.setSettleDatas(new ArrayList<>());
      requestBody.getSettleDatas().add(settlementCancelListDto.getCancelSettleData());

      boolean isCall = BooleanUtils.toBoolean(QConfigHelper.getSwitchConfigByKey("FGCostNotifySettle", "F"));

      if(!isCall){
        clientCompareHelper.addComparing(settlementCancelListDto.getReferenceId(), "settlementCancelList", settlementCancelListDto.getCancelSettleData(), false);
        return true;
      }

      SettlementCancelListResponse response =
          soaHelper.callSoa(
              requestBody,
              SettlementCommonSOAV2Client.class,
              "settlementCancelList",
              SettlementCancelListResponse.class);
      if (response != null && !ListHelper.isEmpty(response.getSettleDatas())) {
        if (response.getSettleDatas().get(0) != null
            && DefaultValueHelper.getValue(response.getSettleDatas().get(0).getRetCode()) != 0) {
          throw new Exception(response.getSettleDatas().get(0).getMessage());
        }
      } else {
        throw new Exception("response is null");
      }
    } catch (Exception ex) {
      result = false;
    }
    return result;
  }
}
