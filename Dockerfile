# ------------------------------------------------------
# STAGE 1: Build Java + Python (TensorFlow 2.13 compatible)
# ------------------------------------------------------
FROM ubuntu:22.04 AS builder

ENV DEBIAN_FRONTEND=noninteractive

# Install Java 17, Python 3.10, pip, Maven + build tools
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk python3.10 python3.10-distutils \
                       python3.10-venv python3-pip maven build-essential && \
    ln -sf /usr/bin/python3.10 /usr/bin/python3 && \
    ln -sf /usr/bin/pip3 /usr/bin/pip

WORKDIR /app

# Copy project files
COPY . .

# ✅ Upgrade pip first, then install requirements
RUN pip install --upgrade pip
RUN pip install --no-cache-dir -r requirements.txt

# Build Spring Boot JAR
RUN mvn clean package -DskipTests

# ------------------------------------------------------
# STAGE 2: Run Application
# ------------------------------------------------------
FROM ubuntu:22.04

ENV DEBIAN_FRONTEND=noninteractive

# Install Java 17 + Python 3.10 runtime
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk python3.10 python3.10-distutils \
                       python3.10-venv python3-pip && \
    ln -sf /usr/bin/python3.10 /usr/bin/python3 && \
    ln -sf /usr/bin/pip3 /usr/bin/pip && \
    apt-get clean

WORKDIR /app

# Copy everything from builder
COPY --from=builder /app /app

# Expose Spring Boot port
EXPOSE 8080

# Start ML server + Spring Boot together
CMD python3 ml_server.py & sleep 8 && java -jar target/planthealth-0.0.1-SNAPSHOT.jar

