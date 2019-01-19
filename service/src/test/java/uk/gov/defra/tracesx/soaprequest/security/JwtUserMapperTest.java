package uk.gov.defra.tracesx.soaprequest.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static uk.gov.defra.tracesx.soaprequest.fixtures.IdTokenUserDetailsFixture.*;

import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import uk.gov.defra.tracesx.soaprequest.exceptions.InsSecurityException;
import uk.gov.defra.tracesx.soaprequest.fixtures.IdTokenUserDetailsFixture;
import uk.gov.defra.tracesx.soaprequest.security.jwt.JwtUserMapper;

@RunWith(Theories.class)
public class JwtUserMapperTest {

  private Map<String, Object> decoded;
  private JwtUserMapper jwtUserMapper = new JwtUserMapper();

  @Before
  public void before() {
    decoded = new HashMap<>();
    decoded.put("oid", USER_OBJECT_ID);
    decoded.put("roles", ROLES);
  }

  @Test
  public void createUser_fromCompleteClaims_isFullyPopulated() {
    IdTokenUserDetails user = jwtUserMapper.createUser(decoded, ID_TOKEN);
    IdTokenUserDetails expected = IdTokenUserDetailsFixture.create();
    assertThat(user).isEqualTo(expected);
  }

  @DataPoints("API Methods")
  public static final String[] missingClaims = new String[]{
      "oid",
      "roles"
  };

  @Theory
  public void createUser_fromIncompleteClaims_throwsException(String missingClaim) {
    decoded.remove(missingClaim);
    assertThatExceptionOfType(InsSecurityException.class)
        .isThrownBy(() -> jwtUserMapper.createUser(decoded, ID_TOKEN));
  }

}
