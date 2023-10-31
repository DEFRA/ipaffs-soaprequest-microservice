package uk.gov.defra.tracesx.soaprequest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class SoapRequestConfigurationTest {

  @Test
  void instanceShouldNotBeNullWhenCreated() {
    SoapRequestConfiguration instance = new SoapRequestConfiguration();
    assertNotNull(instance);
  }
}
