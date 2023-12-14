# wex-coding-challenge

WEX Coding Challenge

## WEX Coding Challenge

This project is part of the WEX Coding Challenge. Below are instructions on how to set up and run the project, and conduct code analysis with SonarQube.

### Documentation
This project leverages an OpenAPI specification to define and document its API. The specification file can be found in the resources folder.

Additionally, the **maven-openapi-generator-plugin** is used to generate server stubs and client SDKs based on this OpenAPI specification. 

### Prerequisites
- Docker
- Docker Compose

### Running the Project
#### Starting the Application and Database

To start the application and PostgreSQL database, run the following command in the project's root directory:

```bash
docker-compose -f docker-compose.yml up
```
The application will be available at http://localhost:8080.

#### Running SonarQube for Code Analysis

To start SonarQube, execute:

```bash
docker-compose -f docker-compose-sonar.yml up
```

On first run, you will need to set up a new password for the default admin user.

#### Analyzing the Project with SonarQube Scanner

For analysis with Maven execute the command:

```bash
mvn clean verify sonar:sonar \
-Dsonar.projectKey=wex-coding-challenge \
-Dsonar.host.url=http://localhost:9000 \
-Dsonar.login=your_token
```

#### Additional Notes
Ensure that the services do not conflict with other applications that may be using the same ports.
To stop the services, use docker-compose -f <file> down.

### The following topics were deprioritized due to the short deadline.

However, as the requirements suggest building an application as if it were to be deployed in a production environment, it's important to consider some key features that this project lacks.

Some production-grade features essential for a maintainable and scalable project, but currently absent, are:

1. Security Layer (with Spring Security)
2. Secrets Management
3. Observability (with Prometheus)
    1. Logging (via the ELK Stack)
    2. Tracing (also using the ELK Stack)
    3. Metrics (implemented with Grafana)
4. Proper Continuous Integration for image generation and version management
5. End-to-End Tests
6. Code Style Guides
    1. Automated Code Style (using the ravelc formatter plugin)
    2. Automated Check Style (with the Maven Checkstyle plugin)