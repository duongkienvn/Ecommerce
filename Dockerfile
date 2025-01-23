# stage 1
FROM maven:3.9.9-amazoncorretto-21 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests


#stage 2
FROM amazoncorretto:21.0.4

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

