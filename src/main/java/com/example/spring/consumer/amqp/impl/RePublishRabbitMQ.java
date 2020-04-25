package com.example.spring.consumer.amqp.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.spring.consumer.amqp.AmqpRePublish;

@Component
public class RePublishRabbitMQ implements AmqpRePublish {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.request.exchange.name}")
    private String exchangeName;

    @Value("${spring.rabbitmq.request.queue.routing-key}")
    private String queueRoutingKey;

    @Value("${spring.rabbitmq.request.dead-letter.name}")
    private String deadLetterName;

    @Value("${spring.rabbitmq.request.parking-lot.routing-key}")
    private String parkingLotRoutingKey;

    private static final String X_RETRIES_HEADER = "x-retries";

    @Override
    @Scheduled(fixedDelay=5000)
    public void rePublish() {
    	
    	Set<Message> messages = getQueueMessages();

        messages.forEach(message -> {
            Map<String, Object> headers = message.getMessageProperties().getHeaders();
            Integer retriesHeader = (Integer) headers.get(X_RETRIES_HEADER);

            if(retriesHeader == null) {
                retriesHeader = 0;
            }

            if (retriesHeader < 3) {
                headers.put(X_RETRIES_HEADER, retriesHeader + 1);
                rabbitTemplate.send(exchangeName,  queueRoutingKey, message);
            } else {
                rabbitTemplate.send(parkingLotRoutingKey, message);
            }
        });
    }

    private Set<Message> getQueueMessages() {
        Set<Message> messages = new HashSet<>();
        Boolean isNull;
        Message message;

        do {
            message = rabbitTemplate.receive(deadLetterName);
            isNull = message != null;

            if(Boolean.TRUE.equals(isNull)) {
                messages.add(message);
            }
        } while (Boolean.TRUE.equals(isNull));

        return messages;
    }

}
