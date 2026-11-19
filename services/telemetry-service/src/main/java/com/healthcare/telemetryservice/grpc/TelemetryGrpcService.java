package com.healthcare.telemetryservice.grpc;

import com.healthcare.telemetryservice.model.AlertRecord;
import com.healthcare.telemetryservice.repository.AlertRecordRepository;
import com.healthcare.telemetryservice.service.AlertBroadcaster;
import com.healthcare.telemetryservice.service.ThresholdDetector;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TelemetryGrpcService extends TelemetryServiceGrpc.TelemetryServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(TelemetryGrpcService.class);

    private final ThresholdDetector detector;
    private final AlertBroadcaster broadcaster;
    private final AlertRecordRepository alertRepository;

    @Override
    public StreamObserver<VitalReading> streamVitals(StreamObserver<StreamSummary> responseObserver) {
        return new StreamObserver<>() {
            int readings = 0;
            int alerts = 0;

            @Override
            public void onNext(VitalReading reading) {
                readings++;
                detector.detect(reading).ifPresent(alert -> {
                    alerts++;
                    alertRepository.save(AlertRecord.builder()
                            .patientId(alert.getPatientId())
                            .type(alert.getType())
                            .severity(alert.getSeverity())
                            .message(alert.getMessage())
                            .heartRate(alert.getHeartRate())
                            .spo2(alert.getSpo2())
                            .createdAt(LocalDateTime.now())
                            .build());
                    broadcaster.publish(alert);
                    log.warn("ALERTE {} pour le patient {}", alert.getType(), alert.getPatientId());
                });
            }

            @Override
            public void onError(Throwable t) {
                log.error("Flux capteur interrompu : {}", t.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(StreamSummary.newBuilder()
                        .setReadingsReceived(readings)
                        .setAlertsRaised(alerts)
                        .build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void subscribeAlerts(AlertSubscription request, StreamObserver<AlertEvent> responseObserver) {
        broadcaster.add(responseObserver);
        if (responseObserver instanceof ServerCallStreamObserver<AlertEvent> call) {
            call.setOnCancelHandler(() -> broadcaster.remove(responseObserver));
        }
        // On ne termine pas le flux : il reste ouvert tant que l'abonné est connecté
    }
}