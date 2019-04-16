package uk.gov.defra.tracesx.soaprequest.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import uk.gov.defra.tracesx.soaprequest.audit.dao.entities.Audit;
import uk.gov.defra.tracesx.soaprequest.audit.dao.repositories.AuditRepository;

public class AuditServiceImplTest {

  private static final String EXPECTED_DATA = "{\"reference\":\"CVEDP.GB.2019.1000002\",\"type\":\"CVEDP\",\"requestId\":1549469808042}";
  private static final String EXPECTED_READ_DELETE_DATA = "{\"requestId\":1549469808042}";

  @Mock
  private AuditConfig auditConfig;
  @Mock
  private AuditRepository auditRepository;

  @Captor
  private ArgumentCaptor<Audit> auditCaptor;

  private AuditService auditService;
  private ObjectMapper mapper = new ObjectMapper();
  private Long REQUEST_ID = new Long("1549469808042");
  private static final String TEST_OBJECT_ID = "123-123-123";
  private JsonNode jsonNode;


  @Before
  public void init() {
    initMocks(this);
    auditService = new AuditServiceImpl(auditConfig, auditRepository);
    jsonNode = mapper.createObjectNode();
  }

  @Test
  public void shouldCallAuditRepositoryOnCreateIfCreateFlagIsSet() {
    when(auditConfig.isAuditOnCreate()).thenReturn(true);
    ((ObjectNode) jsonNode).put("reference", "CVEDP.GB.2019.1000002");
    ((ObjectNode) jsonNode).put("type", "CVEDP");
    ((ObjectNode) jsonNode).put("requestId", REQUEST_ID);
    auditService.create(TEST_OBJECT_ID, jsonNode);

    verify(auditRepository).save(auditCaptor.capture());

    assertThat(auditCaptor.getValue().getData()).isEqualTo(EXPECTED_DATA);
    verify(auditRepository).save(any(Audit.class));
  }

  @Test
  public void shouldCallAuditRepositoryOnCreateIfCreateFlagIsNotSet() {
    when(auditConfig.isAuditOnCreate()).thenReturn(false);
    auditService.create(TEST_OBJECT_ID, jsonNode);

    verify(auditRepository, never()).save(any(Audit.class));
  }

  @Test
  public void shouldCallAuditRepositoryOnReadIfReadFlagIsSet() {
    ((ObjectNode) jsonNode).put("requestId", REQUEST_ID);
    when(auditConfig.isAuditOnRead()).thenReturn(true);

    auditService.read(TEST_OBJECT_ID, jsonNode);

    verify(auditRepository).save(auditCaptor.capture());
    verify(auditRepository).save(any(Audit.class));

    assertThat(auditCaptor.getValue().getData()).isEqualTo(EXPECTED_READ_DELETE_DATA);
  }

  @Test
  public void shouldNotCallAuditRepositoryOnReadIfReadFlagIsNotSet() {
    when(auditConfig.isAuditOnRead()).thenReturn(false);
    auditService.read(TEST_OBJECT_ID, jsonNode);

    verify(auditRepository, never()).save(any(Audit.class));
  }

  @Test
  public void shouldCallAuditRepositoryOnDeleteIfDeleteFlagIsSet() {
    ((ObjectNode) jsonNode).put("requestId", REQUEST_ID);
    when(auditConfig.isAuditOnDelete()).thenReturn(true);
    auditService.delete(TEST_OBJECT_ID, jsonNode);

    verify(auditRepository).save(auditCaptor.capture());
    verify(auditRepository).save(any(Audit.class));

    assertThat(auditCaptor.getValue().getData()).isEqualTo(EXPECTED_READ_DELETE_DATA);
  }

  @Test
  public void shouldNotCallAuditRepositoryOnDeleteIfDeleteFlagIsNotSet() {
    when(auditConfig.isAuditOnDelete()).thenReturn(false);
    auditService.delete(TEST_OBJECT_ID, jsonNode);

    verify(auditRepository, never()).save(any(Audit.class));
  }
}
