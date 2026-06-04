# ---- Build stage ----
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -q

COPY src ./src
RUN ./mvnw -q package -DskipTests

# Extract Spring Boot layers for lean final image
RUN java -Djarmode=layertools -jar target/ocean-sensor-1.0.0.jar extract --destination target/extracted

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Non-root user for K8s security contexts
RUN addgroup -S simulator && adduser -S simulator -G simulator
USER simulator

# Copy layered JAR content in cache-friendly order (deps change least often)
COPY --from=builder /workspace/target/extracted/dependencies/           ./
COPY --from=builder /workspace/target/extracted/spring-boot-loader/    ./
COPY --from=builder /workspace/target/extracted/snapshot-dependencies/ ./
COPY --from=builder /workspace/target/extracted/application/           ./

# JVM flags: container-aware heap, fast startup, smaller footprint
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:InitialRAMPercentage=50.0 \
               -XX:+ExitOnOutOfMemoryError \
               -Djava.security.egd=file:/dev/./urandom"

ENV SIMULATOR_BUOY_COUNT=8

EXPOSE 8080

HEALTHCHECK --interval=15s --timeout=5s --start-period=30s --retries=3 \
  CMD wget -qO- http://localhost:8080/ocean-overview/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
