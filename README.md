# dtb-technical-assessment
## Overview

This project is a collection of eSpring Boot-based applications that allows onboarding of customers on to a card system, creating accounts for them and subsequently providing them with cards linked to those accounts to further transactions.

## Features

- RESTful APIs
- Spring Boot 3
- Spring Data JPA
- PostgreSQL integration
- Unit tests

## Tech Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Lombok
- PostgreSQL
- JUnit & Mockito
- Jacoco

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.9+
- Git

### Clone the repository

```bash
https://github.com/KarenKipchirchir/dtb-technical-assessment.git
cd dtb-technical-assessment
cd into specific-service e.g cd dtb-technical-test-card-service
```

### Build the project
```bash
mvn clean install
```

### Running Locally
```bash
mvn spring-boot:run
```

### Tests
```bash
mvn test
```

### Code Coverage
```bash
mvn clean verify
```
```bash
cd target/site/jacoco
```
```bash
view index.html on browser
```
