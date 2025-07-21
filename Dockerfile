FROM gradle:8.7.0-jdk21 AS builder

COPY build.gradle.kts settings.gradle.kts gradle.properties ./

COPY gradle ./gradle

RUN gradle dependencies --no-daemon || true

COPY . .

RUN gradle bootJar --no-daemon

FROM eclipse-temurin:21-jre

COPY --from=builder /home/gradle/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
