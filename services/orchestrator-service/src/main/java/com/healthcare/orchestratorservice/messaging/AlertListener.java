package com.healthcare.orchestratorservice.messaging;

import com.healthcare.orchestratorservice.config.RabbitConfig;
import com.healthcare.orchestratorservice.event.AlertRaisedEvent;
import com.healthcare.orchestratorservice.service.InterventionWorkflow;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlertListener {

    private final InterventionWorkflow workflow;

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void onAlert(AlertRaisedEvent event) {
        workflow.handle(event);
    }
}