build:
	docker compose -f infrastucture/docker-compose.yaml build

build-no-cache:
	docker compose -f infrastucture/docker-compose.yaml build --no-cache

run:
	docker compose -f infrastucture/docker-compose.yaml up -d

run-it:
	docker compose -f infrastucture/docker-compose.yaml up

stop:
	docker compose -f infrastucture/docker-compose.yaml down

stop-volumes:
	docker compose -f infrastucture/docker-compose.yaml down -v

enter-weather-service:
	docker compose -f infrastucture/docker-compose.yaml exec weather-service bash

test-weather-service:
	docker compose -f infrastucture/docker-compose.yaml exec weather-service bash -c "cd /app && ./mvnw test"

format-weather-service:
	docker compose -f infrastucture/docker-compose.yaml exec weather-service bash -c "cd /app && ./mvnw checkstyle:check"

.PHONY: build run run-it stop stop-volumes enter-weather-service format-weather-service test-weather-service
