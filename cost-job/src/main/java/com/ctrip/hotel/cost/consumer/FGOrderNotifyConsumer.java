package com.ctrip.hotel.cost.consumer;

import com.ctrip.hotel.cost.infrastructure.repository.OrderAuditFgMqRepository;
import com.ctrip.hotel.cost.infrastructure.repository.SettleCallbackInfoRepository;
import com.ctrip.hotel.cost.common.LongHelper;
import hotel.settlement.common.LogHelper;
import hotel.settlement.common.json.JsonUtils;
import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.OrderAuditFgMqTiDBGen;
import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.SettleCallbackInfoTiDBGen;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import qunar.tc.qmq.Message;

import java.util.Objects;
import java.util.Optional;

@Service
public class FGOrderNotifyConsumer extends BaseOrderNotifyConsumer<OrderAuditFgMqTiDBGen> {
  // 不可变 同一订单号/房间号需要在同一分片中
  final int sliceCount = 32;

  // 业务类型 抛结算：0 noshow自动付：1 noshow返佣：2
  final Integer[] businessTypeArr = new Integer[] {0, 1, 2};

  // 操作类型 创建：C 修改：U 删除：D
  final String[] opTypeArr = new String[] {"C", "U", "D"};

  // 是否抛单 T:抛单 F:不抛
  final String[] isThrowArr = new String[] {"T", "F"};

  @Autowired OrderAuditFgMqRepository orderAuditFgMqRepository;

  @Autowired
  SettleCallbackInfoRepository settleCallbackInfoRepository;

  protected SettleCallbackInfoTiDBGen getSettleCallbackInfo(Message message){
    SettleCallbackInfoTiDBGen settleCallbackInfoTiDBGen = new SettleCallbackInfoTiDBGen();

    // 幂等字段
    String referenceId = message.getStringProperty("referenceId");
    // 业务字段
    Long settlementId = LongHelper.getNullableLong(message.getStringProperty("settlementId"));
    Long orderInfoId = LongHelper.getNullableLong(message.getStringProperty("orderInfoId"));
    Long hwpSettlementId = LongHelper.getNullableLong(message.getStringProperty("hwpSettlementId"));
    String pushWalletPay = BooleanUtils.toBoolean(message.getStringProperty("pushWalletPay")) ? "T" : "F";
    String pushReferenceId = message.getStringProperty("pushReferenceId");
    String hwpReferenceId = message.getStringProperty("hwpReferenceId");

    settleCallbackInfoTiDBGen.setReferenceId(referenceId);
    settleCallbackInfoTiDBGen.setSettlementId(settlementId);
    settleCallbackInfoTiDBGen.setOrderInfoId(orderInfoId);
    settleCallbackInfoTiDBGen.setHwpSettlementId(hwpSettlementId);
    settleCallbackInfoTiDBGen.setPushWalletPay(pushWalletPay);
    settleCallbackInfoTiDBGen.setPushReferenceId(pushReferenceId);
    settleCallbackInfoTiDBGen.setHwpReferenceId(hwpReferenceId);

    return settleCallbackInfoTiDBGen;
  }

  @Override
  protected Integer getSliceIndex(Object... values) {
    return Objects.hash(values) % sliceCount;
  }

  @Override
  protected OrderAuditFgMqTiDBGen convertTo(Message message) throws Exception {
    try {
      Long orderId =
              Long.parseLong(Optional.ofNullable(message.getStringProperty("orderId")).orElse("-1"));
      Long fgId =
              Long.parseLong(Optional.ofNullable(message.getStringProperty("fgId")).orElse("-1"));
      Integer businessType = Integer.parseInt(Optional.ofNullable(message.getStringProperty("businessType")).orElse("0"));
      String opType = message.getStringProperty("opType");
      String isThrow = message.getStringProperty("isThrow");
      String referenceId = message.getStringProperty("referenceId");
      OrderAuditFgMqTiDBGen orderAuditFgMqTiDBGen = new OrderAuditFgMqTiDBGen();
      orderAuditFgMqTiDBGen.setOrderId(orderId);
      orderAuditFgMqTiDBGen.setFgId(fgId);
      orderAuditFgMqTiDBGen.setBusinessType(businessType);
      orderAuditFgMqTiDBGen.setOpType(opType);
      orderAuditFgMqTiDBGen.setIsThrow(isThrow);
      orderAuditFgMqTiDBGen.setReferenceId(referenceId);
      orderAuditFgMqTiDBGen.setSliceIndex(getSliceIndex(orderId, fgId));
      return orderAuditFgMqTiDBGen;
    } catch (Exception e) {
      throw new Exception("property is null or parse error");
    }
  }

  @Override
  protected boolean legalCheck(OrderAuditFgMqTiDBGen item) {
    if (item.getOrderId() != -1L
            && item.getOpType() != null
            && item.getIsThrow() != null
            && item.getReferenceId() != null) {
      // 业务类型 操作类型 OrderId检查
      if (isInLegalArr(item.getBusinessType(), businessTypeArr)
              && isInLegalArr(item.getOpType(), opTypeArr)
              && isInLegalArr(item.getIsThrow(), isThrowArr)) {
        // 如果业务类型不为ns 需要校验fgId是否有效
        if (item.getBusinessType() == 0) {
          if (item.getFgId() > 0) {
            return true;
          }
          // 如果业务类型为ns 不需要校验fgId 直接返回true
        } else {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public void insertInto(Message message) throws Exception {
    OrderAuditFgMqTiDBGen orderAuditFgMqTiDBGen = convertTo(message);
    if (!legalCheck(orderAuditFgMqTiDBGen)) {
      String jsonStr = JsonUtils.beanToJson(orderAuditFgMqTiDBGen);
      LogHelper.logError("FGOrderNotifyListener", "message not legal:" + jsonStr);
      orderAuditFgMqTiDBGen.setJobStatus("F");
    }
    SettleCallbackInfoTiDBGen settleCallbackInfoTiDBGen = getSettleCallbackInfo(message);
    orderAuditFgMqRepository.insert(orderAuditFgMqTiDBGen);
    settleCallbackInfoRepository.insert(settleCallbackInfoTiDBGen);
  }
}
