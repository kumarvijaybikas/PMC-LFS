# Stage 1: Build stage with Maven and OpenJDK 21
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build package
COPY src ./src
RUN mvn clean package -DskipTests=false -B

# Stage 2: Minimal runtime image
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Create non-root system user for security
RUN groupadd -r appgroup && useradd -r -g appgroup appuser
USER appuser:appgroup

# Copy built jar from build stage
COPY --from=build /app/target/literature-funding-service-*.jar app.jar

# Expose standard REST API port
EXPOSE 8080

# Environment options
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0"

# Healthcheck
HEALTHCHECK --interval=30s --timeout=5s --start-period=15s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
