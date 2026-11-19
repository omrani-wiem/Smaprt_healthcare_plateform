import { useState } from 'react';
import keycloak from './keycloak';
import Appointments from './pages/Appointments';
import Dashboard from './pages/Dashboard';
import Interventions from './pages/Interventions';
import Cnam from './pages/Cnam';

export default function App() {
  const staff = keycloak.hasRealmRole('MEDECIN') || keycloak.hasRealmRole('ADMINISTRATEUR');
  const TABS = [
    { id: 'rdv', label: 'Rendez-vous', visible: true, view: <Appointments /> },
    { id: 'dash', label: 'Tableau de bord', visible: staff, view: <Dashboard /> },
    { id: 'interv', label: 'Interventions', visible: staff, view: <Interventions /> },
    { id: 'cnam', label: 'Droits CNAM', visible: staff, view: <Cnam /> },
  ].filter((t) => t.visible);

  const [tab, setTab] = useState(TABS[0].id);
  const roles = ((keycloak.tokenParsed as any)?.realm_access?.roles ?? [])
    .filter((r: string) => ['PATIENT', 'MEDECIN', 'ADMINISTRATEUR'].includes(r));

  return (
    <div className="app">
      <header>
        <strong>Smart Healthcare</strong>
        <nav>
          {TABS.map((t) => (
            <button key={t.id} className={t.id === tab ? 'active' : ''} onClick={() => setTab(t.id)}>
              {t.label}
            </button>
          ))}
        </nav>
        <span>
          {(keycloak.tokenParsed as any)?.preferred_username} ({roles.join(', ')})
          <button onClick={() => keycloak.logout({ redirectUri: window.location.origin })}>Déconnexion</button>
        </span>
      </header>
      <main>{TABS.find((t) => t.id === tab)?.view}</main>
    </div>
  );
}