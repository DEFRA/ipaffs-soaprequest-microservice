package uk.gov.defra.tracesx.soaprequest.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uk.gov.defra.tracesx.soaprequest.dao.entities.SoapRequest;
import uk.gov.defra.tracesx.soaprequest.dto.SoapRequestDTO;
import uk.gov.defra.tracesx.soaprequest.security.IdTokenUserDetails;

@Service
public class AuditServiceWrapper {

  private final AuditService auditService;
  private final ObjectMapper objectMapper;

  @Autowired
  public AuditServiceWrapper(AuditService auditService,
      ObjectMapper objectMapper) {
    this.auditService = auditService;
    this.objectMapper = objectMapper;
  }

  public void create(SoapRequest soapRequest) {
    auditService.create(getUserObjectId(), objectMapper.valueToTree(soapRequest));
  }

  public void delete(SoapRequestDTO soapRequestDTO) {
    auditService.delete(getUserObjectId(), objectMapper.valueToTree(soapRequestDTO));
  }

  public void read(SoapRequestDTO soapRequestDTO) {
    auditService.read(getUserObjectId(), objectMapper.valueToTree(soapRequestDTO));
  }

  private String getUserObjectId() {
    return ((IdTokenUserDetails) SecurityContextHolder.getContext().getAuthentication().getDetails()).getUserObjectId();
  }
}
