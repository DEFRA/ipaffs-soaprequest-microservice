package uk.gov.defra.tracesx.soaprequest;

import java.security.Security;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertySetter {

  @Value("${networking.dnsCacheTtl}")
  private String dnsCacheTtl;

  @PostConstruct
  public void setProperty() {
    Security.setProperty("networkaddress.cache.ttl", dnsCacheTtl);
  }
}
