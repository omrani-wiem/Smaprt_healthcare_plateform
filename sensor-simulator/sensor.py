import sys, time, random
import grpc
import telemetry_pb2, telemetry_pb2_grpc

patient_id = int(sys.argv[1]) if len(sys.argv) > 1 else 1
crisis = len(sys.argv) > 2 and sys.argv[2] == "crisis"

def readings():
    for i in range(20):
        hr = random.randint(70, 90)
        spo2 = random.randint(95, 99)
        if crisis and i >= 10:
            hr = random.randint(150, 180)
            spo2 = 88
        yield telemetry_pb2.VitalReading(
            patient_id=patient_id, heart_rate=hr, spo2=spo2,
            temperature=36.8, fall_detected=False,
            timestamp_ms=int(time.time() * 1000))
        print(f"envoyé : FC={hr} SpO2={spo2}")
        time.sleep(0.5)

with grpc.insecure_channel("localhost:9090") as channel:
    stub = telemetry_pb2_grpc.TelemetryServiceStub(channel)
    summary = stub.StreamVitals(readings())
    print(f"Résumé : {summary.readings_received} mesures, {summary.alerts_raised} alertes")