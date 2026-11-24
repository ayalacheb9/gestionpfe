
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '10s', target: 10 },
    { duration: '30s', target: 20 },
    { duration: '10s', target: 0 },
  ],
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<500'],
  },
};

export default function () {
  const base = __ENV.BASE_URL || 'http://localhost:8080';

  // Connexion pour obtenir une session authentifiée
  const login = http.post(
    `${base}/api/auth/login`,
    JSON.stringify({
      username: 'etudiant',
      password: 'test123'
    }),
    {
      headers: { 'Content-Type': 'application/json' }
    }
  );

  check(login, {
    'connexion réussie': (r) => r.status === 200,
  });

  // Accès à l'API des rapports avec la session
  const res = http.get(`${base}/api/rapports`);

  check(res, {
    'statut 200': (r) => r.status === 200,
  });

  sleep(1);
}
