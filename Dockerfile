## Use an official OpenJDK runtime as the base image
## Example if your project are using jdk17 then change its base image to version 17
#FROM maven:3.8.7-eclipse-temurin-19
#
## Set the working directory inside the container
#WORKDIR /app
#
## Copy the executable JAR file and any other necessary files
#COPY target/todo_api-0.0.1-SNAPSHOT.jar /app
#
## Expose the port on which your Spring application will run (change as per your application)
#EXPOSE 8080
#
## Set the command to run your Spring application when the container starts
#CMD ["java", "-jar", "/app/todo_api-0.0.1-SNAPSHOT.jar"]
FROM openjdk:17
COPY ./target/todo_api-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "todo_api-0.0.1-SNAPSHOT.jar"]
