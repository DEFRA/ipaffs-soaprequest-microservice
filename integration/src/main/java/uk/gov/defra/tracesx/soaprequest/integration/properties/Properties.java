package uk.gov.defra.tracesx.soaprequest.integration.properties;

/**
 * This is properties class.
 */
public final class Properties {

  /**
   * This is the constructor.
   */
  private Properties() {
  }

  public static final String SERVICE_BASE_URL = System
      .getProperty("service.base.url", "http://localhost:5260");
}
