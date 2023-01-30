package uk.gov.defra.tracesx.soaprequest.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class NotImplementedExceptionTest {

  @Test
  public void instanceShouldContainExceptionMessageWhenCreated() {
    NotImplementedException instance = new NotImplementedException("test_message");
    assertEquals(instance.getMessage(), "test_message");
  }

}
