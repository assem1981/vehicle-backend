# ==============================================================================
# Build Stage: Compile Maven Project using OpenJDK 21
# ==============================================================================
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copy pom.xml and resolve dependencies first (enables Docker layer caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy application source code and compile jar artifact
COPY src ./src
RUN mvn clean package -DskipTests -B

# ==============================================================================
# Runtime Stage: Lightweight JRE 21 Alpine Execution Environment
# ==============================================================================
FROM eclipse-temurin:21-jre-alpine AS runner

WORKDIR /app

# Create non-root user and group for enhanced security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy compiled spring boot jar artifact from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Adjust file ownership
RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8070

ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

# Redis on the docker bridge gateway (host-side redis-server must bind 172.17.0.1)
ENV SPRING_REDIS_HOST=172.17.0.1
ENV SPRING_REDIS_PORT=6379

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
