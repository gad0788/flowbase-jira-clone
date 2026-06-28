import { useState, useEffect, useRef } from 'react';
import { BrowserRouter, Routes, Route, Link, useLocation, useNavigate, Navigate } from 'react-router-dom';
import ErrorBoundary from './ErrorBoundary';
import { ToastProvider } from './ToastContext';
import Dashboard from './pages/Dashboard';
import Board from './pages/Board';
import IssueDetail from './pages/IssueDetail';
import CreateIssue from './pages/CreateIssue';
import Sprints from './pages/Sprints';
import Login from './pages/Login';
import Register from './pages/Register';
import { get } from './api';
import './App.css';

function JiraIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="currentColor" width="20" height="20">
      <path d="M11.53 2.59L5.39 9.46a1.02 1.02 0 000 1.4l8.41 8.97c.36.39 1 .39 1.37 0l6.14-6.87a1.02 1.02 0 000-1.4L12.9 2.59a.93.93 0 00-1.37 0z"/>
      <path d="M6.66 11.29L2.4 7.75a.84.84 0 010-1.21l4.35-3.85" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"/>
    </svg>
  );
}

function NavGroup({ title, icon, defaultOpen = true, children }) {
  const [open, setOpen] = useState(defaultOpen);
  return (
    <div className="nav-group">
      <div className="nav-group-header" onClick={() => setOpen(!open)}>
        <span className={`chevron ${open ? 'open' : ''}`}>▶</span>
        {icon && <span className="icon">{icon}</span>}
        <span>{title}</span>
      </div>
      {open && <div className="nav-group-content">{children}</div>}
    </div>
  );
}

