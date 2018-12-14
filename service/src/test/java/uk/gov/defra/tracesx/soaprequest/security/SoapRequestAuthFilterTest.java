package uk.gov.defra.tracesx.soaprequest.security;

import static java.lang.Boolean.TRUE;
import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.singletonList;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.defra.tracesx.soaprequest.service.PermissionsService;

@RunWith(MockitoJUnitRunner.class)
public class SoapRequestAuthFilterTest {

  private static final String READ = "read";
  private static final String PERMISSIONS_ARE_EMPTY = "Permissions are empty";
  private static final String SECURITY_TOKEN_FEATURE_SWITCH = "securityTokenFeatureSwitch";
  private static final String SOAP_REQUEST_READ = "soaprequest.read";

  @Mock
  private HttpServletRequest request;
  @Mock
  private HttpServletResponse response;
  @Mock
  private FilterChain filterChain;
  @Mock
  private Authentication authentication;

  @Mock
  private UserDetails userDetails;
  @Mock
  private PermissionsService permissionsService;

  @Mock
  private AuthenticationFacade authenticationFacade;

  @InjectMocks
  private  SoapRequestAuthFilter  soapRequestAuthFilter;

  private List<String> perms = singletonList(READ);
  private  List<SimpleGrantedAuthority> grantedAuthoritiesList = new ArrayList<>();
  private  Collection grantedAuthorities = singletonList(new SimpleGrantedAuthority(READ));


  @Before
  public void setup() {
    ReflectionTestUtils.setField(soapRequestAuthFilter, SECURITY_TOKEN_FEATURE_SWITCH, TRUE);

    grantedAuthoritiesList.add(new SimpleGrantedAuthority(SOAP_REQUEST_READ));

    when(permissionsService.permissionsList(any(), any())).thenReturn(perms);
    when(authenticationFacade.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getAuthorities()).thenReturn(grantedAuthorities);
  }

  @Test
  public void filterAddsAuthoritiesToCurrentSecurityContext() throws Exception {

    when(permissionsService.permissionsList(any(), any())).thenReturn(singletonList(
        SOAP_REQUEST_READ));
    soapRequestAuthFilter.doFilterInternal(request, response, filterChain);

    verify(authenticationFacade).replaceAuthorities(grantedAuthoritiesList);
  }

  @Test
  public void filterReturnsUnauthorisedResponseWhenUserHasNoPermissions() throws Exception {

    when(permissionsService.permissionsList(any(), any())).thenReturn(EMPTY_LIST);

    soapRequestAuthFilter.doFilterInternal(request, response, filterChain);

    verify(response).sendError(SC_UNAUTHORIZED, PERMISSIONS_ARE_EMPTY);
  }
}
