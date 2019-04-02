package uk.gov.defra.tracesx.soaprequest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.defra.tracesx.soaprequest.security.jwks.JwksConfiguration;

@Getter
@Configuration
@EnableConfigurationProperties
@EnableJpaRepositories("uk.gov.defra.tracesx.soaprequest.dao.repositories")
public class SoapRequestConfiguration implements WebMvcConfigurer {

  public static final int SOAP_REQUEST_ORDER = 1;

  @Value("${spring.security.jwt.jwks}")
  private String jwkUrl;

  @Value("${spring.security.jwt.iss}")
  private String iss;

  @Value("${spring.security.jwt.aud}")
  private String aud;

  @Bean
  @Qualifier("jwksConfiguration")
  public List<JwksConfiguration> jwksConfiguration() throws MalformedURLException {
    String[] jwkUrls = jwkUrl.split(",");
    String[] issuers = iss.split(",");
    String[] auds = aud.split(",");
    List<JwksConfiguration> jwksConfigurations = new ArrayList<>();
    if (jwkUrls.length == issuers.length) {
      for (int i = 0; i < jwkUrls.length; i++) {
        jwksConfigurations.add(JwksConfiguration.builder()
            .jwksUrl(new URL(jwkUrls[i]))
            .issuer(issuers[i])
            .aud(auds[i]).build());
      }
      return Collections.unmodifiableList(jwksConfigurations);
    } else {
      throw new IllegalArgumentException(
          "The comma-separated properties spring.security.jwt.[jwks and iss] must all have the same number of elements.");
    }
  }

}
