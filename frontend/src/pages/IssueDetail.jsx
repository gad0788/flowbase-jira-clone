import { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { get, post, put, del } from '../api';
import { useToast } from '../ToastContext';
import { useConfirm } from '../ConfirmModal';
import { SkeletonDetail } from '../Skeleton';

const PRIORITY_ICONS = { HIGHEST: '▲', HIGH: '↑', MEDIUM: '◆', LOW: '↓', LOWEST: '▼' };

export default function IssueDetail() {
  const { id, issueId } = useParams();
  const navigate = useNavigate();
  const [issue, setIssue] = useState(null);
  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [commentBody, setCommentBody] = useState('');
  const [editing, setEditing] = useState(false);
  const [activeTab, setActiveTab] = useState('details');
  const [users, setUsers] = useState([]);
  const [sprints, setSprints] = useState([]);
  const [epics, setEpics] = useState([]);
  const [childIssues, setChildIssues] = useState([]);
  const addToast = useToast();
  const { confirm, modal: confirmModal } = useConfirm();

  useEffect(() => {
    Promise.all([
      get(`/issues/${issueId}`), get(`/issues/${issueId}/comments`),
      get('/users'), get(`/sprints/project/${id}`),
    ]).then(([iss, coms, usrs, sps]) => {
      setIssue(iss); setComments(coms); setUsers(usrs); setSprints(sps);
      get(`/issues/project/${iss.project.id}`).then(all => {
        setEpics(all.filter(i => i.issueType === 'EPIC' && i.id !== parseInt(issueId)));
        setChildIssues(all.filter(i => i.parent && i.parent.id === parseInt(issueId)));
      });
    }).finally(() => setLoading(false));
  }, [issueId, id]);

  const [editForm, setEditForm] = useState({});
  useEffect(() => {
    if (issue) setEditForm({
      summary: issue.summary, description: issue.description || '',
      priority: issue.priority, issueType: issue.issueType,
      assigneeId: issue.assignee ? issue.assignee.id : '',
      sprintId: issue.sprint ? issue.sprint.id : '',
      storyPoints: issue.storyPoints ?? '',
      dueDate: issue.dueDate || '',
    });
  }, [issue]);

  const handleEditChange = (e) => setEditForm({ ...editForm, [e.target.name]: e.target.value });

  const saveEdit = async () => {
    if (!issue) return;
    try {
      const body = {
        summary: editForm.summary, description: editForm.description,
        issueType: editForm.issueType, priority: editForm.priority,
        reporterId: issue.reporter.id, projectId: issue.project.id,
        assigneeId: editForm.assigneeId ? Number(editForm.assigneeId) : null,
        sprintId: editForm.sprintId ? Number(editForm.sprintId) : null,
        storyPoints: editForm.storyPoints ? Number(editForm.storyPoints) : null,
        dueDate: editForm.dueDate || null,
      };
      const updated = await put(`/issues/${issueId}`, body);
      setIssue(updated);
      setEditing(false);
      addToast('Issue updated successfully');
    } catch (err) { addToast(err.message, 'error'); }
  };

  const linkEpic = async (parentId) => {
    if (!issue) return;
    try {
      const body = {
        summary: issue.summary, description: issue.description || '',
        issueType: issue.issueType, priority: issue.priority,
        reporterId: issue.reporter.id, projectId: issue.project.id,
        assigneeId: issue.assignee ? issue.assignee.id : null,
        sprintId: issue.sprint ? issue.sprint.id : null,
        parentId: parentId ? Number(parentId) : null,
      };
      const updated = await put(`/issues/${issueId}`, body);
      setIssue(updated);
      addToast(parentId ? 'Linked to epic' : 'Unlinked from epic');
    } catch (err) { addToast(err.message, 'error'); }
  };

  const transition = async (status) => {
    try {
      const updated = await post(`/issues/${issueId}/transitions`, { status, userId: 1 });
      setIssue(updated);
      addToast(`Moved to ${status.replace('_', ' ')}`);
    } catch (err) { addToast(err.message, 'error'); }
  };

  const addComment = async (e) => {
    e.preventDefault();
    try {
      const c = await post(`/issues/${issueId}/comments`, { body: commentBody, authorId: 1 });
      setComments([...comments, c]);
      setCommentBody('');
      addToast('Comment added');
    } catch (err) { addToast(err.message, 'error'); }
  };

  const deleteIssue = () => {
    confirm('Delete this issue? This action cannot be undone.', async () => {
      try {
        await del(`/issues/${issueId}`);
        navigate(`/projects/${id}`);
      } catch (err) { addToast(err.message, 'error'); }
    });
  };

  if (loading) return <SkeletonDetail />;
  if (!issue) return <div className="empty-state"><div className="empty-icon">⚠️</div><h3>Issue not found</h3></div>;

  const TRANSITIONS = {
    BACKLOG: ['TO_DO'],
    TO_DO: ['IN_PROGRESS'],
    IN_PROGRESS: ['IN_REVIEW', 'DONE'],
    IN_REVIEW: ['IN_PROGRESS', 'DONE'],
    DONE: ['IN_PROGRESS'],
    CANCELLED: ['BACKLOG'],
  };

  return (
    <div>
      {confirmModal}

      <div className="detail-breadcrumb mb-8">
        <Link to="/">Dashboard</Link> / <Link to={`/projects/${id}`}>{issue.project.name}</Link> / <span>{issue.key}</span>
      </div>

      <div className="detail-header">
        <div>
          <h1><span className="key">{issue.key}</span> {editing ? (
            <input value={editForm.summary} name="summary" onChange={handleEditChange}
              style={{ fontSize: 22, fontWeight: 600, border: '2px solid #4c9aff', borderRadius: 4, padding: '4px 10px', width: '70%', fontFamily: 'inherit' }} />
          ) : issue.summary}</h1>
          <div className="flex gap-8 items-center" style={{ marginTop: 4 }}>
            <span className={`badge badge-${issue.issueType.toLowerCase()}`}>{issue.issueType}</span>
            <span className={`priority-icon priority-${issue.priority}`} style={{ fontSize: 12 }}>
              {PRIORITY_ICONS[issue.priority]} {issue.priority}
            </span>
            <span className={`status-lozenge status-${issue.status}`}>{issue.status.replace('_', ' ')}</span>
            {issue.resolution && <span style={{ fontSize: 12, color: '#5e6c84' }}>· {issue.resolution}</span>}
          </div>
          {issue.parent && (
            <div style={{ fontSize: 13, color: '#5e6c84', marginTop: 8 }}>
              Parent: <Link to={`/projects/${id}/issues/${issue.parent.id}`}>{issue.parent.key} - {issue.parent.summary}</Link>
            </div>
          )}
        </div>
        <div className="flex gap-8">
          {TRANSITIONS[issue.status]?.map(s => (
            <button key={s} className={`btn btn-sm ${s === 'DONE' ? 'btn-success' : ''}`} onClick={() => transition(s)}>
              {s === 'IN_PROGRESS' ? '▶ Start' : s === 'DONE' ? '✓ Done' : s === 'IN_REVIEW' ? '👁 Review' : s.replace('_', ' ')}
            </button>
          ))}
          {editing ? (
            <>
              <button className="btn btn-sm btn-success" onClick={saveEdit}>Save</button>
              <button className="btn btn-sm btn-secondary" onClick={() => setEditing(false)}>Cancel</button>
            </>
          ) : (
            <button className="btn btn-sm btn-secondary" onClick={() => setEditing(true)}>✏ Edit</button>
          )}
          <button className="btn btn-sm btn-danger" onClick={deleteIssue}>🗑 Delete</button>
        </div>
      </div>

      <div className="detail-layout">
        <div className="detail-main">
          <div className="tabs">
            <span className={`tab ${activeTab === 'details' ? 'active' : ''}`} onClick={() => setActiveTab('details')}>Details</span>
            <span className={`tab ${activeTab === 'children' ? 'active' : ''}`} onClick={() => setActiveTab('children')}>
              Children {childIssues.length > 0 && `(${childIssues.length})`}
            </span>
            <span className={`tab ${activeTab === 'comments' ? 'active' : ''}`} onClick={() => setActiveTab('comments')}>
              Comments ({comments.length})
            </span>
            <span className={`tab ${activeTab === 'activity' ? 'active' : ''}`} onClick={() => setActiveTab('activity')}>Activity</span>
          </div>

          {activeTab === 'details' && (
            <>
              <div className="card">
                <h3>Description</h3>
                {editing ? (
                  <textarea name="description" value={editForm.description} onChange={handleEditChange}
                    style={{ width: '100%', minHeight: 150, padding: 10, border: '2px solid #dfe1e6', borderRadius: 4, fontFamily: 'inherit', fontSize: 14 }} />
                ) : (
                  <p style={{ whiteSpace: 'pre-wrap', fontSize: 14, lineHeight: 1.6, color: issue.description ? undefined : '#8993a4' }}>
                    {issue.description || 'No description provided.'}
                  </p>
                )}
              </div>

              <div className="card">
                <h3>Link to Epic</h3>
                {issue.issueType !== 'EPIC' ? (
                  <div className="flex gap-8 items-center">
                    <select className="status-select" style={{ flex: 1, padding: '6px 8px', border: '2px solid #dfe1e6', borderRadius: 4, fontSize: 13 }}
                      value={issue.parent ? issue.parent.id : ''}
                      onChange={(e) => linkEpic(e.target.value)}
                    >
                      <option value="">None (not linked to an epic)</option>
                      {epics.map(e => (
                        <option key={e.id} value={e.id}>
                          {e.key} - {e.summary}
                        </option>
                      ))}
                    </select>
                  </div>
                ) : (
                  <p style={{ fontSize: 13, color: '#5e6c84' }}>This is an epic. Other issues can link to it as children.</p>
                )}
              </div>
            </>
          )}

          {activeTab === 'children' && (
            <div className="card">
              <h3>Child Issues ({childIssues.length})</h3>
              {childIssues.length === 0 && (
                <div className="empty-state" style={{ padding: 24 }}>
                  <div className="empty-icon">🔗</div>
                  <h3>No linked issues</h3>
                  <p>Link stories, tasks, or bugs to this epic.</p>
                  <Link to={`/projects/${id}/issues/new`} className="btn btn-sm">Create child issue</Link>
                </div>
              )}
              {childIssues.map(child => (
                <div key={child.id} className="child-item">
                  <span className={`badge badge-${child.issueType.toLowerCase()}`}>{child.issueType}</span>
                  <Link to={`/projects/${id}/issues/${child.id}`} style={{ fontWeight: 600, fontSize: 13, minWidth: 80 }}>{child.key}</Link>
                  <span style={{ flex: 1, fontSize: 14 }}>{child.summary}</span>
                  <span className={`status-lozenge status-${child.status}`} style={{ fontSize: 11 }}>{child.status.replace('_', ' ')}</span>
                  {child.assignee && (
                    <span className="avatar avatar-sm" title={child.assignee.displayName}>
                      {child.assignee.displayName.charAt(0)}
                    </span>
                  )}
                  {child.storyPoints != null && (
                    <span style={{ fontSize: 11, color: '#5e6c84' }}>{child.storyPoints}pt</span>
                  )}
                </div>
              ))}
            </div>
          )}

          {activeTab === 'comments' && (
            <div className="card">
              <h3>Comments ({comments.length})</h3>
              {comments.map(c => (
                <div key={c.id} className="comment">
                  <div className="comment-header">
                    <span className={`avatar avatar-sm ${
                      ['avatar-blue','avatar-green','avatar-red','avatar-orange','avatar-purple'][c.author.id % 5]
                    }`}>{c.author.displayName.charAt(0)}</span>
                    <span className="author">{c.author.displayName}</span>
                    <span className="time">{new Date(c.createdAt).toLocaleString()}</span>
                  </div>
                  <div className="body">{c.body}</div>
                </div>
              ))}
              <form onSubmit={addComment} style={{ marginTop: 16 }}>
                <textarea value={commentBody} onChange={e => setCommentBody(e.target.value)}
                  placeholder="Add a comment..." required
                  style={{ width: '100%', padding: 10, border: '2px solid #dfe1e6', borderRadius: 4, minHeight: 80, fontFamily: 'inherit', fontSize: 14 }} />
                <button type="submit" className="btn btn-sm mt-8">Add Comment</button>
              </form>
            </div>
          )}

          {activeTab === 'activity' && (
            <div className="card">
              <h3>Activity</h3>
              <div className="empty-state" style={{ padding: 24 }}>
                <div className="empty-icon">📜</div>
                <h3>Activity log coming soon</h3>
                <p>Workflow transitions and field changes will appear here.</p>
              </div>
            </div>
          )}
        </div>

        <div className="detail-sidebar">
          <div className="card" style={{ padding: 16 }}>
            <h3 style={{ fontSize: 13, textTransform: 'uppercase', letterSpacing: '0.3px', color: '#5e6c84' }}>Details</h3>
            <div className="detail-meta">
              <div className="detail-meta-row">
                <span className="label">Status</span>
                <span className="value"><span className={`status-lozenge status-${issue.status}`}>{issue.status.replace('_', ' ')}</span></span>
              </div>
              <div className="detail-meta-row">
                <span className="label">Priority</span>
                <span className="value">
                  {editing ? (
                    <select name="priority" value={editForm.priority} onChange={handleEditChange} className="status-select" style={{ padding: '2px 6px', border: '2px solid #4c9aff', borderRadius: 4, fontSize: 13 }}>
                      <option value="HIGHEST">▲ Highest</option>
                      <option value="HIGH">↑ High</option>
                      <option value="MEDIUM">◆ Medium</option>
                      <option value="LOW">↓ Low</option>
                      <option value="LOWEST">▼ Lowest</option>
                    </select>
                  ) : (
                    <><span className={`priority-icon priority-${issue.priority}`}>{PRIORITY_ICONS[issue.priority]}</span> {issue.priority}</>
                  )}
                </span>
              </div>
              <div className="detail-meta-row">
                <span className="label">Type</span>
                <span className="value"><span className={`badge badge-${issue.issueType.toLowerCase()}`}>{issue.issueType}</span></span>
              </div>
              <div className="detail-meta-row">
                <span className="label">Assignee</span>
                <span className="value">
                  {editing ? (
                    <select name="assigneeId" value={editForm.assigneeId} onChange={handleEditChange} className="status-select" style={{ padding: '2px 6px', border: '2px solid #4c9aff', borderRadius: 4, fontSize: 13, width: '100%' }}>
                      <option value="">Unassigned</option>
                      {users.map(u => <option key={u.id} value={u.id}>{u.displayName}</option>)}
                    </select>
                  ) : issue.assignee ? (
                    <div className="flex items-center gap-8">
                      <span className="avatar avatar-sm">{issue.assignee.displayName.charAt(0)}</span>
                      {issue.assignee.displayName}
                    </div>
                  ) : 'Unassigned'}
                </span>
              </div>
              <div className="detail-meta-row">
                <span className="label">Reporter</span>
                <span className="value">
                  {issue.reporter ? (
                    <div className="flex items-center gap-8">
                      <span className="avatar avatar-sm">{issue.reporter.displayName.charAt(0)}</span>
                      {issue.reporter.displayName}
                    </div>
                  ) : '-'}
                </span>
              </div>
              <div className="detail-meta-row">
                <span className="label">Sprint</span>
                <span className="value">
                  {editing ? (
                    <select name="sprintId" value={editForm.sprintId} onChange={handleEditChange} className="status-select" style={{ padding: '2px 6px', border: '2px solid #4c9aff', borderRadius: 4, fontSize: 13, width: '100%' }}>
                      <option value="">None</option>
                      {sprints.map(s => <option key={s.id} value={s.id}>{s.name}</option>)}
                    </select>
                  ) : issue.sprint ? issue.sprint.name : 'None'}
                </span>
              </div>
              <div className="detail-meta-row">
                <span className="label">Story Points</span>
                <span className="value">
                  {editing ? (
                    <input name="storyPoints" type="number" value={editForm.storyPoints} onChange={handleEditChange}
                      style={{ width: 80, padding: '2px 6px', border: '2px solid #4c9aff', borderRadius: 4, fontSize: 13 }} />
                  ) : issue.storyPoints != null ? `${issue.storyPoints} pts` : '-'}
                </span>
              </div>
              <div className="detail-meta-row">
                <span className="label">Due Date</span>
                <span className="value">
                  {editing ? (
                    <input name="dueDate" type="date" value={editForm.dueDate} onChange={handleEditChange}
                      style={{ padding: '2px 6px', border: '2px solid #4c9aff', borderRadius: 4, fontSize: 13 }} />
                  ) : issue.dueDate || '-'}
                </span>
              </div>
              {issue.resolution && (
                <div className="detail-meta-row">
                  <span className="label">Resolution</span>
                  <span className="value">{issue.resolution}</span>
                </div>
              )}
              {issue.labels && issue.labels.length > 0 && (
                <div className="detail-meta-row">
                  <span className="label">Labels</span>
                  <span className="value flex gap-4" style={{ flexWrap: 'wrap' }}>
                    {issue.labels.map(l => (
                      <span key={l} style={{ background: '#eaecf0', padding: '2px 8px', borderRadius: 3, fontSize: 11, fontWeight: 500 }}>{l}</span>
                    ))}
                  </span>
                </div>
              )}
            </div>
          </div>

          <div className="card" style={{ padding: 16 }}>
            <h3 style={{ fontSize: 13, textTransform: 'uppercase', letterSpacing: '0.3px', color: '#5e6c84' }}>Dates</h3>
            <div className="detail-meta">
              <div className="detail-meta-row">
                <span className="label">Created</span>
                <span className="value" style={{ fontSize: 13 }}>{new Date(issue.createdAt).toLocaleDateString()}</span>
              </div>
              <div className="detail-meta-row">
                <span className="label">Updated</span>
                <span className="value" style={{ fontSize: 13 }}>{new Date(issue.updatedAt).toLocaleDateString()}</span>
              </div>
              {issue.resolvedAt && (
                <div className="detail-meta-row">
                  <span className="label">Resolved</span>
                  <span className="value" style={{ fontSize: 13 }}>{new Date(issue.resolvedAt).toLocaleDateString()}</span>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
