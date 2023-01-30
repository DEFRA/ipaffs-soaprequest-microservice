package uk.gov.defra.tracesx.soaprequest.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.gov.defra.tracesx.common.security.IdTokenUserDetails;
import uk.gov.defra.tracesx.soaprequest.dao.entities.SoapRequest;
import uk.gov.defra.tracesx.soaprequest.dto.SoapRequestDto;

import java.util.UUID;

public class AuditServiceWrapperTest {

  private static final String TEST_OBJECT_ID = "123-123-123";
  private static final String QUERY = "test";
  private static final String TEST_USER = "testUser";
  private Long REQUEST_ID = new Long("1549469808042");
  private String QUERY_STRING = "{'reference':'CVEDP.GB.2019.1000002','type':'CVEDP'}";

  private ObjectMapper objectMapper = new ObjectMapper();
  private AuditServiceWrapper auditServiceWrapper;
  private SoapRequest soapRequest;

  @Mock
  private AuditService auditService;
  @Mock
  private Authentication authentication;
  @Mock
  private SecurityContext securityContext;
  @Captor
  private ArgumentCaptor<JsonNode> jsonNodeCaptor;

  @Before
  public void setUp() {
    initMocks(this);
    auditServiceWrapper = new AuditServiceWrapper(auditService, objectMapper);
    IdTokenUserDetails userDetails = IdTokenUserDetails.builder().userObjectId(TEST_OBJECT_ID).build();
    when(authentication.getDetails()).thenReturn(userDetails);
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
    JsonNode expectedArgument = objectMapper.valueToTree(soapRequest);

    auditServiceWrapper.create(soapRequest);

    verify(auditService).create(eq(TEST_OBJECT_ID), jsonNodeCaptor.capture());

    assertThat(jsonNodeCaptor.getValue()).isEqualTo(expectedArgument);
  }

  @Test
  public void read() {
    JsonNode expectedArgument = objectMapper.valueToTree(createDefaultSoapRequestDTO());

    auditServiceWrapper.read(createDefaultSoapRequestDTO());

    verify(auditService).read(eq(TEST_OBJECT_ID), jsonNodeCaptor.capture());

    assertThat(jsonNodeCaptor.getValue()).isEqualTo(expectedArgument);
  }

  @Test
  public void delete() {
    JsonNode expectedArgument = objectMapper.valueToTree(createDefaultSoapRequestDTO());

    auditServiceWrapper.delete(createDefaultSoapRequestDTO());

    verify(auditService).delete(eq(TEST_OBJECT_ID), jsonNodeCaptor.capture());

    assertThat(jsonNodeCaptor.getValue()).isEqualTo(expectedArgument);
  }

  private SoapRequestDto createDefaultSoapRequestDTO() {
    SoapRequestDto soapRequestDTO = new SoapRequestDto();
    soapRequestDTO.setQuery(QUERY);
    soapRequestDTO.setUsername(TEST_USER);
    return soapRequestDTO;
  }
}
