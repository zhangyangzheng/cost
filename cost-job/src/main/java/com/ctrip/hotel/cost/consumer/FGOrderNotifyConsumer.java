package com.ctrip.hotel.cost.consumer;

import com.ctrip.hotel.cost.infrastructure.repository.OrderAuditFgMqRepository;
import hotel.settlement.common.LogHelper;
import hotel.settlement.common.json.JsonUtils;
import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.OrderAuditFgMqTiDBGen;
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

  @Autowired OrderAuditFgMqRepository orderAuditFgMqRepository;

  @Override
  protected Integer getSliceIndex(Object... values) {
    return Objects.hash(values) % sliceCount;
  }

  @Override
  protected OrderAuditFgMqTiDBGen convertTo(Message message) throws Exception {
    try {
      Long orderId = Long.parseLong(message.getStringProperty("orderId"));
      Integer fgId =
          Integer.parseInt(Optional.ofNullable(message.getStringProperty("fgId")).orElse("-1"));
      Integer businessType = Integer.parseInt(message.getStringProperty("businessType"));
      String opType = message.getStringProperty("opType");
      String referenceId = message.getStringProperty("referenceId");
      OrderAuditFgMqTiDBGen orderAuditFgMqTiDBGen = new OrderAuditFgMqTiDBGen();
      orderAuditFgMqTiDBGen.setOrderId(orderId);
      orderAuditFgMqTiDBGen.setFgId(fgId);
      orderAuditFgMqTiDBGen.setBusinessType(businessType);
      orderAuditFgMqTiDBGen.setOpType(opType);
      orderAuditFgMqTiDBGen.setReferenceId(referenceId);
      orderAuditFgMqTiDBGen.setSliceIndex(getSliceIndex(orderId, fgId));
      return orderAuditFgMqTiDBGen;
    } catch (Exception e) {
      throw new Exception("property is null or parse error");
    }
  }

  @Override
  protected boolean legalCheck(OrderAuditFgMqTiDBGen item) throws Exception {
    if (item.getOrderId() != null
        && item.getFgId() != null
        && item.getBusinessType() != null
        && item.getOpType() != null
        && item.getReferenceId() != null) {
      // 业务类型 操作类型 OrderId检查
      if (isInLegalArr(item.getBusinessType(), businessTypeArr)
          && isInLegalArr(item.getOpType(), opTypeArr)
          && item.getOrderId() > 0) {
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
    orderAuditFgMqRepository.insert(orderAuditFgMqTiDBGen);
  }
}
