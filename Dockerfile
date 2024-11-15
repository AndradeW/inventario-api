# Etapa 1: Construcción de la aplicación con Maven
FROM maven:3.8.5-openjdk-17 AS build

# Establece el directorio de trabajo en el contenedor
WORKDIR /app

# Copia solo el archivo pom.xml primero para aprovechar la cache de Docker
COPY pom.xml ./

# Resuelve las dependencias antes de copiar el código fuente, para que no se vuelva a descargar si no cambia pom.xml
RUN mvn dependency:go-offline

# Copia el código fuente
COPY src ./src

# Ejecuta mvn clean package para compilar y empaquetar la aplicación
RUN mvn clean package -DskipTests

# Etapa 2: Ejecutar la aplicación con una imagen Java
FROM openjdk:17-jdk-slim

# Establece el directorio de trabajo para la ejecución
WORKDIR /app

# Copia el archivo .jar generado en la etapa de construcción
COPY --from=build /app/target/inventario-api-0.0.1-SNAPSHOT.jar app.jar

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
