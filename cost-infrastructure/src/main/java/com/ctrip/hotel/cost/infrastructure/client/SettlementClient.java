package com.ctrip.hotel.cost.infrastructure.client;

import com.ctrip.framework.foundation.Foundation;
import com.ctrip.hotel.cost.infrastructure.model.dto.CancelOrderDto;
import com.ctrip.hotel.cost.infrastructure.model.dto.SettlementApplyListDto;
import com.ctrip.hotel.cost.infrastructure.model.dto.SettlementCancelListDto;
import com.ctrip.hotel.cost.infrastructure.model.dto.SettlementPayDataReceiveDto;
import com.ctrip.soa.hotel.settlement.api.*;
import com.ctrip.soa.hotel.settlement.api.commission.CheckFgBidSplitDto;
import com.ctrip.soa.hotel.settlement.api.commission.FgCostThrowJudgeRequestType;
import com.ctrip.soa.hotel.settlement.api.commission.FgCostThrowJudgeResponseType;
import com.ctrip.soa.hotel.vendor.settlement.v2.SettlementCommonSOAV2Client;
import com.ctrip.soa.settlement.common.api.v2.CodeEnum;
import com.ctriposs.baiji.rpc.common.types.AckCodeType;
import hotel.settlement.common.ListHelper;
import hotel.settlement.common.LongHelper;
import hotel.settlement.common.helpers.DefaultValueHelper;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import soa.ctrip.com.hotel.vendor.settlement.v1.SettlementWsClient;
import soa.ctrip.com.hotel.vendor.settlement.v1.cancelorder.CancelorderResponseType;
import soa.ctrip.com.hotel.vendor.settlement.v1.settlementdata.SettlementPayData;
import soa.ctrip.com.hotel.vendor.settlement.v1.settlementdata.SettlementPayDataReceiveRequestType;
import soa.ctrip.com.hotel.vendor.settlement.v1.settlementdata.SettlementPayDataReceiveResponseType;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class SettlementClient {

  @Resource(name = "FGNotifySettleCompare")
  CompareClient compareClient;

  @Autowired SoaHelper soaHelper;

  // 现付订单抛到前置模块
  public Long callSettlementPayDataReceive(
      SettlementPayDataReceiveDto settlementPayDataReceiveDto) throws Exception {
    boolean result = true;
    SettlementPayDataReceiveRequestType prepayRequest = new SettlementPayDataReceiveRequestType();
    List<SettlementPayData> datalist = new ArrayList<>();
    datalist.add(settlementPayDataReceiveDto.getSettlementPayData());
    prepayRequest.setSettlementPayDataList(datalist);

    boolean isThrow = BooleanUtils.toBoolean(settlementPayDataReceiveDto.getIsThrow());
    if (!isThrow) {
      compareClient.addComparing(
          settlementPayDataReceiveDto.getReferenceId(),
          "settlementPayDataReceive",
          settlementPayDataReceiveDto.getSettlementPayData(),
          false);
      return -1L;
    }

    SettlementPayDataReceiveResponseType response =
        soaHelper.callSoa(
            prepayRequest,
            SettlementWsClient.class,
            "settlementPayDataReceive",
            SettlementPayDataReceiveResponseType.class);

    if (response == null) {
      throw new Exception("settlementPayDataReceive client error, response is null");
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
        throw new Exception(
            "settlementPayDataReceive client error, msg: "
                + response.getSettlementPayDataResultList().get(0).getMessage());
      }
    }

    return LongHelper.tryGetValue(response.getSettlementPayDataResultList().get(0).getSerialNo());
  }

  // 抛结算新增修改流程
  public Long callSettlementApplyList(SettlementApplyListDto settlementApplyListDto)
      throws Exception {
    SettlementApplyListRequestType request = new SettlementApplyListRequestType();
    request.setSettleDatas(new ArrayList<>());
    request.getSettleDatas().add(settlementApplyListDto.getSettleDataRequest());

    RequestHead requestHead = new RequestHead();

    requestHead.setAsyncRequest("false");
    requestHead.setRequestType("Account.Vendor.SettlementWebServiceV2.SettlementApplyListRequest");
    requestHead.setReCallType("Account.Vendor.SettlementStatusWSV2.UpdateStatus");
    requestHead.setReferenceID(settlementApplyListDto.getReferenceId());
    requestHead.setEnvironment("FWS");
    requestHead.setUserID(Foundation.app().getAppId());
    request.setHeader(requestHead);

    boolean isThrow = BooleanUtils.toBoolean(settlementApplyListDto.getIsThrow());

    if (!isThrow) {
      compareClient.addComparing(
          settlementApplyListDto.getReferenceId(),
          "settlementApplyList",
          settlementApplyListDto.getSettleDataRequest(),
          false);
      return -1L;
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
        throw new Exception(
            "settlementApplyList client error, msg: "
                + response.getSettleDatas().get(0).getMessage());
      }
    } else {
      throw new Exception("settlementApplyList client error, response is null");
    }
    return DefaultValueHelper.getValue(response.getSettleDatas().get(0).getSettlementId());
  }

  public boolean callCancelOrder(CancelOrderDto cancelOrderDto) throws Exception {
    boolean result = true;

    boolean isThrow = BooleanUtils.toBoolean(cancelOrderDto.getIsThrow());

    if (!isThrow) {
      compareClient.addComparing(
          cancelOrderDto.getReferenceId(),
          "cancelorder",
          cancelOrderDto.getCancelOrderRequest(),
          false);
      return true;
    }

    CancelorderResponseType response =
        soaHelper.callSoa(
            cancelOrderDto.getCancelOrderRequest(),
            SettlementWsClient.class,
            "cancelorder",
            CancelorderResponseType.class);

    if (response == null) {
      throw new Exception("cancelorder client error, response is null");
    }

    if (response != null
        && response.getResponseStatus() != null
        && (response.getResponseStatus().getAck() == null
            || response.getResponseStatus().getAck().getValue() == AckCodeType.Success.getValue())
        && response.getResponseResult() != null) {
      if (response.getResponseResult().getStatus() != null
          && response.getResponseResult().getStatus().getValue()
              == AckCodeType.Failure.getValue()) {
        throw new Exception("cancelorder client error, response is fail");
      }
    }
    return result;
  }

  public boolean callSettlementCancelList(SettlementCancelListDto settlementCancelListDto)
      throws Exception {
    boolean result = true;
    SettlementCancelListRequest requestBody = new SettlementCancelListRequest();
    requestBody.setSettleDatas(new ArrayList<>());
    requestBody.getSettleDatas().add(settlementCancelListDto.getCancelSettleData());

    boolean isThrow = BooleanUtils.toBoolean(settlementCancelListDto.getIsThrow());

    if (!isThrow) {
      compareClient.addComparing(
          settlementCancelListDto.getReferenceId(),
          "settlementCancelList",
          settlementCancelListDto.getCancelSettleData(),
          false);
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
        throw new Exception(
            "settlementCancelList client error, msg: "
                + response.getSettleDatas().get(0).getMessage());
      }
    } else {
      throw new Exception("settlementCancelList client error, response is null");
    }
    return result;
  }

  public boolean callConfigCanPush() throws Exception {
    FgCostThrowJudgeRequestType request = new FgCostThrowJudgeRequestType();
    request.setJudgeType(1);
    FgCostThrowJudgeResponseType response =
        soaHelper.callSoa(
            request,
            SettlementCommonSOAV2Client.class,
            "fgCostThrowJudge",
            FgCostThrowJudgeResponseType.class);
    if (response == null
        || response.getResponseResult() == null
        || response.getResponseResult().getCode() == null
        || response.getResponseResult().getCode() == CodeEnum.Failure) {
      throw new Exception("configCanPush client error, response is fail");
    }
    return response.getResult();
  }

  public boolean callCheckFGBidSplit(
      Integer vendorChannelID,
      Integer hotelId,
      String orderConfirmType,
      Boolean isSupportAnticipation)
      throws Exception {
    FgCostThrowJudgeRequestType request = new FgCostThrowJudgeRequestType();

    CheckFgBidSplitDto checkFgBidSplitDto = new CheckFgBidSplitDto();
    checkFgBidSplitDto.setVendorChannelID(vendorChannelID);
    checkFgBidSplitDto.setHotelId(hotelId);
    checkFgBidSplitDto.setOrderConfirmType(orderConfirmType);
    checkFgBidSplitDto.setIsSupportAnticipation(isSupportAnticipation);

    request.setCheckFgBidSplitDto(checkFgBidSplitDto);
    request.setJudgeType(0);

    FgCostThrowJudgeResponseType response =
        soaHelper.callSoa(
            request,
            SettlementCommonSOAV2Client.class,
            "fgCostThrowJudge",
            FgCostThrowJudgeResponseType.class);
    if (response == null
        || response.getResponseResult() == null
        || response.getResponseResult().getCode() == null
        || response.getResponseResult().getCode() == CodeEnum.Failure) {
      throw new Exception("checkFGBidSplit client error, response is fail");
    }
    return response.getResult();
  }
}
