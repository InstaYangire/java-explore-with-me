FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY ./pom.xml ./pom.xml
COPY ./main-service ./main-service
COPY ./stats ./stats

RUN mvn -q -e -DskipTests -pl main-service -am clean package

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/main-service/target/main-service-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]