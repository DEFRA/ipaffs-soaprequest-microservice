package uk.gov.defra.tracesx.soaprequest.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uk.gov.defra.tracesx.common.security.IdTokenUserDetails;
import uk.gov.defra.tracesx.soaprequest.dao.entities.SoapRequest;
import uk.gov.defra.tracesx.soaprequest.dto.SoapRequestDto;

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

  public void delete(SoapRequestDto soapRequestDto) {
    auditService.delete(getUserObjectId(), objectMapper.valueToTree(soapRequestDto));
  }

  public void read(SoapRequestDto soapRequestDto) {
    auditService.read(getUserObjectId(), objectMapper.valueToTree(soapRequestDto));
  }

  private String getUserObjectId() {
    return ((IdTokenUserDetails) SecurityContextHolder
        .getContext()
        .getAuthentication()
        .getDetails())
        .getUserObjectId();
  }
}
