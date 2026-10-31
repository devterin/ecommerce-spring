# Base image
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy JAR file
COPY target/ecommerce-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Run the JAR with environment variables
ENTRYPOINT ["java", "-jar", "app.jar"]