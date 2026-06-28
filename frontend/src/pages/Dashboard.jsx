import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { get, post } from '../api';
import { useToast } from '../ToastContext';

const COLORS = ['#0052cc', '#00875a', '#de350b', '#ff8b00', '#6942b5', '#00b8d9', '#344563', '#e34900'];
const STATUS_ORDER = ['BACKLOG', 'TO_DO', 'IN_PROGRESS', 'IN_REVIEW', 'DONE'];
const STATUS_LABELS = { BACKLOG: 'Backlog', TO_DO: 'To Do', IN_PROGRESS: 'In Progress', IN_REVIEW: 'In Review', DONE: 'Done' };
const STATUS_COLORS = { BACKLOG: '#97a0af', TO_DO: '#4c9aff', IN_PROGRESS: '#0052cc', IN_REVIEW: '#ff8b00', DONE: '#36b37e' };

export default function Dashboard({ search }) {
  const [projects, setProjects] = useState([]);
  const [projectStats, setProjectStats] = useState({});
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showCreate, setShowCreate] = useState(false);
  const [newKey, setNewKey] = useState('');
  const [newName, setNewName] = useState('');
  const addToast = useToast();

  useEffect(() => {
    Promise.all([get('/projects'), get('/users')]).then(async ([projects, users]) => {
      setProjects(projects);
      setUsers(users);
      const stats = {};
      const results = await Promise.allSettled(
        projects.map(p => get(`/issues/project/${p.id}`).then(issues => ({ projectId: p.id, issues })))
      );
      results.forEach(r => {
        if (r.status === 'fulfilled') {
          const { projectId, issues } = r.value;
          stats[projectId] = {};
          for (const i of issues) {
            stats[projectId][i.status] = (stats[projectId][i.status] || 0) + 1;
          }
        }
      });
      setProjectStats(stats);
    }).finally(() => setLoading(false));
  }, []);

  const totalIssues = Object.values(projectStats).reduce((sum, s) => sum + Object.values(s).reduce((a, b) => a + b, 0), 0);
  const totalByStatus = {};
  for (const s of Object.values(projectStats)) {
    for (const [status, count] of Object.entries(s)) {
      totalByStatus[status] = (totalByStatus[status] || 0) + count;
    }
  }
  const maxStatusCount = Math.max(...Object.values(totalByStatus), 1);

  const createProject = async (e) => {
    e.preventDefault();
    try {
      const p = await post('/projects', { key: newKey.toUpperCase(), name: newName });
      setProjects([...projects, p]);
      setNewKey(''); setNewName(''); setShowCreate(false);
    } catch (err) { addToast(err.message, 'error'); }
  };

  const filtered = projects.filter(p =>
    !search || p.name.toLowerCase().includes(search.toLowerCase()) || p.key.toLowerCase().includes(search.toLowerCase())
  );

  if (loading) return (
    <div>
      <div className="flex justify-between items-center mb-16">
        <div>
          <div className="skeleton skeleton-text-lg" style={{ width: 200 }} />
          <div className="skeleton skeleton-text-sm" style={{ width: 120 }} />
        </div>
        <div className="skeleton" style={{ width: 160, height: 36, borderRadius: 4 }} />
      </div>
      <div className="project-grid">
        {[1, 2, 3].map(i => (
          <div key={i} className="card">
            <div className="flex gap-12 items-center">
              <div className="skeleton" style={{ width: 40, height: 40, borderRadius: 6 }} />
              <div style={{ flex: 1 }}>
                <div className="skeleton skeleton-text" style={{ width: '60%' }} />
                <div className="skeleton skeleton-text-sm" style={{ width: '30%' }} />
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );

  return (
    <div>
      <div className="flex justify-between items-center mb-16">
        <div>
          <h1 className="mb-0">Dashboard</h1>
          <span className="text-muted" style={{ fontSize: 13 }}>{projects.length} projects</span>
        </div>
        <button className="btn btn-lg" onClick={() => setShowCreate(!showCreate)}>
          {showCreate ? '− Cancel' : '+ Create Project'}
        </button>
      </div>

      {projects.length > 0 && (
        <div className="sys-dashboard">
          <div className="sys-dashboard-header">
            <span className="sys-dashboard-icon">📊</span>
            <div>
              <h2 className="mb-0">System Dashboard</h2>
              <span className="text-muted" style={{ fontSize: 12 }}>Across all projects</span>
            </div>
          </div>
          <div className="sys-dashboard-cards">
            <div className="sys-stat-card">
              <span className="sys-stat-icon">📁</span>
              <div className="sys-stat-value">{projects.length}</div>
              <div className="sys-stat-label">Projects</div>
            </div>
            <div className="sys-stat-card">
              <span className="sys-stat-icon">📝</span>
              <div className="sys-stat-value">{totalIssues}</div>
              <div className="sys-stat-label">Total Issues</div>
            </div>
            <div className="sys-stat-card">
              <span className="sys-stat-icon">👥</span>
              <div className="sys-stat-value">{users.length}</div>
              <div className="sys-stat-label">Users</div>
            </div>
          </div>
          {totalIssues > 0 && (
            <div className="sys-status-breakdown">
              <div className="sys-status-title">Status Breakdown</div>
              <div className="sys-status-bars">
                {STATUS_ORDER.map(s => {
                  const count = totalByStatus[s] || 0;
                  if (!count && s !== 'BACKLOG') return null;
                  return (
                    <div key={s} className="sys-status-row">
                      <span className="sys-status-name">{STATUS_LABELS[s]}</span>
                      <div className="sys-status-track">
                        <div className="sys-status-fill" style={{ width: `${(count / maxStatusCount) * 100}%`, background: STATUS_COLORS[s] }} />
                      </div>
                      <span className="sys-status-count">{count}</span>
                    </div>
                  );
                })}
              </div>
            </div>
          )}
        </div>
      )}

      {showCreate && (
        <form onSubmit={createProject} className="card flex gap-12 items-center" style={{ marginBottom: 20, padding: 16 }}>
          <div className="form-group mb-0" style={{ flex: '0 0 120px' }}>
            <label>Project Key</label>
            <input value={newKey} onChange={e => setNewKey(e.target.value.toUpperCase().slice(0, 10))} placeholder="e.g. PROJ" required maxLength={10} />
          </div>
          <div className="form-group mb-0" style={{ flex: 1 }}>
            <label>Project Name</label>
            <input value={newName} onChange={e => setNewName(e.target.value)} placeholder="e.g. My Project" required />
          </div>
          <button type="submit" className="btn" style={{ marginTop: 20 }}>Create</button>
        </form>
      )}

      {filtered.length === 0 && (
        <div className="empty-state">
          <div className="empty-icon">📋</div>
          <h3>{projects.length === 0 ? 'No projects yet' : 'No projects match your search'}</h3>
          <p>{projects.length === 0 ? 'Create your first project to get started.' : 'Try a different search term.'}</p>
          {projects.length === 0 && <button className="btn" onClick={() => setShowCreate(true)}>Create Project</button>}
        </div>
      )}

      <h2 className="mb-16" style={{ fontSize: 16, fontWeight: 600 }}>All Projects</h2>

      <div className="project-grid">
        {filtered.map((p, i) => (
          <ProjectCard key={p.id} project={p} stats={projectStats[p.id]} color={COLORS[i % COLORS.length]} />
        ))}
      </div>
    </div>
  );
}

function ProjectCard({ project: p, stats, color }) {
  const total = stats ? Object.values(stats).reduce((a, b) => a + b, 0) : 0;

  return (
    <div className="project-card">
      <div className="project-top">
        <div className="project-avatar-lg" style={{ background: color }}>{p.key.charAt(0)}</div>
        <div style={{ flex: 1, minWidth: 0 }}>
          <div className="project-name truncate">{p.name}</div>
          <div className="project-key">{p.key} {p.category && `· ${p.category}`}</div>
        </div>
      </div>
      {p.description && <div className="project-desc">{p.description}</div>}
      {p.lead && (
        <div className="flex items-center gap-8" style={{ fontSize: 13, color: '#5e6c84', marginBottom: 12 }}>
          <span className="avatar avatar-sm avatar-blue">{p.lead.displayName.charAt(0)}</span>
          Lead: {p.lead.displayName}
        </div>
      )}
      {total > 0 && (
        <div className="project-stats">
          {STATUS_ORDER.map(s => {
            const count = stats[s] || 0;
            if (!count) return null;
            return (
              <div key={s} className="stat-item">
                <span className="stat-bar" style={{ width: `${(count / total) * 100}%`, background: STATUS_COLORS[s] }} />
                <span className="stat-label">{STATUS_LABELS[s]}</span>
                <span className="stat-count">{count}</span>
              </div>
            );
          })}
        </div>
      )}
      <div className="project-actions">
        <Link to={`/projects/${p.id}`} className="btn btn-sm">Board</Link>
        <Link to={`/projects/${p.id}/sprints`} className="btn btn-sm btn-secondary">Sprints</Link>
        <Link to={`/projects/${p.id}/issues/new`} className="btn btn-sm btn-secondary">+ Issue</Link>
      </div>
    </div>
  );
}
