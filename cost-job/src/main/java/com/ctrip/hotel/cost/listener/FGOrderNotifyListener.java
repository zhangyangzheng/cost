package com.ctrip.hotel.cost.listener;

import com.ctrip.hotel.cost.consumer.BaseOrderNotifyConsumer;
import com.dianping.cat.annotation.CatTrace;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import hotel.settlement.common.LogHelper;
import hotel.settlement.common.entity.CatBizTypeConstant;
import hotel.settlement.common.json.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qunar.tc.qmq.Message;
import qunar.tc.qmq.NeedRetryException;
import qunar.tc.qmq.consumer.annotation.QmqConsumer;

@Component
public class FGOrderNotifyListener {

  @Autowired BaseOrderNotifyConsumer fgOrderNotifyConsumer;

  private boolean isDuplicateMessage(Exception e){
    Throwable cause = e.getCause();
    if (cause instanceof MySQLIntegrityConstraintViolationException) {
      MySQLIntegrityConstraintViolationException mySQLIntegrityConstraintViolationException =
              (MySQLIntegrityConstraintViolationException) cause;
      String sqlState = mySQLIntegrityConstraintViolationException.getSQLState();
      if (StringUtils.equals(sqlState, "23000")) {
        return true;
      }
    }
    return false;
  }

  private void processMessage(Message message) throws NeedRetryException {
    LogHelper.logInfo("FGOrderNotifyListener", JsonUtils.beanToJson(message));
    try {
      fgOrderNotifyConsumer.insertInto(message);
    } catch (Exception e) {
      String logMessage = String.format(
              "insert into order_audit_fg_mq fail messageId : %s reason : %s",
              message.getMessageId(), e.getMessage());
      if(isDuplicateMessage(e)){
        LogHelper.logWarn(
                "FGOrderNotifyListener", logMessage);
      }
      else {
        LogHelper.logError(
                "FGOrderNotifyListener", logMessage);
        throw new NeedRetryException(logMessage);
      }
    }
  }

  @QmqConsumer(prefix = "hotel.audit.auditnotifycost", consumerGroup = "100042902")
  @CatTrace(type = CatBizTypeConstant.BIZ_QMQ_ACCEPT + ".Cost", name = "hotel.audit.auditnotifycost")
  public void onMessage(Message message) throws NeedRetryException {
    processMessage(message);
  }
}