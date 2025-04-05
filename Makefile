build:
	docker compose -f infrastructure/docker-compose.yaml build

build-no-cache:
	docker compose -f infrastructure/docker-compose.yaml build --no-cache

run:
	docker compose -f infrastructure/docker-compose.yaml up -d

run-it:
	docker compose -f infrastructure/docker-compose.yaml up

stop:
	docker compose -f infrastructure/docker-compose.yaml down

stop-volumes:
	docker compose -f infrastructure/docker-compose.yaml down -v

enter-weather-service:
	docker compose -f infrastructure/docker-compose.yaml exec weather-service bash

test-weather-service:
	docker compose -f infrastructure/docker-compose.yaml exec weather-service bash -c "cd /app && ./mvnw test"

test-auth-service:
	docker compose -f infrastructure/docker-compose.yaml exec auth-service bash -c "cd /app && ./mvnw test"

format-weather-service:
	docker compose -f infrastructure/docker-compose.yaml exec weather-service bash -c "cd /app && ./mvnw spotless:apply"

format-auth-service:
	docker compose -f infrastructure/docker-compose.yaml exec auth-service bash -c "cd /app && ./mvnw spotless:apply"

format-gateway-service:
	docker compose -f infrastructure/docker-compose.yaml exec gateway bash -c "cd /app && ./mvnw spotless:apply"

.PHONY: build run run-it stop stop-volumes enter-weather-service format-weather-service test-weather-service format-auth-service test-auth-service format-gateway-service
