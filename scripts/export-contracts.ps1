# Exporte les contrats d'API dans docs/api (plateforme démarrée : docker compose --profile apps up -d)
$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$out  = Join-Path $root "docs/api"
New-Item -ItemType Directory -Force $out | Out-Null

$r = Invoke-RestMethod -Method Post -Uri http://localhost:8080/realms/healthcare/protocol/openid-connect/token `
  -Body @{grant_type="password"; client_id="healthcare-web"; username="medecin1"; password="medecin1"}
$h = @{Authorization = "Bearer $($r.access_token)"}

# REST : OpenAPI généré par springdoc
Invoke-WebRequest http://localhost:8000/rest/v3/api-docs.yaml -Headers $h -OutFile "$out/openapi.yaml"

# SOAP : WSDL généré par Spring-WS, avec l'adresse publique (la Gateway) dans <soap:address>
Invoke-WebRequest http://localhost:8000/soap/cnam.wsdl -Headers $h -OutFile "$out/cnam.wsdl"
(Get-Content "$out/cnam.wsdl" -Raw) -replace 'location="[^"]*"', 'location="http://localhost:8000/soap"' |
  Set-Content "$out/cnam.wsdl" -Encoding utf8

# Sources de vérité : copiées telles quelles
Copy-Item "$root/services/soap-service/src/main/resources/xsd/cnam.xsd"                    "$out/cnam.xsd" -Force
Copy-Item "$root/services/graphql-service/src/main/resources/graphql/schema.graphqls"       "$out/schema.graphqls" -Force
Copy-Item "$root/services/telemetry-service/src/main/proto/telemetry.proto"                 "$out/telemetry.proto" -Force

Get-ChildItem $out | Select-Object Name, Length