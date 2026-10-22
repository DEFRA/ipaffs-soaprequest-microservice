package uk.gov.defra.tracesx.soaprequest.integration;

import static org.testcontainers.utility.DockerImageName.parse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.RemoteDockerImage;

class OAuthMockServerContainer extends GenericContainer<OAuthMockServerContainer> {

  OAuthMockServerContainer() {
    super(new RemoteDockerImage(parse("ghcr.io/navikt/mock-oauth2-server:2.1.10")));

    try {
      final Path path = Paths.get(
          IntegrationBase.class.getResource("/integration/oauth2-mock-server.json").toURI());
      this.withEnv("JSON_CONFIG", Files.readString(path));
      this.withExposedPorts(8080);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }
}