function ProfileDropdown({ user, onSignOut }) {
  const [open, setOpen] = useState(false);
  const ref = useRef(null);

  useEffect(() => {
    const handler = (e) => { if (ref.current && !ref.current.contains(e.target)) setOpen(false); };
    document.addEventListener('mousedown', handler);
    return () => document.removeEventListener('mousedown', handler);
  }, []);

  return (
    <div className="profile-trigger" ref={ref} onClick={() => setOpen(!open)}>
      <span className="profile-avatar">{user.displayName.charAt(0)}</span>
      <span className="profile-name">{user.displayName}</span>
      {open && (
        <div className="dropdown-menu" onClick={e => e.stopPropagation()}>
          <div className="dropdown-header">
            <div className="name">{user.displayName}</div>
            <div className="email">{user.email}</div>
          </div>
          <div className="dropdown-body">
            <button className="dropdown-item" onClick={() => setOpen(false)}>
              <span>👤</span> Profile
            </button>
            <button className="dropdown-item" onClick={() => setOpen(false)}>
              <span>⚙️</span> Settings
            </button>
            <div className="dropdown-divider" />
            <button className="dropdown-item danger" onClick={onSignOut}>
              <span>🚪</span> Sign out
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

function Sidebar({ open }) {
  const location = useLocation();
  const [projects, setProjects] = useState([]);
  const currentProjectId = location.pathname.match(/\/projects\/(\d+)/)?.[1];

  useEffect(() => { get('/projects').then(setProjects).catch(() => {}); }, []);

  const projectColors = ['#0052cc', '#00875a', '#de350b', '#ff8b00', '#6942b5', '#00b8d9'];

  return (
    <aside className={`sidebar ${!open ? 'closed' : ''}`}>
      <div className="sidebar-scroll">
        <NavGroup title="Dashboard" icon="📊" defaultOpen={true}>
          <Link to="/" className={`sidebar-link ${location.pathname === '/' ? 'active' : ''}`}>
            <span className="icon" style={{ marginLeft: 0 }}>📋</span> All Projects
          </Link>
        </NavGroup>

        {currentProjectId && (() => {
          const project = projects.find(p => String(p.id) === currentProjectId);
          return (
            <>
              <NavGroup title="Planning" icon="📅" defaultOpen={true}>
                <Link
                  to={`/projects/${currentProjectId}/sprints`}
                  className={`sidebar-link ${location.pathname.includes('/sprints') ? 'active' : ''}`}
                >
                  <span className="icon" style={{ marginLeft: 0 }}>🏃</span> Sprints
                </Link>
              </NavGroup>
              <NavGroup title="Development" icon="⚡" defaultOpen={true}>
                <Link
                  to={`/projects/${currentProjectId}`}
                  className={`sidebar-link ${location.pathname.match(/\/projects\/\d+$/) ? 'active' : ''}`}
                >
                  <span className="icon" style={{ marginLeft: 0 }}>📋</span> Board
                </Link>
                <Link
                  to={`/projects/${currentProjectId}/issues/new`}
                  className={`sidebar-link ${location.pathname.includes('/issues/new') ? 'active' : ''}`}
                >
                  <span className="icon" style={{ marginLeft: 0 }}>➕</span> Create Issue
                </Link>
              </NavGroup>
            </>
          );
        })()}

        <NavGroup title="Projects" icon="📁" defaultOpen={true}>
          {projects.map((p, i) => (
            <Link
              key={p.id}
              to={`/projects/${p.id}`}
              className={`sidebar-link ${String(p.id) === currentProjectId ? 'active' : ''}`}
            >
              <span className="project-avatar" style={{ background: projectColors[i % projectColors.length] }}>
                {p.key.charAt(0)}
              </span>
              <span className="truncate">{p.name}</span>
            </Link>
          ))}
          {projects.length === 0 && (
            <span style={{ fontSize: 12, color: '#8993a4', padding: '0 12px 0 32px' }}>No projects yet</span>
          )}
        </NavGroup>
      </div>
    </aside>
  );
}

export default function App() {
  const [search, setSearch] = useState('');
  const [searchResults, setSearchResults] = useState(null);
  const [showResults, setShowResults] = useState(false);
  const [searching, setSearching] = useState(false);
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const searchRef = useRef(null);
  const debounceRef = useRef(null);
  const location = useLocation();
  const navigate = useNavigate();
  const showSidebar = !location.pathname.match(/^\/(login|register)/);
  const [user, setUser] = useState(() => {
    const u = localStorage.getItem('user');
    return u ? JSON.parse(u) : null;
  });
  const [token, setToken] = useState(() => localStorage.getItem('token'));
  const [dark, setDark] = useState(() => {
    const saved = localStorage.getItem('theme');
    if (saved) return saved === 'dark';
    return window.matchMedia('(prefers-color-scheme: dark)').matches;
  });

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', dark ? 'dark' : 'light');
    localStorage.setItem('theme', dark ? 'dark' : 'light');
  }, [dark]);

  const handleAuth = () => {
    const t = localStorage.getItem('token');
    setToken(t);
    const u = localStorage.getItem('user');
    setUser(u ? JSON.parse(u) : null);
  };

  useEffect(() => {
    const handler = (e) => {
      if (!searchRef.current?.contains(e.target)) setShowResults(false);
    };
    document.addEventListener('mousedown', handler);
    return () => document.removeEventListener('mousedown', handler);
  }, []);

  const doSearch = (query) => {
    if (!query.trim()) { setSearchResults(null); setShowResults(false); return; }

    const keyMatch = query.toUpperCase().match(/^([A-Z][A-Z0-9]+)-(\d+)$/);
    if (keyMatch) {
      get(`/issues/key/${query.toUpperCase()}`)
        .then(issue => {
          setSearchResults([{ type: 'exact', issue }]);
          setShowResults(true);
        })
        .catch(() => {
          setSearchResults(null);
          setShowResults(false);
        });
      return;
    }

    setSearching(true);
    get('/projects').then(projects => {
      const queries = projects.map(p =>
        get(`/issues/project/${p.id}`).then(issues => {
          const q = query.toLowerCase();
          return issues.filter(i =>
            i.key.toLowerCase().includes(q) ||
            i.summary.toLowerCase().includes(q)
          );
        }).catch(() => [])
      );
      Promise.all(queries).then(results => {
        const flat = results.flat();
        if (flat.length > 0) {
          setSearchResults(flat.slice(0, 8));
          setShowResults(true);
        } else {
          setSearchResults(null);
          setShowResults(false);
        }
        setSearching(false);
      });
    }).catch(() => setSearching(false));
  };

  const handleSearchChange = (e) => {
    const val = e.target.value;
    setSearch(val);
    if (debounceRef.current) clearTimeout(debounceRef.current);
    debounceRef.current = setTimeout(() => doSearch(val), 250);
  };

  const handleSearchKeyDown = (e) => {
    if (e.key === 'Enter' && search.trim()) {
      const keyMatch = search.toUpperCase().match(/^([A-Z][A-Z0-9]+)-(\d+)$/);
      if (keyMatch) {
        get(`/issues/key/${search.toUpperCase()}`)
          .then(issue => {
            navigate(`/projects/${issue.projectId}/issues/${issue.id}`);
            setSearch('');
            setShowResults(false);
          })
          .catch(() => {});
      }
    }
    if (e.key === 'Escape') {
      setShowResults(false);
      searchRef.current?.querySelector('input')?.blur();
    }
  };

  const goToIssue = (issue) => {
    navigate(`/projects/${issue.projectId}/issues/${issue.id}`);
    setSearch('');
    setShowResults(false);
  };

  const signOut = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setToken(null);
    setUser(null);
    navigate('/login');
  };

  if (!token) {
    return (
      <Routes>
        <Route path="/login" element={<Login onAuth={handleAuth} />} />
        <Route path="/register" element={<Register onAuth={handleAuth} />} />
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    );
  }

  return (
    <div className="app">
      <header className="header-bar">
        <button className="hamburger" onClick={() => setSidebarOpen(!sidebarOpen)} title={sidebarOpen ? 'Collapse sidebar' : 'Expand sidebar'}>
          {sidebarOpen ? '✕' : '☰'}
        </button>
        <div className="logo">
          <JiraIcon />
          <Link to="/">Flowbase Jira</Link>
        </div>
        <div className="search-box" ref={searchRef} style={{ position: 'relative' }}>
          <span style={{ opacity: 0.5, marginRight: 6 }}>
            {searching ? '⏳' : '🔍'}
          </span>
          <input
            value={search}
            onChange={handleSearchChange}
            onKeyDown={handleSearchKeyDown}
            onFocus={() => searchResults && setShowResults(true)}
            placeholder="Search issues by key or summary..."
          />
          {showResults && searchResults && (
            <div style={{
              position: 'absolute', top: '100%', left: 0, right: 0,
              background: 'white', borderRadius: 4, boxShadow: '0 4px 12px rgba(0,0,0,0.2)',
              marginTop: 4, maxHeight: 360, overflowY: 'auto', zIndex: 200,
            }}>
              {searchResults.length === 1 && searchResults[0].type === 'exact' ? (
                <div
                  onClick={() => goToIssue(searchResults[0].issue)}
                  style={{ padding: '10px 14px', cursor: 'pointer', borderBottom: '1px solid #f4f5f7' }}
                  onMouseEnter={e => e.target.style.background = '#f4f5f7'}
                  onMouseLeave={e => e.target.style.background = 'white'}
                >
                  <div style={{ fontWeight: 600, fontSize: 13, color: '#0052cc' }}>
                    {searchResults[0].issue.key}
                    <span className={`badge badge-${searchResults[0].issue.issueType.toLowerCase()}`} style={{ marginLeft: 8, fontSize: 10 }}>
                      {searchResults[0].issue.issueType}
                    </span>
                    <span className={`status-lozenge status-${searchResults[0].issue.status}`} style={{ marginLeft: 4, fontSize: 10 }}>
                      {searchResults[0].issue.status.replace('_', ' ')}
                    </span>
                  </div>
                  <div style={{ fontSize: 13, color: '#172b4d', marginTop: 2 }}>{searchResults[0].issue.summary}</div>
                  <div style={{ fontSize: 11, color: '#8993a4', marginTop: 2 }}>{searchResults[0].issue.projectId}</div>
                </div>
              ) : (
                searchResults.map(issue => (
                  <div key={issue.id}
                    onClick={() => goToIssue(issue)}
                    style={{ padding: '8px 14px', cursor: 'pointer', borderBottom: '1px solid #f4f5f7', display: 'flex', alignItems: 'center', gap: 8 }}
                    onMouseEnter={e => e.target.style.background = '#f4f5f7'}
                    onMouseLeave={e => e.target.style.background = 'white'}
                  >
                    <span style={{ fontWeight: 600, fontSize: 12, color: '#0052cc', minWidth: 80 }}>{issue.key}</span>
                    <span className={`badge badge-${issue.issueType.toLowerCase()}`} style={{ fontSize: 10, flexShrink: 0 }}>{issue.issueType}</span>
                    <span style={{ fontSize: 13, flex: 1, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{issue.summary}</span>
                    <span className={`status-lozenge status-${issue.status}`} style={{ fontSize: 10 }}>{issue.status.replace('_', ' ')}</span>
                  </div>
                ))
              )}
              <div style={{ padding: '6px 14px', fontSize: 11, color: '#8993a4', textAlign: 'center', background: '#fafbfc' }}>
                Press Enter to jump to issue by key
              </div>
            </div>
          )}
        </div>
        <div className="header-right">
          <button className="theme-toggle" onClick={() => setDark(!dark)} title={dark ? 'Light mode' : 'Dark mode'}>
            {dark ? '☀️' : '🌙'}
          </button>
          {user && <ProfileDropdown user={user} onSignOut={signOut} />}
        </div>
      </header>

      {showSidebar && (
        <div className="app-body">
          <Sidebar open={sidebarOpen} />
          <main className="main-content">
            <ErrorBoundary>
              <ToastProvider>
                <Routes>
                  <Route path="/" element={<Dashboard search={search} />} />
                  <Route path="/projects/:id" element={<Board />} />
                  <Route path="/projects/:id/issues/new" element={<CreateIssue />} />
                  <Route path="/projects/:id/issues/:issueId" element={<IssueDetail />} />
                  <Route path="/projects/:id/sprints" element={<Sprints />} />
                  <Route path="/login" element={<Login onAuth={handleAuth} />} />
                  <Route path="/register" element={<Register onAuth={handleAuth} />} />
                </Routes>
              </ToastProvider>
            </ErrorBoundary>
          </main>
        </div>
      )}
    </div>
  );
}

export function WrappedApp() {
  return (
    <BrowserRouter>
      <App />
    </BrowserRouter>
  );
}
