package uk.gov.defra.tracesx.soaprequest.security.jwks;

import com.auth0.jwk.GuavaCachedJwkProvider;
import com.auth0.jwk.JwkProvider;

import java.util.concurrent.TimeUnit;

public class ClaimsAwareJwkProvider extends GuavaCachedJwkProvider {

  private final String issuer;
  private final String aud;

  public ClaimsAwareJwkProvider(JwkProvider provider, long size, long expiresIn,
                                TimeUnit expiresUnit, String issuer, String aud) {
    super(provider, size, expiresIn, expiresUnit);
    this.issuer = issuer;
    this.aud = aud;
  }

  public String getIssuer() {
    return issuer;
  }

  public String getAud() {
    return aud;
  }

}
