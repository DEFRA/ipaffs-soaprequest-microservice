package uk.gov.defra.tracesx.soaprequest;

import static java.util.Arrays.asList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.defra.tracesx.soaprequest.security.PreAuthorizeChecker;
import uk.gov.defra.tracesx.soaprequest.security.SoapRequestAuthFilter;

@Configuration
@EnableConfigurationProperties
@EnableJpaRepositories("uk.gov.defra.tracesx.soaprequest.dao.repositories")
public class SoapRequestConfiguration implements WebMvcConfigurer {

  private static final String BASE_URL_MATCHER = "/soaprequest/*";
  private static final String SOAP_REQUEST_URL_MATCHER = "/soaprequest";
  public static final String SOAP_REQUEST_AUTH_FILTER = "soapRequestAuthFilter";
  public static final int SOAP_REQUEST_ORDER = 1;

  @Value("${permissions.service.connectionTimeout}")
  private int permissionsServiceConnectionTimeout;

  @Value("${permissions.service.readTimeout}")
  private int permissionsServiceReadTimeout;

  @Value("${permissions.service.user}")
  private String permissionsServiceUser;

  @Value("${permissions.service.password}")
  private String permissionsServicePassword;

  @Autowired
  private SoapRequestAuthFilter soapRequestAuthFilter;

  @Bean(name = "permissionsRestTemplate")
  public RestTemplate permissionsRestTemplate() {
    return createRestTemplate(
        permissionsServiceConnectionTimeout,
        permissionsServiceReadTimeout,
        permissionsServiceUser,
        permissionsServicePassword);
  }

  private RestTemplate createRestTemplate(
      final int connectionTimeout,
      final int readTimeout,
      final String serviceUser,
      final String servicePassword) {

    final HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
    clientHttpRequestFactory.setConnectTimeout(connectionTimeout);
    clientHttpRequestFactory.setReadTimeout(readTimeout);

    final RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
    restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(serviceUser, servicePassword));
    restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

    return restTemplate;
  }

  @Bean
  public FilterRegistrationBean commodityCategoryAuthFilterRegistration() {

    FilterRegistrationBean result = new FilterRegistrationBean();
    result.setFilter(soapRequestAuthFilter);
    result.setUrlPatterns(asList(BASE_URL_MATCHER, SOAP_REQUEST_URL_MATCHER));
    result.setName(SOAP_REQUEST_AUTH_FILTER);
    result.setOrder(SOAP_REQUEST_ORDER);
    return result;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new PreAuthorizeChecker())
        .addPathPatterns(new String[] {BASE_URL_MATCHER, SOAP_REQUEST_URL_MATCHER});
  }
}
