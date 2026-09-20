FROM maven:3.9.11-eclipse-temurin-21 AS local
WORKDIR /workspace
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src src
CMD ["mvn", "-Dspring-boot.run.fork=true", "spring-boot:run"]

FROM local AS build
WORKDIR /workspace
COPY src src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre-alpine AS runtime
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
