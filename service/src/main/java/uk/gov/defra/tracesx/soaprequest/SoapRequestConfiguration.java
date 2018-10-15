package uk.gov.defra.tracesx.soaprequest;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableConfigurationProperties
@EnableJpaRepositories("uk.gov.defra.tracesx.soaprequest.dao.repositories")
public class SoapRequestConfiguration {
  //Custom Configuration properties can be loaded here
}
