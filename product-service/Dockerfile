FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/product-service-1.0-SNAPSHOT.jar product-service.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "product-service.jar"]
