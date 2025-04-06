# Weather Forecast System

A microservices-based weather forecasting application built with Java and Spring Boot.

## Overview

This system retrieves, processes, and serves weather data from third-party providers through a set of independently deployable microservices.

## Architecture

The Weather Forecast System consists of 3 services:

- **Weather Service**: Core service for retrieving and processing weather data. - fetch data from OpenWeatherMap, cache it using Redis, and expose data via gRPC.
- **Auth Service**: Handles user registration, authentication (using JWT), and user data management via gRPC, backed by a PostgreSQL database.
- **Gateway Service**: Acts as the public API gateway and entry point for clients. It routes HTTP requests to the appropriate backend services using gRPC and handles request validation and JWT authentication.

## Technology Stack

- **Language**: Java 17
- **Framework**: Spring Boot 3.x (including WebFlux for reactive programming in weather and gateway services)
- **Inter-service Communication**: gRPC
- **Database**: PostgreSQL (for Auth Service)
- **Caching**: Redis (for Weather Service)
- **Resilience**: Resilience4j (Circuit Breaker in Weather and Gateway services)
- **Containerization**: Docker

## Getting Started

### Prerequisites

- Docker and Docker Compose
- Java 17 (for local development)
- Maven 3.9+ (or use the included Maven wrapper)
- Create `.env` files within each service directory (`services/auth-service`, `services/weather-service`, `services/gateway-service`) based on their respective `env.example` files and provide necessary values (e.g., `OPENWEATHERMAP_API_KEY`, `JWT_SECRET`, DB credentials, Redis host/port if not default).

### Running with Docker

1. Clone the repository:

   ```bash
   git clone <https://github.com/benidevo/weather-forecast-system.git>
   cd weather-forecast-system
   ```

2. Create the necessary `.env` files as described in Prerequisites.
3. Build and start the services using Docker Compose:

   ```bash
   docker-compose -f infrastructure/docker-compose.yaml up --build -d
   ```

4. Access the API gateway at: `http://localhost:8080`

## Project Structure

```
├── infrastructure/         # Docker configuration
│   ├── docker/
│   └── docker-compose.yaml
├── services/              # Microservices
│   ├── auth-service/      # Authentication and user management service
│   ├── gateway-service/   # API Gateway service
│   └── weather-service/   # Weather data retrieval and processing service
└── README.md              # This file
```

## API Documentation

The API is exposed through the `gateway-service`.

**Base Path:** `/api/v1`

### Authentication (`/auth`)

- **Endpoint:** `POST /auth/login`
  - **Description:** Authenticates a user and returns a JWT token upon success.
  - **Request Body:** *(application/json)*

        ```json
        {
          "username": "your_username",
          "password": "your_password"
        }
        ```

  - **Success Response (200 OK):** *(application/json)*

        ```json
        {
          "success": true,
          "message": "Login successful",
          "data": {
            "success": true,
            "token": "jwt.token.string",
            "expiresAt": "iso_timestamp_string"
          }
        }
        ```

  - **Error Response (401 Unauthorized):** *(application/json)* If authentication fails via `AuthServiceGrpcClientImpl`.

        ```json
        {
          "success": false,
          "message": "Login failed",
          "data": null
        }
        ```

  - **Error Response (400 Bad Request):** *(application/json)* If request body validation fails.

        ```json
        {
          "success": false,
          "message": "Validation error",
          "errors": {
            "username": "Username is required",
            "password": "Password is required"
          }
        }
        ```

- **Endpoint:** `POST /auth/register`
  - **Description:** Registers a new user.
  - **Request Body:** *(application/json)*

        ```json
        {
          "username": "new_username",
          "password": "new_password"
        }
        ```

  - **Success Response (200 OK):** *(application/json)*

        ```json
        {
          "success": true,
          "message": "Register successful"
        }
        ```

  - **Error Response (400 Bad Request):** *(application/json)* If registration fails (e.g., user already exists via `AuthServiceGrpcClientImpl`).

        ```json
        {
          "success": true,
          "message": "User already exists"
        }
        ```

  - **Error Response (400 Bad Request):** *(application/json)* If request body validation fails.

        ```json
        {
          "success": false,
          "message": "Validation error",
          "errors": {
            "username": "Username is required",
          }
        }
        ```

### Weather (`/weather`)

- **Endpoint:** `GET /weather`
  - **Description:** Retrieves weather data for a specified location using either coordinates or a city name. Requires authentication.
  - **Headers:**
    - `Authorization: Bearer <your_jwt_token>` (Required, validated by `JwtAuthenticationFilter`)
  - **Query Parameters:**
    - `lat` (Double): Latitude. Required if `lon` is provided and `city` is not.
    - `lon` (Double): Longitude. Required if `lat` is provided and `city` is not.
    - `city` (String): City name. Required if `lat` and `lon` are not provided.
        *(Provide *either* `lat` and `lon` *or* `city`)*
  - **Success Response (200 OK):** *(application/json)*

        ```json
        {
          "success": true,
          "message": "Weather data fetched successfully.",
          "data": {
            "latitude": 48.8566,
            "longitude": 2.3522,
            "timezone": "Europe/Paris",
            "timezoneOffset": "+02:00",
            "description": "clear sky",
            "temperature": 15.5,
            "feelsLike": 14.8,
            "pressure": 1012.0,
            "humidity": 60,
            "windSpeed": 3.1,
            "forecast": [
              {
                "description": "light rain",
                "temperature": 14.0,
                "feelsLike": 13.5,
                "pressure": 1010.0,
                "humidity": 75,
                "windSpeed": 5.0
              }
              // ... more forecast objects
            ],
            "alerts": [
              {
                "name": "NWS National Weather Service",
                "description": "High wind warning in effect...",
                "startTime": "unix_timestamp_string",
                "endTime": "unix_timestamp_string"
              }
              // ... more alert objects
            ]
          }
        }
        ```

  - **Error Response (400 Bad Request):** *(application/json)* If query parameters are invalid.

        ```json
        {
          "success": false,
          "message": "Please provide either lat and lon or city.",
          "data": null
        }
        ```

  - **Error Response (401 Unauthorized):** If the JWT token is missing, invalid, or expired. *(Response body might be empty)*
  - **Error Response (5xx Internal Server Error):** If communication with the `weather-service` via gRPC fails or other backend errors occur. *(Response body might vary)*

## License

This project is licensed under the MIT License - see the LICENSE file for details.
