const API_BASE = '/api/v1';

function authHeaders() {
  const token = localStorage.getItem('token');
  return token ? { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' } : { 'Content-Type': 'application/json' };
}

export async function get(url) {
  const res = await fetch(`${API_BASE}${url}`, { headers: authHeaders() });
  if (res.status === 401) { localStorage.removeItem('token'); localStorage.removeItem('user'); window.location.href = '/login'; }
  if (!res.ok) throw new Error(`GET ${url} failed: ${res.status}`);
  return res.json();
}

export async function post(url, body) {
  const res = await fetch(`${API_BASE}${url}`, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify(body),
  });
  if (res.status === 401) { localStorage.removeItem('token'); localStorage.removeItem('user'); window.location.href = '/login'; }
  if (!res.ok) throw new Error(`POST ${url} failed: ${res.status}`);
  return res.json();
}

export async function put(url, body) {
  const res = await fetch(`${API_BASE}${url}`, {
    method: 'PUT',
    headers: authHeaders(),
    body: JSON.stringify(body),
  });
  if (res.status === 401) { localStorage.removeItem('token'); localStorage.removeItem('user'); window.location.href = '/login'; }
  if (!res.ok) throw new Error(`PUT ${url} failed: ${res.status}`);
  return res.json();
}

export async function del(url) {
  const res = await fetch(`${API_BASE}${url}`, {
    method: 'DELETE',
    headers: authHeaders(),
  });
  if (res.status === 401) { localStorage.removeItem('token'); localStorage.removeItem('user'); window.location.href = '/login'; }
  if (!res.ok) throw new Error(`DELETE ${url} failed: ${res.status}`);
}
