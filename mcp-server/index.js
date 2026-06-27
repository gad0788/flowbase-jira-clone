import { Server } from '@modelcontextprotocol/sdk/server/index.js';
import { StdioServerTransport } from '@modelcontextprotocol/sdk/server/stdio.js';
import {
  CallToolRequestSchema,
  ListToolsRequestSchema,
} from '@modelcontextprotocol/sdk/types.js';
import { JiraClient } from './jira-client.js';

const JIRA_URL = process.env.JIRA_URL || 'http://localhost:8080';
const JIRA_EMAIL = process.env.JIRA_EMAIL || 'admin@flowbase.com';
const JIRA_PASSWORD = process.env.JIRA_PASSWORD || 'password';

const jira = new JiraClient(JIRA_URL);

const server = new Server(
  { name: 'flowbase-jira-mcp', version: '1.0.0' },
  { capabilities: { tools: {} } },
);

server.setRequestHandler(ListToolsRequestSchema, async () => ({
  tools: [
    {
      name: 'jira_login',
      description: 'Authenticate with the Jira instance.',
      inputSchema: {
        type: 'object',
        properties: {
          email: { type: 'string', description: 'User email (e.g. admin@flowbase.com)' },
          password: { type: 'string', description: 'User password' },
        },
        required: ['email', 'password'],
      },
    },
    {
      name: 'jira_list_projects',
      description: 'List all Jira projects.',
      inputSchema: { type: 'object', properties: {} },
    },
    {
      name: 'jira_get_project',
      description: 'Get details of a specific project by ID.',
      inputSchema: {
        type: 'object',
        properties: { id: { type: 'number', description: 'Project ID' } },
        required: ['id'],
      },
    },
    {
      name: 'jira_create_project',
      description: 'Create a new Jira project.',
      inputSchema: {
        type: 'object',
        properties: {
          name: { type: 'string', description: 'Project name' },
          key: { type: 'string', description: 'Project key (e.g. PROJ)' },
          description: { type: 'string', description: 'Project description' },
          leadId: { type: 'number', description: 'User ID of the project lead' },
        },
        required: ['name', 'key'],
      },
    },
    {
      name: 'jira_list_issues_by_project',
      description: 'List all issues in a project.',
      inputSchema: {
        type: 'object',
        properties: { projectId: { type: 'number', description: 'Project ID' } },
        required: ['projectId'],
      },
    },
    {
      name: 'jira_list_issues_by_sprint',
      description: 'List all issues in a sprint.',
      inputSchema: {
        type: 'object',
        properties: { sprintId: { type: 'number', description: 'Sprint ID' } },
        required: ['sprintId'],
      },
    },
    {
      name: 'jira_get_issue',
      description: 'Get details of a specific issue by ID.',
      inputSchema: {
        type: 'object',
        properties: { id: { type: 'number', description: 'Issue ID' } },
        required: ['id'],
      },
    },
    {
      name: 'jira_create_issue',
      description: 'Create a new issue in a project.',
      inputSchema: {
        type: 'object',
        properties: {
          projectId: { type: 'number', description: 'Project ID' },
          summary: { type: 'string', description: 'Issue summary/title' },
          description: { type: 'string', description: 'Issue description' },
          issueType: { type: 'string', description: 'Issue type: TASK, BUG, STORY, EPIC, SUB_TASK', default: 'TASK' },
          priority: { type: 'string', description: 'Priority: LOWEST, LOW, MEDIUM, HIGH, HIGHEST', default: 'MEDIUM' },
          assigneeId: { type: 'number', description: 'User ID of assignee' },
          reporterId: { type: 'number', description: 'User ID of reporter (defaults to authenticated user)' },
          sprintId: { type: 'number', description: 'Sprint ID' },
          labels: { type: 'array', items: { type: 'string' }, description: 'Label names' },
          storyPoints: { type: 'number', description: 'Story points estimate' },
        },
        required: ['projectId', 'summary'],
      },
    },
    {
      name: 'jira_update_issue',
      description: 'Update an existing issue.',
      inputSchema: {
        type: 'object',
        properties: {
          id: { type: 'number', description: 'Issue ID' },
          summary: { type: 'string', description: 'New summary' },
          description: { type: 'string', description: 'New description' },
          issueType: { type: 'string', description: 'Issue type: TASK, BUG, STORY, EPIC, SUB_TASK' },
          priority: { type: 'string', description: 'Priority: LOWEST, LOW, MEDIUM, HIGH, HIGHEST' },
          assigneeId: { type: 'number', description: 'Assignee user ID' },
          reporterId: { type: 'number', description: 'Reporter user ID' },
          sprintId: { type: 'number', description: 'Sprint ID' },
          labels: { type: 'array', items: { type: 'string' }, description: 'Label names' },
          storyPoints: { type: 'number', description: 'Story points estimate' },
        },
        required: ['id'],
      },
    },
    {
      name: 'jira_delete_issue',
      description: 'Delete an issue by ID.',
      inputSchema: {
        type: 'object',
        properties: { id: { type: 'number', description: 'Issue ID' } },
        required: ['id'],
      },
    },
    {
      name: 'jira_transition_issue',
      description: 'Change the status of an issue.',
      inputSchema: {
        type: 'object',
        properties: {
          id: { type: 'number', description: 'Issue ID' },
          status: { type: 'string', description: 'Target status: BACKLOG, TO_DO, IN_PROGRESS, IN_REVIEW, DONE, CANCELLED' },
          userId: { type: 'number', description: 'User ID performing the transition' },
          resolution: { type: 'string', description: 'Resolution (e.g. FIXED, WONT_FIX, DUPLICATE)' },
        },
        required: ['id', 'status'],
      },
    },
    {
      name: 'jira_list_sprints',
      description: 'List all sprints for a project.',
      inputSchema: {
        type: 'object',
        properties: { projectId: { type: 'number', description: 'Project ID' } },
        required: ['projectId'],
      },
    },
    {
      name: 'jira_create_sprint',
      description: 'Create a new sprint in a project.',
      inputSchema: {
        type: 'object',
        properties: {
          name: { type: 'string', description: 'Sprint name' },
          projectId: { type: 'number', description: 'Project ID' },
          goal: { type: 'string', description: 'Sprint goal' },
          startDate: { type: 'string', description: 'Start date (ISO 8601)' },
          endDate: { type: 'string', description: 'End date (ISO 8601)' },
        },
        required: ['name', 'projectId'],
      },
    },
    {
      name: 'jira_list_comments',
      description: 'List all comments on an issue.',
      inputSchema: {
        type: 'object',
        properties: { issueId: { type: 'number', description: 'Issue ID' } },
        required: ['issueId'],
      },
    },
    {
      name: 'jira_add_comment',
      description: 'Add a comment to an issue.',
      inputSchema: {
        type: 'object',
        properties: {
          issueId: { type: 'number', description: 'Issue ID' },
          body: { type: 'string', description: 'Comment body text' },
          authorId: { type: 'number', description: 'User ID of the comment author (defaults to authenticated user)' },
        },
        required: ['issueId', 'body'],
      },
    },
    {
      name: 'jira_list_users',
      description: 'List all users.',
      inputSchema: { type: 'object', properties: {} },
    },
    {
      name: 'jira_list_labels',
      description: 'List all labels.',
      inputSchema: { type: 'object', properties: {} },
    },
  ],
}));

