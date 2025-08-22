FROM maven:3.8.7-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

COPY simsun.ttf /usr/share/fonts/simsun.ttf
RUN apt update && \
    apt install -y ttf-mscorefonts-installer fontconfig && \
    fc-cache -fv && \
    rm -rf /var/lib/apt/lists/* \

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]