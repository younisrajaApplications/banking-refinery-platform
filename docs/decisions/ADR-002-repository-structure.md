# ADR-002 - Repository Structure

## Status

Accepted

## Context

The Banking Refinery Platform includes multiple types of work:

- Java application code
- SQL transformations
- Terraform infrastructure
- Jenkins pipeline configuration
- documentation
- data schemas

Without a clear structure, the repository would become difficult to understand and maintain.

## Decision

Use a separated repository structure:

- app/ for Java application code
- data/ for sample data and schemas
- sql/ for SQL transformations and tests
- terraform/ for infrastructure as code
- jenkins/ for Jenkins pipeline files
- docs/ for architecture, requirements, runbooks and decisions

## Consequences

### Benefits

- Easier to navigate
- Clear separation of responsibilities
- Better alignment with real team structures
- Easier to explain in interviews
- Easier to add CI/CD checks later

### Trade-offs

- Slightly more setup at the beginning
- More folders to manage
- Requires discipline to keep files in the correct place
