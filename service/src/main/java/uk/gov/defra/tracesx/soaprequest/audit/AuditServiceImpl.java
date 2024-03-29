package uk.gov.defra.tracesx.soaprequest.audit;

import static org.slf4j.LoggerFactory.getLogger;
import static uk.gov.defra.tracesx.soaprequest.audit.AuditRequestType.DELETE;
import static uk.gov.defra.tracesx.soaprequest.audit.AuditRequestType.READ;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.defra.tracesx.soaprequest.audit.dao.entities.Audit;
import uk.gov.defra.tracesx.soaprequest.audit.dao.repositories.AuditRepository;

@Service
public class AuditServiceImpl implements AuditService {

  private static final String REQUEST_ID = "requestId";
  private static final String REGEX_REMOVE_QUOTES = "^\"|\"$|\\\\";
  private final AuditRepository<Audit> auditRepository;
  private final AuditConfig auditConfig;
  private final Logger logger = getLogger(AuditServiceImpl.class);

  @Autowired
  public AuditServiceImpl(
      AuditConfig auditConfig, AuditRepository<Audit> auditRepository) {
    this.auditConfig = auditConfig;
    this.auditRepository = auditRepository;
  }

  @Override
  public void create(String user, JsonNode created) {
    if (auditConfig.isAuditOnCreate()) {
      processAuditForInsert(user, created);
    }
  }

  @Override
  public void read(String user, JsonNode current) {
    if (auditConfig.isAuditOnRead()) {
      processAudit(user, READ, current);
    }
  }

  @Override
  public void delete(String user, JsonNode deleted) {
    if (auditConfig.isAuditOnDelete()) {
      processAudit(user, DELETE, deleted);
    }
  }

  private void processAuditForInsert(String user, JsonNode created) {
    Audit audit = Audit.builder()
        .userId(user)
        .data(created.toString().replaceAll(REGEX_REMOVE_QUOTES, ""))
        .entityId(created.get(REQUEST_ID).asLong())
        .type(AuditRequestType.CREATE)
        .build();
    try {
      auditRepository.save(audit);
    } catch (Exception exception) {
      handleError(audit, exception);
    }
  }

  private void processAudit(String user, AuditRequestType type, JsonNode jsonNode) {
    Audit audit = Audit.builder()
        .userId(user)
        .data(jsonNode.toString().replaceAll(REGEX_REMOVE_QUOTES, ""))
        .entityId(jsonNode.get(REQUEST_ID).asLong())
        .type(type)
        .build();
    try {
      auditRepository.save(audit);
    } catch (Exception exception) {
      handleError(audit, exception);
    }
  }

  private void handleError(Audit audit, Exception exception) {
    logger.error("Unable to persist audit: {} {}", audit, exception.getMessage());
  }
}
