# Conway's Game of Life (Component-Based Implementation)

A robust implementation of Conway's Game of Life designed with **Component-Based Software Engineering (CBSE)** and OOP principles. The architecture is built around interchangeable, loosely-coupled components, specifically focusing on a modular DAO layer for flexible state management.

## Project Overview

This project was developed as part of a **Component-Based Programming** course. The primary goal was to create a system where the business logic, graphical representation, and data persistence layers act as independent components that communicate through well-defined interfaces.

## Key Component Features

- **Modular DAO Component:** Implements a plug-and-play architecture for data persistence. 
  - `FileGameOfLifeBoardDao`: A component for high-speed local state serialization.
  - `JdbcGameOfLifeBoardDao`: A heavy-duty component for relational storage in PostgreSQL.
- **Interchangeable Persistence Layer:** The application can switch between database and file storage components without modifying the core simulation logic, demonstrating true modularity.
- **Simulation Engine:** A standalone component responsible for the cellular automata logic, supporting deep cloning and state validation.
- **JavaFX GUI Component:** A modern graphical interface that interacts with the simulation engine through a reactive component-based approach.

## Technologies Used

- **Java** (JDK 25)
- **Component Infrastructure:** Maven (for dependency management and build lifecycle)
- **GUI Layer:** JavaFX
- **Persistence Components:** PostgreSQL, JDBC
- **Environment Orchestration:** Docker & Docker Compose
- **Quality Assurance Components:** JUnit 5, JaCoCo (Code Coverage), PMD (Static Analysis)

## Project Structure

- `src/main/java/pl/lodz/p/` - Core logic and component implementations (DAO, GUI, Exceptions).
- `src/main/resources/` - Component resources, including FXML layouts and localization bundles (PL/EN).
- `src/test/java/` - Unit and integration tests ensuring component reliability.
- `docker-compose.yml` - Containerized infrastructure for the database component.
- `pom.xml` - Project configuration defining component dependencies and quality gates.

## Build and Run Instructions

### Prerequisites
- Java JDK (Version 21 or higher recommended, configured for 25 in POM)
- Apache Maven
- Docker (for database functionality)

  
### Step 1: Database Setup
Start the PostgreSQL and pgAdmin containers using Docker Compose:
```bash
docker-compose up -d
```
*Note: The database runs on localhost:5432 with user nbd and password nbdpassword. pgAdmin is available on port 80.*

### Step 2: Compile and Run
Use the Maven wrapper or your local Maven installation to run the JavaFX application:
```bash
mvn clean javafx:run
```

### Step 3: Run Tests & Quality Checks
To execute the JUnit tests and generate JaCoCo/PMD reports:
```bash
mvn test
mvn pmd:pmd
```

## License
This project is licensed under the MIT License. See the LICENSE file for details.

