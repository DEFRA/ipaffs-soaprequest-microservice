package uk.gov.defra.tracesx.soaprequest.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class InsSecurityExceptionTest {

  @Test
  public void instanceContainsMessageWhenCreated() {
    InsSecurityException instance = new InsSecurityException("test_message");
    assertEquals(instance.getMessage(), "test_message");
  }

}
