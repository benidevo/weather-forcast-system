FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY ./services/weather-service/.mvn/ ./.mvn/
COPY ./services/weather-service/mvnw ./services/weather-service/pom.xml ./

RUN ./mvnw dependency:go-offline -B

COPY ./services/weather-service/src ./src

RUN ./mvnw package -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM eclipse-temurin:17-jre

RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

WORKDIR /app

COPY --from=build /app/target/*.jar /app/app.jar

ENV SPRING_PROFILES_ACTIVE=docker

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

HEALTHCHECK --interval=30s --timeout=3s --retries=3 CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1
