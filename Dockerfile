FROM maven:3.9.1-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml ./
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine AS runtime

WORKDIR /app

COPY --from=build /app/target/backend-*.jar ./backend.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "backend.jar"]