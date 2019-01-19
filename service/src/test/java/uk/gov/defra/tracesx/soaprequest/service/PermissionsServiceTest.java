package uk.gov.defra.tracesx.soaprequest.service;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.singletonList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class PermissionsServiceTest {

  private static final String READ = "read";
  private static final String ROLE = "importer";
  private static final String TOKEN = "dummyToken";
  private static final String INVALID_ROLE = "invalid.role";
   private final List<String> perms = singletonList(READ);

  @Mock
  private RestTemplate restTemplate;
  @InjectMocks
  private PermissionsService permissionsService;

  @Before
  public void setup() {
    ReflectionTestUtils.setField(permissionsService, "permissionsScheme", "http");
    ReflectionTestUtils.setField(permissionsService, "permissionsHost", "permissions");
    ReflectionTestUtils.setField(permissionsService, "permissionsPort", "4000");
    ReflectionTestUtils.setField(permissionsService, "permissionsUser", "soapUser");
    ReflectionTestUtils.setField(permissionsService, "permissionsPassword", "soapUserPassword");
    
    final ResponseEntity<List<String>> responseEntity = createResponseEntity();
    when(restTemplate.exchange(any(), eq(GET), any(HttpEntity.class),
        eq(new ParameterizedTypeReference<List<String>>() {})))
        .thenReturn(responseEntity);
  }

  @Test
  public void testWhenPermissionsListIsCalledThenReturnListOfPermissions() {

    final List<String> permissionsList = permissionsService.permissionsList(ROLE, TOKEN);

    assertThat(permissionsList)
        .hasSize(1);
    assertThat(permissionsList.get(0))
        .isEqualTo(READ);
  }

  @Test
  public void testWhenPermissionsCalledWithInvalidRoleThenReturnEmptyList() {

    when(restTemplate.exchange(any(), eq(GET), any(HttpEntity.class),
        eq(new ParameterizedTypeReference<List<String>>() {})))
        .thenReturn(new ResponseEntity<>(EMPTY_LIST, OK));

    final List<String> permissionsList = permissionsService.permissionsList(INVALID_ROLE, TOKEN);

    assertThat(permissionsList)
        .hasSize(0);
  }

  private ResponseEntity<List<String>> createResponseEntity() {
    return new ResponseEntity<>(perms, OK);
  }
}