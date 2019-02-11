package uk.gov.defra.tracesx.soaprequest.integration;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static uk.gov.defra.tracesx.soaprequest.integration.helper.JwtConstants.BEARER;
import static uk.gov.defra.tracesx.soaprequest.integration.properties.Properties.SERVICE_BASE_URL;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.defra.tracesx.soaprequest.integration.dto.SoapRequestDTO;
import uk.gov.defra.tracesx.soaprequest.integration.helper.TokenHelper;

public class TestBasicEndpoints {

  private static final String LOCATION = "Location";
  private static final String[] READ_ROLES = {"soap"};
  private static final String SOAP_REQUEST_ENDPOINT = "soaprequest";
  private static final String TEST_USERNAME = "testUser";
  private static final String TEST_QUERY = "testQuery";
  private String baseUrl;
  private String resourceUrl;

  @Before
  public void setup() {
    baseUrl = SERVICE_BASE_URL;

    resourceUrl =
      UriComponentsBuilder
        .fromHttpUrl(baseUrl)
        .path(SOAP_REQUEST_ENDPOINT)
        .build()
        .toString();
  }

  @Test
  public void canCreateSoapRequest() {
    createSoapRequest(TEST_USERNAME, TEST_QUERY).then().statusCode(201);
  }

  @Test
  public void rejectInvalidSoapRequest() {
    given()
      .body("{\"exampleWrong\": \"test\"}")
      .header(AUTHORIZATION, BEARER + TokenHelper.getValidToken(READ_ROLES))
      .contentType(ContentType.JSON)
      .when()
      .post(resourceUrl)
      .then()
      .statusCode(400);
  }

  @Test
  public void canGetSoapRequest() {
    String id =
      createSoapRequest(TEST_USERNAME, TEST_QUERY)
        .then()
        .statusCode(201)
        .extract()
        .header(LOCATION);

    getSoapRequestById(id).then().statusCode(200);
  }

  @Test
  public void getNonExistentSoapRequestReturnsNotFound() {
    getSoapRequestById(getSoapRequestEndpoint() + UUID.randomUUID().toString())
      .then()
      .statusCode(404);
  }

  @Test
  public void canGetSoapRequestByRequestIdAndUsername() {
    String id =
      createSoapRequest(TEST_USERNAME, TEST_QUERY)
        .then()
        .statusCode(201)
        .extract()
        .header(LOCATION);
    Response soapRequestResponse = getSoapRequestById(id);
    SoapRequestDTO soapRequest = soapRequestResponse.body().as(SoapRequestDTO.class);

    Response response =
      getSoapRequestByRequestIdAndUsername(soapRequest.getRequestId(), soapRequest.getUsername());
    response.then().statusCode(200);
    SoapRequestDTO result = response.body().as(SoapRequestDTO.class);

    assertEquals(result.getId(), soapRequest.getId());
    assertEquals(result.getRequestId(), soapRequest.getRequestId());
    assertEquals(result.getUsername(), soapRequest.getUsername());
    assertEquals(result.getQuery(), soapRequest.getQuery());
  }

  @Test
  public void getNonExistentSoapRequestByRequestIdAndUsernameReturnsNotFound() {
    getSoapRequestByRequestIdAndUsername(123L, "missing").then().statusCode(404);
  }

  @Test
  public void canDeleteSoapRequest() {
    String id =
      createSoapRequest(TEST_USERNAME, TEST_QUERY)
        .then()
        .statusCode(201)
        .extract()
        .header(LOCATION);
    deleteSoapRequestById(id).then().statusCode(200);
  }

  @Test
  public void deleteNonExistentSoapRequestReturnsNotFound() {
    deleteSoapRequestById(getSoapRequestEndpoint() + UUID.randomUUID().toString())
      .then()
      .statusCode(404);
  }

  private Response createSoapRequest(String username, String query) {
    return given()
      .body("{\"username\": \"" + username + "\", \"query\": \"" + query + "\"}")
      .header(AUTHORIZATION, BEARER + TokenHelper.getValidToken(READ_ROLES))
      .contentType(ContentType.JSON)
      .when()
      .post(resourceUrl);
  }

  private Response getSoapRequestById(String id) {
    return given()
      .header(AUTHORIZATION, BEARER + TokenHelper.getValidToken(READ_ROLES))
      .when()
      .get(baseUrl + id);
  }

  private Response deleteSoapRequestById(String id) {
    return given()
      .header(AUTHORIZATION, BEARER + TokenHelper.getValidToken(READ_ROLES))
      .when()
      .delete(baseUrl + id);
  }

  private Response getSoapRequestByRequestIdAndUsername(Long requestId, String username) {
    return given()
      .header(AUTHORIZATION, BEARER + TokenHelper.getValidToken(READ_ROLES))
      .when()
      .queryParam("requestId", requestId)
      .queryParam("username", username)
      .get(resourceUrl);
  }

  private String getSoapRequestEndpoint() {
    return "/" + SOAP_REQUEST_ENDPOINT + "/";
  }
}
