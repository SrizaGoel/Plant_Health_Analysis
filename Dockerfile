# -----------------------------
# STAGE 1: Build Java + Python (TF 2.13.0 compatible)
# -----------------------------
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

# Install Python 3.10 (compatible with TF 2.13.0)
RUN apt-get update && \
    apt-get install -y software-properties-common && \
    add-apt-repository ppa:deadsnakes/ppa && \
    apt-get update && \
    apt-get install -y python3.10 python3.10-venv python3.10-distutils python3-pip build-essential && \
    ln -sf /usr/bin/python3.10 /usr/bin/python3 && \
    ln -sf /usr/bin/pip3 /usr/bin/pip

# Copy all project files
COPY . .

# Install Python dependencies (PEP 668 safe)
RUN pip install --no-cache-dir --break-system-packages -r requirements.txt

# Build the Spring Boot JAR file
RUN chmod +x mvnw || true
RUN ./mvnw clean package -DskipTests || mvn clean package -DskipTests

# -----------------------------
# STAGE 2: Run Application
# -----------------------------
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy everything from builder
COPY --from=builder /app /app

# Expose Spring Boot port
EXPOSE 8080

# Start ML server and Spring Boot together
CMD python3 ml_server.py & java -jar target/planthealth-0.0.1-SNAPSHOT.jar
