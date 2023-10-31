package uk.gov.defra.tracesx.soaprequest.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class SoapRequestServiceUrlPatternsTest {

  @Test
  void shouldReturnURLPatternWhenCalled() {
    // Given
    SoapRequestServiceUrlPatterns instance = new SoapRequestServiceUrlPatterns();

    //When
    List<String> actual = instance.getAuthorizedPatterns();
    List<String> expected = Collections.singletonList("/soaprequest/**");

    //Then
    assertThat(actual).isEqualTo(expected);
  }
}
