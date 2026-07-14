# Etapa 1: Construir la aplicación con Maven
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Levantar el servidor de Spring Boot
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/virtual-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]