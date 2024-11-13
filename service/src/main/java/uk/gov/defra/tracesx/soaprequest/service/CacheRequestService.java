package uk.gov.defra.tracesx.soaprequest.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.defra.tracesx.soaprequest.dao.entities.CacheRequest;
import uk.gov.defra.tracesx.soaprequest.dao.repositories.CacheRequestRepository;

@Service
public class CacheRequestService {

  private final CacheRequestRepository cacheRequestRepository;

  @Autowired
  public CacheRequestService(CacheRequestRepository cacheRequestRepository) {
    this.cacheRequestRepository = cacheRequestRepository;
  }

  public void create(List<CacheRequest> cacheRequests) {
    cacheRequestRepository.saveAll(cacheRequests);
  }

  public List<String> getMatchedCacheRequests(List<CacheRequest> cacheRequests) {
    List<String> ids = cacheRequests.stream().map(CacheRequest::getId).toList();
    return cacheRequestRepository.findAllByIdIn(ids).stream()
        .filter(cacheRequests::contains)
        .map(CacheRequest::getId).toList();
  }
}
