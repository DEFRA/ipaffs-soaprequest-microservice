package uk.gov.defra.tracesx.soaprequest.integration;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.UUID;
import org.junit.Before;
import org.junit.Ignore;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.defra.tracesx.soaprequest.integration.dto.SoapRequestDTO;
import java.io.UnsupportedEncodingException;

public class TestBasicEndpoints {

  public static final String SOAP_REQUEST_ENDPOINT = "soaprequest";
  private static String TEST_USERNAME = "testUser";
  private static String TEST_QUERY = "testQuery";
  private String baseUrl;
  private String resourceUrl;

  private String stripEnvDelims(String src) {
    return src.substring(2, src.length() - 1);
  }

  @Before
  public void setup() throws UnsupportedEncodingException{
    baseUrl = System.getProperty("service.base.url");

    resourceUrl =
        UriComponentsBuilder.newInstance()
            .fromHttpUrl(baseUrl)
            .path(SOAP_REQUEST_ENDPOINT)
            .build()
            .toString();
  }

  @Ignore
  public void canCreateSoapRequest() {
    createSoapRequest(TEST_USERNAME, TEST_QUERY).then().statusCode(201);
  }

  @Ignore
  public void rejectInvalidSoapRequest() {
    given()
        .body("{\"exampleWrong\": \"test\"}")
        .contentType(ContentType.JSON)
        .when()
        .post(resourceUrl)
        .then()
        .statusCode(400);
  }

  @Ignore
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

  @Ignore
  public void getNonExistentSoapRequestReturnsNotFound() {
    getSoapRequestById(getSoapRequestEndpoint() + UUID.randomUUID().toString())
        .then()
        .statusCode(404);
  }
/*
  @Ignore
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
*/
/*
  @Ignore
  public void getNonExistentSoapRequestByRequestIdAndUsernameReturnsNotFound() {
    getSoapRequestByRequestIdAndUsername(123L, "missing").then().statusCode(404);
  }
*/
  @Ignore
  public void canDeleteSoapRequest() {
    String id =
        createSoapRequest(TEST_USERNAME, TEST_QUERY)
            .then()
            .statusCode(201)
            .extract()
            .header("Location");
    deleteSoapRequestById(id).then().statusCode(200);
  }

  @Ignore
  public void deleteNonExistentSoapRequestReturnsNotFound() {
    deleteSoapRequestById(getSoapRequestEndpoint() + UUID.randomUUID().toString())
        .then()
        .statusCode(404);
  }

  private Response createSoapRequest(String username, String query) {
    return given()
        .body("{\"username\": \"" + username + "\", \"query\": \"" + query + "\"}")
        .contentType(ContentType.JSON)
        .when()
        .post(resourceUrl);
  }

  private Response getSoapRequestById(String id) {
    return given().when().get(baseUrl + id);
  }

  private Response deleteSoapRequestById(String id) {
    return given().when().delete(baseUrl + id);
  }
/*
  private Response getSoapRequestByRequestIdAndUsername(Long requestId, String username) {
    return given()
        .when()
        .queryParam("requestId", requestId)
        .queryParam("username", username)
        .get(resourceUrl);
  }
*/
  private String getSoapRequestEndpoint() {
    return "/" + SOAP_REQUEST_ENDPOINT + "/";
  }
}
