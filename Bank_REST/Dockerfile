FROM openjdk:17-jdk-slim

RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

ARG JAR_FILE=target/Bank_REST-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
