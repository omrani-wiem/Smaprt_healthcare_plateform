package com.healthcare.telemetryservice.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "healthcare.events";

    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(EXCHANGE, true, false);   
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();       //converit objct en java pour les evoyer dans RabbitMQ et inversement
}
}