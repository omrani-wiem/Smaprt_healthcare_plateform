import grpc
import telemetry_pb2, telemetry_pb2_grpc

with grpc.insecure_channel("localhost:9090") as channel:
    stub = telemetry_pb2_grpc.TelemetryServiceStub(channel)
    print("En attente d'alertes...")
    for alert in stub.SubscribeAlerts(telemetry_pb2.AlertSubscription()):
        print(f"ALERTE [{alert.severity}] patient {alert.patient_id} : {alert.type} - {alert.message}")