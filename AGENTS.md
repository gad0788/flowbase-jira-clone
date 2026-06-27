# Flowbase Jira — AI Agent Context

## Project Overview

A Jira clone with Spring Boot 3.3.5 (Java 21) backend and React 19 frontend. PostgreSQL via Docker Compose. Full DevSecOps toolchain configured but disabled for open source.

## Quick Start for AI Agents

```bash
# Full stack (requires Docker Desktop)
docker compose up -d --build
# Opens at http://localhost:3000

# Backend only (for API testing)
./mvnw spring-boot:run  # http://localhost:8080

# Frontend only (requires backend running)
cd frontend && npm install && npm run dev  # http://localhost:3000

# Run tests
./mvnw test

# Smoke test (validates full stack)
bash smoke-test.sh
```

## Architecture

```
Browser → Nginx (:3000) → Spring Boot (:8080) → PostgreSQL (:5432)
         or Vite proxy (dev mode)
```

- All API calls use relative URLs: `/api/v1/...`
- Vite dev proxy forwards `/api` to `localhost:8080`
- Nginx (Docker) proxies `/api/` to `backend:8080`

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 3.3.5, Java 21, Maven |
| Database | PostgreSQL 16 (Docker), H2 (local dev tests) |
| ORM | JPA / Hibernate + Flyway migrations |
| Auth | JWT (jjwt 0.12.6) + BCrypt |
| Frontend | React 19, Vite 8, React Router 7 |
| Infrastructure | Docker Compose, Helm chart |

## Project Structure

