package com.ctrip.hotel.cost.listener;

import com.ctrip.hotel.cost.infrastructure.consumer.FGOrderNotifyConsumer;
import com.ctrip.hotel.cost.infrastructure.repository.OrderAuditFgMqRepositoryImpl;
import com.ctrip.platform.dal.dao.DalHints;
import hotel.settlement.common.LogHelper;
import hotel.settlement.common.json.JsonUtils;
import hotel.settlement.dao.dal.htlcalculatefeetidb.entity.OrderAuditFgMqTiDBGen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qunar.tc.qmq.Message;
import qunar.tc.qmq.consumer.annotation.QmqConsumer;

@Component
public class FGOrderNotifyListener {

  @Autowired
  FGOrderNotifyConsumer fgOrderNotifyConsumer;

  @QmqConsumer(prefix = "hotel.audit.auditnotifycost", consumerGroup = "100040760")
  public void onMessage(Message message) {
    LogHelper.logInfo("FGOrderNotifyListener", JsonUtils.beanToJson(message));
    try {
      fgOrderNotifyConsumer.insertInto(message);
    } catch (Exception e) {
      LogHelper.logWarn(
          "FGOrderNotifyListener",
          String.format(
              "insert into order_audit_fg_mq fail messageId : %s reason : %s", message.getMessageId(), e.getMessage()));
    }
  }
}
