FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY GenerateUsers.java ./
RUN javac GenerateUsers.java

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/*.class ./
COPY config.properties ./
ENTRYPOINT ["java", "GenerateUsers"]
