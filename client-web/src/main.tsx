import { createRoot } from 'react-dom/client';
import keycloak from './keycloak';
import App from './App';
import './index.css';

keycloak
  .init({ onLoad: 'login-required', pkceMethod: 'S256', checkLoginIframe: false })
  .then(() => createRoot(document.getElementById('root')!).render(<App />))
  .catch(() => {
    document.body.textContent = 'Impossible de joindre Keycloak (http://localhost:8080).';
  });