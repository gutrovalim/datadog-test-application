# Build either service: docker build --build-arg SERVICE=orders-service .
FROM maven:3.9-eclipse-temurin-21 AS build
ARG SERVICE
WORKDIR /src
COPY ${SERVICE}/pom.xml .
RUN mvn -q dependency:go-offline
COPY ${SERVICE}/src src
RUN mvn -q package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
# Datadog Java tracer (dd-java-agent), attached with -javaagent at startup
ADD https://dtdg.co/latest-java-tracer /app/dd-java-agent.jar
COPY --from=build /src/target/app.jar /app/app.jar
ENTRYPOINT ["java", "-javaagent:/app/dd-java-agent.jar", "-jar", "/app/app.jar"]
