import { useEffect, useState } from 'react';
import { bookAppointment, getAppointments, getDoctors } from '../api';

type Doctor = { id: number; firstName: string; lastName: string; specialty: string };
type Appointment = { id: number; dateTime: string; status: string; reason: string; doctor: Doctor };

export default function Appointments() {
  const [doctors, setDoctors] = useState<Doctor[]>([]);
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [patientId, setPatientId] = useState(1);
  const [doctorId, setDoctorId] = useState<number>(0);
  const [dateTime, setDateTime] = useState('');
  const [reason, setReason] = useState('');
  const [message, setMessage] = useState('');

  const refresh = () =>
    getAppointments(patientId).then(setAppointments).catch((e) => setMessage(e.message));

  useEffect(() => {
    getDoctors().then((d: Doctor[]) => { setDoctors(d); if (d[0]) setDoctorId(d[0].id); })
      .catch((e) => setMessage(e.message));
  }, []);
  useEffect(() => { refresh(); }, [patientId]);

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await bookAppointment({ patientId, doctorId, dateTime, reason });
      setMessage('Rendez-vous créé.');
      refresh();
    } catch (err) { setMessage((err as Error).message); }
  };

  return (
    <section>
      <h2>Rendez-vous</h2>
      <form onSubmit={submit}>
        <label>Patient (id) <input type="number" value={patientId} onChange={(e) => setPatientId(+e.target.value)} /></label>
        <label>Médecin
          <select value={doctorId} onChange={(e) => setDoctorId(+e.target.value)}>
            {doctors.map((d) => <option key={d.id} value={d.id}>Dr {d.lastName} ({d.specialty})</option>)}
          </select>
        </label>
        <label>Date <input type="datetime-local" required value={dateTime} onChange={(e) => setDateTime(e.target.value)} /></label>
        <label>Motif <input value={reason} onChange={(e) => setReason(e.target.value)} /></label>
        <button>Réserver</button>
      </form>
      {message && <p className="msg">{message}</p>}
      <table>
        <thead><tr><th>Date</th><th>Médecin</th><th>Statut</th><th>Motif</th></tr></thead>
        <tbody>
          {appointments.map((a) => (
            <tr key={a.id}>
              <td>{a.dateTime.replace('T', ' ')}</td>
              <td>Dr {a.doctor.lastName}</td>
              <td>{a.status}</td><td>{a.reason}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}