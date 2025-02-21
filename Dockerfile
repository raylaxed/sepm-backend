# Build stage
FROM maven:3.9.6-eclipse-temurin-21-jammy AS builder
COPY . .
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-jammy
COPY --from=builder /target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-default}", "app.jar"]

