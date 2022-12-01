package com.ctrip.hotel.cost.infrastructure.mq;

import com.ctrip.hotel.cost.infrastructure.util.type.StringHelper;
import org.springframework.stereotype.Component;
import qunar.tc.qmq.Message;
import qunar.tc.qmq.producer.MessageProducerProvider;

import javax.annotation.Resource;
import java.lang.reflect.Field;

@Component
public class QmqHelper {

    @Resource(name = "messageProducer")
    MessageProducerProvider messageProducer;

    void sendMessage(String subject, Object obj) throws Exception {
        Message message = messageProducer.generateMessage(subject);

        Class objClass = obj.getClass();
        Field[] fields = objClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object fieldValue = field.get(obj);
            message.setProperty(field.getName(), StringHelper.valueOf(fieldValue));
        }

        messageProducer.sendMessage(message);
    }
}
