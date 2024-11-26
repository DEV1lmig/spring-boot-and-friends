FROM gradle:8.11.1-jdk17 AS build

WORKDIR /app

COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle gradle
COPY src src

RUN ./gradlew clean bootJar

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
