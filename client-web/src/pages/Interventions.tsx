import { useEffect, useState } from 'react';
import { getInterventions, getInterventionSheet } from '../api';

type Intervention = {
  id: number; patientId: number; alertType: string; severity: string;
  coverageOpen: boolean; coverageMessage: string; appointmentId: number; createdAt: string;
};

export default function Interventions() {
  const [items, setItems] = useState<Intervention[]>([]);
  const [sheet, setSheet] = useState<unknown>(null);
  const [error, setError] = useState('');

  const load = () => getInterventions().then(setItems).catch((e) => setError(e.message));
  useEffect(() => { load(); const t = setInterval(load, 5000); return () => clearInterval(t); }, []);

  return (
    <section>
      <h2>Interventions d'urgence</h2>
      <p>Actualisation automatique toutes les 5 secondes.</p>
      {error && <p className="msg">{error}</p>}
      <table>
        <thead><tr><th>Heure</th><th>Patient</th><th>Alerte</th><th>Couverture</th><th>RDV</th><th /></tr></thead>
        <tbody>
          {items.map((i) => (
            <tr key={i.id}>
              <td>{i.createdAt.replace('T', ' ').slice(0, 19)}</td>
              <td>{i.patientId}</td><td>{i.alertType}</td>
              <td>{i.coverageOpen ? '✔' : '✘'} {i.coverageMessage}</td>
              <td>#{i.appointmentId}</td>
              <td><button onClick={() => getInterventionSheet(i.id).then(setSheet)}>Fiche</button></td>
            </tr>
          ))}
        </tbody>
      </table>
      {sheet !== null && <pre>{JSON.stringify(sheet, null, 2)}</pre>}
    </section>
  );
}