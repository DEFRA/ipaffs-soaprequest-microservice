package uk.gov.defra.tracesx.soaprequest.integration.properties;

import org.apache.commons.lang3.StringUtils;

public class Properties {

  private Properties() {
  }

  public static final String TEST_OPENID_TOKEN_SERVICE_URL = getPropertyOrEnv(
    "test.openid.service.url", "TEST_OPENID_TOKEN_SERVICE_URL");
  public static final String TEST_OPENID_TOKEN_SERVICE_AUTH_USERNAME = getPropertyOrEnv(
    "test.openid.service.auth.username", "TEST_OPENID_TOKEN_SERVICE_AUTH_USERNAME");
  public static final String TEST_OPENID_TOKEN_SERVICE_AUTH_PASSWORD = getPropertyOrEnv(
    "test.openid.service.auth.password", "TEST_OPENID_TOKEN_SERVICE_AUTH_PASSWORD");
  public static final String SERVICE_BASE_URL = System
    .getProperty("service.base.url", "http://localhost:4000");

  private static String getPropertyOrEnv(String property, String envKey) {
    String value = System.getProperty(property);
    if (StringUtils.isEmpty(value)) {
      value = System.getenv(envKey);
    }
    return value;
  }

}
