package uk.gov.defra.tracesx.soaprequest.security;

import org.springframework.stereotype.Component;
import uk.gov.defra.tracesx.common.security.ServiceUrlPatterns;

import java.util.Collections;
import java.util.List;

@Component
public class SoapRequestServiceUrlPatterns implements ServiceUrlPatterns {

  @Override
  public List<String> getAuthorizedPatterns() {
    return Collections.singletonList("/soaprequest/**");
  }
}
