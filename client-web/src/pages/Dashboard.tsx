import { useState } from 'react';
import { graphql } from '../api';

type Row = { id: string; firstName: string; lastName: string; service: string; pathologie: string; risque: string };

const LIST = `query($service: String, $risque: RiskLevel) {
  patients(service: $service, risque: $risque) { id firstName lastName service pathologie risque }
}`;
const DETAIL = `query($id: ID!) {
  patient(id: $id) {
    firstName lastName age pathologie risque
    vitals(last: 3) { heartRate spo2 temperature recordedAt }
    alerts(limit: 3) { type severity message }
    treatments { medication dosage }
  }
}`;

export default function Dashboard() {
  const [rows, setRows] = useState<Row[]>([]);
  const [service, setService] = useState('');
  const [risque, setRisque] = useState('');
  const [detail, setDetail] = useState<unknown>(null);
  const [error, setError] = useState('');

  const search = () =>
    graphql(LIST, { service: service || null, risque: risque || null })
      .then((d) => { setRows(d.patients); setError(''); })
      .catch((e) => setError(e.message));

  const open = (id: string) =>
    graphql(DETAIL, { id }).then((d) => setDetail(d.patient)).catch((e) => setError(e.message));

  return (
    <section>
      <h2>Tableau de bord médical</h2>
      <div className="filters">
        <input placeholder="Service (ex : Cardiologie)" value={service} onChange={(e) => setService(e.target.value)} />
        <select value={risque} onChange={(e) => setRisque(e.target.value)}>
          <option value="">Tous les risques</option>
          {['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'].map((r) => <option key={r}>{r}</option>)}
        </select>
        <button onClick={search}>Filtrer</button>
      </div>
      {error && <p className="msg">{error}</p>}
      <table>
        <thead><tr><th>Patient</th><th>Service</th><th>Pathologie</th><th>Risque</th><th /></tr></thead>
        <tbody>
          {rows.map((r) => (
            <tr key={r.id}>
              <td>{r.firstName} {r.lastName}</td><td>{r.service}</td><td>{r.pathologie}</td>
              <td className={`risk-${r.risque}`}>{r.risque}</td>
              <td><button onClick={() => open(r.id)}>Fiche</button></td>
            </tr>
          ))}
        </tbody>
      </table>
      {detail !== null && <pre>{JSON.stringify(detail, null, 2)}</pre>}
    </section>
  );
}