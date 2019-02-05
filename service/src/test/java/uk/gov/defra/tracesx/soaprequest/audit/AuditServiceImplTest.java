package uk.gov.defra.tracesx.soaprequest.audit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import uk.gov.defra.tracesx.soaprequest.audit.dao.entities.Audit;
import uk.gov.defra.tracesx.soaprequest.audit.dao.repositories.AuditRepository;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AuditServiceImplTest {

  private static final String EXPECTED_DATA = "{\"reference\":\"CVEDP.GB.2019.1000002\",\"type\":\"CVEDP\"}";

  @Mock private AuditConfig auditConfig;
  @Mock private AuditRepository auditRepository;

  @Captor
  private ArgumentCaptor<Audit> auditCaptor;

  private AuditService<UUID> auditService;
  private ObjectMapper mapper = new ObjectMapper();
  private Long  REQUEST_ID = new Long("1549469808042");
  private static final String TEST_OBJECT_ID = "123-123-123";

  @Before
  public void init() {
    initMocks(this);
    auditService = new AuditServiceImpl<UUID>(auditConfig, auditRepository);
  }

  @Test
  public void shouldCallAuditRepositoryOnCreateIfCreateFlagIsSet() {
    when(auditConfig.isAuditOnCreate()).thenReturn(true);
    JsonNode jsonNode = mapper.createObjectNode();
    ((ObjectNode) jsonNode).put("reference", "CVEDP.GB.2019.1000002");
    ((ObjectNode) jsonNode).put("type", "CVEDP");
    auditService.create(TEST_OBJECT_ID, jsonNode);

    verify(auditRepository).save(auditCaptor.capture());

    assertThat(auditCaptor.getValue().getData()).isEqualTo(EXPECTED_DATA);
    verify(auditRepository, times(1)).save(any(Audit.class));
  }

  @Test
  public void shouldCallAuditRepositoryOnCreateIfCreateFlagIsNotSet() {
    when(auditConfig.isAuditOnCreate()).thenReturn(false);
    JsonNode jsonNode = mapper.createObjectNode();
    auditService.create(TEST_OBJECT_ID, jsonNode);

    verify(auditRepository, never()).save(any(Audit.class));
  }

  @Test
  public void shouldCallAuditRepositoryOnReadIfReadFlagIsSet() {
    when(auditConfig.isAuditOnRead()).thenReturn(true);
    auditService.read(TEST_OBJECT_ID, REQUEST_ID.toString());

    verify(auditRepository, times(1)).save(any(Audit.class));
  }

  @Test
  public void shouldNotCallAuditRepositoryOnReadIfReadFlagIsNotSet() {
    when(auditConfig.isAuditOnRead()).thenReturn(false);
    auditService.read(TEST_OBJECT_ID,REQUEST_ID.toString() );

    verify(auditRepository, never()).save(any(Audit.class));
  }

  @Test
  public void shouldCallAuditRepositoryOnDeleteIfDeleteFlagIsSet() {
    when(auditConfig.isAuditOnDelete()).thenReturn(true);
    auditService.delete(TEST_OBJECT_ID, REQUEST_ID.toString());

    verify(auditRepository, times(1)).save(any(Audit.class));
  }

  @Test
  public void shouldNotCallAuditRepositoryOnDeleteIfDeleteFlagIsNotSet() {
    when(auditConfig.isAuditOnDelete()).thenReturn(false);
    auditService.delete(TEST_OBJECT_ID,REQUEST_ID.toString());

    verify(auditRepository, never()).save(any(Audit.class));
  }
}
