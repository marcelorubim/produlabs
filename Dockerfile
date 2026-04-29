FROM maven:3.6.0-jdk-11-slim as build-env
WORKDIR /app
COPY pom.xml .
RUN mvn package --fail-never
COPY . .
RUN mvn package

FROM adoptopenjdk:11-jre-openj9
ENV TZ=America/Sao_Paulo
COPY --from=build-env app/target/lib/* /opt/lib/
COPY --from=build-env app/target/*-runner.jar /opt/app.jar
ENTRYPOINT ["java", "-jar", "/opt/app.jar"]
