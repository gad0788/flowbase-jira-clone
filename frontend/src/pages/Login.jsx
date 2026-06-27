import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { post } from '../api';

export default function Login({ onAuth }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await post('/auth/login', { email, password });
      localStorage.setItem('token', res.token);
      localStorage.setItem('user', JSON.stringify({ id: res.userId, displayName: res.displayName, email: res.email }));
      if (onAuth) onAuth();
      navigate('/');
    } catch (err) {
      setError('Invalid email or password');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <div className="auth-logo">
          <svg viewBox="0 0 24 24" fill="currentColor" width="32" height="32"><path d="M11.53 2.59L5.39 9.46a1.02 1.02 0 000 1.4l8.41 8.97c.36.39 1 .39 1.37 0l6.14-6.87a1.02 1.02 0 000-1.4L12.9 2.59a.93.93 0 00-1.37 0z"/><path d="M6.66 11.29L2.4 7.75a.84.84 0 010-1.21l4.35-3.85" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"/></svg>
          <h1>Flowbase Jira</h1>
        </div>
        <h2>Sign in</h2>
        {error && <div className="auth-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="auth-field">
            <label>Email</label>
            <input type="email" value={email} onChange={e => setEmail(e.target.value)} placeholder="admin@flowbase.com" required />
          </div>
          <div className="auth-field">
            <label>Password</label>
            <input type="password" value={password} onChange={e => setPassword(e.target.value)} placeholder="password" required />
          </div>
          <button type="submit" className="auth-btn" disabled={loading}>{loading ? 'Signing in...' : 'Sign in'}</button>
        </form>
        <p className="auth-footer">Don't have an account? <Link to="/register">Create one</Link></p>
      </div>
    </div>
  );
}
