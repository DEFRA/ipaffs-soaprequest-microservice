package uk.gov.defra.tracesx.soaprequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Security;
import javax.annotation.PostConstruct;

@Component
public class PropertySetter {

  @Value("${networking.dnsCacheTtl}")
  private String dnsCacheTtl;

  @PostConstruct
  public void setProperty() {
    Security.setProperty("networkaddress.cache.ttl", dnsCacheTtl);
  }
}
