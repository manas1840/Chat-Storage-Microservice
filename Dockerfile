# ============================================================
# Stage 1 — Build
# ============================================================
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN apk add --no-cache maven && \
    mvn clean package -DskipTests

# ============================================================
# Stage 2 — Runtime
# ============================================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S ragchat && adduser -S ragchat -G ragchat
USER ragchat

COPY --from=build /app/target/rag-chat-service-1.0.0.jar app.jar

RUN mkdir -p logs

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
