package com.ctrip.hotel.cost.infrastructure.helper;

import com.ctrip.framework.foundation.Foundation;
import com.ctrip.hotel.cost.domain.helper.SoaHelper;
import com.ctrip.hotel.cost.infrastructure.model.bo.CancelOrderUsedBo;
import com.ctrip.hotel.cost.infrastructure.model.bo.SettlementApplyListUsedBo;
import com.ctrip.hotel.cost.infrastructure.model.bo.SettlementCancelListUsedBo;
import com.ctrip.hotel.cost.infrastructure.model.bo.SettlementPayDataUsedBo;
import com.ctrip.soa.hotel.settlement.api.*;
import com.ctrip.soa.hotel.vendor.settlement.v2.SettlementCommonSOAV2Client;
import com.ctriposs.baiji.rpc.common.types.AckCodeType;
import hotel.settlement.common.*;
import hotel.settlement.common.helpers.DefaultValueHelper;
import org.springframework.stereotype.Component;
import soa.ctrip.com.hotel.vendor.settlement.v1.SettlementWsClient;
import soa.ctrip.com.hotel.vendor.settlement.v1.cancelorder.CancelorderRequesttype;
import soa.ctrip.com.hotel.vendor.settlement.v1.cancelorder.CancelorderResponseType;
import soa.ctrip.com.hotel.vendor.settlement.v1.settlementdata.SettlementPayData;
import soa.ctrip.com.hotel.vendor.settlement.v1.settlementdata.SettlementPayDataReceiveRequestType;
import soa.ctrip.com.hotel.vendor.settlement.v1.settlementdata.SettlementPayDataReceiveResponseType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ThrowOrderSoaHelper extends SoaHelper {

  // 现付订单抛到前置模块
  protected boolean callSettlementPayDataReceive(SettlementPayDataUsedBo settlementPayDataUsedBo) {
    boolean result = true;
    try {
      SettlementPayData prepaidData = settlementPayDataUsedBo.convertTo();
      SettlementPayDataReceiveRequestType prepayRequest = new SettlementPayDataReceiveRequestType();
      List<SettlementPayData> datalist = new ArrayList<>();
      datalist.add(prepaidData);
      prepayRequest.setSettlementPayDataList(datalist);

      SettlementPayDataReceiveResponseType response =
          callSoa(
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

  // 为了数据比对方便 现在都是假批量 一个一个调的（与原来一致） 后续比对完成会改成真批量
  public List<Boolean> batchCallSettlementPayDataReceive(List<SettlementPayDataUsedBo> settlementPayDataUsedBoList) {
    List<Boolean> resList = settlementPayDataUsedBoList.stream().map(p -> callSettlementPayDataReceive(p)).collect(Collectors.toList());
    return resList;
  }



  // 抛结算新增修改流程
  protected boolean callSettlementApplyList(SettlementApplyListUsedBo settlementApplyListUsedBo){
    boolean result = true;
    try {
      SettleDataRequest settleDataRequest = settlementApplyListUsedBo.convertTo();
      SettlementApplyListRequestType request = new SettlementApplyListRequestType();
      request.setSettleDatas(new ArrayList<>());
      request.getSettleDatas().add(settleDataRequest);


      RequestHead requestHead = new RequestHead();

      requestHead.setAsyncRequest("false");
      requestHead.setRequestType("Account.Vendor.SettlementWebServiceV2.SettlementApplyListRequest");
      requestHead.setReCallType("Account.Vendor.SettlementStatusWSV2.UpdateStatus");
      requestHead.setReferenceID(settlementApplyListUsedBo.getReferenceId());
      requestHead.setEnvironment("FWS");
      requestHead.setUserID(Foundation.app().getAppId());
      request.setHeader(requestHead);

      SettlementApplyListResponseType response = callSoa(
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
  public List<Boolean> batchCallSettlementApplyList(List<SettlementApplyListUsedBo> settlementApplyListUsedBoList){
    List<Boolean> resList = settlementApplyListUsedBoList.stream().map(p -> callSettlementApplyList(p)).collect(Collectors.toList());
    return resList;
  }



  protected boolean callCancelOrder(CancelOrderUsedBo cancelOrderUsedBo) {
    boolean result = true;
    try {
      CancelorderRequesttype request = cancelOrderUsedBo.convertTo();

      CancelorderResponseType response = callSoa(
                      request, SettlementWsClient.class, "cancelorder", CancelorderResponseType.class);

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


  // 为了数据比对方便 现在都是假批量 一个一个调的（与原来一致） 后续比对完成会改成真批量
  public List<Boolean> batchCallCancelOrder(List<CancelOrderUsedBo> cancelOrderUsedBoList){
    List<Boolean> resList = cancelOrderUsedBoList.stream().map(p -> callCancelOrder(p)).collect(Collectors.toList());
    return resList;
  }



  protected boolean callSettlementCancelList(SettlementCancelListUsedBo settlementCancelListUsedBo) {
    boolean result = true;
    try {
      SettlementCancelListRequest requestBody = new SettlementCancelListRequest();
      requestBody.setSettleDatas(new ArrayList<>());
      requestBody.getSettleDatas().add(settlementCancelListUsedBo.convertTo());

      SettlementCancelListResponse response = callSoa(
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

  // 为了数据比对方便 现在都是假批量 一个一个调的（与原来一致） 后续比对完成会改成真批量
  public List<Boolean> batchCallSettlementCancelList(List<SettlementCancelListUsedBo> settlementCancelListUsedBoList){
    List<Boolean> resList = settlementCancelListUsedBoList.stream().map(p -> callSettlementCancelList(p)).collect(Collectors.toList());
    return resList;
  }

}
