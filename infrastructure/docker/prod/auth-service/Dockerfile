FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY ./services/auth-service/.mvn/ ./.mvn/
COPY ./services/auth-service/mvnw ./services/auth-service/pom.xml ./

RUN ./mvnw dependency:go-offline -B

COPY ./services/auth-service/src ./src

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
