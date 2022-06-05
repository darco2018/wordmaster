# syntax=docker/dockerfile:1

FROM amazoncorretto:17

WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

COPY . /app

CMD ["./mvnw", "spring-boot:run", "-Dspring-boot.run.profiles=dev", "-Dspring.profiles.active=dev"]
