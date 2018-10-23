package uk.gov.defra.tracesx.soaprequest;

import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.util.UriComponentsBuilder;

public class TestBasicEndpoints {

  private String baseUrl;
  private String resourceUrl;
  private String userName;
  private String password;
  
  private String stripEnvDelims(String src) {
    return src.substring(2, src.length() - 1);
  }
  
  @Before
  public void setup() {
    userName = System.getProperty("basicAuthUserName");
    password = System.getProperty("basicAuthPassword");

    baseUrl = System.getProperty("baseUrl");
    resourceUrl = UriComponentsBuilder.newInstance().fromHttpUrl(baseUrl).path("soapRequest").build().toString();
  }
  
  @Test
  public void canCreateDocument(){

    given()
      .auth().basic(userName, password)
      .body("{\"soapRequest\": \"test\"}")
      .contentType(ContentType.JSON)
    .when()
      .post(resourceUrl)
    .then().statusCode(201);
  }
  
  @Test
  public void rejectsInvalidDocument() {
    given()
      .auth().basic(userName, password)            
      .body("{\"exampleWrong\": \"test\"}")
      .contentType(ContentType.JSON)
    .when()
      .post(resourceUrl)
    .then()
      .statusCode(400);
  }
  
   @Test
  public void canGetDocument() {
    String getId = given()
      .body("{\"soapRequest\": \"test\"}")
      .auth().basic(userName, password)
      .contentType(ContentType.JSON)
    .when()
      .post(resourceUrl)
    .then()
        .statusCode(201).
        extract().header("Location");

    given()
      .auth().basic(userName, password)
     .when()
      .get(baseUrl + getId)
    .then()
      .statusCode(200);
  }

  @Test()
  public void patchShouldReturn501() {

    String patchId = given()
      .body("{\"soapRequest\": \"test\"}")
      .auth().basic(userName, password)
      .contentType(ContentType.JSON)
    .when()
      .post(resourceUrl)
    .then()
        .statusCode(201).
        extract().header("Location");

    given()
      .auth().basic(userName, password)
      .body("[{\"op\":\"replace\",\"path\":\"/soapRequest\",\"value\":\"TEST\"}]")
      .contentType("application/merge-patch+json")
    .when()
      .patch(baseUrl + patchId)
      .then()
    .statusCode(501);
  }
}
