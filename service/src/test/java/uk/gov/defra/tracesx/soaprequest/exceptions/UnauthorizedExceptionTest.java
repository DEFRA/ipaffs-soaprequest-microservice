package uk.gov.defra.tracesx.soaprequest.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UnauthorizedExceptionTest {

  @Test
  void instanceShouldContainExceptionMessageWhenCreated() {
    UnauthorizedException instance = new UnauthorizedException("test_message");
    assertThat(instance.getMessage()).isEqualTo("test_message");
  }
}
