# ===== Stage 1: Build the application =====
FROM maven:3.9.4-eclipse-temurin-17 AS build

# Set working directory inside builder container
WORKDIR /app

# Copy pom.xml and download dependencies first (makes caching faster)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build JAR filea
RUN mvn clean package -DskipTests

# ===== Stage 2: Run the application =====
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
