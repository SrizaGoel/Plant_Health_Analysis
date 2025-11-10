# -----------------------------
# STAGE 1: Build Java + ML environment
# -----------------------------
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

# Install Python and pip (for ML)
RUN apt-get update && apt-get install -y python3 python3-pip

# Copy all project files into the container
COPY . .

# Install Python dependencies safely (PEP 668 fix)
RUN pip3 install --no-cache-dir --break-system-packages -r requirements.txt

# Build the Spring Boot JAR file
RUN chmod +x mvnw || true
RUN ./mvnw clean package -DskipTests || mvn clean package -DskipTests

# -----------------------------
# STAGE 2: Run the full application
# -----------------------------
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy everything (built JAR + model + Python files)
COPY --from=builder /app /app

# Expose the Java app port (Spring Boot default)
EXPOSE 8080

# Start the Python ML server and Spring Boot together
CMD python3 ml_server.py & java -jar target/planthealth-0.0.1-SNAPSHOT.jar
