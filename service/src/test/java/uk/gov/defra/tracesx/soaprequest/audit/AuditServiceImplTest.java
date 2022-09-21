package uk.gov.defra.tracesx.soaprequest.audit;

import static ch.qos.logback.classic.Level.ERROR;
import static ch.qos.logback.classic.Level.INFO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import uk.gov.defra.tracesx.soaprequest.audit.dao.entities.Audit;
import uk.gov.defra.tracesx.soaprequest.audit.dao.repositories.AuditRepository;

@RunWith(MockitoJUnitRunner.class)
public class AuditServiceImplTest {

  private static final String EXPECTED_DATA = "{\"reference\":\"CVEDP.GB.2019.1000002\",\"type\":\"CVEDP\",\"requestId\":1549469808042}";
  private static final String EXPECTED_READ_DELETE_DATA = "{\"requestId\":1549469808042}";
  private static final String TEST_OBJECT_ID = "123-123-123";
  private final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
  private final Long REQUEST_ID = new Long("1549469808042");
  private final ObjectMapper mapper = new ObjectMapper();
  private JsonNode jsonNode;

  @Mock
  private AuditConfig auditConfig;
  @Mock
  private AuditRepository auditRepository;
  @Mock
  private Appender mockAppender;

  @Captor
  private ArgumentCaptor<Audit> auditCaptor;
  @Captor
  private ArgumentCaptor<LoggingEvent> captorLoggingEvent;

  @InjectMocks
  private AuditServiceImpl auditService;

  @Before
  public void init() {
    jsonNode = mapper.createObjectNode();
    logger.addAppender(mockAppender);
  }

  @After
  public void teardown() {
    logger.detachAppender(mockAppender);
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

  @Test
  public void shouldCatchExceptionWhenAuditCouldNotBeSaved() {
    when(auditConfig.isAuditOnCreate()).thenReturn(true);
    ((ObjectNode) jsonNode).put("reference", "CVEDP.GB.2019.1000002");
    ((ObjectNode) jsonNode).put("type", "CVEDP");
    ((ObjectNode) jsonNode).put("requestId", REQUEST_ID);
    when(auditRepository.save(any(Audit.class))).thenThrow(new NullPointerException());
    auditService.create(TEST_OBJECT_ID, jsonNode);

    verify(mockAppender, times(2)).doAppend(captorLoggingEvent.capture());
    LoggingEvent loggingEventInfo = captorLoggingEvent.getAllValues().get(0);
    assertThat(INFO).isEqualTo(loggingEventInfo.getLevel());
    assertThat("audit: Audit(id=null, userId=123-123-123, data={\"reference\":\"CVEDP.GB.2019.1000002\",\"type\":\"CVEDP\",\"requestId\":1549469808042}, createdAt=null, type=CREATE, entityId=1549469808042)")
        .isEqualTo(loggingEventInfo.getFormattedMessage());
    LoggingEvent loggingEventError = captorLoggingEvent.getAllValues().get(1);
    assertThat(ERROR).isEqualTo(loggingEventError.getLevel());
    assertThat("Unable to persist audit: null")
        .isEqualTo(loggingEventError.getFormattedMessage());
  }

  @Test
  public void shouldCatchExceptionWhenAuditCouldNotBeSavedForInsert() {
    when(auditConfig.isAuditOnRead()).thenReturn(true);
    ((ObjectNode) jsonNode).put("reference", "CVEDP.GB.2019.1000002");
    ((ObjectNode) jsonNode).put("type", "CVEDP");
    ((ObjectNode) jsonNode).put("requestId", REQUEST_ID);
    when(auditRepository.save(any(Audit.class))).thenThrow(new NullPointerException());
    auditService.read(TEST_OBJECT_ID, jsonNode);

    verify(mockAppender, times(2)).doAppend(captorLoggingEvent.capture());
    LoggingEvent loggingEventInfo = captorLoggingEvent.getAllValues().get(0);
    assertThat(INFO).isEqualTo(loggingEventInfo.getLevel());
    assertThat("audit: Audit(id=null, userId=123-123-123, data={\"reference\":\"CVEDP.GB.2019.1000002\",\"type\":\"CVEDP\",\"requestId\":1549469808042}, createdAt=null, type=READ, entityId=1549469808042)")
        .isEqualTo(loggingEventInfo.getFormattedMessage());
    LoggingEvent loggingEventError = captorLoggingEvent.getAllValues().get(1);
    assertThat(ERROR).isEqualTo(loggingEventError.getLevel());
    assertThat("Unable to persist audit: null")
        .isEqualTo(loggingEventError.getFormattedMessage());
  }
}
