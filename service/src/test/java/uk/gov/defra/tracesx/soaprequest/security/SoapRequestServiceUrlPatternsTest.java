package uk.gov.defra.tracesx.soaprequest.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

public class SoapRequestServiceUrlPatternsTest {

  @Test
  public void shouldReturnURLPatternWhenCalled() {
    // Given
    SoapRequestServiceUrlPatterns instance = new SoapRequestServiceUrlPatterns();

    //When
    List<String> actual = instance.getAuthorizedPatterns();
    List<String> expected = Collections.singletonList("/soaprequest/**");

    //Then
    assertEquals(actual, expected);
  }

}
