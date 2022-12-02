package com.ctrip.hotel.cost.infrastructure.mq;

import com.alibaba.fastjson.JSON;
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


    final private void sendMessage(String subject, Message message) throws Exception {
        ThreadLocalCostHolder.allLinkTracingLog(subject + ": " + JSON.toJSONString(message), LogLevel.INFO);
        messageProducer.sendMessage(message, new MessageSendStateListener() {
            @Override
            public void onSuccess(Message message) {
                LogHelper.logInfo("subject:" + subject, JsonUtils.beanToJson(message));
            }

            @Override
            public void onFailed(Message message) {
                LogHelper.logError("subject:" + subject, JsonUtils.beanToJson(message));
            }
        });
    }
}
