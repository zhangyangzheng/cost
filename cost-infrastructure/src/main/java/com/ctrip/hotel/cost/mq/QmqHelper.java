package com.ctrip.hotel.cost.mq;

import com.ctrip.framework.clogging.domain.thrift.LogLevel;
import com.ctrip.hotel.cost.common.StringHelper;
import com.ctrip.hotel.cost.common.ThreadLocalCostHolder;
import hotel.settlement.common.LogHelper;
import hotel.settlement.common.json.JsonUtils;
import org.springframework.stereotype.Component;
import qunar.tc.qmq.Message;
import qunar.tc.qmq.MessageSendStateListener;
import qunar.tc.qmq.producer.MessageProducerProvider;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.concurrent.Semaphore;

@Component
public class QmqHelper {

  @Resource(name = "messageProducer")
  MessageProducerProvider messageProducer;

  public final void sendMessageSync(String subject, Object obj) throws Exception {
    Message message = messageProducer.generateMessage(subject);

    Class objClass = obj.getClass();
    Field[] fields = objClass.getDeclaredFields();
    for (Field field : fields) {
      field.setAccessible(true);
      Object fieldValue = field.get(obj);
      message.setProperty(field.getName(), StringHelper.valueOf(fieldValue));
    }

    sendMessageSync(subject, message);
  }

  private void sendMessageSync(String subject, Message message) throws Exception {

    Thread mainThread = Thread.currentThread();

    Semaphore sem = new Semaphore(0);
    messageProducer.sendMessage(
        message,
        new MessageSendStateListener() {
          @Override
          public void onSuccess(Message message) {
            sem.release();
            ThreadLocalCostHolder.allLinkTracingLog(
                      subject + ": " + JsonUtils.beanToJson(message), LogLevel.INFO);
          }

          @Override
          public void onFailed(Message message) {
            mainThread.interrupt();
              ThreadLocalCostHolder.allLinkTracingLog(
                      subject + ": " + JsonUtils.beanToJson(message), LogLevel.ERROR);
          }
        });
    try {
      sem.acquire();
    } catch (InterruptedException e) {
      throw new Exception("message Send Error");
    }
  }
}
