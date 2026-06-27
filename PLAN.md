# Flowbase Jira — Implementation Plan

A Jira clone built with Spring Boot 3.3.5 + React 19, with a DevSecOps pipeline.

## Project Structure

```
java-pipeline/
├── .github/workflows/ci-cd.yml
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── checkstyle.xml
├── .gitleaks.toml
├── .zap/rules.tsv
├── src/
│   ├── main/java/com/flowbase/jira/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── model/
│   │   ├── repository/
│   │   └── service/
│   └── main/resources/
│       ├── db/migration/
│       └── application.yml
├── frontend/
│   ├── src/pages/
│   └── nginx.conf
├── helm/
│   └── my-java-app/
└── tests/
    └── load/
```

## Tech Stack

- **Backend:** Spring Boot 3.3.5, Java 21, JPA, Flyway, PostgreSQL
- **Frontend:** React 19, Vite, React Router
- **Infrastructure:** Docker Compose, Helm, Kubernetes
- **Quality:** Checkstyle, JaCoCo, SpotBugs, OWASP Dependency Check, SonarQube
- **Security:** Gitleaks, ZAP, Trivy, Cosign
- **CI/CD:** GitHub Actions, ArgoCD, Argo Rollouts, k6
