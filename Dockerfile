# ========== 1. Frontend-Builder ==========
FROM node:23 AS frontend-builder

WORKDIR /app/frontend

COPY frontend/package*.json ./
RUN npm install

COPY frontend/ ./
RUN npm run build


# ========== 2. Maven Dependency Cacher ==========
FROM maven:3.9.9-eclipse-temurin-23 AS dependencies

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline


# ========== 3. Backend-Builder ==========
FROM maven:3.9.9-eclipse-temurin-23 AS backend-builder

WORKDIR /app

COPY --from=dependencies /root/.m2 /root/.m2

COPY . .

# Kopiere das React-Build in das Spring Boot static-Verzeichnis
COPY --from=frontend-builder /app/frontend/dist ./src/main/resources/static

RUN mvn clean package -DskipTests


# ========== 4. Final Runtime ==========
FROM eclipse-temurin:23-jre

WORKDIR /app

COPY --from=backend-builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
