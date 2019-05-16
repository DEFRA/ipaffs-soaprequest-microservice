package uk.gov.defra.tracesx.soaprequest.integration.properties;

import org.apache.commons.lang3.StringUtils;

/**
 * This is properties class.
 */
public final class Properties {

  /**
   * This is the constructor.
   */
  private Properties() {
  }

  /**
   * TEST_OPENID_TOKEN_SERVICE_URL.
   * @param property
   * @param envKey
   */
  public static final String TEST_OPENID_TOKEN_SERVICE_URL = getPropertyOrEnv(
    "test.openid.service.url", "TEST_OPENID_TOKEN_SERVICE_URL");
  /**
   * TEST_OPENID_TOKEN_SERVICE_AUTH_USERNAME.
   * @param property
   * @param envKey
   */
  public static final String TEST_OPENID_TOKEN_SERVICE_AUTH_USERNAME =
          getPropertyOrEnv(
    "test.openid.service.auth.username",
                  "TEST_OPENID_TOKEN_SERVICE_AUTH_USERNAME");
  /**
   * TEST_OPENID_TOKEN_SERVICE_AUTH_PASSWORD.
   * @param property
   * @param envKey
   */
  public static final String TEST_OPENID_TOKEN_SERVICE_AUTH_PASSWORD =
          getPropertyOrEnv(
    "test.openid.service.auth.password",
                  "TEST_OPENID_TOKEN_SERVICE_AUTH_PASSWORD");
  public static final String SERVICE_BASE_URL = System
    .getProperty("service.base.url", "http://localhost:4000");

  /**
   * getPropertyOrEnv.
   * @param property propertyName.
   * @param envKey envKeyName.
   * @return Get the property.
   */
  private static String getPropertyOrEnv(final String property,
                                         final String envKey) {
    String value = System.getProperty(property);
    if (StringUtils.isEmpty(value)) {
      value = System.getenv(envKey);
    }
    return value;
  }

}
