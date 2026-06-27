# Implementation Plan — Java DevSecOps Pipeline

---

## 1. Project Scaffold

### Directory Structure

```
java-pipeline/
├── .github/workflows/ci-cd.yml
├── .mvn/                          # Maven wrapper
├── mvnw / mvnw.cmd
├── pom.xml                        # Spring Boot 3.x + all plugins
├── Dockerfile
├── checkstyle.xml                 # Checkstyle ruleset
├── .owasp-suppression.xml         # False-positive CVE suppressions
├── .gitleaks.toml                 # Gitleaks config
├── .zap/rules.tsv                 # ZAP DAST rule overrides
├── tests/load/smoke.js            # k6 smoke test
├── helm/my-java-app/              # Helm chart for ArgoCD
│   ├── Chart.yaml
│   ├── values.yaml
│   ├── values-staging.yaml
│   ├── values-production.yaml
│   └── templates/
│       ├── deployment.yaml
│       ├── service.yaml
│       ├── ingress.yaml
│       ├── configmap.yaml
│       └── hpa.yaml
└── src/
    ├── main/java/com/tapestry/javaapp/
    │   ├── TapestryJavaApplication.java
    │   ├── config/
    │   │   ├── SecurityConfig.java
    │   │   └── MetricsConfig.java
    │   ├── controller/
    │   │   ├── PingController.java
    │   │   └── TaskController.java        # example CRUD
    │   ├── service/
    │   │   ├── TaskService.java
    │   │   └── impl/TaskServiceImpl.java
    │   ├── repository/
    │   │   └── TaskRepository.java
    │   ├── model/
    │   │   └── Task.java
    │   └── dto/
    │       ├── TaskRequest.java
    │       └── TaskResponse.java
    ├── main/resources/
    │   ├── application.yml
    │   ├── application-production.yml
    │   └── db/migration/                 # Flyway migrations
    │       └── V1__create_task_table.sql
    └── test/java/com/tapestry/javaapp/
        ├── controller/
        │   └── PingControllerTest.java
        ├── service/
        │   └── TaskServiceTest.java
        └── repository/
            └── TaskRepositoryTest.java
```

### Maven POM — Key Dependencies & Plugins

| Purpose | Dependency / Plugin |
|---------|-------------------|
| Framework | spring-boot-starter-web, spring-boot-starter-actuator |
| Database | spring-boot-starter-data-jpa, postgresql, flyway-core |
| Cache | spring-boot-starter-data-redis |
| Validation | spring-boot-starter-validation |
| Testing | spring-boot-starter-test, testcontainers (postgresql, redis) |
| Code style | maven-checkstyle-plugin |
| Coverage | jacoco-maven-plugin (line ≥80%, branch ≥75%) |
| SAST | spotbugs-maven-plugin + find-sec-bugs |
| CVE scan | dependency-check-maven (fail on CVSS ≥9) |
| Quality | sonar-maven-plugin |
| License | license-maven-plugin (fail on unknown) |
| Packaging | spring-boot-maven-plugin (layered JAR) |

---

## 2. Implementation Roadmap

### Phase 1: Foundation (Week 1)
- [ ] Generate project with `mvn archetype:generate` or Spring Initializr
- [ ] Write `pom.xml` with all plugins and dependencies
- [ ] `TapestryJavaApplication.java` — entry point
- [ ] `PingController.java` — `GET /api/v1/ping` returns `{"pong": true}`
- [ ] `application.yml` — Actuator health, metrics, logging config
- [ ] `checkstyle.xml` — Google Java Style or custom
- [ ] Verify `mvnw compile` passes
- [ ] Verify Docker build succeeds end-to-end

### Phase 2: Core API (Week 2)
- [ ] `Task` entity + `TaskRepository` (Spring Data JPA)
- [ ] `V1__create_task_table.sql` — Flyway migration
- [ ] `TaskService` + `TaskServiceImpl` — CRUD with Redis caching
- [ ] `TaskController` — REST endpoints (`GET/POST/PUT/DELETE /api/v1/tasks`)
- [ ] `TaskRequest` / `TaskResponse` DTOs with validation
- [ ] Integration with PostgreSQL + Redis

### Phase 3: Testing (Week 2-3)
- [ ] `PingControllerTest` — unit test
- [ ] `TaskServiceTest` — unit test with mocking (JaCoCo ≥80%)
- [ ] `TaskRepositoryTest` — integration test with Testcontainers
- [ ] Integration profile (`application-integration.yml`)
- [ ] `mvnw verify -P integration-tests` passes
- [ ] JaCoCo coverage gates enforced

### Phase 4: Security Config (Week 3)
- [ ] `.owasp-suppression.xml` — suppress known false positives
- [ ] `.gitleaks.toml` — allowlist patterns
- [ ] `spotbugs-security-include.xml` — Find Security Bugs rules
- [ ] `.zap/rules.tsv` — ZAP alert thresholds
- [ ] `SecurityConfig.java` — Spring Security with OAuth2/JWT (optional)

