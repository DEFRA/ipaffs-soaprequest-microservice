package uk.gov.defra.tracesx.soaprequest.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;
import uk.gov.defra.tracesx.soaprequest.exceptions.InsSecurityException;
import uk.gov.defra.tracesx.soaprequest.fixtures.IdTokenUserDetailsFixture;
import uk.gov.defra.tracesx.soaprequest.security.jwt.JwtUserMapper;

import java.util.HashMap;
import java.util.Map;

@RunWith(Theories.class)
public class JwtUserMapperTest {

  private Map<String, Object> decoded;
  private JwtUserMapper jwtUserMapper = new JwtUserMapper();

  public static final String USER_OBJECT_ID = "e9f6447d-2979-4322-8e52-307dafdef649";
  public static final String ID_TOKEN = "adfgsdf.dfgsdrgerg.dfgdfgd";
  public static final String USERNAME = "SOAP";

  @Before
  public void before() {
    decoded = new HashMap<>();
    decoded.put("oid", USER_OBJECT_ID);
    decoded.put("name", USERNAME);
  }

  @Test
  public void createUser_fromCompleteClaims_isFullyPopulated() {
    IdTokenUserDetails user = jwtUserMapper.createUser(decoded, ID_TOKEN);
    IdTokenUserDetails expected = IdTokenUserDetailsFixture.create();
    assertThat(user).isEqualTo(expected);
  }

  @Test(expected = InsSecurityException.class)
  public void createUser_fromIncompleteClaims_throwsException() {
    decoded.remove("oid");
    jwtUserMapper.createUser(decoded, ID_TOKEN);
  }

}
