package uk.gov.defra.tracesx.soaprequest.service;

import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.defra.tracesx.soaprequest.audit.AuditServiceWrapper;
import uk.gov.defra.tracesx.soaprequest.dao.entities.SoapRequest;
import uk.gov.defra.tracesx.soaprequest.dao.repositories.SoapRequestRepository;
import uk.gov.defra.tracesx.soaprequest.dto.SoapRequestDTO;

@Service
public class SoapRequestService {

  private final SoapRequestRepository soapRequestRepository;

  private final AuditServiceWrapper auditServiceWrapper;

  @Autowired
  public SoapRequestService(SoapRequestRepository soapRequestRepository, AuditServiceWrapper auditServiceWrapper) {
    this.soapRequestRepository = soapRequestRepository;
    this.auditServiceWrapper = auditServiceWrapper;
  }

  public UUID create(SoapRequestDTO soapRequest) {
    SoapRequest searchCertificateRequest =
        soapRequestRepository.save(
            new SoapRequest(soapRequest.getUsername(), soapRequest.getQuery()));

    auditServiceWrapper.create(searchCertificateRequest);

    return searchCertificateRequest.getId();
  }

  public SoapRequestDTO get(UUID id) {
    Optional<SoapRequest> soapRequest = soapRequestRepository.findById(id);
    SoapRequestDTO soapRequestDTO = soapRequest.map(SoapRequestDTO::from).get();
    auditServiceWrapper.read(soapRequestDTO);
    return soapRequestDTO;
  }

  public SoapRequestDTO getByRequestId(Long id) {
    Optional<SoapRequest> soapRequest =   soapRequestRepository.findByRequestId(id);
    SoapRequestDTO soapRequestDTO = soapRequest.map(SoapRequestDTO::from).get();

    auditServiceWrapper.read(soapRequestDTO);

    return soapRequestDTO;
  }

  public void deleteData(UUID id) {
    SoapRequestDTO soapRequestDTO = soapRequestRepository.findById(id).map(SoapRequestDTO::from).get();
    soapRequestRepository.deleteById(id);

    auditServiceWrapper.delete(soapRequestDTO);
  }

}
