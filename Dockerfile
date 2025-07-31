FROM eclipse-temurin:24-jdk-alpine
WORKDIR /app
COPY target/auth-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
