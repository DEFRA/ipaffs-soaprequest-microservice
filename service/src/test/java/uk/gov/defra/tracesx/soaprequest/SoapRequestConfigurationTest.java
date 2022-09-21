package uk.gov.defra.tracesx.soaprequest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class SoapRequestConfigurationTest {

  @Test
  public void instanceShouldNotBeNullWhenCreated() {
    SoapRequestConfiguration instance = new SoapRequestConfiguration();
    assertNotNull(instance);
  }

}
