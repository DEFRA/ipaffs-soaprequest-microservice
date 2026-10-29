ARG IMAGE_ACR=docker.io
ARG IMAGE_REPOSITORY=liquibase/liquibase
ARG IMAGE_TAG=5.0.4
ARG MAVEN_IMAGE=maven:3.9.11-eclipse-temurin-21-noble

# Resolve the JDBC driver and Azure Identity jars and fetch the go-sqlcmd
# release in a throwaway stage so that neither Maven nor bzip2 ends up in
# the migrations image.
FROM ${IMAGE_ACR}/${MAVEN_IMAGE} AS deps

ARG SQLCMD_VERSION=v1.10.0
ARG SQLCMD_SHA256=92516d98c63d99b0994de5b61350c91f6915f9b76f139a59039fbcb225c2e987

WORKDIR /build

COPY liquibase-deps/pom.xml .
RUN mvn -B -q package

RUN apt-get update && \
    apt-get install -y --no-install-recommends bzip2 ca-certificates curl && \
    curl -fsSL -o sqlcmd.tar.bz2 "https://github.com/microsoft/go-sqlcmd/releases/download/${SQLCMD_VERSION}/sqlcmd-linux-amd64.tar.bz2" && \
    echo "${SQLCMD_SHA256}  sqlcmd.tar.bz2" | sha256sum -c - && \
    tar -xjf sqlcmd.tar.bz2 sqlcmd

FROM ${IMAGE_ACR}/${IMAGE_REPOSITORY}:${IMAGE_TAG}

WORKDIR /opt

USER root

# Apply OS security updates and remove the Liquibase package manager, which
# is not used.
RUN apt-get update && \
    apt-get upgrade -y && \
    rm -rf /var/lib/apt/lists/* && \
    rm -f /liquibase/bin/lpm

COPY --from=deps /build/sqlcmd /usr/local/bin/sqlcmd
COPY --from=deps /build/target/lib/*.jar /liquibase/lib/

COPY setup.sh mi.sh ./
COPY database/scripts/migrate.sh liquibase/
COPY database/src/main/resources/changelogs liquibase/changelogs
COPY database/src/main/resources/migrations.xml liquibase
COPY database/src/main/resources/logback liquibase/lib

RUN chown -R liquibase:liquibase /liquibase/lib /opt && chmod -R 0700 /opt

USER liquibase:liquibase

CMD ["/bin/sh", "setup.sh"]