```
java-pipeline/
├── pom.xml                          # Maven with DevSecOps plugins
├── docker-compose.yml               # PostgreSQL + backend + frontend
├── Dockerfile                       # Multi-stage Maven → JRE
├── frontend/
│   ├── Dockerfile                   # Vite build → Nginx
│   ├── nginx.conf                   # Reverse proxy /api → backend
│   └── src/
│       ├── App.jsx                  # Main layout, auth state, routing, search
│       ├── api.js                   # fetch wrapper with JWT auth headers
│       └── pages/
│           ├── Dashboard.jsx        # Project grid overview
│           ├── Board.jsx            # Kanban board (drag & drop)
│           ├── CreateIssue.jsx      # Issue creation form
│           ├── IssueDetail.jsx      # Issue view with inline editing, comments
│           ├── Sprints.jsx          # Sprint planning
│           ├── Login.jsx            # Auth sign-in
│           └── Register.jsx         # User registration
├── src/main/java/com/flowbase/jira/
│   ├── config/
│   │   ├── SecurityConfig.java      # Spring Security + JWT filter chain
│   │   ├── GlobalExceptionHandler.java
│   │   └── DataSeeder.java          # Seeds 3 users + 6 labels
│   ├── controller/                  # REST controllers (11 resources)
│   │   ├── AuthController.java      # POST /auth/register, /auth/login
│   │   ├── ProjectController.java
│   │   ├── IssueController.java     # CRUD + transitions
│   │   ├── SprintController.java
│   │   ├── CommentController.java
│   │   ├── UserController.java
│   │   ├── LabelController.java
│   │   ├── ComponentController.java
│   │   ├── AttachmentController.java
│   │   └── PingController.java
│   ├── model/                       # JPA entities
│   │   ├── User.java                # id, displayName, emailAddress, password (BCrypt)
│   │   ├── Project.java
│   │   ├── ProjectSequence.java     # Auto-incrementing issue key counter
│   │   ├── Issue.java               # issueKey, summary, type, status, priority, labels, components
│   │   ├── Sprint.java
│   │   ├── Comment.java
│   │   ├── Component.java
│   │   ├── Label.java
│   │   ├── Attachment.java
│   │   └── WorkflowTransition.java
│   ├── repository/                  # JpaRepository interfaces
│   ├── service/                     # Business logic interfaces
│   ├── service/impl/                # @Transactional implementations
│   ├── security/
│   │   ├── JwtUtil.java             # JWT generation & validation
│   │   └── JwtAuthenticationFilter.java  # OncePerRequestFilter
│   └── dto/                         # Request/Response DTOs
└── src/main/resources/
    ├── application.yml              # Configs: local (H2), development, production
    ├── application-compose.yml      # PostgreSQL connection for Docker
    └── db/migration/
        ├── V1__jira_schema.sql      # All tables, FK, indexes
        └── V2__add_password_to_users.sql

## Seed Data

| Email | Password | Role |
|-------|----------|------|
| admin@flowbase.com | password | Admin |
| dev@flowbase.com | password | Developer |
| pm@flowbase.com | password | Product Manager |

## API Endpoints

All under `/api/v1/`.

| Method | Path | Auth |
|--------|------|------|
| POST | /auth/register | No |
| POST | /auth/login | No |
| GET | /actuator/health | No |
| GET/POST | /projects | Yes |
| GET/PUT/DELETE | /projects/{id} | Yes |
| GET/POST | /issues | Yes |
| GET/PUT/DELETE | /issues/{id} | Yes |
| POST | /issues/{id}/transitions | Yes |
| GET | /issues/project/{projectId} | Yes |
| GET | /issues/sprint/{sprintId} | Yes |
| GET/POST | /sprints | Yes |
| GET | /sprints/project/{projectId} | Yes |
| GET/POST | /users | Yes |
| GET | /users/{id} | Yes |
| GET/POST | /labels | Yes |
| GET/POST | /components | Yes |
| GET | /components/project/{projectId} | Yes |
| GET/POST/DELETE | /issues/{id}/comments | Yes |
| POST | /attachments | Yes |
| GET | /attachments/issue/{issueId} | Yes |

## Coding Conventions

- **Java:** No Lombok (explicit getters/setters). No comments in code. `@Entity` with scalar Long IDs (no `@ManyToOne`). DTOs use `from(Entity)` static factory methods.
- **Frontend:** React functional components with hooks. Relative API URLs via `./api.js` helpers. CSS custom properties for theming.
- **Tests:** JUnit 5 + Mockito. `@WebMvcTest` controllers with `@AutoConfigureMockMvc(addFilters = false)` to bypass auth. Service tests use `@ExtendWith(MockitoExtension.class)`.

## Authentication Flow

1. User registers or logs in → backend returns JWT
2. Frontend stores token in `localStorage` as `token`
3. All API calls attach `Authorization: Bearer <token>` header
4. `JwtAuthenticationFilter` validates token on every request
5. 401 responses clear localStorage and redirect to `/login`

## Port Mappings

| Container | Internal | Host |
|-----------|----------|------|
| PostgreSQL | 5432 | 5432 |
| Backend | 8080 | 8080 |
| Frontend | 80 | 3000 |

## Common Tasks

### Add a new API endpoint
1. Create DTOs in `dto/request/` and `dto/response/`
2. Add repository query method in `repository/` if needed
3. Add service interface + impl in `service/` and `service/impl/`
4. Add controller method in `controller/`
5. Add Flyway migration in `resources/db/migration/` if schema changes

### Add a new frontend page
1. Create page component in `frontend/src/pages/`
2. Add `<Route>` in `frontend/src/App.jsx`
3. Add nav link if needed

## MCP Server (AI Tool Integration)

An MCP server is in `mcp-server/` that exposes the Jira API as AI-callable tools. It uses stdio transport for integration with Claude Desktop, Cursor, and other MCP-compatible AI tools.

### 17 available tools

- `jira_login` — authenticate with email/password
- `jira_list_projects` / `jira_get_project` / `jira_create_project` — project CRUD
- `jira_list_issues_by_project` / `jira_list_issues_by_sprint` / `jira_get_issue` — issue queries
- `jira_create_issue` / `jira_update_issue` / `jira_delete_issue` / `jira_transition_issue` — issue CRUD + workflow
- `jira_list_sprints` / `jira_create_sprint` — sprint management
- `jira_list_comments` / `jira_add_comment` — comments
- `jira_list_users` / `jira_list_labels` — reference data

### API field conventions for MCP tools

| Field | Enum values |
|-------|-------------|
| `issueType` | `TASK`, `BUG`, `STORY`, `EPIC`, `SUB_TASK` |
| `priority` | `LOWEST`, `LOW`, `MEDIUM`, `HIGH`, `HIGHEST` |
| `status` (transitions) | `BACKLOG`, `TO_DO`, `IN_PROGRESS`, `IN_REVIEW`, `DONE`, `CANCELLED` |

### Required fields for create/update

- **Create issue**: `projectId`, `summary`, `issueType`, `reporterId` (defaults to 1 = admin)
- **Add comment**: `issueId`, `body`, `authorId` (defaults to 1 = admin)
- **Transition issue**: `id`, `status`, `userId` (defaults to 1 = admin)

## State of the Project

- All 31 unit tests pass
- Docker Compose stack verified: PostgreSQL + backend + frontend
- Auth: registration, login, JWT-protected endpoints
- Sensitive/internal data removed for open source
- CI/CD pipeline disabled (`.github/workflows/` deleted)
- MCP server built and tested: create issues, add comments, transition status all verified
- Ready for `git clone && docker compose up -d --build`
