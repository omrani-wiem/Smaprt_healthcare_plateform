package com.healthcare.telemetryservice.service;

import com.healthcare.telemetryservice.config.RabbitConfig;
import com.healthcare.telemetryservice.event.AlertRaisedEvent;
import com.healthcare.telemetryservice.grpc.AlertEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlertEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(AlertEventPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    public void publish(AlertEvent a) {
        AlertRaisedEvent event = new AlertRaisedEvent(a.getPatientId(), a.getType(), a.getSeverity(),
                a.getMessage(), a.getHeartRate(), a.getSpo2(), a.getTimestampMs());
        try {
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE,
                    "alert." + a.getSeverity().toLowerCase(), event);
        } catch (Exception e) {
            // une panne de RabbitMQ ne doit jamais interrompre le flux du capteur
            log.error("Publication RabbitMQ impossible : {}", e.getMessage());
        }
    }
}