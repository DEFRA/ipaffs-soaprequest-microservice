package uk.gov.defra.tracesx.soaprequest.dao.entities;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SoapRequestTest {

  private static final String USERNAME = "username";
  private static final String QUERY = "test";

  @Test
  void verifyProperties() {
    // Given
    SoapRequest instance = new SoapRequest(USERNAME, QUERY);
    Long requestId = 123L;

    //When
    instance.setRequestId(requestId);

    //Then
    assertThat(instance.getRequestId()).isEqualTo(requestId);
    assertThat(instance.getUsername()).isEqualTo(USERNAME);
    assertThat(instance.getQuery()).isEqualTo(QUERY);
  }

  @Test
  void verifySetUsernameAndQuery() {
    // Given
    SoapRequest instance = new SoapRequest();

    //When
    instance.setUsername(USERNAME);
    instance.setQuery(QUERY);

    //Then
    assertThat(instance.getUsername()).isEqualTo(USERNAME);
    assertThat(instance.getQuery()).isEqualTo(QUERY);
  }
}
