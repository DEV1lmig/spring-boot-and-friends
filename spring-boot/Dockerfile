# Stage 1: Build the application
FROM gradle:8.11.1-jdk17 AS build

WORKDIR /app

# Copy Gradle wrapper and project files
COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle gradle
COPY src src

# Build the application
RUN ./gradlew clean bootJar

# Stage 2: Run the application
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
