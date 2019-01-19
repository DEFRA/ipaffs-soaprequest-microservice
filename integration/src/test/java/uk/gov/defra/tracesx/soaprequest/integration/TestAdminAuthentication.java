package uk.gov.defra.tracesx.soaprequest.integration;

import static io.restassured.RestAssured.given;

import io.restassured.http.ContentType;
import java.io.UnsupportedEncodingException;
import org.junit.Before;
import org.junit.Test;

public class TestAdminAuthentication {

  private String baseUrl;

  @Before
  public void setup() throws UnsupportedEncodingException {
    baseUrl = System.getProperty("service.base.url");
  }

  @Test
  public void callAdmin_withoutBasicAuth_respondsWith200Success() {
    given()
        .contentType(ContentType.JSON)
        .when()
        .get(baseUrl+ "/admin/info")
        .then()
        .statusCode(200);
  }

}

