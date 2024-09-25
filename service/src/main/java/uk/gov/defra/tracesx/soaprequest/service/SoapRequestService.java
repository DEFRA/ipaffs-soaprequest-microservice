package uk.gov.defra.tracesx.soaprequest.service;

import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.defra.tracesx.soaprequest.audit.AuditServiceWrapper;
import uk.gov.defra.tracesx.soaprequest.dao.entities.SoapRequest;
import uk.gov.defra.tracesx.soaprequest.dao.repositories.SoapRequestRepository;
import uk.gov.defra.tracesx.soaprequest.dto.SoapRequestDto;
import uk.gov.defra.tracesx.soaprequest.exceptions.NotFoundException;

@Service
public class SoapRequestService {

  private final SoapRequestRepository soapRequestRepository;

  private final AuditServiceWrapper auditServiceWrapper;

  @Autowired
  public SoapRequestService(
      SoapRequestRepository soapRequestRepository,
      AuditServiceWrapper auditServiceWrapper) {
    this.soapRequestRepository = soapRequestRepository;
    this.auditServiceWrapper = auditServiceWrapper;
  }

  public UUID create(SoapRequestDto soapRequest) {
    SoapRequest searchCertificateRequest =
        soapRequestRepository.save(
            new SoapRequest(soapRequest.getUsername(), soapRequest.getQuery()));

    auditServiceWrapper.create(searchCertificateRequest);

    return searchCertificateRequest.getId();
  }

  public Optional<SoapRequestDto> get(UUID id) {
    Optional<SoapRequestDto> optionalSoapRequestDto = soapRequestRepository.findById(id)
        .map(SoapRequestDto::from);
    optionalSoapRequestDto.ifPresent(auditServiceWrapper::read);
    return optionalSoapRequestDto;
  }

  public Optional<SoapRequestDto> getByRequestIdAndUsername(Long id, String username) {
    Optional<SoapRequestDto> optionalSoapRequestDto = soapRequestRepository
        .findByRequestIdAndUsername(id, username)
        .map(SoapRequestDto::from);
    optionalSoapRequestDto.ifPresent(auditServiceWrapper::read);
    return optionalSoapRequestDto;
  }

  public void deleteData(UUID id) {
    SoapRequest request = soapRequestRepository.findById(id)
        .orElseThrow(NotFoundException::new);
    soapRequestRepository.delete(request);
    auditServiceWrapper.delete(SoapRequestDto.from(request));
  }
}
