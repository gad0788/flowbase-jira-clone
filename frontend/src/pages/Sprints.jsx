import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { get, post } from '../api';

export default function Sprints() {
  const { id } = useParams();
  const [sprints, setSprints] = useState([]);
  const [project, setProject] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showCreate, setShowCreate] = useState(false);
  const [newName, setNewName] = useState('');
  const [toast, setToast] = useState(null);

  useEffect(() => {
    Promise.all([get(`/projects/${id}`), get(`/sprints/project/${id}`)])
      .then(([proj, sps]) => { setProject(proj); setSprints(sps); })
      .finally(() => setLoading(false));
  }, [id]);

  const showToast = (msg, type = 'success') => {
    setToast({ msg, type });
    setTimeout(() => setToast(null), 3000);
  };

  const startSprint = async (sprintId) => {
    try {
      const updated = await post(`/sprints/${sprintId}/start`, {});
      setSprints(sprints.map(s => s.id === sprintId ? updated : s));
      showToast('Sprint started');
    } catch (err) { showToast(err.message, 'error'); }
  };

  const completeSprint = async (sprintId) => {
    try {
      const updated = await post(`/sprints/${sprintId}/complete`, {});
      setSprints(sprints.map(s => s.id === sprintId ? updated : s));
      showToast('Sprint completed');
    } catch (err) { showToast(err.message, 'error'); }
  };

  const createSprint = async (e) => {
    e.preventDefault();
    try {
      const sprint = await post('/sprints', { name: newName, projectId: Number(id) });
      setSprints([sprint, ...sprints]);
      setNewName(''); setShowCreate(false);
      showToast('Sprint created');
    } catch (err) { showToast(err.message, 'error'); }
  };

  if (loading) return <div className="empty-state"><div className="empty-icon">⏳</div><h3>Loading sprints...</h3></div>;
  if (!project) return <div className="empty-state"><div className="empty-icon">⚠️</div><h3>Project not found</h3></div>;

  return (
    <div>
      {toast && <div className={`toast toast-${toast.type}`}>{toast.msg}</div>}

      <div className="detail-breadcrumb mb-8">
        <Link to="/">Dashboard</Link> / <Link to={`/projects/${id}`}>{project.name}</Link> / <span>Sprints</span>
      </div>

      <div className="flex justify-between items-center mb-16">
        <div>
          <h1 className="mb-0">Sprints</h1>
          <span className="text-muted" style={{ fontSize: 13 }}>{project.key} · {sprints.length} sprints</span>
        </div>
        <button className="btn btn-lg" onClick={() => setShowCreate(!showCreate)}>
          {showCreate ? '− Cancel' : '+ Create Sprint'}
        </button>
      </div>

      {showCreate && (
        <form onSubmit={createSprint} className="card flex gap-12 items-center" style={{ marginBottom: 20, padding: 16 }}>
          <div className="form-group mb-0" style={{ flex: 1 }}>
            <label>Sprint Name</label>
            <input value={newName} onChange={e => setNewName(e.target.value)} placeholder="e.g. Sprint 2" required />
          </div>
          <button type="submit" className="btn" style={{ marginTop: 20 }}>Create</button>
        </form>
      )}

      {sprints.length === 0 && (
        <div className="empty-state">
          <div className="empty-icon">🏃</div>
          <h3>No sprints yet</h3>
          <p>Create your first sprint to start organizing work.</p>
          <button className="btn" onClick={() => setShowCreate(true)}>Create Sprint</button>
        </div>
      )}

      <div className="sprint-list">
        {sprints.map(sprint => (
          <div key={sprint.id} className="sprint-item">
            <div>
              <div className="sprint-name">
                {sprint.name}
                {sprint.active && (
                  <span style={{ display: 'inline-block', marginLeft: 8, padding: '2px 8px', background: '#e3fcef', color: '#006644', borderRadius: 8, fontSize: 11, fontWeight: 600 }}>
                    ACTIVE
                  </span>
                )}
              </div>
              {sprint.goal && <div style={{ fontSize: 13, color: '#5e6c84', marginTop: 2 }}>{sprint.goal}</div>}
              <div className="sprint-dates">
                {sprint.startDate ? `${sprint.startDate} → ${sprint.endDate || '?'}` : 'Dates not set'}
              </div>
            </div>
            <div className="flex gap-8 items-center">
              {!sprint.active && (
                <button className="btn btn-success btn-sm" onClick={() => startSprint(sprint.id)}>▶ Start sprint</button>
              )}
              {sprint.active && (
                <button className="btn btn-warning btn-sm" onClick={() => completeSprint(sprint.id)}>✓ Complete sprint</button>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
