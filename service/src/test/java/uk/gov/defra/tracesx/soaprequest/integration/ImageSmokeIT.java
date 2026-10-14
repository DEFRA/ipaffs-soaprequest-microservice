package uk.gov.defra.tracesx.soaprequest.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.mssqlserver.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Builds the service image from the real Dockerfile and starts it the way Kubernetes does
 * (UID/GID 1000). Catches packaging problems (entrypoint, layers, file permissions, missing
 * environment variables) before the image is pushed.
 *
 * <p>This service has no OAuth or permissions mocks, so the image runs against a SQL Server
 * container only and is checked up to the point of rejecting an unauthenticated request.
 *
 * <p>The image is built with the {@code docker build} CLI (BuildKit), exactly as the pipeline
 * builds it, rather than Testcontainers' legacy-builder image support, which failed in CI with
 * "failed to export image ... layer does not exist". The CLI must use the same Docker daemon as
 * Testcontainers.
 *
 * <p>Requires {@code target/application.jar}, so it must run after {@code mvn package}, which
 * Failsafe does. Base images are pulled from {@code IMAGE_ACR} (default docker.io), as in the
 * pipeline's docker build.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ImageSmokeIT {

  private static final Logger log = LoggerFactory.getLogger(ImageSmokeIT.class);

  private static final int SERVICE_PORT = 8000;
  private static final String KUBERNETES_USER = "1000:1000";
  private static final Duration BUILD_TIMEOUT = Duration.ofMinutes(10);

  private static final String SQL_ALIAS = "mssql";

  private final Network network = Network.newNetwork();
  private final String imageTag = "soaprequest-smoke-test:" + UUID.randomUUID();
  private MSSQLServerContainer database;
  private GenericContainer<?> service;

  @BeforeAll
  @SuppressWarnings("resource")
  void startServiceImage() throws IOException, InterruptedException {
    buildImage();

    database = new MSSQLServerContainer(DockerImageName.parse(
        "mcr.microsoft.com/mssql/server:2022-latest"))
        .acceptLicense()
        .withNetwork(network)
        .withNetworkAliases(SQL_ALIAS);
    database.start();

    service = new GenericContainer<>(DockerImageName.parse(imageTag))
        .withNetwork(network)
        .withExposedPorts(SERVICE_PORT)
        .withCreateContainerCmdModifier(cmd -> cmd.withUser(KUBERNETES_USER))
        .withEnv(serviceEnvironment())
        .withLogConsumer(new Slf4jLogConsumer(log).withPrefix("service-image"))
        .waitingFor(Wait.forLogMessage(".*Started SoapRequestApplication.*", 1)
            .withStartupTimeout(Duration.ofMinutes(2)));
    service.start();
  }

  @AfterAll
  void stopServiceImage() throws IOException, InterruptedException {
    if (service != null) {
      service.stop();
    }
    if (database != null) {
      database.stop();
    }
    network.close();
    docker("rmi", "--force", imageTag);
  }

  // The health check includes the database, so this also shows the image can reach it
  @Test
  void healthCheckReportsUp() {
    JsonNode health = serviceClient().get().uri("/admin/health-check")
        .retrieve()
        .body(JsonNode.class);

    assertThat(health.get("status").asText()).isEqualTo("UP");
  }

  @Test
  void rejectsAnUnauthenticatedRequest() {
    HttpStatus status = serviceClient().get().uri("/soaprequest")
        .exchange((request, response) -> HttpStatus.valueOf(response.getStatusCode().value()));

    assertThat(status).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  private void buildImage() throws IOException, InterruptedException {
    String imageAcr = System.getenv().getOrDefault("IMAGE_ACR", "docker.io");
    DockerResult build = docker("build", "--progress=plain",
        "--build-arg", "IMAGE_ACR=" + imageAcr, "--tag", imageTag, ".");
    if (build.exitCode() != 0) {
      throw new IllegalStateException(
          "docker build failed (exit %d):%n%s".formatted(build.exitCode(), build.output()));
    }
    log.debug("docker build output:\n{}", build.output());
  }

  private static DockerResult docker(String... args) throws IOException, InterruptedException {
    List<String> command = new ArrayList<>(List.of("docker"));
    command.addAll(List.of(args));
    // Output goes to a file so the process can't block on a full pipe and the timeout still applies
    Path outputFile = Files.createTempFile("docker-", ".log");
    try {
      Process process = new ProcessBuilder(command)
          .directory(Path.of("").toAbsolutePath().toFile())
          .redirectErrorStream(true)
          .redirectOutput(outputFile.toFile())
          .start();
      if (!process.waitFor(BUILD_TIMEOUT.toSeconds(), TimeUnit.SECONDS)) {
        process.destroyForcibly();
        throw new IllegalStateException("Timed out running " + command);
      }
      return new DockerResult(process.exitValue(), Files.readString(outputFile));
    } finally {
      Files.deleteIfExists(outputFile);
    }
  }

  private record DockerResult(int exitCode, String output) {
  }

  private Map<String, String> serviceEnvironment() {
    String jdbcUrl =
        "jdbc:sqlserver://%s:%d;encrypt=false;user=%s;password=%s".formatted(
            SQL_ALIAS, 1433, database.getUsername(), database.getPassword());

    // The service doesn't call the JWKS URL until it gets a token, which these checks don't send
    return Map.of(
        "DATABASE_DB_CONNECTION_STRING_SOAPREQUEST_MICROSERVICE", jdbcUrl,
        "FEATURES_ENABLE_MANAGED_IDENTITY_AUTH", "false",
        "JWT_JWKS", "http://oauth.invalid/jwks",
        "JWT_CERT_ISSUER", "http://oauth.invalid",
        "JWT_AUDIENCE", "integration-test",
        "MONITORING_TYPE", "log");
  }

  private RestClient serviceClient() {
    return RestClient.builder()
        .baseUrl("http://%s:%d".formatted(service.getHost(), service.getMappedPort(SERVICE_PORT)))
        .defaultHeader("INS-ConversationId", UUID.randomUUID().toString())
        .build();
  }
}
