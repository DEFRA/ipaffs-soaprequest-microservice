package uk.gov.defra.tracesx.soaprequest;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties
@EnableJpaRepositories("uk.gov.defra.tracesx.soaprequest.dao.repositories")
public class SoapRequestConfiguration {
  @Value("${spring.datasource.url}")
  private String jdbcUrl;

  @Value("${spring.datasource.managed-identity-url}")
  private String managedIdentityJdbcUrl;

  @Value("${features.enable-managed-identity-auth}")
  private boolean enableManagedIdentityAuth;

  @Bean
  public DataSource getDataSource() {
    if (enableManagedIdentityAuth) {
      return DataSourceBuilder.create().url(managedIdentityJdbcUrl).build();
    }
    return DataSourceBuilder.create().url(jdbcUrl).build();
  }
}