### Phase 5: Helm + GitOps (Week 3)
- [ ] `helm/my-java-app/Chart.yaml`
- [ ] `templates/deployment.yaml` — digest-pinned image, nonroot, readonly FS
- [ ] `templates/service.yaml` + `ingress.yaml`
- [ ] `values-staging.yaml` / `values-production.yaml`
- [ ] ArgoCD Application manifests in separate gitops repo
- [ ] Argo Rollout (canary) manifest

### Phase 6: Load Testing (Week 4)
- [ ] `tests/load/smoke.js` — k6 script (50 VUs, 2 min)
- [ ] Prometheus queries for canary error rate
- [ ] Verify p99 < 500ms SLO

---

## 3. Pipeline Setup

### GitHub Secrets Required

| Secret | Source | Used By |
|--------|--------|---------|
| `AWS_ACCOUNT_ID` | AWS console | ECR login |
| `AWS_REGION` | AWS console | All AWS steps |
| `AWS_CI_ROLE_ARN` | IAM → OIDC provider | build-image stage |
| `AWS_STAGING_ROLE_ARN` | IAM → OIDC provider | deploy-staging |
| `AWS_PROD_ROLE_ARN` | IAM → OIDC provider | deploy-production |
| `NVD_API_KEY` | nvd.nist.gov | dependency-check |
| `SEMGREP_APP_TOKEN` | semgrep.dev | Semgrep SAST |
| `SONAR_TOKEN` | SonarQube | Sonar analysis |
| `SONAR_HOST_URL` | SonarQube | Sonar analysis |
| `GITLEAKS_LICENSE` | gitleaks.io | Secret detection |
| `GITOPS_PAT` | GitHub → PAT | GitOps manifest push |
| `ARGOCD_SERVER` | ArgoCD | Sync status checks |
| `ARGOCD_TOKEN` | ArgoCD | Sync status checks |
| `PROMETHEUS_URL` | Prometheus | Canary error rate |
| `SLACK_WEBHOOK_URL` | Slack → Apps | Deploy notifications |
| `PAGERDUTY_INTEGRATION_KEY` | PagerDuty | Incident creation |
| `NEXUS_USERNAME` / `NEXUS_PASSWORD` | Nexus | Artifact publish |

### AWS Infrastructure Needed

1. **ECR repositories**: `my-java-app`
2. **EKS clusters**: `tapestry-prod-cluster-staging` + `tapestry-prod-cluster`
3. **IAM OIDC provider** for GitHub Actions
4. **IAM roles** (CI, staging, prod) with trust policies scoped to:
   - `repo:org/java-pipeline`
   - `sub:repo:org/java-pipeline:ref:refs/heads/main`
5. **EKS node groups** with IRSA for ECR pull

### GitHub Environments

| Environment | Protection Rules | URL |
|-------------|-----------------|-----|
| `staging` | Auto-deploy after tests | `https://staging.myapp.internal` |
| `production` | 2 required reviewers, main branch | `https://myapp.tapestry.internal` |

---

## 4. Security Hardening (NIST 800-53)

### Already Covered by Pipeline

| Control | Pipeline Stage | Evidence |
|---------|---------------|----------|
| SI-2 Flaw remediation | OWASP Dependency-Check | `dependency-check-report.html` (30 day) |
| SA-11 Developer testing | JaCoCo ≥80% coverage | `jacoco-report` artifact |
| IA-5 Authenticator mgmt | Gitleaks | Pipeline log |
| SI-3 Malicious code | Trivy container scan | SARIF → GitHub Security |
| CM-14 Signed components | cosign | Signature in ECR |
| SA-12 Supply chain | Syft SBOM | `sbom.spdx.json` (90 day) |
| CA-8 Penetration testing | ZAP DAST | `zap-report.html` (30 day) |
| CM-2 Baseline config | GitOps manifests | Git history in separate repo |
| CM-5 Access restrictions | Manual approval gate | GitHub Environment audit log |

### Additional Hardening to Implement

| Area | Action |
|------|--------|
| **Container** | Distroless base (already done), read-only root FS, CPU/mem limits, no shell |
| **Pod Security** | Pod Security Standards (Restricted profile), `seccomp` profile, no privileged escalation |
| **Network** | NetworkPolicy: deny-all by default, allow only needed ingress/egress |
| **Runtime** | AppArmor profile, `securityContext.runAsNonRoot: true`, `readOnlyRootFilesystem: true` |
| **Secrets** | External Secrets Operator (AWS Secrets Manager) — no K8s secrets |
| **RBAC** | Cluster-scoped resources locked down, least-privilege ServiceAccount per app |
| **Audit** | CloudTrail + EKS audit logs → S3 → Athena for SIEM queries |
| **Backup** | Velero for EKS backup/DR |
| **Encryption** | EBS encryption at rest, TLS everywhere (mTLS between services) |

### Recommended Order of Implementation

```
Month 1:  Phase 1 (Foundation) + AWS OIDC + ECR setup
Month 2:  Phase 2-3 (Core API + Tests) + CI pipeline green
Month 3:  Phase 4 (Security) + CD pipeline (staging)
Month 4:  Phase 5-6 (Helm + Load Test) + Production canary
Ongoing:  Additional hardening + compliance evidence collection
```
