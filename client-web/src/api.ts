import keycloak from './keycloak';

const BASE = import.meta.env.VITE_API_URL ?? 'http://localhost:8000';
const JSON_HEADERS = { 'Content-Type': 'application/json' };
const SOAP_NS = 'http://healthcare.com/soap/cnam';

async function authFetch(path: string, init: RequestInit = {}): Promise<Response> {
  await keycloak.updateToken(30); // renouvelle le token s'il expire dans moins de 30 s
  const res = await fetch(BASE + path, {
    ...init,
    headers: { ...(init.headers ?? {}), Authorization: `Bearer ${keycloak.token}` },
  });
  if (res.status === 403) throw new Error("Accès refusé : votre rôle ne permet pas cette action.");
  if (res.status === 409) throw new Error('Ce créneau est déjà réservé.');
  if (!res.ok) throw new Error(`Erreur ${res.status}`);
  return res;
}

// REST
export const getDoctors = async () => (await authFetch('/rest/api/doctors')).json();

export const getAppointments = async (patientId: number) =>
  (await authFetch(`/rest/api/appointments?patientId=${patientId}`)).json();

export const bookAppointment = async (body: {
  patientId: number; doctorId: number; dateTime: string; reason: string;
}) =>
  (await authFetch('/rest/api/appointments', {
    method: 'POST', headers: JSON_HEADERS, body: JSON.stringify(body),
  })).json();

// GraphQL
export async function graphql(query: string, variables: Record<string, unknown> = {}) {
  const res = await authFetch('/graphql', {
    method: 'POST', headers: JSON_HEADERS, body: JSON.stringify({ query, variables }),
  });
  const body = await res.json();
  if (body.errors) throw new Error(body.errors[0].message);
  return body.data;
}

// Orchestrateur
export const getInterventions = async () =>
  (await authFetch('/orchestrator/api/interventions')).json();

export const getInterventionSheet = async (id: number) =>
  (await authFetch(`/orchestrator/api/interventions/${id}/sheet`)).json();

// SOAP : enveloppe XML construite à la main, réponse lue avec DOMParser
export async function verifyCnam(patientId: number, numeroCnam: string, codeActe: string) {
  const esc = (s: string) => s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  const envelope = `<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cn="${SOAP_NS}">
    <soapenv:Body>
      <cn:verifierDroitsCNAMRequest>
        <cn:patientId>${patientId}</cn:patientId>
        <cn:numeroCNAM>${esc(numeroCnam)}</cn:numeroCNAM>
        <cn:codeActe>${esc(codeActe)}</cn:codeActe>
      </cn:verifierDroitsCNAMRequest>
    </soapenv:Body>
  </soapenv:Envelope>`;
  const res = await authFetch('/soap', {
    method: 'POST', headers: { 'Content-Type': 'text/xml' }, body: envelope,
  });
  const doc = new DOMParser().parseFromString(await res.text(), 'text/xml');
  const get = (tag: string) => doc.getElementsByTagNameNS(SOAP_NS, tag)[0]?.textContent ?? '';
  return {
    droitsOuverts: get('droitsOuverts') === 'true',
    taux: Number(get('tauxRemboursement')),
    motif: get('motif'),
  };
}