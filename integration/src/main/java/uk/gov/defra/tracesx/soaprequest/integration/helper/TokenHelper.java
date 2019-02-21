package uk.gov.defra.tracesx.soaprequest.integration.helper;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static uk.gov.defra.tracesx.soaprequest.integration.helper.JwtConstants.EXP;
import static uk.gov.defra.tracesx.soaprequest.integration.helper.JwtConstants.NAME;
import static uk.gov.defra.tracesx.soaprequest.integration.helper.JwtConstants.UNIQUE_NAME;
import static uk.gov.defra.tracesx.soaprequest.integration.helper.JwtConstants.ROLES;
import static uk.gov.defra.tracesx.soaprequest.integration.helper.JwtConstants.UPN;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import uk.gov.defra.tracesx.soaprequest.integration.properties.Properties;

public class TokenHelper {

  private TokenHelper() {}

  private static final ObjectMapper objectMapper = new ObjectMapper();

  private static final String TEST_OPENID_BASIC;

  static {
    String encodedBasicAuth =
      Base64.getEncoder()
        .encodeToString(
          (Properties.TEST_OPENID_TOKEN_SERVICE_AUTH_USERNAME + ":"
            + Properties.TEST_OPENID_TOKEN_SERVICE_AUTH_PASSWORD).getBytes(StandardCharsets.UTF_8));
    TEST_OPENID_BASIC = "Basic " + encodedBasicAuth;
  }

  // intentionally cached, do not access directly
  private static Map<String, Object> claims = null;

  private static synchronized Map<String, Object> getClaims() {
    if (null == claims) {
      Response response = given()
        .header(AUTHORIZATION, TEST_OPENID_BASIC)
        .when()
        .get(Properties.TEST_OPENID_TOKEN_SERVICE_URL + "/claims");
      response
        .then()
        .statusCode(200);
      claims = Collections.unmodifiableMap(response.getBody().as(Map.class));
    }
    return claims;
  }

  private static String createValidTokenBody(Map<String, Object> additionalClaims) {
    long exp = LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC).toEpochMilli() / 1000L;
    Map<String, Object> body = new HashMap<>(getClaims());
    body.put(EXP, exp);
    body.put(NAME, "Test User");
    body.put(UPN, "test.user@test-openid.com");
    body.put(UNIQUE_NAME, "Soap Service");
    body.putAll(additionalClaims);
    try {
      return objectMapper.writeValueAsString(body);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public static String getValidToken(String... roles) {
    Map<String, Object> additionalClaims = new HashMap<>();
    additionalClaims.put(ROLES, Arrays.asList(roles));
    Response response = given()
      .header(AUTHORIZATION, TEST_OPENID_BASIC)
      .when()
      .body(createValidTokenBody(additionalClaims))
      .post(Properties.TEST_OPENID_TOKEN_SERVICE_URL + "/sign");
    response.then()
      .statusCode(200);
    return response.getBody().asString();
  }
}
