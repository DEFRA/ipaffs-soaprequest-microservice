package uk.gov.defra.tracesx.soaprequest.audit;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.defra.tracesx.soaprequest.audit.dao.entities.Audit;
import uk.gov.defra.tracesx.soaprequest.audit.dao.repositories.AuditRepository;

import static java.time.LocalDateTime.now;
import static org.slf4j.LoggerFactory.getLogger;
import static uk.gov.defra.tracesx.soaprequest.audit.AuditRequestType.CREATE;
import static uk.gov.defra.tracesx.soaprequest.audit.AuditRequestType.DELETE;
import static uk.gov.defra.tracesx.soaprequest.audit.AuditRequestType.READ;

@Service
public class AuditServiceImpl <T> implements AuditService<T> {

  private final AuditRepository auditRepository;
  private final AuditConfig auditConfig;
  private final Logger logger = getLogger(AuditServiceImpl.class);

  @Autowired
  public AuditServiceImpl(
      AuditConfig auditConfig, AuditRepository auditRepository) {
    this.auditConfig = auditConfig;
    this.auditRepository = auditRepository;
  }

  @Override
  public void create(String user, JsonNode created) {
    if (auditConfig.isAuditOnCreate()) {
      processAuditForInsert(user, created, CREATE);
    }
  }

  @Override
  public void read(String user, String id) {
    if (auditConfig.isAuditOnRead()) {
      processAudit(user, READ, id);
    }
  }

  @Override
  public void delete(String user, String id) {
    if (auditConfig.isAuditOnDelete()) {
      processAudit(user, DELETE, id);
    }
  }

  private void processAuditForInsert(String user, JsonNode diff, AuditRequestType type) {
    Audit audit = Audit.builder()
            .userId(user)
            .data(diff.toString().replaceAll("^\"|\"$|\\\\", ""))
            .type(type)
            .createdAt(now())
        .build();
    try {
      auditRepository.save(audit);
    } catch (Exception e) {
      handleError(audit, e);
    }
  }

  private void processAudit(String user, AuditRequestType type , String id) {
    Audit audit = Audit.builder()
            .userId(user)
            .data(id)
            .type(type)
            .createdAt(now())
            .build();
    try {
      auditRepository.save(audit);
    } catch (Exception e) {
      handleError(audit, e);
    }
  }

  private void handleError(Audit audit, Exception e) {
    logger.info("audit: {}", audit.toString());
    logger.error("Unable to persist audit: {}", e.getMessage());
  }
}
