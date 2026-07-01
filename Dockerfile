# Paso 1: Compilar usando Maven y Eclipse Temurin Java 17
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Paso 2: Imagen oficial y ligera de Eclipse Temurin para ejecutar Java 17
FROM eclipse-temurin:17-jre-alpine
COPY --from=build /target/folio-lp3-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]