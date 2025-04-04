# Weather Forecast System

A microservices-based weather forecasting application built with Java and Spring Boot.

## Overview

This system retrieves, processes, and serves weather data from third-party providers through a set of independently deployable microservices.

## Architecture

The Weather Forecast System consists of 2 services:

- **Weather Service**: Core service for retrieving and processing weather data
- **Auth Service**: Handles authentication and authorization

## Technology Stack

- **Language**: Java 17
- **Framework**: Spring Boot 3.x
- **Database**: PostgreSQL
- **Caching**: Redis
- **Containerization**: Docker
- **Service Discovery**: Spring Cloud Netflix Eureka
- **API Gateway**: Spring Cloud Gateway

## Getting Started

### Prerequisites

- Docker and Docker Compose
- Java 17 (for local development)
- Maven 3.9+ (or use the included Maven wrapper)

### Running with Docker

1. Clone the repository:

   ```
   git clone https://github.com/benidevo/weather-forecast-system.git
   cd weather-forecast-system
   ```

2. Build and start the services:

   ```
   docker-compose -f infrastucture/docker-compose.yaml up -d
   ```

3. Access the weather service at <http://localhost:8080>
4. Access the auth service at <http://localhost:8081>

## Project Structure

```
├── docs/                  # Documentation
├── infrastucture/         # Docker and deployment configuration
├── services/              # Microservices
│   ├── weather-service/   # Weather data service
│   └── ...                # Other services
└── README.md              # This file
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.
