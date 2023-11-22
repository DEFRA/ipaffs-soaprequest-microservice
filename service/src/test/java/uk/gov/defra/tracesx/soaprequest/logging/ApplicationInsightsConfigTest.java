package uk.gov.defra.tracesx.soaprequest.logging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import javax.servlet.Filter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

@ExtendWith(MockitoExtension.class)
class ApplicationInsightsConfigTest {

  private static final String APPLICATIONINSIGHTS_CONNECTION_STRING =
      "APPLICATIONINSIGHTS_CONNECTION_STRING";
  private static final String APPLICATIONINSIGHTS_CONNECTION_STRING_VALUE =
      "InstrumentationKey=00000000-0000-0000-0000-000000000000";
  private static final String APPLICATION_NAME = "soaprequest-microservice";

  private static final String BLANK = "";
  @Mock
  private Environment environment;
  @InjectMocks
  private ApplicationInsightsConfig underTest;

  @Test
  void whenEnvHasVariableSetThenTheResultContainsValue() {

    when(environment.getProperty(APPLICATIONINSIGHTS_CONNECTION_STRING))
        .thenReturn(APPLICATIONINSIGHTS_CONNECTION_STRING_VALUE);
    String result = underTest.telemetryConfig(environment);
    assertThat(result).isEqualTo(APPLICATIONINSIGHTS_CONNECTION_STRING_VALUE);
  }

  @Test
  void whenEnvHasVariableSetToBlankThenTheResultDoesntContainValue() {

    when(environment.getProperty(APPLICATIONINSIGHTS_CONNECTION_STRING)).thenReturn(BLANK);
    String result = underTest.telemetryConfig(environment);
    assertThat(result).isEqualTo(BLANK);
  }

  @Test
  void whenEnvHasVariableNotSetThenTheResultDoesntContainValue() {

    when(environment.getProperty(APPLICATIONINSIGHTS_CONNECTION_STRING)).thenReturn(null);
    String result = underTest.telemetryConfig(environment);
    assertThat(result).isNull();
  }

  @Test
  void filterRegistrationBeanHasCatchAllUrl() {
    //When
    FilterRegistrationBean<Filter> filterRegistration = underTest.aiFilterRegistration(
        APPLICATION_NAME);

    //Then
    assertThat(filterRegistration.getUrlPatterns()).hasSize(1);
    assertThat(filterRegistration.getUrlPatterns().iterator().next()).isEqualTo("/*");
  }

  @Test
  void filterRegistrationBeanHasHighOrder() {
    //When
    FilterRegistrationBean<Filter> filterRegistration = underTest.aiFilterRegistration(
        APPLICATION_NAME);

    //Then
    assertThat(filterRegistration.getOrder()).isEqualTo(Ordered.HIGHEST_PRECEDENCE + 10);
  }

  @Test
  void shouldCreateNewWebRequestTrackingFilter() {
    Filter webRequestTrackingFilter = underTest.webRequestTrackingFilter(APPLICATION_NAME);
    assertThat(webRequestTrackingFilter).isNotNull();
  }
}
