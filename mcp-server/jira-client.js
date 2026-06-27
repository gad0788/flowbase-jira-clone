export class JiraClient {
  constructor(baseUrl = 'http://localhost:8080') {
    this.baseUrl = baseUrl;
    this.token = null;
  }

  async login(email, password) {
    const res = await fetch(`${this.baseUrl}/api/v1/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    });
    if (!res.ok) {
      const text = await res.text();
      throw new Error(`Login failed (${res.status}): ${text}`);
    }
    const data = await res.json();
    this.token = data.token;
    return data;
  }

  async ensureAuth(email, password) {
    if (!this.token) {
      await this.login(email, password);
    }
  }

  async get(path) {
    const res = await fetch(`${this.baseUrl}/api/v1${path}`, {
      headers: { Authorization: `Bearer ${this.token}` },
    });
    if (res.status === 401) {
      this.token = null;
      throw new Error('Unauthorized — token expired or invalid');
    }
    if (!res.ok) throw new Error(`GET ${path} failed (${res.status})`);
    if (res.status === 204) return null;
    return res.json();
  }

  async post(path, body) {
    const res = await fetch(`${this.baseUrl}/api/v1${path}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${this.token}`,
      },
      body: JSON.stringify(body),
    });
    if (res.status === 401) {
      this.token = null;
      throw new Error('Unauthorized — token expired or invalid');
    }
    if (!res.ok) throw new Error(`POST ${path} failed (${res.status})`);
    if (res.status === 204) return null;
    return res.json();
  }

  async put(path, body) {
    const res = await fetch(`${this.baseUrl}/api/v1${path}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${this.token}`,
      },
      body: JSON.stringify(body),
    });
    if (res.status === 401) {
      this.token = null;
      throw new Error('Unauthorized — token expired or invalid');
    }
    if (!res.ok) throw new Error(`PUT ${path} failed (${res.status})`);
    if (res.status === 204) return null;
    return res.json();
  }

  async del(path) {
    const res = await fetch(`${this.baseUrl}/api/v1${path}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${this.token}` },
    });
    if (res.status === 401) {
      this.token = null;
      throw new Error('Unauthorized — token expired or invalid');
    }
    if (!res.ok) throw new Error(`DELETE ${path} failed (${res.status})`);
  }

  // ── Project tools ──

  async listProjects() {
    return this.get('/projects');
  }

  async getProject(id) {
    return this.get(`/projects/${id}`);
  }

  async createProject(name, key, description, leadId) {
    return this.post('/projects', { name, key, description, leadId });
  }

  // ── Issue tools ──

  async listIssuesByProject(projectId) {
    return this.get(`/issues/project/${projectId}`);
  }

  async listIssuesBySprint(sprintId) {
    return this.get(`/issues/sprint/${sprintId}`);
  }

  async getIssue(id) {
    return this.get(`/issues/${id}`);
  }

  async createIssue(issue) {
    return this.post('/issues', issue);
  }

  async updateIssue(id, issue) {
    return this.put(`/issues/${id}`, issue);
  }

  async deleteIssue(id) {
    return this.del(`/issues/${id}`);
  }

  async transitionIssue(id, status, userId, resolution) {
    return this.post(`/issues/${id}/transitions`, { status, userId, resolution });
  }

  // ── Sprint tools ──

  async listSprints(projectId) {
    return this.get(`/sprints/project/${projectId}`);
  }

  async createSprint(sprint) {
    return this.post('/sprints', sprint);
  }

  // ── Comment tools ──

  async listComments(issueId) {
    return this.get(`/issues/${issueId}/comments`);
  }

  async addComment(issueId, body, authorId) {
    return this.post(`/issues/${issueId}/comments`, { body, authorId });
  }

  async deleteComment(issueId, commentId) {
    return this.del(`/issues/${issueId}/comments/${commentId}`);
  }

  // ── User tools ──

  async listUsers() {
    return this.get('/users');
  }

  async getUser(id) {
    return this.get(`/users/${id}`);
  }

  // ── Label tools ──

  async listLabels() {
    return this.get('/labels');
  }

  // ── Component tools ──

  async listComponents(projectId) {
    return this.get(`/components/project/${projectId}`);
  }
}
