# Jenkins Pipeline

## Purpose

This project uses a Jenkinsfile to define the CI pipeline as code.

The pipeline currently performs the following stages:

1. Verify required tools
2. Start Postgres
3. Run Java tests
4. Run SQL data quality checks
5. Publish test results and archive Postgres logs

## Pipeline File

The Jenkins pipeline is defined in the root-level:

`Jenkinsfile`

## Required Agent Tools

The Jenkins agent must have:

- Java
- Docker
- Docker Compose
- Git

## Why Pipeline as Code?

Defining the pipeline in Git makes the delivery process version-controlled, repeatable and reviewable.

Instead of relying on manual instructions, the repository contains the exact steps needed to validate the project.

## Current Pipeline Flow

```text
Checkout code
    ↓
Verify Java and Docker
    ↓
Start Postgres
    ↓
Run Maven tests
    ↓
Run SQL data quality checks
    ↓
Publish test results
```

## Important Note

The current Jenkinsfile assumes the Jenkins agent can run Docker commands.

If Jenkins runs inside a Docker container, additional Docker socket configuration may be needed. That setup will be handled separately.

## Local Jenkins Setup

Jenkins can be run locally using Docker.

Start Jenkins:

```bash
docker compose -f docker-compose.jenkins.yml up -d --build
```
Open Jenkins:

`http://localhost:8081`

Get the initial admin password:
```bash
docker exec banking-refinery-jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```
## Local Jenkins Architecture

```text
Jenkins container
    ↓
Docker socket mount
    ↓
Docker Desktop on host
    ↓
Postgres container
```

The Jenkins container includes Docker CLI and Docker Compose so it can run the same Docker commands used locally.

## Pipeline Job

Create a Pipeline job called:

`banking-refinery-platform-ci`

Configure it with:

* Definition: Pipeline script from SCM
* SCM: Git
* Repository URL: GitHub repository URL
* Branch: active feature branch or develop
* Script Path: Jenkinsfile


## Important Notes

The Jenkins container uses host.docker.internal to connect to Postgres from inside Docker.

The local Spring Boot app uses localhost.

This difference exists because localhost inside a container refers to the container itself, not the host machine.