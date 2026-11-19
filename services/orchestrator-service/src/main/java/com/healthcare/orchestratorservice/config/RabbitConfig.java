package com.healthcare.orchestratorservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "healthcare.events";
    public static final String QUEUE = "orchestrator.alerts";
    public static final String DLX = "healthcare.events.dlx";
    public static final String DLQ = "orchestrator.alerts.dlq";

    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(EXCHANGE, true, false);   // identique à la télémétrie
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX);
    }

    @Bean
    public Queue alertsQueue() {
        return QueueBuilder.durable(QUEUE)
                .withArgument("x-dead-letter-exchange", DLX)
                .withArgument("x-dead-letter-routing-key", DLQ)
                .build();
    }

    @Bean
    public Queue alertsDeadLetterQueue() {
        return QueueBuilder.durable(DLQ).build();
    }

    @Bean
    public Binding alertsBinding() {
        return BindingBuilder.bind(alertsQueue()).to(eventsExchange()).with("alert.*");
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(alertsDeadLetterQueue()).to(deadLetterExchange()).with(DLQ);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}