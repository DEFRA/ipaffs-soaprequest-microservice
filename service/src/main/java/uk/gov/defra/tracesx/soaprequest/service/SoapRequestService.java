package uk.gov.defra.tracesx.soaprequest.service;

import java.io.IOException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.defra.tracesx.soaprequest.dao.entities.SoapRequest;
import uk.gov.defra.tracesx.soaprequest.dao.repositories.SoapRequestRepository;
import uk.gov.defra.tracesx.soaprequest.dto.SoapRequestDTO;

@Service
public class SoapRequestService {

  private final SoapRequestRepository soapRequestRepository;

  @Autowired
  public SoapRequestService(SoapRequestRepository soapRequestRepository) throws IOException {
    this.soapRequestRepository = soapRequestRepository;
  }

  public UUID create(SoapRequestDTO soapRequest) {
    SoapRequest searchCertificateRequest =
        soapRequestRepository.save(
            new SoapRequest(soapRequest.getUsername(), soapRequest.getQuery()));
    return searchCertificateRequest.getId();
  }

  public SoapRequestDTO get(UUID id) throws IOException {
    return soapRequestRepository.findById(id).map(SoapRequestDTO::from).get();
  }

  public void deleteData(UUID id) {
    soapRequestRepository.deleteById(id);
  }

  public SoapRequestDTO get(Long requestId, String username) {
    return soapRequestRepository
        .findByRequestIdAndUsername(requestId, username)
        .map(SoapRequestDTO::from)
        .get();
  }
}
