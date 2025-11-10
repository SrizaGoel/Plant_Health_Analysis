# ------------------------------------------------------
# STAGE 1: Build Java Spring Boot App
# ------------------------------------------------------
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy project files and build
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# ------------------------------------------------------
# STAGE 2: Run Application
# ------------------------------------------------------
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy the built JAR from builder
COPY --from=builder /app/target/planthealth-0.0.1-SNAPSHOT.jar /app/planthealth.jar

# Expose Spring Boot port
EXPOSE 8080

# Run the Spring Boot app only (Flask runs separately)
CMD ["java", "-jar", "/app/planthealth.jar"]
