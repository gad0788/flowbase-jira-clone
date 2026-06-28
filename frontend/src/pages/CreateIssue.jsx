import { useState, useEffect } from 'react';
import { useNavigate, useParams, Link } from 'react-router-dom';
import { get, post } from '../api';
import { useToast } from '../ToastContext';

export default function CreateIssue() {
  const { id } = useParams();
  const navigate = useNavigate();
  const addToast = useToast();
  const [users, setUsers] = useState([]);
  const [sprints, setSprints] = useState([]);
  const [epics, setEpics] = useState([]);
  const [form, setForm] = useState({
    summary: '', description: '', issueType: 'TASK', priority: 'MEDIUM',
    assigneeId: '', sprintId: '', parentId: '', storyPoints: '', dueDate: '',
  });

  useEffect(() => {
    get('/users').then(setUsers);
    get(`/sprints/project/${id}`).then(setSprints);
    get(`/issues/project/${id}`).then(all => setEpics(all.filter(i => i.issueType === 'EPIC')));
  }, [id]);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const body = {
        summary: form.summary, description: form.description,
        issueType: form.issueType, priority: form.priority,
        reporterId: 1, projectId: Number(id),
      };
      if (form.assigneeId) body.assigneeId = Number(form.assigneeId);
      if (form.sprintId) body.sprintId = Number(form.sprintId);
      if (form.parentId) body.parentId = Number(form.parentId);
      if (form.storyPoints) body.storyPoints = Number(form.storyPoints);
      if (form.dueDate) body.dueDate = form.dueDate;

      const issue = await post('/issues', body);
      addToast(`Created ${issue.key}`);
      setTimeout(() => navigate(`/projects/${id}/issues/${issue.id}`), 500);
    } catch (err) {
      addToast(err.message, 'error');
    }
  };

  return (
    <div>
      <div className="detail-breadcrumb mb-16">
        <Link to="/">Dashboard</Link> / <Link to={`/projects/${id}`}>Board</Link> / <span>Create Issue</span>
      </div>

      <h1>Create Issue</h1>

      <form onSubmit={handleSubmit} className="card" style={{ maxWidth: 680, padding: 24 }}>
        <div className="form-group">
          <label>Summary <span className="text-danger" style={{ color: '#de350b' }}>*</span></label>
          <input name="summary" value={form.summary} onChange={handleChange}
            placeholder="e.g. Implement user authentication" required autoFocus />
        </div>

        <div className="form-group">
          <label>Description</label>
          <textarea name="description" value={form.description} onChange={handleChange}
            placeholder="Describe the issue in detail..." />
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 16 }}>
          <div className="form-group">
            <label>Issue Type</label>
            <select name="issueType" value={form.issueType} onChange={handleChange}>
              <option value="TASK">Task</option>
              <option value="STORY">Story</option>
              <option value="BUG">Bug</option>
              <option value="EPIC">Epic</option>
              <option value="SUB_TASK">Sub-task</option>
            </select>
          </div>
          <div className="form-group">
            <label>Priority</label>
            <select name="priority" value={form.priority} onChange={handleChange}>
              <option value="HIGHEST">▲ Highest</option>
              <option value="HIGH">↑ High</option>
              <option value="MEDIUM">◆ Medium</option>
              <option value="LOW">↓ Low</option>
              <option value="LOWEST">▼ Lowest</option>
            </select>
          </div>
          <div className="form-group">
            <label>Assignee</label>
            <select name="assigneeId" value={form.assigneeId} onChange={handleChange}>
              <option value="">Unassigned</option>
              {users.map(u => <option key={u.id} value={u.id}>{u.displayName}</option>)}
            </select>
          </div>
          <div className="form-group">
            <label>Sprint</label>
            <select name="sprintId" value={form.sprintId} onChange={handleChange}>
              <option value="">None</option>
              {sprints.map(s => <option key={s.id} value={s.id}>{s.name}</option>)}
            </select>
          </div>
          <div className="form-group">
            <label>Parent Epic</label>
            <select name="parentId" value={form.parentId} onChange={handleChange}>
              <option value="">None</option>
              {epics.map(e => <option key={e.id} value={e.id}>{e.key} - {e.summary}</option>)}
            </select>
          </div>
          <div className="form-group">
            <label>Story Points</label>
            <input name="storyPoints" type="number" value={form.storyPoints} onChange={handleChange} placeholder="e.g. 3" />
          </div>
          <div className="form-group">
            <label>Due Date</label>
            <input name="dueDate" type="date" value={form.dueDate} onChange={handleChange} />
          </div>
        </div>

        <div className="flex gap-8 mt-16">
          <button type="submit" className="btn btn-lg">Create Issue</button>
          <button type="button" className="btn btn-secondary btn-lg" onClick={() => navigate(`/projects/${id}`)}>Cancel</button>
        </div>
      </form>
    </div>
  );
}
