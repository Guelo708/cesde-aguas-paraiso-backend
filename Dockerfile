# =========================================================================
# Etapa 1: Compilación del proyecto (Builder)
# =========================================================================
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copiar Maven Wrapper y pom.xml primero para aprovechar la caché
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Dar permisos al wrapper y descargar las dependencias
RUN chmod +x ./mvnw && ./mvnw dependency:go-offline -B

# Copiar el código fuente y generar el JAR ejecutable
COPY src/ ./src/
RUN ./mvnw clean package -DskipTests -B

# =========================================================================
# Etapa 2: Imagen ligera para ejecución (Runtime)
# =========================================================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Crear un usuario sin privilegios root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copiar únicamente el JAR generado
COPY --from=builder \
    --chown=appuser:appgroup \
    /app/target/*.jar app.jar

USER appuser

# Valor local de respaldo; Render proporciona PORT en producción
ENV PORT=8080

EXPOSE ${PORT}

# Ejecutar Spring Boot usando el puerto proporcionado por Render
ENTRYPOINT ["sh", "-c", "java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Dserver.port=${PORT} -jar app.jar"]