import { Link } from 'react-router-dom';

export default function NotFound() {
  return (
    <div style={{ textAlign: 'center', padding: '64px 24px' }}>
      <div style={{ fontSize: 72, fontWeight: 700, color: 'var(--accent)', lineHeight: 1, marginBottom: 8 }}>404</div>
      <h1 style={{ marginTop: 0 }}>Page not found</h1>
      <p style={{ color: 'var(--text-secondary)', fontSize: 15, marginBottom: 24 }}>
        The page you're looking for doesn't exist or has been moved.
      </p>
      <Link to="/" className="btn btn-lg">Back to Dashboard</Link>
    </div>
  );
}
