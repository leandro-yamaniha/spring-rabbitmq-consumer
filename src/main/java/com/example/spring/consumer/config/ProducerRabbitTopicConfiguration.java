package com.example.spring.consumer.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProducerRabbitTopicConfiguration {

	@Value("${spring.rabbitmq.request.exchange.name}")
	private String exchangeName;
	 
    @Value("${spring.rabbitmq.request.queue.name}")
    private String queueName;
    
    @Value("${spring.rabbitmq.request.queue.routing-key}")
    private String queueRoutingKey;
    
    @Value("${spring.rabbitmq.request.dead-letter.name}")
    private String deadLetterName;
    
    @Value("${spring.rabbitmq.request.dead-letter.routing-key}")
    private String deadLetterRoutingKey;

    @Value("${spring.rabbitmq.request.parking-lot.name}")
    private String parkingLotName;
    
    @Value("${spring.rabbitmq.request.parking-lot.routing-key}")
    private String parkingLotRoutingKey;
    
    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange(exchangeName);        
    }
        
    @Bean
    Queue queue() {
        return  QueueBuilder.durable(queueName)
                .deadLetterExchange("")
                .deadLetterRoutingKey(deadLetterRoutingKey)   
                .quorum()
                .build();
    }
        
    @Bean
    Queue deadLetter() {
        return QueueBuilder.durable(deadLetterName)
                .quorum()
                .build();
    }

    @Bean
    Queue parkingLot() {
    	return QueueBuilder.durable(parkingLotName)
    			.quorum()
    			.build();
    }

    @Bean
    public Binding bindingQueue() {
        return BindingBuilder.bind(queue())
                .to(topicExchange())
                .with(queueRoutingKey);
    }

    /*
    @Bean
    public Binding bindingDeadLetter() {
        return BindingBuilder.bind(deadLetter())
                .to(topicExchange())
                .with(deadLetterRoutingKey);
    }

    @Bean
    public Binding bindingParkingLot() {
        return BindingBuilder.bind(parkingLot())
        		.to(topicExchange())
        		.with(parkingLotRoutingKey);
    }    
    */

}
