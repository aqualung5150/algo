FROM openjdk:17-jdk-slim
COPY build/libs/app.jar app.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "/app.jar"]
