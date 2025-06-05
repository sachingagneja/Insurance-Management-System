FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod +x ./mvnw && ./mvnw package -DskipTests

EXPOSE 8081

ENV SERVER_PORT=8081

ENTRYPOINT ["java", "-jar", "target/digital-insurance-management-system-0.0.1-SNAPSHOT.jar"]
