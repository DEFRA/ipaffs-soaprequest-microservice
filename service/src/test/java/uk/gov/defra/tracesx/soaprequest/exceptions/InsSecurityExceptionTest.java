package uk.gov.defra.tracesx.soaprequest.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class InsSecurityExceptionTest {

  @Test
  void instanceContainsMessageWhenCreated() {
    InsSecurityException instance = new InsSecurityException("test_message");
    assertThat(instance.getMessage()).isEqualTo("test_message");
  }
}
