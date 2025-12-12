FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY . .

# Give execution permission to the Gradle Wrapper
RUN chmod +x gradlew

# Build the application using the Gradle Wrapper
RUN ./gradlew bootJar --no-daemon

RUN mv build/libs/oncolly-0.0.1-SNAPSHOT.jar app.jar

# Run the application
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]