package uk.gov.defra.tracesx.soaprequest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.defra.tracesx.soaprequest.audit.AuditServiceWrapper;
import uk.gov.defra.tracesx.soaprequest.dao.entities.SoapRequest;
import uk.gov.defra.tracesx.soaprequest.dao.repositories.SoapRequestRepository;
import uk.gov.defra.tracesx.soaprequest.dto.SoapRequestDto;

import java.util.Optional;
import java.util.UUID;

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

  public Optional<SoapRequestDto> getByRequestId(Long id) {
    Optional<SoapRequestDto> optionalSoapRequestDto = soapRequestRepository.findByRequestId(id)
        .map(SoapRequestDto::from);
    optionalSoapRequestDto.ifPresent(auditServiceWrapper::read);
    return optionalSoapRequestDto;
  }

  public void deleteData(UUID id) {
    soapRequestRepository.deleteById(id);
    soapRequestRepository
        .findById(id)
        .map(SoapRequestDto::from)
        .ifPresent(auditServiceWrapper::delete);
  }

}