server.setRequestHandler(CallToolRequestSchema, async (request) => {
  const { name, arguments: args } = request.params;

  try {
    switch (name) {
      case 'jira_login': {
        const data = await jira.login(args.email, args.password);
        return {
          content: [{ type: 'text', text: JSON.stringify(data, null, 2) }],
        };
      }

      case 'jira_list_projects': {
        await jira.ensureAuth(JIRA_EMAIL, JIRA_PASSWORD);
        const projects = await jira.listProjects();
        return {
          content: [{ type: 'text', text: JSON.stringify(projects, null, 2) }],
        };
      }

      case 'jira_get_project': {
        await jira.ensureAuth(JIRA_EMAIL, JIRA_PASSWORD);
        const project = await jira.getProject(args.id);
        return {
          content: [{ type: 'text', text: JSON.stringify(project, null, 2) }],
        };
      }

      case 'jira_create_project': {
        await jira.ensureAuth(JIRA_EMAIL, JIRA_PASSWORD);
        const project = await jira.createProject(args.name, args.key, args.description, args.leadId);
        return {
          content: [{ type: 'text', text: JSON.stringify(project, null, 2) }],
        };
      }

      case 'jira_list_issues_by_project': {
        await jira.ensureAuth(JIRA_EMAIL, JIRA_PASSWORD);
        const issues = await jira.listIssuesByProject(args.projectId);
        return {
          content: [{ type: 'text', text: JSON.stringify(issues, null, 2) }],
        };
      }

      case 'jira_list_issues_by_sprint': {
        await jira.ensureAuth(JIRA_EMAIL, JIRA_PASSWORD);
        const issues = await jira.listIssuesBySprint(args.sprintId);
        return {
          content: [{ type: 'text', text: JSON.stringify(issues, null, 2) }],
        };
      }

      case 'jira_get_issue': {
        await jira.ensureAuth(JIRA_EMAIL, JIRA_PASSWORD);
        const issue = await jira.getIssue(args.id);
        return {
          content: [{ type: 'text', text: JSON.stringify(issue, null, 2) }],
        };
      }

      case 'jira_create_issue': {
        await jira.ensureAuth(JIRA_EMAIL, JIRA_PASSWORD);
        const body = {
          projectId: args.projectId,
          summary: args.summary,
          issueType: args.issueType || 'TASK',
          reporterId: args.reporterId || 1,
        };
        if (args.description) body.description = args.description;
        if (args.priority) body.priority = args.priority;
        if (args.assigneeId) body.assigneeId = args.assigneeId;
        if (args.sprintId) body.sprintId = args.sprintId;
        if (args.labels) body.labels = args.labels;
        if (args.storyPoints) body.storyPoints = args.storyPoints;
        const issue = await jira.createIssue(body);
        return {
          content: [{ type: 'text', text: JSON.stringify(issue, null, 2) }],
        };
      }

      case 'jira_update_issue': {
        await jira.ensureAuth(JIRA_EMAIL, JIRA_PASSWORD);
        const body = {};
        if (args.summary) body.summary = args.summary;
        if (args.description) body.description = args.description;
        if (args.issueType) body.issueType = args.issueType;
        if (args.priority) body.priority = args.priority;
        if (args.assigneeId) body.assigneeId = args.assigneeId;
        if (args.reporterId) body.reporterId = args.reporterId;
        if (args.sprintId) body.sprintId = args.sprintId;
        if (args.labels) body.labels = args.labels;
        if (args.storyPoints) body.storyPoints = args.storyPoints;
        const issue = await jira.updateIssue(args.id, body);
        return {
          content: [{ type: 'text', text: JSON.stringify(issue, null, 2) }],
        };
      }

      case 'jira_delete_issue': {
        await jira.ensureAuth(JIRA_EMAIL, JIRA_PASSWORD);
        await jira.deleteIssue(args.id);
        return {
          content: [{ type: 'text', text: JSON.stringify({ success: true, message: `Issue ${args.id} deleted` }, null, 2) }],
        };
      }

      case 'jira_transition_issue': {
        await jira.ensureAuth(JIRA_EMAIL, JIRA_PASSWORD);
        const result = await jira.transitionIssue(args.id, args.status, args.userId || 1, args.resolution);
        return {
          content: [{ type: 'text', text: JSON.stringify(result || { success: true }, null, 2) }],
        };
      }

      case 'jira_list_sprints': {
        await jira.ensureAuth(JIRA_EMAIL, JIRA_PASSWORD);
        const sprints = await jira.listSprints(args.projectId);
        return {
          content: [{ type: 'text', text: JSON.stringify(sprints, null, 2) }],
        };
      }

      case 'jira_create_sprint': {
        await jira.ensureAuth(JIRA_EMAIL, JIRA_PASSWORD);
        const sprint = await jira.createSprint({
          name: args.name,
          projectId: args.projectId,
          goal: args.goal,
          startDate: args.startDate,
          endDate: args.endDate,
        });
        return {
          content: [{ type: 'text', text: JSON.stringify(sprint, null, 2) }],
        };
      }

      case 'jira_list_comments': {
        await jira.ensureAuth(JIRA_EMAIL, JIRA_PASSWORD);
        const comments = await jira.listComments(args.issueId);
        return {
          content: [{ type: 'text', text: JSON.stringify(comments, null, 2) }],
        };
      }

      case 'jira_add_comment': {
        await jira.ensureAuth(JIRA_EMAIL, JIRA_PASSWORD);
        const comment = await jira.addComment(args.issueId, args.body, args.authorId || 1);
        return {
          content: [{ type: 'text', text: JSON.stringify(comment, null, 2) }],
        };
      }

      case 'jira_list_users': {
        await jira.ensureAuth(JIRA_EMAIL, JIRA_PASSWORD);
        const users = await jira.listUsers();
        return {
          content: [{ type: 'text', text: JSON.stringify(users, null, 2) }],
        };
      }

      case 'jira_list_labels': {
        await jira.ensureAuth(JIRA_EMAIL, JIRA_PASSWORD);
        const labels = await jira.listLabels();
        return {
          content: [{ type: 'text', text: JSON.stringify(labels, null, 2) }],
        };
      }

      default:
        throw new Error(`Unknown tool: ${name}`);
    }
  } catch (err) {
    return {
      content: [{ type: 'text', text: `Error: ${err.message}` }],
      isError: true,
    };
  }
});

async function main() {
  const transport = new StdioServerTransport();
  await server.connect(transport);
  console.error('Flowbase Jira MCP server running on stdio');
  console.error(`  Backend: ${JIRA_URL}`);
  console.error(`  User:    ${JIRA_EMAIL}`);
}

main().catch((err) => {
  console.error('Fatal error:', err);
  process.exit(1);
});
