package com.healthcare.telemetryservice.service;

import com.healthcare.telemetryservice.grpc.AlertEvent;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class AlertBroadcaster {

    private static final Logger log = LoggerFactory.getLogger(AlertBroadcaster.class);
    private final List<StreamObserver<AlertEvent>> subscribers = new CopyOnWriteArrayList<>();

    public void add(StreamObserver<AlertEvent> observer) {
        subscribers.add(observer);
        log.info("Nouvel abonné aux alertes ({} au total)", subscribers.size());
    }

    public void remove(StreamObserver<AlertEvent> observer) {
        subscribers.remove(observer);
        log.info("Abonné retiré ({} restants)", subscribers.size());
    }

    public void publish(AlertEvent event) {
        for (StreamObserver<AlertEvent> s : subscribers) {
            try {
                synchronized (s) {
                    s.onNext(event);
                }
            } catch (Exception e) {
                subscribers.remove(s);
            }
        }
    }
}