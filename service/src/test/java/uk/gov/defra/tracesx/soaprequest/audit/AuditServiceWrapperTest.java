package uk.gov.defra.tracesx.soaprequest.audit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.gov.defra.tracesx.soaprequest.dao.entities.SoapRequest;
import uk.gov.defra.tracesx.soaprequest.security.IdTokenUserDetails;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AuditServiceWrapperTest {

  private static final String TEST_OBJECT_ID = "123-123-123";
  @Mock
  private AuditService auditService;
  private ObjectMapper objectMapper = new ObjectMapper();
  private AuditServiceWrapper auditServiceWrapper;
  private SoapRequest soapRequest;
  private Long  REQUEST_ID = new Long("1549469808042");
  private String QUERY_STRING = "{'reference':'CVEDP.GB.2019.1000002','type':'CVEDP'}";

  @Before
  public void setUp() {
    initMocks(this);
    auditServiceWrapper = new AuditServiceWrapper(auditService, objectMapper);
    IdTokenUserDetails userDetails = IdTokenUserDetails.builder().userObjectId(TEST_OBJECT_ID).build();
    Authentication authentication = mock(Authentication.class);
    when(authentication.getDetails()).thenReturn(userDetails);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    soapRequest = SoapRequest.builder()
            .query(QUERY_STRING)
            .id(UUID.randomUUID())
            .requestId(REQUEST_ID)
            .build();
  }

  @Test
  public void create() {
    JsonNode expectedArgument = objectMapper.valueToTree(soapRequest.getQuery());

    ArgumentCaptor<JsonNode> captor = ArgumentCaptor.forClass(JsonNode.class);
    auditServiceWrapper.create(soapRequest);

    verify(auditService).create(eq(TEST_OBJECT_ID), captor.capture());

    assertThat(captor.getValue()).isEqualTo(expectedArgument);
  }

  @Test
  public void read() {
    auditServiceWrapper.read(REQUEST_ID.toString());

    verify(auditService).read(eq(TEST_OBJECT_ID), eq(REQUEST_ID.toString()));
  }

  @Test
  public void delete() {
    auditServiceWrapper.delete(REQUEST_ID.toString());

    verify(auditService).delete(eq(TEST_OBJECT_ID), eq(REQUEST_ID.toString()));
  }
}