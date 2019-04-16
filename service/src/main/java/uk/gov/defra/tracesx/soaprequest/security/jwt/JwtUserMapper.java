package uk.gov.defra.tracesx.soaprequest.security.jwt;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.gov.defra.tracesx.soaprequest.exceptions.InsSecurityException;
import uk.gov.defra.tracesx.soaprequest.security.IdTokenUserDetails;

import java.util.Map;

@Component
public class JwtUserMapper {

  private static final Logger LOGGER = LoggerFactory.getLogger(JwtUserMapper.class);
  public static final String NAME = "name";
  public static final String UPN = "upn";
  public static final String OID = "oid";

  public IdTokenUserDetails createUser(Map<String, Object> decoded, String idToken) {
    return IdTokenUserDetails.builder()
        .idToken(idToken)
        .displayName("SOAP Request Service")
        .username("SOAP")
        .userObjectId(getRequiredClaim(OID, decoded))
        .build();
  }

  private String getRequiredClaim(String claimName, Map<String, Object> body) {
    String value = (String) body.get(claimName);
    if (StringUtils.isEmpty(value)) {
      LOGGER.error("The JWT token is missing the claim '{}'", claimName);
      throw new InsSecurityException("User is missing claim: " + claimName);
    }
    return value;
  }

}
