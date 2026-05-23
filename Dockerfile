FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .

RUN chmod +x gradlew
COPY src src

RUN ./gradlew build -Dquarkus.package.type=fast-jar -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /work/

COPY --from=build /app/build/quarkus-app/lib/ /work/lib/
COPY --from=build /app/build/quarkus-app/*.jar /work/
COPY --from=build /app/build/quarkus-app/app/ /work/app/
COPY --from=build /app/build/quarkus-app/quarkus/ /work/quarkus/

EXPOSE 8080
CMD ["java", "-jar", "quarkus-run.jar"]