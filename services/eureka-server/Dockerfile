
FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY src src

RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

RUN --mount=type=cache,target=/root/.m2 ./mvnw -B -q package -DskipTests

FROM eclipse-temurin:25-jre

WORKDIR /app

RUN useradd --system --uid 1001 appuser

USER appuser

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
