import { useState } from 'react';
import { verifyCnam } from '../api';

export default function Cnam() {
  const [patientId, setPatientId] = useState(1);
  const [numero, setNumero] = useState('12345678');
  const [acte, setActe] = useState('CS');
  const [result, setResult] = useState<string>('');

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const r = await verifyCnam(patientId, numero, acte);
      setResult(r.droitsOuverts ? `Droits ouverts, remboursement ${r.taux} %` : `Refusé : ${r.motif}`);
    } catch (err) { setResult((err as Error).message); }
  };

  return (
    <section>
      <h2>Vérification des droits CNAM</h2>
      <form onSubmit={submit}>
        <label>Patient (id) <input type="number" value={patientId} onChange={(e) => setPatientId(+e.target.value)} /></label>
        <label>N° CNAM <input value={numero} onChange={(e) => setNumero(e.target.value)} /></label>
        <label>Acte
          <select value={acte} onChange={(e) => setActe(e.target.value)}>
            {['C', 'CS', 'K', 'B'].map((a) => <option key={a}>{a}</option>)}
          </select>
        </label>
        <button>Vérifier</button>
      </form>
      {result && <p className="msg">{result}</p>}
    </section>
  );
}