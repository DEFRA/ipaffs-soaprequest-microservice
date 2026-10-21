package uk.gov.defra.tracesx.soaprequest.integration;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.testcontainers.utility.DockerImageName.parse;

import java.util.Map;
import java.util.UUID;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockserver.client.MockServerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.RestClient;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.mockserver.MockServerContainer;
import uk.gov.defra.tracesx.common.permissions.PermissionsCache;

/**
 * Starts the service against an OAuth server that issues its JWTs and a MockServer that plays the
 * permissions service. The database is the SQL Server that {@code TestContainerConfig} starts:
 * the service's component scan picks that test configuration up, and its data source is primary.
 * Each test creates the service's tables there if they don't exist ({@code setup-db.sql}).
 */
@ActiveProfiles("integration-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = {"classpath:application-integration-test.yml"})
abstract class IntegrationBase {

  static final OAuthMockServerContainer OAUTH_CONTAINER = new OAuthMockServerContainer();
  static final MockServerContainer MOCK_SERVER_CONTAINER =
      new MockServerContainer(parse("mockserver/mockserver")
          .withTag("mockserver-" + MockServerClient.class.getPackage().getImplementationVersion()));

  static {
    Startables.deepStart(OAUTH_CONTAINER, MOCK_SERVER_CONTAINER).join();
  }

  @LocalServerPort
  int port;

  @Autowired
  PermissionsCache permissionsCache;

  @Autowired
  DataSource dataSource;

  private MockServerClient mockServerClient;

  @DynamicPropertySource
  static void setupContextValues(DynamicPropertyRegistry registry) {
    registry.add("permissions.service.url",
        () -> "%s/permissions/".formatted(MOCK_SERVER_CONTAINER.getEndpoint()));
    registry.add("spring.security.jwt.iss", () -> oauthUrl() + "/default");
    registry.add("spring.security.jwt.jwks", () -> oauthUrl() + "/default/jwks");
    registry.add("spring.security.jwt.aud", () -> "integration-test");
  }

  @BeforeEach
  void createTables() {
    new ResourceDatabasePopulator(new ClassPathResource("integration/setup-db.sql"))
        .execute(dataSource);
  }

  @AfterEach
  void tearDown() {
    permissionsCache.clearCache();
  }

  void givenUserHasPermissions(String... permissions) {
    permissionsCache.clearCache();
    String jsonPermissions = permissions.length == 0
        ? "[]"
        : "[\"" + String.join("\",\"", permissions) + "\"]";
    usingStub().clear(request().withPath("/permissions/roles/Inspector/permissions"));
    usingStub()
        .when(request().withPath("/permissions/roles/Inspector/permissions"))
        .respond(response().withBody(
            jsonPermissions, org.mockserver.model.MediaType.APPLICATION_JSON));
  }

  WebTestClient webClient() {
    return WebTestClient.bindToServer()
        .baseUrl("http://localhost:%d".formatted(port))
        .defaultHeader("Authorization", "Bearer " + getToken())
        .defaultHeader("INS-ConversationId", UUID.randomUUID().toString())
        .build();
  }

  WebTestClient anonymousWebClient() {
    return WebTestClient.bindToServer()
        .baseUrl("http://localhost:%d".formatted(port))
        .defaultHeader("INS-ConversationId", UUID.randomUUID().toString())
        .build();
  }

  private MockServerClient usingStub() {
    if (mockServerClient == null) {
      mockServerClient = new MockServerClient(
          MOCK_SERVER_CONTAINER.getHost(), MOCK_SERVER_CONTAINER.getServerPort());
    }
    return mockServerClient;
  }

  private static String getToken() {
    Map<String, Object> body = RestClient.create(oauthUrl())
        .post()
        .uri("/default/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body("grant_type=authorization_code&code=userid&client_id=inspector")
        .retrieve()
        .body(new ParameterizedTypeReference<>() {});
    return (String) body.get("access_token");
  }

  private static String oauthUrl() {
    return "http://%s:%d".formatted(
        OAUTH_CONTAINER.getHost(), OAUTH_CONTAINER.getMappedPort(8080));
  }
}
