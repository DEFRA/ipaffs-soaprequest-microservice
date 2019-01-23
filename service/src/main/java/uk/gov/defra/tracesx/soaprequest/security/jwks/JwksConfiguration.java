package uk.gov.defra.tracesx.soaprequest.security.jwks;

import java.net.URL;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@Getter
@Builder
@EqualsAndHashCode
public class JwksConfiguration {
  private URL jwksUrl;
  private String aud;
  private String issuer;
}
