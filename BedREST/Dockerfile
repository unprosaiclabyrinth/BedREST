## Stage 1: Build the project
#FROM hseeberger/scala-sbt:graalvm-ce-21.3.0-java17_1.6.2_3.1.1 as builder
#
## Set working directory inside the container
#WORKDIR /app
#
## Copy the SBT project definition files
#COPY build.sbt .
#COPY project/ ./project/
#
## Cache dependencies
#RUN sbt update
#
## Copy the rest of the project files
#COPY . .
#
## Build the project (produces a fat JAR if you're using sbt-assembly)
#RUN sbt assembly
#
## Stage 2: Create a runtime image
#FROM openjdk:17-jdk-slim
#
## Set the working directory inside the container
#WORKDIR /app
#
## Copy the built JAR from the builder stage
#COPY --from=builder /app/target/scala-*/grpc-proxy-fat.jar app.jar
#
## Expose the port your application runs on
#EXPOSE 50051
#
## Command to run the application
#CMD ["java", "-jar", "app.jar"]
FROM hseeberger/scala-sbt:graalvm-ce-21.3.0-java17_1.6.2_3.1.1

# Set the working directory inside the container
WORKDIR /app

# Copy the entire project into the container
COPY . .

# Install dependencies and run the application
CMD ["sbt", "run passive"]