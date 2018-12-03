package uk.gov.defra.tracesx.soaprequest.integration;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.defra.tracesx.soaprequest.integration.dto.SoapRequestDTO;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import static java.nio.charset.StandardCharsets.UTF_8;

public class TestBasicEndpoints {

  public static final String SOAP_REQUEST_ENDPOINT = "soaprequest";
  private static String TEST_USERNAME = "testUser";
  private static String TEST_QUERY = "testQuery";
  private String baseUrl;
  private String resourceUrl;
  private String userName;
  private String password;
  public static final String COLON = ":";
  public static final String BASIC = "Basic ";
  private String encodedBasicAuth;

  private String stripEnvDelims(String src) {
    return src.substring(2, src.length() - 1);
  }

  @Before
  public void setup() throws UnsupportedEncodingException{
    userName = System.getProperty("auth.username");
    password = System.getProperty("auth.password");
    baseUrl = System.getProperty("service.base.url");

    resourceUrl =
        UriComponentsBuilder.newInstance()
            .fromHttpUrl(baseUrl)
            .path(SOAP_REQUEST_ENDPOINT)
            .build()
            .toString();

    encodedBasicAuth = BASIC + Base64.getEncoder().encodeToString(
            new StringBuffer().append(userName).append(COLON).append(password).toString().getBytes(
                    UTF_8.name()));

  }

  @Test
  public void canCreateSoapRequest() {
    createSoapRequest(TEST_USERNAME, TEST_QUERY).then().statusCode(201);
  }

  @Test
  public void rejectInvalidSoapRequest() {
    given().header("x-auth-basic", encodedBasicAuth)
        .body("{\"exampleWrong\": \"test\"}")
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
            .header("Location");
    ;
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
            .header("Location");
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
            .header("Location");
    deleteSoapRequestById(id).then().statusCode(200);
  }

  @Test
  public void deleteNonExistentSoapRequestReturnsNotFound() {
    deleteSoapRequestById(getSoapRequestEndpoint() + UUID.randomUUID().toString())
        .then()
        .statusCode(404);
  }

  private Response createSoapRequest(String username, String query) {
    return given().header("x-auth-basic",encodedBasicAuth)
        .body("{\"username\": \"" + username + "\", \"query\": \"" + query + "\"}")
        .contentType(ContentType.JSON)
        .when()
        .post(resourceUrl);
  }

  private Response getSoapRequestById(String id) {
    return given().header("x-auth-basic",encodedBasicAuth).when().get(baseUrl + id);
  }

  private Response deleteSoapRequestById(String id) {
    return given().header("x-auth-basic",encodedBasicAuth).when().delete(baseUrl + id);
  }

  private Response getSoapRequestByRequestIdAndUsername(Long requestId, String username) {
    return given().header("x-auth-basic",encodedBasicAuth)
        .when()
        .queryParam("requestId", requestId)
        .queryParam("username", username)
        .get(resourceUrl);
  }

  private String getSoapRequestEndpoint() {
    return "/" + SOAP_REQUEST_ENDPOINT + "/";
  }
}
