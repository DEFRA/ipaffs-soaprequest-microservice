package uk.gov.defra.tracesx.soaprequest.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static uk.gov.defra.tracesx.common.security.tests.jwt.JwtConstants.BEARER;
import static uk.gov.defra.tracesx.soaprequest.integration.properties.Properties.SERVICE_BASE_URL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.defra.tracesx.common.security.tests.jwt.SelfSignedTokenClient;
import uk.gov.defra.tracesx.soaprequest.integration.dto.SoapRequestDTO;

class TestBasicEndpoints {

  private static final String LOCATION = "Location";
  private static final String[] READ_ROLES = {"soap"};
  private static final String SOAP_REQUEST_ENDPOINT = "soaprequest";
  private static final String TEST_USERNAME = "testUser";
  private static final String TEST_QUERY = "testQuery";
  private static final String ROLES_CLAIM = "roles";

  private String baseUrl;
  private String resourceUrl;
  private SelfSignedTokenClient selfSignedTokenClient;

  @BeforeEach
  public void setup() {
    baseUrl = SERVICE_BASE_URL;

    resourceUrl =
        UriComponentsBuilder
            .fromHttpUrl(baseUrl)
            .path(SOAP_REQUEST_ENDPOINT)
            .build()
            .toString();

    selfSignedTokenClient = new SelfSignedTokenClient();
  }

  @Test
  void canCreateSoapRequest() {
    createSoapRequest().then().statusCode(201);
  }

  @Test
  void rejectInvalidSoapRequest() {
    given()
        .body("{\"exampleWrong\": \"test\"}")
        .header(AUTHORIZATION,
            BEARER + selfSignedTokenClient.getTokenWithClaim(SelfSignedTokenClient.TokenType.AD,
                ROLES_CLAIM, READ_ROLES))
        .contentType(ContentType.JSON)
        .when()
        .post(resourceUrl)
        .then()
        .statusCode(400);
  }

  @Test
  void canGetSoapRequest() {
    String id =
        createSoapRequest()
            .then()
            .statusCode(201)
            .extract()
            .header(LOCATION);

    getSoapRequestById(id).then().statusCode(200);
  }

  @Test
  void getNonExistentSoapRequestReturnsNotFound() {
    getSoapRequestById(getSoapRequestEndpoint() + UUID.randomUUID())
        .then()
        .statusCode(404);
  }

  @Test
  void canGetSoapRequestByRequestIdAndUsername() {
    String id =
        createSoapRequest()
            .then()
            .statusCode(201)
            .extract()
            .header(LOCATION);
    Response soapRequestResponse = getSoapRequestById(id);
    SoapRequestDTO soapRequest = soapRequestResponse.body().as(SoapRequestDTO.class);

    Response response =
        getSoapRequestByRequestIdAndUsername(soapRequest.getRequestId(), soapRequest.getUsername());
    response.then().statusCode(200);

    List<SoapRequestDTO> result = response.as(new TypeRef<>() {});

    assertThat(result).singleElement().isEqualTo(soapRequest);
  }

  @Test
  void getNonExistentSoapRequestByRequestIdAndUsernameReturnsNotFound() {
    getSoapRequestByRequestIdAndUsername(123L, "missing").then().statusCode(404);
  }

  @Test
  void canDeleteSoapRequest() {
    String id =
        createSoapRequest()
            .then()
            .statusCode(201)
            .extract()
            .header(LOCATION);
    deleteSoapRequestById(id).then().statusCode(200);
  }

  @Test
  void deleteNonExistentSoapRequestReturnsNotFound() {
    deleteSoapRequestById(getSoapRequestEndpoint() + UUID.randomUUID())
        .then()
        .statusCode(404);
  }

  private Response createSoapRequest() {
    return given()
        .body("{\"username\": \"" + TestBasicEndpoints.TEST_USERNAME + "\", \"query\": \"" + TestBasicEndpoints.TEST_QUERY
            + "\"}")
        .header(AUTHORIZATION,
            BEARER + selfSignedTokenClient.getTokenWithClaim(SelfSignedTokenClient.TokenType.AD,
                ROLES_CLAIM, READ_ROLES))
        .contentType(ContentType.JSON)
        .when()
        .post(resourceUrl);
  }

  private Response getSoapRequestById(String id) {
    return given()
        .header(AUTHORIZATION,
            BEARER + selfSignedTokenClient.getTokenWithClaim(SelfSignedTokenClient.TokenType.AD,
                ROLES_CLAIM, READ_ROLES))
        .when()
        .get(baseUrl + id);
  }

  private Response deleteSoapRequestById(String id) {
    return given()
        .header(AUTHORIZATION,
            BEARER + selfSignedTokenClient.getTokenWithClaim(SelfSignedTokenClient.TokenType.AD,
                ROLES_CLAIM, READ_ROLES))
        .when()
        .delete(baseUrl + id);
  }

  private Response getSoapRequestByRequestIdAndUsername(Long requestId, String username) {
    return given()
        .header(AUTHORIZATION,
            BEARER + selfSignedTokenClient.getTokenWithClaim(SelfSignedTokenClient.TokenType.AD,
                ROLES_CLAIM, READ_ROLES))
        .when()
        .queryParam("requestId", requestId)
        .queryParam("username", username)
        .get(resourceUrl);
  }

  private String getSoapRequestEndpoint() {
    return "/" + SOAP_REQUEST_ENDPOINT + "/";
  }
}
