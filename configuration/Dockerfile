ARG IMAGE_ACR=docker.io
ARG IMAGE_REPOSITORY=liquibase/liquibase
ARG IMAGE_TAG=5.0.4
ARG MAVEN_IMAGE=maven:3.9.11-eclipse-temurin-21-noble
ARG GO_IMAGE=golang:1.27.3-bookworm

# Resolve the JDBC driver and Azure Identity jars in a throwaway stage so
# that Maven does not end up in the migrations image.
FROM ${IMAGE_ACR}/${MAVEN_IMAGE} AS deps

WORKDIR /build

COPY liquibase-deps/pom.xml .
RUN mvn -B -q package

# Build go-sqlcmd from source. The published binaries are compiled with the
# Go toolchain and golang.org/x module versions of their release date, which
# accumulate vulnerability findings; building here keeps both current.
FROM ${IMAGE_ACR}/${GO_IMAGE} AS sqlcmd

ARG SQLCMD_VERSION=v1.10.0
ARG SQLCMD_COMMIT=affbc17e1e0e97b0bb625ef8185afea3d746067f
ARG GOLANG_X_NET_VERSION=v0.62.0
ARG GOLANG_X_CRYPTO_VERSION=v0.58.0
ARG GOLANG_X_TEXT_VERSION=v0.43.0
ARG GOLANG_X_SYS_VERSION=v0.49.0

WORKDIR /src
RUN git clone --quiet --depth 1 --branch "${SQLCMD_VERSION}" https://github.com/microsoft/go-sqlcmd.git . && \
    test "$(git rev-parse HEAD)" = "${SQLCMD_COMMIT}" && \
    go get "golang.org/x/net@${GOLANG_X_NET_VERSION}" \
           "golang.org/x/crypto@${GOLANG_X_CRYPTO_VERSION}" \
           "golang.org/x/text@${GOLANG_X_TEXT_VERSION}" \
           "golang.org/x/sys@${GOLANG_X_SYS_VERSION}" && \
    go mod tidy && \
    CGO_ENABLED=0 go build -trimpath -ldflags="-s -w -X main.version=${SQLCMD_VERSION}" -o /out/sqlcmd ./cmd/modern

FROM ${IMAGE_ACR}/${IMAGE_REPOSITORY}:${IMAGE_TAG}

WORKDIR /opt

USER root

# Apply OS security updates and remove the Liquibase package manager, which
# is not used.
RUN apt-get update && \
    apt-get upgrade -y && \
    rm -rf /var/lib/apt/lists/* && \
    rm -f /liquibase/bin/lpm

COPY --from=sqlcmd /out/sqlcmd /usr/local/bin/sqlcmd
COPY --from=deps /build/target/lib/*.jar /liquibase/lib/

COPY setup.sh mi.sh ./
COPY database/scripts/migrate.sh liquibase/
COPY database/src/main/resources/changelogs liquibase/changelogs
COPY database/src/main/resources/migrations.xml liquibase
COPY database/src/main/resources/logback liquibase/lib

RUN chown -R liquibase:liquibase /liquibase/lib /opt && chmod -R 0700 /opt

USER liquibase:liquibase

CMD ["/bin/sh", "setup.sh"]
