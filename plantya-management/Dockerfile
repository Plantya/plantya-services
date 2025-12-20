FROM eclipse-temurin:21-jre-jammy

WORKDIR /app
COPY target/quarkus-app/ /app/

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/quarkus-run.jar"]