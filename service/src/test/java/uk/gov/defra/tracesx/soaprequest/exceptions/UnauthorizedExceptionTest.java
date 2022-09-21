package uk.gov.defra.tracesx.soaprequest.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class UnauthorizedExceptionTest {

  @Test
  public void instanceShouldContainExceptionMessageWhenCreated() {
    UnauthorizedException instance = new UnauthorizedException("test_message");
    assertEquals(instance.getMessage(), "test_message");
  }

}
