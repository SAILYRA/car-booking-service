# Use the official OpenJDK image as the base image
FROM openjdk:17-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the compiled Java application and its dependencies into the container
COPY target/*.jar /app/bookingService.jar

# Expose the port that the application will run on
EXPOSE 8083

# Start the Java application
CMD ["java", "-jar", "/app/bookingService.jar"]