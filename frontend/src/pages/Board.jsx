import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { get, post } from '../api';
import { useToast } from '../ToastContext';
import { SkeletonBoard } from '../Skeleton';

const COLUMNS = ['BACKLOG', 'TO_DO', 'IN_PROGRESS', 'IN_REVIEW', 'DONE'];
const STATUS_LABELS = { BACKLOG: 'Backlog', TO_DO: 'To Do', IN_PROGRESS: 'In Progress', IN_REVIEW: 'In Review', DONE: 'Done' };

const PRIORITY_ICONS = {
  HIGHEST: '▲', HIGH: '↑', MEDIUM: '◆', LOW: '↓', LOWEST: '▼'
};

function IssueCard({ issue, projectId, onTransition }) {
  const [dragging, setDragging] = useState(false);
  const addToast = useToast();

  const onDragStart = (e) => {
    setDragging(true);
    e.dataTransfer.effectAllowed = 'move';
    e.dataTransfer.setData('text/plain', JSON.stringify({ id: issue.id, status: issue.status }));
  };

  const copyKey = () => {
    navigator.clipboard.writeText(issue.key);
    addToast(`Copied ${issue.key}`);
  };

  return (
    <div
      className={`issue-card ${dragging ? 'dragging' : ''}`}
      draggable
      onDragStart={onDragStart}
      onDragEnd={() => setDragging(false)}
    >
      <div className="card-top">
        <span className="key" onClick={copyKey} style={{ cursor: 'pointer' }}>{issue.key}</span>
        <span className={`priority-icon priority-${issue.priority}`} title={issue.priority}>
          {PRIORITY_ICONS[issue.priority] || '◆'}
        </span>
      </div>
      {issue.parent && (
        <div className="parent-link">
          ↑ <Link to={`/projects/${projectId}/issues/${issue.parent.id}`}>{issue.parent.key}</Link>
        </div>
      )}
      <Link to={`/projects/${projectId}/issues/${issue.id}`} style={{ textDecoration: 'none', color: 'inherit' }}>
        <div className="summary">{issue.summary}</div>
      </Link>
      <div className="card-footer">
        <span className={`badge badge-${issue.issueType.toLowerCase()}`}>{issue.issueType}</span>
        <span className={`status-lozenge status-${issue.status}`}>{STATUS_LABELS[issue.status]}</span>
        {issue.storyPoints != null && (
          <span style={{ fontSize: 11, color: '#5e6c84', background: '#f4f5f7', padding: '1px 6px', borderRadius: 8 }}>{issue.storyPoints}pt</span>
        )}
        <div className="right">
          {issue.assignee && (
            <span className={`avatar avatar-sm ${
              ['avatar-blue','avatar-green','avatar-red','avatar-orange','avatar-purple'][issue.assignee.id % 5]
            }`} title={issue.assignee.displayName}>
              {issue.assignee.displayName.charAt(0)}
            </span>
          )}
        </div>
      </div>
    </div>
  );
}

export default function Board() {
  const { id } = useParams();
  const [issues, setIssues] = useState([]);
  const [project, setProject] = useState(null);
  const [loading, setLoading] = useState(true);
  const [dragOverCol, setDragOverCol] = useState(null);
  const [compact, setCompact] = useState(false);
  const addToast = useToast();

  useEffect(() => {
    Promise.all([get(`/projects/${id}`), get(`/issues/project/${id}`)])
      .then(([proj, iss]) => { setProject(proj); setIssues(iss); })
      .finally(() => setLoading(false));
  }, [id]);

  const transition = async (issueId, status) => {
    try {
      const updated = await post(`/issues/${issueId}/transitions`, { status, userId: 1 });
      setIssues(issues.map(i => i.id === issueId ? updated : i));
    } catch (err) { addToast(err.message, 'error'); }
  };

  const onDragOver = (e, status) => {
    e.preventDefault();
    e.dataTransfer.dropEffect = 'move';
    setDragOverCol(status);
  };

  const onDrop = (e, targetStatus) => {
    e.preventDefault();
    setDragOverCol(null);
    try {
      const data = JSON.parse(e.dataTransfer.getData('text/plain'));
      if (data.status !== targetStatus) transition(data.id, targetStatus);
    } catch {}
  };

  if (loading) return (
    <div>
      <div className="skeleton" style={{ height: 14, width: 200, marginBottom: 16 }} />
      <div className="skeleton skeleton-text-lg" style={{ width: 300, marginBottom: 4 }} />
      <div className="skeleton skeleton-text-sm" style={{ width: 150, marginBottom: 24 }} />
      <SkeletonBoard />
    </div>
  );
  if (!project) return <div className="empty-state"><div className="empty-icon">⚠️</div><h3>Project not found</h3></div>;

  return (
    <div>
      <div className="flex justify-between items-center mb-16">
        <div>
          <div className="detail-breadcrumb mb-8">
            <Link to="/">Dashboard</Link> / <span>{project.name}</span>
          </div>
          <h1 className="mb-0">{project.name} Board</h1>
          <span className="text-muted" style={{ fontSize: 13 }}>{project.key} · {issues.length} issues</span>
        </div>
        <div className="flex gap-8">
          <button className="btn btn-secondary btn-lg" onClick={() => setCompact(c => !c)}>
            {compact ? '⊟ Normal' : '⊞ Compact'}
          </button>
          <Link to={`/projects/${id}/sprints`} className="btn btn-secondary btn-lg">Sprints</Link>
          <Link to={`/projects/${id}/issues/new`} className="btn btn-lg">+ Create Issue</Link>
        </div>
      </div>

      <div className={`board${compact ? ' compact' : ''}`}>
        {issues.length === 0 ? (
          <div className="empty-state" style={{ gridColumn: '1 / -1', padding: 48 }}>
            <div className="empty-icon">📝</div>
            <h3>No issues yet</h3>
            <p>Create the first issue for {project.name} to start tracking work.</p>
            <Link to={`/projects/${id}/issues/new`} className="btn btn-lg">+ Create Issue</Link>
          </div>
        ) : COLUMNS.map(status => {
          const colIssues = issues.filter(i => i.status === status);
          return (
            <div
              key={status}
              className={`column ${dragOverCol === status ? 'drag-over' : ''}`}
              onDragOver={(e) => onDragOver(e, status)}
              onDragLeave={() => setDragOverCol(null)}
              onDrop={(e) => onDrop(e, status)}
            >
              <div className="column-header">
                <span>{STATUS_LABELS[status]}</span>
                <span className="count">{colIssues.length}</span>
              </div>
              {colIssues.map(issue => (
                <IssueCard key={issue.id} issue={issue} projectId={id} onTransition={transition} />
              ))}
              {colIssues.length === 0 && (
                <div style={{ padding: 16, textAlign: 'center', color: '#8993a4', fontSize: 12, border: '2px dashed #dfe1e6', borderRadius: 4 }}>
                  Drop issues here
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}
