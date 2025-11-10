# -----------------------------
# STAGE 1: Build Java App
# -----------------------------
FROM openjdk:17-jdk-slim AS builder

WORKDIR /app

# Install Python (for ML)
RUN apt-get update && apt-get install -y python3 python3-pip

# Copy project files
COPY . .

# Install Python dependencies
RUN pip3 install -r requirements.txt

# Build Spring Boot JAR
RUN ./mvnw clean package -DskipTests || mvn clean package -DskipTests

# -----------------------------
# STAGE 2: Run App
# -----------------------------
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy built app + ML files
COPY --from=builder /app /app

# Expose Spring Boot port
EXPOSE 8080

# Run both ML server and Spring Boot together
CMD python3 ml_server.py & java -jar target/planthealth-0.0.1-SNAPSHOT.jar
