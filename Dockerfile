# ------------------------------------------------------
# STAGE 1: Build Java + Python (TensorFlow 2.13 compatible)
# ------------------------------------------------------
FROM ubuntu:22.04 AS builder

ENV DEBIAN_FRONTEND=noninteractive

# Install Java 17, Python 3.10, pip, Maven, and build tools
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk python3.10 python3.10-distutils \
                       python3.10-venv python3-pip maven build-essential && \
    ln -sf /usr/bin/python3.10 /usr/bin/python3 && \
    ln -sf /usr/bin/pip3 /usr/bin/pip

WORKDIR /app

# Copy project files
COPY . .

# Install Python dependencies (TF 2.13 works here)
RUN pip install --no-cache-dir --break-system-packages -r requirements.txt

# Build Spring Boot JAR (only mvn, no mvnw)
RUN mvn clean package -DskipTests

# ------------------------------------------------------
# STAGE 2: Run Application
# ------------------------------------------------------
FROM ubuntu:22.04

ENV DEBIAN_FRONTEND=noninteractive

# Install Java 17 and Python 3.10
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk python3.10 python3.10-distutils \
                       python3.10-venv python3-pip && \
    ln -sf /usr/bin/python3.10 /usr/bin/python3 && \
    ln -sf /usr/bin/pip3 /usr/bin/pip && \
    apt-get clean

WORKDIR /app

# Copy everything from builder
COPY --from=builder /app /app

# Expose the Spring Boot port
EXPOSE 8080

# Start ML server + Spring Boot together
CMD python3 ml_server.py & java -jar target/planthealth-0.0.1-SNAPSHOT.jar
