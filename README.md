# Bookstore API Automated Tests

API automation test framework for the Bookstore service, built with **Java 21**, **Junit 5**, **RestAssured**, and **Allure**.  
The project is designed for maintainability, configurability across environments, and seamless CI/CD integration.

---

## Table of Contents

- Overview
- Technology Stack
- Prerequisites
- Configuration
- Environment Resolution Logic
- Running Tests
- Test Suites & Tags
- Gradle Tasks
- Reporting
- Parallel Execution
- CI/CD Usage
- Best Practices

---

## Overview

This repository contains automated API tests validating Bookstore service functionality.  
Tests are written using JUnit 5.

The framework supports:
- Multiple environments (The framework currently includes a qa environment by default. Additional environments (e.g. stage, prod) can be added easily by providing corresponding configuration files under the env/ directory.)
- Runtime overrides via system properties or environment variables
- Tag-based execution (smoke, regression, etc.)
- Rich test reporting with Allure

---

## Technology Stack

- Java 21
- JUnit 5
- RestAssured
- Allure Report
- Gradle (wrapper)


## Prerequisites

- JDK 21
- Git
- Internet access for dependency resolution

No local Gradle installation is required (Gradle Wrapper is used).

---

## Configuration

Configuration is environment-based and resolved at runtime.

### Environment Files

Located under:

```
env/{env}.properties
```

Example `qa.properties`:

```properties
BASE_URL=https://qa.bookstore.api
API_PREFIX=api/v1
HTTP_CONNECT_TIMEOUT_MS=5000
HTTP_SOCKET_TIMEOUT_MS=10000
```

---

## Configuration Parameters

| Parameter                 | Description |
|---------------------------|------------|
| `ENV`                     | Target environment (`qa`, `stage`, `prod`) |
| `BASE_URL`                | API base URL override |
| `API_PREFIX`              | API path prefix |
| `HTTP_CONNECT_TIMEOUT_MS` | HTTP connect timeout |
| `HTTP_SOCKET_TIMEOUT_MS`  | HTTP socket timeout |

---

## Environment Resolution Logic

The configuration resolution order is:

1. JVM system property (`-DKEY=value`)
2. OS environment variable
3. Environment properties file (`env/{env}.properties`)
4. Default value (if defined)

If a required parameter is missing, test execution fails immediately.

---

## Running Tests

### Default execution (qa environment)

```bash
./gradlew test -DENV=qa
```

### Overriding configuration values

```bash
./gradlew test -DENV=qa -DBASE_URL=https://custom.bookstore.api -DAPI_PREFIX=/api/v2
```

---

## Test Suites & Tags

Tests can be grouped using tags.

### Available suites

| Task | Description |
|-----|-------------|
| `test` | Runs all tests |
| `smoke` | Runs smoke tests |
| `regression` | Runs regression tests |

### Run tagged tests

```bash
./gradlew test -Dtag=smoke
```

Multiple tags:

```bash
./gradlew test -Dtag=smoke,critical
```

---

## Gradle Tasks

| Task | Description |
|-----|-------------|
| `test` | Full test execution |
| `smoke` | tags.Smoke test suite |
| `regression` | Regression test suite |
| `allureReport` | Generates Allure report |
| `allureServe` | Serves Allure report locally |

---

## Reporting

The project integrates **Allure** for test reporting.

After test execution:

```bash
./gradlew allureReport
```

To open the report locally:

```bash
./gradlew allureServe
```

---

## Parallel Execution

Parallel execution is configurable per task:

- Default `test` task runs with a single fork
- `smoke` and `regression` tasks scale based on available CPU cores

This ensures stability while allowing faster execution for larger suites.

---
## [CI/CD (GitHub Actions)](https://github.com/annatsyhanko-a11y/Bookstore.Tests.API/actions)

This repository includes **four workflows** under `.github/workflows/`:
- **CI Check** (`ci-check.yml`) — runs on push to `main`
- **Smoke Tests** (`smoke.yml`) — manual run (`workflow_dispatch`)
- **Regression Tests** (`regression.yml`) — manual run + scheduled daily run
- **API Tests by params** (`tests.yml`) — manual run with inputs (`env`, `tags`)

### Workflows

#### CI Check (push to main)

Purpose: quick, cheap signal that the pipeline and tests can start successfully after each push to `main`.

It runs a limited smoke validation:
- `./gradlew smoke --tests "*BooksApiSpec*"` (a focused subset)

This is intentionally fast to catch obvious breakages early (Gradle, dependencies, test discovery, basic execution).  
See workflow: `.github/workflows/ci-check.yml`.

#### tags.Smoke Tests (manual)

Purpose: run the smoke suite on demand and publish a **tags.Smoke Allure** report.

Key behavior:
- Executes `./gradlew smoke`
- Generates Allure report 
- Publishes to **GitHub Pages** under `/smoke/`
- Uploads artifacts (Allure report/results + Gradle test outputs)

See workflow: `.github/workflows/smoke.yml`.

#### Regression Tests (manual + scheduled)

Purpose: run the full regression suite regularly (daily) and on demand, and publish a **Regression Allure** report.

Key behavior:
- Triggers:
    - manual (`workflow_dispatch`)
    - scheduled daily at `02:00 UTC` (`cron: "0 2 * * *"`)
- Executes `./gradlew regression`
- Generates Allure report
- Publishes to **GitHub Pages** under `/regression/`
- Uploads artifacts and fails job if tests failed

See workflow: `.github/workflows/regression.yml`.

#### API Tests by params (manual with inputs)

Purpose: run `./gradlew test` manually while selecting:
- environment (`env` input, used as `-DENV=...`)
- optional tag filter (`tags` input, used as `-Dtag=...`)

Key behavior:
- Builds `PROPS="-DENV=${{ inputs.env }}"`, adds `-Dtag=...` when tags are provided
- Runs `./gradlew test` with those props
- Generates Allure report
- Publishes report to **GitHub Pages** under a folder derived from the tag list (or `all`)
- Adds a nice link + run metadata to the workflow Job Summary
- Uploads build outputs as artifacts

See workflow: `.github/workflows/tests.yml`.

---
## Best practices

- Prefer JVM properties for test runs (most portable across shells):
    - `-DENV=...`, `-DBASE_URL=...`, `-DAPI_PREFIX=...`
- Add new environments by creating `env/{env}.properties`, then  update the **API Tests by params** workflow input list.
- Keep smoke suite quick and stable (high signal / low noise).
- Always check GitHub Actions artifacts when a failure happens (they include Allure results and Gradle reports).
