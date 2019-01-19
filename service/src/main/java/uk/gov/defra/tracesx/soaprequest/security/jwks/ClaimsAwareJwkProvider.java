package uk.gov.defra.tracesx.soaprequest.security.jwks;

import com.auth0.jwk.GuavaCachedJwkProvider;
import com.auth0.jwk.JwkProvider;
import java.util.concurrent.TimeUnit;

public class ClaimsAwareJwkProvider extends GuavaCachedJwkProvider {

  private final String issuer;

  private final String clientId;

  public ClaimsAwareJwkProvider(JwkProvider provider, long size, long expiresIn,
      TimeUnit expiresUnit, String issuer, String clientId) {
    super(provider, size, expiresIn, expiresUnit);
    this.issuer = issuer;
    this.clientId = clientId;
  }

  public String getIssuer() {
    return issuer;
  }

  public String getClientId() {
    return clientId;
  }
  
}
