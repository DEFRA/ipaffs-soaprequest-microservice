package uk.gov.defra.tracesx.soaprequest;

import static java.util.Arrays.asList;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import uk.gov.defra.tracesx.soaprequest.security.jwks.JwksConfiguration;

@Getter
@Configuration
@EnableConfigurationProperties
@EnableJpaRepositories("uk.gov.defra.tracesx.soaprequest.dao.repositories")
public class SoapRequestCofiguration implements WebMvcConfigurer {

  public static final int SOAP_REQUEST_ORDER = 1;

  @Value("${spring.security.jwt.jwks}")
  private String jwkUrl;

  @Value("${spring.security.jwt.iss}")
  private String iss;

  @Value("${spring.security.jwt.clientId}")
  private String clientId;

  @Bean
  @Qualifier("jwksConfiguration")
  public List<JwksConfiguration> jwksConfiguration() throws MalformedURLException {
    String[] jwkUrls = jwkUrl.split(",");
    String[] issuers = iss.split(",");
    List<JwksConfiguration> jwksConfigurations = new ArrayList<>();
    if (jwkUrls.length == issuers.length) {
      for (int i = 0; i < jwkUrls.length; i++) {
        jwksConfigurations.add(JwksConfiguration.builder()
            .jwksUrl(new URL(jwkUrls[i]))
            .issuer(issuers[i])
            .clientId(clientId).build());
      }
      return Collections.unmodifiableList(jwksConfigurations);
    } else {
      throw new RuntimeException(
          "The comma-separated properties spring.security.jwt.[jwks and iss] must all have the same number of elements.");
    }
  }

}