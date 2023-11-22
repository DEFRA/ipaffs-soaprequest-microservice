package uk.gov.defra.tracesx.soaprequest.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.gov.defra.tracesx.common.security.IdTokenUserDetails;
import uk.gov.defra.tracesx.soaprequest.dao.entities.SoapRequest;
import uk.gov.defra.tracesx.soaprequest.dto.SoapRequestDto;

@ExtendWith(MockitoExtension.class)
class AuditServiceWrapperTest {

  private static final String TEST_OBJECT_ID = "123-123-123";
  private static final String QUERY = "test";
  private static final String TEST_USER = "testUser";
  private final Long REQUEST_ID = Long.valueOf("1549469808042");

  private final ObjectMapper objectMapper = new ObjectMapper();
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

  @BeforeEach
  void setUp() {
    auditServiceWrapper = new AuditServiceWrapper(auditService, objectMapper);
    IdTokenUserDetails userDetails = IdTokenUserDetails.builder().userObjectId(TEST_OBJECT_ID)
        .build();
    when(authentication.getDetails()).thenReturn(userDetails);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    String queryString = "{'reference':'CVEDP.GB.2019.1000002','type':'CVEDP'}";
    soapRequest = SoapRequest.builder()
        .query(queryString)
        .id(UUID.randomUUID())
        .requestId(REQUEST_ID)
        .build();
  }

  @Test
  void create() {
    JsonNode expectedArgument = objectMapper.valueToTree(soapRequest);

    auditServiceWrapper.create(soapRequest);

    verify(auditService).create(eq(TEST_OBJECT_ID), jsonNodeCaptor.capture());

    assertThat(jsonNodeCaptor.getValue()).isEqualTo(expectedArgument);
  }

  @Test
  void read() {
    JsonNode expectedArgument = objectMapper.valueToTree(createDefaultSoapRequestDTO());

    auditServiceWrapper.read(createDefaultSoapRequestDTO());

    verify(auditService).read(eq(TEST_OBJECT_ID), jsonNodeCaptor.capture());

    assertThat(jsonNodeCaptor.getValue()).isEqualTo(expectedArgument);
  }

  @Test
  void delete() {
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
