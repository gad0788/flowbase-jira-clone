# Pipeline Setup Guide

To enable the CI/CD pipeline, rename `.github/workflows/ci-cd.yml.disabled` to `.github/workflows/ci-cd.yml` and configure the following GitHub secrets:

## Required GitHub Secrets

| Secret | Description |
|--------|-------------|
| `AWS_ACCOUNT_ID` | AWS account number |
| `AWS_REGION` | AWS region |
| `AWS_CI_ROLE_ARN` | IAM role for CI build/push |
| `AWS_STAGING_ROLE_ARN` | IAM role for staging deploy |
| `AWS_PROD_ROLE_ARN` | IAM role for production deploy |
| `NVD_API_KEY` | NIST NVD API key for OWASP Dependency-Check |
| `SONAR_TOKEN` | SonarQube/SonarCloud token |
| `GITLEAKS_LICENSE` | Gitleaks license (optional) |
| `SLACK_WEBHOOK_URL` | Slack notifications |
| `PAGERDUTY_INTEGRATION_KEY` | PagerDuty alerts |
| `ARGOCD_SERVER` | ArgoCD server URL |
| `ARGOCD_TOKEN` | ArgoCD auth token |
| `GITOPS_PAT` | GitOps repo PAT |

## Pipeline Stages

1. Checkstyle — Code style
2. JaCoCo — Coverage (≥80%)
3. SpotBugs — Static analysis
4. OWASP Dependency Check — Vulnerability scan
5. SonarQube — Quality gate
6. Gitleaks — Secret scan
7. ZAP — DAST
8. Docker build & push — ECR
9. Helm deploy — EKS with canary
