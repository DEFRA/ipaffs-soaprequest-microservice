package uk.gov.defra.tracesx.soaprequest.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class JsonPatchExceptionTest {

  @Test
  public void instanceShouldContainExceptionMessageWhenCreated() {
    JsonPatchException instance = new JsonPatchException("test_message");
    assertEquals(instance.getMessage(), "test_message");
  }

  @Test
  public void instanceShouldContainExceptionMessageAndCauseWhenCreated() {
    Throwable cause = new Throwable();
    JsonPatchException instance = new JsonPatchException("test_message", cause);
    assertEquals(instance.getMessage(), "test_message");
    assertEquals(instance.getCause(), cause);
  }

}
