package uk.gov.defra.tracesx.soaprequest.integration;

import io.restassured.response.Response;
import org.junit.Test;
import uk.gov.defra.tracesx.soaprequest.integration.properties.Properties;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class TestAdmin {
    private static final String ACCEPT = "Accept";
    private static final String APPLICATION_JSON = "application/json";
    private static final String ADMIN_HEALTH_REQUEST_ENDPOINT = "%s/admin/health-check";
    private static final String ADMIN_INFO_REQUEST_ENDPOINT = "%s/admin/info";

    @Test
    public void testAdminHealthWithNoAuthentication() {
        String url = String.format(ADMIN_HEALTH_REQUEST_ENDPOINT, Properties.SERVICE_BASE_URL);

        Response response = given().header(ACCEPT, APPLICATION_JSON).when().get(url);

        response.then().statusCode(200);
        assertThat(response.body().asString()).contains("status");
    }

    @Test
    public void testAdminInfoWithNoAuthentication() {
        String url = String.format(ADMIN_INFO_REQUEST_ENDPOINT, Properties.SERVICE_BASE_URL);

        Response response = given().header(ACCEPT, APPLICATION_JSON).when().get(url);

        response.then().statusCode(200);
        assertThat(response.body().asString()).contains("version");
    }
}
