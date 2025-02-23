# Build stage
FROM maven:3.9.6-eclipse-temurin-21-jammy AS builder
COPY . .
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-jammy
COPY --from=builder /target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-default}
ENV CORS_ALLOWED_ORIGINS=https://sepm-frontend.vercel.app,http://localhost:4200,https://sepm-backend-6xd0.onrender.com

EXPOSE 8080
ENTRYPOINT ["java", "-jar", \
           "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", \
           "-Dspring.mvc.cors.allowed-origins=${CORS_ALLOWED_ORIGINS}", \
           "app.jar"]

