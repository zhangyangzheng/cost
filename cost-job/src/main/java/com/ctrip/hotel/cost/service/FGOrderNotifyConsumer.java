package com.ctrip.hotel.cost.service;

import com.ctrip.hotel.cost.infrastructure.repository.OrderAuditFgMqRepository;
import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.OrderAuditFgMqTiDBGen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import qunar.tc.qmq.Message;

import java.util.Objects;
import java.util.Optional;

@Service
public class FGOrderNotifyConsumer extends BaseOrderNotifyConsumer<OrderAuditFgMqTiDBGen> {
  // 不可变 同一订单号需要在同一分片中
  final int sliceCount = 32;

  // 业务类型 抛结算：0 noshow自动付：1 noshow返佣：2
  final Integer[] businessTypeArr = new Integer[] {0, 1, 2};

  // 操作类型 创建：C 修改：U 删除：D
  final String[] opTypeArr = new String[] {"C", "U", "D"};

  @Autowired
  OrderAuditFgMqRepository orderAuditFgMqRepository;

  @Override
  protected Integer getSliceIndex(Object... values) {
    return Objects.hash(values) % sliceCount;
  }

  @Override
  protected OrderAuditFgMqTiDBGen convertTo(Message message) throws Exception {
    Long orderId = Long.parseLong(message.getStringProperty("orderId"));
    Long fgId =
        Long.parseLong(Optional.ofNullable(message.getStringProperty("fgId")).orElse("-1"));
    Integer businessType = Integer.parseInt(message.getStringProperty("businessType"));
    String opType = message.getStringProperty("opType");
    String referenceId = message.getStringProperty("referenceId");
    if (orderId == null
        || fgId == null
        || businessType == null
        || opType == null
        || referenceId == null) {
      throw new Exception("field can not be null");
    }
    OrderAuditFgMqTiDBGen orderAuditFgMqTiDBGen = new OrderAuditFgMqTiDBGen();
    orderAuditFgMqTiDBGen.setOrderId(orderId);
    orderAuditFgMqTiDBGen.setFgId(fgId);
    orderAuditFgMqTiDBGen.setBusinessType(businessType);
    orderAuditFgMqTiDBGen.setOpType(opType);
    orderAuditFgMqTiDBGen.setReferenceId(referenceId);
    orderAuditFgMqTiDBGen.setSliceIndex(getSliceIndex(orderId, fgId));
    return orderAuditFgMqTiDBGen;
  }

  @Override
  protected void legalCheck(OrderAuditFgMqTiDBGen item) throws Exception {
    if (!(isInLegalArr(item.getBusinessType(), businessTypeArr)
        && isInLegalArr(item.getOpType(), opTypeArr))) {
      throw new Exception("params not legal");
    }
  }

  @Override
  public void insertInto(Message message) throws Exception {
    OrderAuditFgMqTiDBGen orderAuditFgMqTiDBGen = convertTo(message);
    legalCheck(orderAuditFgMqTiDBGen);
    orderAuditFgMqRepository.insert(orderAuditFgMqTiDBGen);
  }
}
