# Elegimos la imagen base para la fase de construcción
FROM eclipse-temurin:21-jdk AS build
# Definimos nuestro directorio de trabajo
WORKDIR /app
# Copiamos tod0 nuestro proyecto desde la máquina local hacia el contenedor
COPY . .
# Setteamos temporalmente la variable de entorno LANG y LC_ALL a C.UTF-8
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8
# Limpiamos, compilamos y empaquetamos el proyecto en un .jar, saltando los tests
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar api-gestion.jar
# Este puerto tiene un propósito declarativo e informativo, porque Docker luego decide un puerto distinto
EXPOSE 8080
CMD ["java","-jar","api-gestion.jar"]

