# Flowbase Jira MCP Server

Exposes the Flowbase Jira API as 17 AI-callable tools via the [Model Context Protocol](https://modelcontextprotocol.io).

## Quick Start

```bash
cd mcp-server
npm install
npm start
```

Connects to `http://localhost:8080` as `admin@flowbase.com` by default.

## Configuration

| Env Var | Default | Description |
|---------|---------|-------------|
| `JIRA_URL` | `http://localhost:8080` | Backend API base URL |
| `JIRA_EMAIL` | `admin@flowbase.com` | Authenticating user email |
| `JIRA_PASSWORD` | `password` | Authenticating user password |

## Integration

### Claude Desktop

Add to `claude_desktop_config.json`:

```json
{
  "mcpServers": {
    "flowbase-jira": {
      "command": "node",
      "args": ["/absolute/path/to/mcp-server/index.js"],
      "env": {
        "JIRA_URL": "http://localhost:8080",
        "JIRA_EMAIL": "admin@flowbase.com",
        "JIRA_PASSWORD": "password"
      }
    }
  }
}
```

## Tools

| Tool | Description |
|------|-------------|
| `jira_login` | Authenticate with email/password |
| `jira_list_projects` | List all projects |
| `jira_get_project` | Get project by ID |
| `jira_create_project` | Create a project |
| `jira_list_issues_by_project` | List issues in a project |
| `jira_list_issues_by_sprint` | List issues in a sprint |
| `jira_get_issue` | Get issue by ID |
| `jira_create_issue` | Create an issue |
| `jira_update_issue` | Update an issue |
| `jira_delete_issue` | Delete an issue |
| `jira_transition_issue` | Change issue status |
| `jira_list_sprints` | List sprints for a project |
| `jira_create_sprint` | Create a sprint |
| `jira_list_comments` | List comments on an issue |
| `jira_add_comment` | Add a comment to an issue |
| `jira_list_users` | List all users |
| `jira_list_labels` | List all labels |

## Enum Values

| Field | Values |
|-------|--------|
| `issueType` | `TASK`, `BUG`, `STORY`, `EPIC`, `SUB_TASK` |
| `priority` | `LOWEST`, `LOW`, `MEDIUM`, `HIGH`, `HIGHEST` |
| `status` | `BACKLOG`, `TO_DO`, `IN_PROGRESS`, `IN_REVIEW`, `DONE`, `CANCELLED` |
