# =============================================================================
# Geo-Explorer — Backend (Spring Boot 3.3.13 / Java 21)
# Multi-stage: build com Maven + JDK | runtime enxuto com JRE e usuário não-root
# =============================================================================

# -----------------------------------------------------------------------------
# Estágio 1 — Build (Maven + JDK)
# -----------------------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# Copia só o POM primeiro: mudanças no código não invalidam o cache de
# dependências baixadas no `dependency:go-offline`.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Depois o código-fonte e o build do jar
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# -----------------------------------------------------------------------------
# Estágio 2 — Runtime (JRE enxuta, usuário não-root)
# -----------------------------------------------------------------------------
FROM eclipse-temurin:21-jre AS runtime

ENV TZ=America/Sao_Paulo \
    JAVA_OPTS=""

WORKDIR /app

# Usuário não-root de execução
RUN groupadd --system app \
    && useradd --system --gid app --home-dir /app app \
    && chown -R app:app /app

COPY --from=build --chown=app:app /build/target/geo-explorer-1.0.0.jar /app/app.jar

USER app
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
