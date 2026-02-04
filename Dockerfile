FROM maven:3.9.1-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml ./
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine AS runtime

# Fix CVE-2026-24515
RUN apk add --no-cache 'libexpat>=2.7.4-r0'

<<<<<<< development
=======

>>>>>>> main
WORKDIR /app

COPY --from=build /app/target/*.jar ./backend.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "backend.jar"]