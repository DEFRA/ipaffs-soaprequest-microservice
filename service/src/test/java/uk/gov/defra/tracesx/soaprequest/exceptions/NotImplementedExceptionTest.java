package uk.gov.defra.tracesx.soaprequest.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class NotImplementedExceptionTest {

  @Test
  void instanceShouldContainExceptionMessageWhenCreated() {
    NotImplementedException instance = new NotImplementedException("test_message");
    assertThat(instance.getMessage()).isEqualTo("test_message");
  }
}
