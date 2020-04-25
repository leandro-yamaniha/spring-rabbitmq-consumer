package com.example.spring.consumer.amqp.impl;

import com.example.spring.consumer.amqp.AmqpConsumer;
import com.example.spring.consumer.dto.MessageQueue;
import com.example.spring.consumer.service.ConsumerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConsumer implements AmqpConsumer<MessageQueue> {

    @Autowired
    private ConsumerService consumerService;

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.request.queue.name}")
    public void consumer(MessageQueue message) {
    	
    	consumerService.action(message);
  
    }
}
