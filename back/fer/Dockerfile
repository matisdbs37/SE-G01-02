FROM maven:3.8.7-openjdk-21.0.9 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:21.0.9-jdk-slim
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]