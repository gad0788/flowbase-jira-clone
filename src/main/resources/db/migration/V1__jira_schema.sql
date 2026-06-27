CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    display_name VARCHAR(255) NOT NULL,
    email_address VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(512),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    project_key VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    lead_id BIGINT REFERENCES users(id),
    category VARCHAR(100),
    archived BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE project_sequences (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL UNIQUE,
    current_value BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE sprints (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    goal TEXT,
    start_date DATE,
    end_date DATE,
    active BOOLEAN NOT NULL DEFAULT FALSE,
    project_id BIGINT NOT NULL REFERENCES projects(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE issues (
    id BIGSERIAL PRIMARY KEY,
    issue_key VARCHAR(20) NOT NULL,
    summary VARCHAR(255) NOT NULL,
    description TEXT,
    issue_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'BACKLOG',
    priority VARCHAR(10) NOT NULL DEFAULT 'MEDIUM',
    resolution VARCHAR(20),
    assignee_id BIGINT REFERENCES users(id),
    reporter_id BIGINT REFERENCES users(id),
    project_id BIGINT NOT NULL REFERENCES projects(id),
    parent_id BIGINT REFERENCES issues(id),
    sprint_id BIGINT REFERENCES sprints(id),
    story_points INT,
    due_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    resolved_at TIMESTAMP,
    UNIQUE (project_id, issue_key)
);

CREATE TABLE issue_labels (
    issue_id BIGINT NOT NULL REFERENCES issues(id),
    label VARCHAR(100) NOT NULL,
    PRIMARY KEY (issue_id, label)
);

CREATE TABLE issue_components (
    issue_id BIGINT NOT NULL REFERENCES issues(id),
    component_id BIGINT NOT NULL,
    PRIMARY KEY (issue_id, component_id)
);

CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    body TEXT NOT NULL,
    author_id BIGINT NOT NULL REFERENCES users(id),
    issue_id BIGINT NOT NULL REFERENCES issues(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE components (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    project_id BIGINT NOT NULL REFERENCES projects(id),
    lead_id BIGINT REFERENCES users(id)
);

CREATE TABLE labels (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    color VARCHAR(7) NOT NULL
);

CREATE TABLE attachments (
    id BIGSERIAL PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    file_url VARCHAR(512) NOT NULL,
    issue_id BIGINT NOT NULL REFERENCES issues(id),
    uploaded_by BIGINT NOT NULL REFERENCES users(id),
    uploaded_at TIMESTAMP NOT NULL DEFAULT NOW(),
    file_size BIGINT
);

CREATE TABLE workflow_transitions (
    id BIGSERIAL PRIMARY KEY,
    issue_id BIGINT NOT NULL REFERENCES issues(id),
    from_status VARCHAR(20),
    to_status VARCHAR(20) NOT NULL,
    by_user_id BIGINT NOT NULL REFERENCES users(id),
    timestamp TIMESTAMP NOT NULL DEFAULT NOW()
);
