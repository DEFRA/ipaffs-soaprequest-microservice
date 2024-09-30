package uk.gov.defra.tracesx.soaprequest.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

  public List<SoapRequestDto> getAllByRequestId(Long id) {
    List<SoapRequestDto> soapRequestDtoList = new ArrayList<>();
    for (SoapRequest soapRequest : soapRequestRepository.findAllByRequestId(id)) {
      SoapRequestDto mapped = SoapRequestDto.from(soapRequest);
      auditServiceWrapper.read(mapped);
      soapRequestDtoList.add(mapped);
    }
    return Collections.unmodifiableList(soapRequestDtoList);
  }

  public void deleteData(UUID id) {
    SoapRequest request = soapRequestRepository.findById(id)
        .orElseThrow(NotFoundException::new);
    soapRequestRepository.delete(request);
    auditServiceWrapper.delete(SoapRequestDto.from(request));
  }
}
