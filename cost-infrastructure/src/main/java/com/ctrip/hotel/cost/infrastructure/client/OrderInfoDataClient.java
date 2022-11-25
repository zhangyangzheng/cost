package com.ctrip.hotel.cost.infrastructure.client;

import com.ctrip.framework.foundation.Foundation;
import com.ctrip.hotel.cost.infrastructure.client.SoaHelper;
import com.ctriposs.baiji.rpc.common.types.AckCodeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import soa.ctrip.com.hotel.order.checkin.audit.v2.OrderAuditV2RegisterClient;
import soa.ctrip.com.hotel.order.checkin.audit.v2.RequestHeadType;
import soa.ctrip.com.hotel.order.checkin.audit.v2.getOrderAuditRoomData.GetOrderAuditRoomDataRequestType;
import soa.ctrip.com.hotel.order.checkin.audit.v2.getOrderAuditRoomData.GetOrderAuditRoomDataResponseType;
import soa.ctrip.com.hotel.order.checkin.audit.v2.getOrderAuditRoomData.OrderAuditRoomData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderInfoDataClient {

  @Autowired SoaHelper soaHelper;

  private RequestHeadType getRequestHead() {
    RequestHeadType requestHead = new RequestHeadType();
    requestHead.setClientAppID(Foundation.app().getAppId());
    return requestHead;
  }

  protected List<OrderAuditRoomData> getOrderAuditRoomData(
      List<Long> orderIdList, List<Integer> fgIdList) {
    GetOrderAuditRoomDataRequestType request = new GetOrderAuditRoomDataRequestType();
    request.setRequestHead(getRequestHead());
    request.setOrderIDList(orderIdList);
    request.setFgidList(fgIdList);
    GetOrderAuditRoomDataResponseType response =
        soaHelper.callSoa(
            request,
            OrderAuditV2RegisterClient.class,
            "getOrderAuditRoomData",
            GetOrderAuditRoomDataResponseType.class);
    if (response == null
        || response.getResponseStatus() == null
        || !AckCodeType.Success.equals(response.getResponseStatus().getAck())
        || response.getRetCode() != 0) {
      return new ArrayList<>();
    }
    return response.getOrderAuditRoomDataList();
  }

  public List<OrderAuditRoomData> getOrderAuditRoomDataByFgId(List<Long> fgIdList) {
    List<Integer> IntegerFgIdList =
        fgIdList.stream().map(p -> (p.intValue())).collect(Collectors.toList());
    return getOrderAuditRoomData(null, IntegerFgIdList);
  }

  public List<OrderAuditRoomData> getOrderAuditRoomDataByOrderId(List<Long> orderIdList) {
    return getOrderAuditRoomData(orderIdList, null);
  }
}
