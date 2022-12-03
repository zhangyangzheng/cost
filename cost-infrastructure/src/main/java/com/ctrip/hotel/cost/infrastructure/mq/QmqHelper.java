package com.ctrip.hotel.cost.infrastructure.mq;

import com.ctrip.hotel.cost.common.StringHelper;
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

    final public void sendMessage(String subject, Object obj) throws Exception {
        Message message = messageProducer.generateMessage(subject);

        Class objClass = obj.getClass();
        Field[] fields = objClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object fieldValue = field.get(obj);
            message.setProperty(field.getName(), StringHelper.valueOf(fieldValue));
        }

        sendMessage(subject, message);
    }


    private void sendMessage(String subject, Message message) throws Exception {
        Semaphore sem = new Semaphore(0);
        messageProducer.sendMessage(message, new MessageSendStateListener() {
            @Override
            public void onSuccess(Message message) {
                sem.release();
                LogHelper.logInfo("subject:" + subject, JsonUtils.beanToJson(message));
            }

            @Override
            public void onFailed(Message message) {
                LogHelper.logError("subject:" + subject, JsonUtils.beanToJson(message));
            }
        });

        sem.acquire();
    }
}
