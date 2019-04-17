package uk.gov.defra.tracesx.soaprequest.security.jwks;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.net.URL;

@Data
@Getter
@Builder
@EqualsAndHashCode
public class JwksConfiguration {
  private URL jwksUrl;
  private String aud;
  private String issuer;
}
