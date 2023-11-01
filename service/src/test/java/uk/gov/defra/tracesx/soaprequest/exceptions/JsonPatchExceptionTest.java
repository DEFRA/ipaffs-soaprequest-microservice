package uk.gov.defra.tracesx.soaprequest.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class JsonPatchExceptionTest {

  @Test
  void instanceShouldContainExceptionMessageWhenCreated() {
    JsonPatchException instance = new JsonPatchException("test_message");
    assertThat(instance.getMessage()).isEqualTo("test_message");
  }

  @Test
  void instanceShouldContainExceptionMessageAndCauseWhenCreated() {
    Throwable cause = new Throwable();
    JsonPatchException instance = new JsonPatchException("test_message", cause);
    assertThat(instance.getMessage()).isEqualTo("test_message");
    assertThat(instance.getCause()).isEqualTo(cause);
  }
}
