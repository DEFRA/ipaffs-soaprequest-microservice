package uk.gov.defra.tracesx.soaprequest.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.defra.tracesx.soaprequest.integration.properties.Properties.SERVICE_BASE_URL;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

class TestRoot {

    private static final String ROOT_REQUEST_ENDPOINT = "%s/";

    @Test
    void testRootWithNoAuthentication() {
        String url = String.format(ROOT_REQUEST_ENDPOINT, SERVICE_BASE_URL);

        Response response = given().header("Accept", "application/json").when().get(url);

        response.then().statusCode(200);
        assertThat(response.body().asString()).isEmpty();
    }
}
