package uk.gov.defra.tracesx.soaprequest.integration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Pins every payload the service sends or stores, so that a framework or Jackson upgrade can't
 * change them unnoticed. Runs one script against empty tables: creates SOAP requests, reads them
 * back in every way, deletes one, sends the requests the service rejects, and updates and queries
 * the cache. Each response's status, content type, Location header and body are compared with
 * {@code integration/payload/<step>.txt}, and at the end so are the audit rows and the cache rows
 * the service wrote ({@code audit_rows.txt}, {@code cache_rows.txt}).
 *
 * <p>The service generates each SOAP request's UUID and takes its request ID from the clock, so
 * each is replaced with a numbered placeholder in the order it first appears. The same ID gets
 * the same placeholder in every step, so the test still checks that IDs match up. Spring Boot's
 * error timestamp is checked for format and then masked. On a mismatch the actual payload is
 * written to {@code target/payload-actual}.
 */
class PayloadContractIT extends IntegrationBase {

  private static final String PAYLOAD_DIR = "integration/payload/";
  private static final String USER = "4850c6e5-cb72-eb11-a812-000d3a23baf0";
  private static final Pattern UUID = Pattern.compile(
      "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");
  private static final Pattern REQUEST_ID = Pattern.compile("(?<!\\d)\\d{13}(?!\\d)");
  private static final Pattern ERROR_TIMESTAMP = Pattern.compile(
      "\"timestamp\":\"\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}\\+00:00\"");
  private static final String FIRST = """
      {"username":"inspector-one","query":"<soap:Envelope>first</soap:Envelope>",\
      "unknownField":"ignored"}""";
  private static final String SECOND = """
      {"id":"00000000-0000-0000-0000-000000000001","requestId":1,\
      "username":"inspector-two","query":"<soap:Envelope>second \\"quoted\\"</soap:Envelope>"}""";

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private final SoftAssertions softly = new SoftAssertions();
  private final Map<String, Map<String, String>> placeholders = new LinkedHashMap<>();

  @BeforeEach
  void setUp() {
    givenUserHasPermissions("soaprequest.create", "soaprequest.read", "soaprequest.delete");
    jdbcTemplate.update("DELETE FROM soaprequest");
    jdbcTemplate.update("DELETE FROM soaprequest_audit");
    jdbcTemplate.update("DELETE FROM cacherequest");
  }

  @Test
  void payloads() {
    WebTestClient client = webClient();

    String first = send("create_first", client, HttpMethod.POST, "/soaprequest", FIRST)
        .getResponseHeaders().getLocation().toString();
    String second = send("create_second", client, HttpMethod.POST, "/soaprequest", SECOND)
        .getResponseHeaders().getLocation().toString();
    send("create_without_query", client, HttpMethod.POST, "/soaprequest",
        "{\"username\":\"inspector-one\"}");
    send("create_invalid_json", client, HttpMethod.POST, "/soaprequest", "{\"username\":");

    String body = body(send("get_first", client, HttpMethod.GET, first, null));
    Matcher requestId = REQUEST_ID.matcher(body);
    softly.assertThat(requestId.find()).as("get_first requestId").isTrue();
    send("get_all_by_request_id", client, HttpMethod.GET,
        "/soaprequest?requestId=" + requestId.group(), null);
    send("get_all_unknown_request_id", client, HttpMethod.GET, "/soaprequest?requestId=1", null);
    send("get_all_without_request_id", client, HttpMethod.GET, "/soaprequest", null);
    send("get_unknown", client, HttpMethod.GET,
        "/soaprequest/00000000-0000-0000-0000-000000000009", null);
    send("get_invalid_id", client, HttpMethod.GET, "/soaprequest/not-a-uuid", null);

    send("delete_second", client, HttpMethod.DELETE, second, null);
    send("delete_second_again", client, HttpMethod.DELETE, second, null);

    send("cache_update", client, HttpMethod.POST, "/cache/update", """
        [{"id":"CHEDA.GB.2026.1000001","value":"hash-one"},\
        {"id":"CHEDP.GB.2026.1000002","value":"hash-two","unknownField":"ignored"}]""");
    send("cache_update_without_value", client, HttpMethod.POST, "/cache/update",
        "[{\"id\":\"CHEDA.GB.2026.1000003\"}]");
    send("cache_hits", client, HttpMethod.POST, "/cache/hits", """
        [{"id":"CHEDA.GB.2026.1000001","value":"hash-one"},\
        {"id":"CHEDP.GB.2026.1000002","value":"changed"},\
        {"id":"CHEDD.GB.2026.1000004","value":"hash-four"}]""");

    send("get_first_without_token", anonymousWebClient(), HttpMethod.GET, first, null);
    send("cache_hits_without_token", anonymousWebClient(), HttpMethod.POST, "/cache/hits",
        "[{\"id\":\"CHEDA.GB.2026.1000001\",\"value\":\"hash-one\"}]");
    send("admin_info", anonymousWebClient(), HttpMethod.GET, "/admin/info", null);
    givenUserHasPermissions("soaprequest.read");
    send("create_without_permission", webClient(), HttpMethod.POST, "/soaprequest", FIRST);

    assertPayload("audit_rows.txt", rows(
        "SELECT type, entity_type, entity_id, user_id, data FROM soaprequest_audit"
            + " ORDER BY type"));
    assertPayload("cache_rows.txt", rows("SELECT id, value FROM cacherequest ORDER BY id"));
    softly.assertAll();
  }

