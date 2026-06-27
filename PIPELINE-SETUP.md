# Java Production Pipeline — Setup Guide

## Required GitHub Secrets

Configure all secrets under:
Settings → Secrets and variables → Actions → New repository secret

### AWS (OIDC — no long-lived keys)
| Secret | Description |
|--------|-------------|
| `AWS_ACCOUNT_ID` | AWS account number (e.g. 123456789012) |
| `AWS_REGION` | e.g. us-gov-west-1 |
| `AWS_CI_ROLE_ARN` | IAM role for CI build/push (ECR write) |
| `AWS_STAGING_ROLE_ARN` | IAM role for staging deploy (EKS + ECR read) |
| `AWS_PROD_ROLE_ARN` | IAM role for production deploy — least privilege |

### Security scanning
| Secret | Description |
|--------|-------------|
| `NVD_API_KEY` | NIST NVD API key for OWASP Dependency-Check (free at nvd.nist.gov) |
| `SEMGREP_APP_TOKEN` | Semgrep Cloud token |
| `SONAR_TOKEN` | SonarQube/SonarCloud token |
| `SONAR_HOST_URL` | e.g. https://sonarqube.tapestry.internal |
| `GITLEAKS_LICENSE` | Gitleaks license key (required for org use) |

### Deployment
| Secret | Description |
|--------|-------------|
| `GITOPS_PAT` | GitHub PAT with write access to gitops-manifests repo |
| `ARGOCD_SERVER` | ArgoCD server URL |
| `ARGOCD_TOKEN` | ArgoCD API token (read + sync permissions) |
| `PROMETHEUS_URL` | Prometheus URL for canary error rate check |

### Notifications
| Secret | Description |
|--------|-------------|
| `SLACK_WEBHOOK_URL` | Slack incoming webhook URL |
| `PAGERDUTY_INTEGRATION_KEY` | PagerDuty Events API v2 key |

### Nexus artifact registry
| Secret | Description |
|--------|-------------|
| `NEXUS_USERNAME` | Nexus deploy user |
| `NEXUS_PASSWORD` | Nexus deploy password |

---

## GitHub Environment Protection Rules

### staging
- No required reviewers (auto-deploys after tests pass)
- Environment URL: https://staging.myapp.internal

### production
- **Required reviewers: 2** (e.g. team lead + security)
- Wait timer: 0 minutes (reviewers provide the gate)
- Branch protection: main only
- Environment URL: https://myapp.tapestry.internal

---

## Pipeline Flow

```
Push to feature/* ──► validate ──► unit-test ─────────────────────────► (PR only — stops here)
                        │           integration-test
                        │           security-scan
                        │           sonar
                        ▼
Push/merge to main ──► build-artifact ──► build-image ──► deploy-staging
                                                               │
                                                        ┌──────┴──────┐
                                                       dast        load-test
                                                        └──────┬──────┘
                                                     [MANUAL APPROVAL]
                                                               │
                                                        deploy-production
                                                         (canary 5%→100%)
                                                               │
                                                         post-deploy
                                                      (health + notify)
```

---

## NIST 800-53 Control Mapping

| Pipeline Stage | Control | Evidence artifact |
|---------------|---------|-------------------|
| security-scan (OWASP) | SI-2 Flaw remediation | dependency-check-report.html (30-day retention) |
| security-scan (Semgrep) | SA-11 Developer testing | SARIF → GitHub Security tab |
| secret detection (Gitleaks) | IA-5 Authenticator mgmt | Pipeline log |
| Trivy image scan | SI-3 Malicious code protection | SARIF → GitHub Security tab |
| cosign sign | CM-14 Signed components | Signature stored in ECR |
| SBOM (Syft) | SA-12 Supply chain | sbom.spdx.json (90-day retention) |
| DAST (ZAP) | CA-8 Penetration testing | zap-report.html (30-day retention) |
| JaCoCo ≥80% | SA-11 Developer testing | jacoco-report artifact |
| GitOps manifest | CM-2 Baseline config | gitops-manifests git history |
| Manual approval gate | CM-5 Access restrictions | GitHub Environment audit log |

---

## Key Design Decisions

### Why digest pinning instead of image tags?
Tags are mutable — `:latest` or `:1.0.0` can be overwritten. Digests (sha256:abc...)
are immutable. Combined with cosign signature verification, you get cryptographic
proof that exactly the tested image is what runs in production.

### Why Buildah over Docker-in-Docker?
Docker-in-Docker requires `--privileged` mode in CI runners — a significant
security risk and a NIST AC-6 violation. Buildah runs rootless with no daemon,
produces identical OCI-compliant images.

### Why a separate GitOps repo?
The application code repo drives CI (build, test, scan).
The GitOps repo drives CD (deploy state). This separation means:
- You can deploy without a code change (config update)
- You can roll back by reverting a Git commit — no CI run needed
- ArgoCD continuously reconciles — drift is detected and auto-corrected

### Why Argo Rollouts canary instead of Helm upgrade?
`helm upgrade` is all-or-nothing. Argo Rollouts enables traffic-weighted
canary with automated Prometheus-based analysis. If the canary fails the
error rate check, rollback is instant and automatic — no human needed at 2am.
