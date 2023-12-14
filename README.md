# wex-coding-challenge

This project is part of the WEX Coding Challenge. Below are instructions on how to set up and run the project, and conduct code analysis with SonarQube.

### Documentation
This project leverages an OpenAPI specification to define and document its API. The specification file can be found in the resources folder.

Additionally, the **maven-openapi-generator-plugin** is used to generate server stubs and client SDKs based on this OpenAPI specification. 

### Prerequisites
- Docker
- Docker Compose

### Building the Project
Before running the project with Docker Compose, it is essential to build the JAR file using Maven. 
This step compiles the project and generates the necessary artifacts for running the application in a Docker container.

To build the project, execute the following command in the root directory of the project:

```bash
mvn clean install
```

This command will clean the target directory, compile the source code, and create the JAR file wex-coding-challenge-0.0.1-SNAPSHOT.jar in the target directory.

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