  private EntityExchangeResult<byte[]> send(
      String step, WebTestClient client, HttpMethod method, String uri, String body) {
    WebTestClient.RequestBodySpec request = client.method(method).uri(uri);
    if (body != null) {
      request.contentType(MediaType.APPLICATION_JSON).bodyValue(body);
    }
    EntityExchangeResult<byte[]> result = request.exchange().expectBody().returnResult();
    HttpHeaders headers = result.getResponseHeaders();
    String responseBody = body(result);
    if (responseBody.contains("\"timestamp\"")) {
      softly.assertThat(responseBody).as("%s timestamp", step).containsPattern(ERROR_TIMESTAMP);
      responseBody = ERROR_TIMESTAMP.matcher(responseBody)
          .replaceAll("\"timestamp\":\"<timestamp>\"");
    }
    assertPayload(step + ".txt", "%d%nContent-Type: %s%nLocation: %s%n%n%s".formatted(
        result.getStatus().value(), headers.getContentType(), headers.getLocation(),
        responseBody));
    return result;
  }

  private static String body(EntityExchangeResult<byte[]> result) {
    byte[] content = result.getResponseBodyContent();
    return content == null ? "" : new String(content, StandardCharsets.UTF_8);
  }

  // Sorted once the generated IDs are masked, since the database's order would depend on them
  private String rows(String sql) {
    return jdbcTemplate.queryForList(sql).stream()
        .map(row -> mask(row.entrySet().stream()
            .map(column -> column.getKey() + "=" + column.getValue())
            .collect(Collectors.joining(" | "))))
        .sorted()
        .collect(Collectors.joining(System.lineSeparator()));
  }

  // Numbers each generated UUID and request ID in the order it first appears
  private String mask(String payload) {
    String masked = replace(UUID, payload, "id");
    return replace(REQUEST_ID, masked, "requestId");
  }

  private String replace(Pattern pattern, String payload, String name) {
    return pattern.matcher(payload).replaceAll(match -> {
      String value = match.group();
      if (value.equalsIgnoreCase(USER) || value.startsWith("00000000-")) {
        return value;
      }
      Map<String, String> named = placeholders.computeIfAbsent(name, key -> new HashMap<>());
      return Matcher.quoteReplacement(named.computeIfAbsent(value.toLowerCase(),
          key -> "<%s-%d>".formatted(name, named.size() + 1)));
    });
  }

  private void assertPayload(String expectedFile, String payload) {
    String actual = mask(payload);
    String expected = null;
    try (InputStream in = PayloadContractIT.class.getClassLoader()
        .getResourceAsStream(PAYLOAD_DIR + expectedFile)) {
      if (in != null) {
        expected = new String(in.readAllBytes(), StandardCharsets.UTF_8);
      }
    } catch (IOException exception) {
      throw new IllegalStateException("Failed to read " + expectedFile, exception);
    }
    if (!actual.equals(expected)) {
      writeActual(expectedFile, actual);
    }
    softly.assertThat(actual).as(expectedFile).isEqualTo(expected);
  }

  private static void writeActual(String name, String actual) {
    try {
      Path dir = Path.of("target", "payload-actual");
      Files.createDirectories(dir);
      Files.writeString(dir.resolve(name), actual);
    } catch (IOException exception) {
      throw new IllegalStateException("Failed to write the actual " + name, exception);
    }
  }
}
