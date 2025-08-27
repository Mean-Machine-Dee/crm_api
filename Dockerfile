FROM maven:3-openjdk-11 AS build
WORKDIR /app
COPY pom.xml .
EXPOSE 8096
COPY src ./src
RUN mvn clean package -DskipTests

#Package stage
FROM amazoncorretto:11-al2-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar crm_api.jar
CMD ["java","-jar","crm_api.jar"]