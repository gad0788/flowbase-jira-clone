import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { get, post } from '../api';

const COLORS = ['#0052cc', '#00875a', '#de350b', '#ff8b00', '#6942b5', '#00b8d9', '#344563', '#e34900'];

export default function Dashboard({ search }) {
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showCreate, setShowCreate] = useState(false);
  const [newKey, setNewKey] = useState('');
  const [newName, setNewName] = useState('');

  useEffect(() => {
    get('/projects').then(setProjects).finally(() => setLoading(false));
  }, []);

  const createProject = async (e) => {
    e.preventDefault();
    try {
      const p = await post('/projects', { key: newKey.toUpperCase(), name: newName });
      setProjects([...projects, p]);
      setNewKey(''); setNewName(''); setShowCreate(false);
    } catch (err) { alert('Error: ' + err.message); }
  };

  const filtered = projects.filter(p =>
    !search || p.name.toLowerCase().includes(search.toLowerCase()) || p.key.toLowerCase().includes(search.toLowerCase())
  );

  if (loading) return <div className="empty-state"><div className="empty-icon">⏳</div><h3>Loading projects...</h3></div>;

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

      <div className="project-grid">
        {filtered.map((p, i) => (
          <ProjectCard key={p.id} project={p} color={COLORS[i % COLORS.length]} />
        ))}
      </div>
    </div>
  );
}

function ProjectCard({ project: p, color }) {
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
      <div className="project-actions">
        <Link to={`/projects/${p.id}`} className="btn btn-sm">Board</Link>
        <Link to={`/projects/${p.id}/sprints`} className="btn btn-sm btn-secondary">Sprints</Link>
        <Link to={`/projects/${p.id}/issues/new`} className="btn btn-sm btn-secondary">+ Issue</Link>
      </div>
    </div>
  );
}
