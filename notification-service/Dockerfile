FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/notification-service-1.0-SNAPSHOT.jar notification-service.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "notification-service.jar"]