# Build stage
FROM maven:3.13.0-eclipse-temurin-21-jammy AS builder
COPY . .
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-jammy
COPY --from=builder /target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

