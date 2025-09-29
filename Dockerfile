# Multi-stage build: Angular client + Spring Boot backend

# 1. Build Angular client
FROM node:20-alpine AS ui-build
WORKDIR /app/client
COPY client/package*.json ./
RUN npm ci --no-audit --no-fund
COPY client/ ./
RUN npm run build -- --configuration production

# 2. Build Spring Boot backend (include built frontend assets)
FROM maven:3.9.9-eclipse-temurin-21 AS backend-build
WORKDIR /build
COPY demo/pom.xml demo/pom.xml
RUN mvn -q -f demo/pom.xml dependency:go-offline
COPY demo ./demo
# Copy built Angular dist into Spring Boot resources (served as static)
# Copy ONLY the built Angular app contents so that index.html ends up directly in static/
# Previous line copied the entire dist directory, resulting in static/d288-front-end/index.html
# which caused Spring Boot to return 404 at '/'. We flatten the dist output here.
COPY --from=ui-build /app/client/dist/d288-front-end/ ./demo/src/main/resources/static/
RUN mvn -q -f demo/pom.xml -DskipTests clean package

# 3. Runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Non-root user
RUN addgroup -S app && adduser -S app -G app
USER app
COPY --from=backend-build /build/demo/target/demo-0.0.1-SNAPSHOT.jar app.jar

# Default: use postgres settings already in application.properties
ENV SPRING_PROFILES_ACTIVE=default
# Allow overriding DB credentials at deploy time
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://database.internal:5432/full_stack_ecommerce
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=postgres

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
