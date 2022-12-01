package com.ctrip.hotel.cost.infrastructure.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import qunar.tc.qmq.producer.MessageProducerProvider;

@Configuration
public class QmqConfig {
    @Bean(value = "messageProducer")
    public MessageProducerProvider messageProducerProviderNoTransaction() {
        MessageProducerProvider messageProducer = new MessageProducerProvider();
        return messageProducer;
    }
}
