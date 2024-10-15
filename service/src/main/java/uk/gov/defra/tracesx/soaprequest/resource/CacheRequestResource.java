package uk.gov.defra.tracesx.soaprequest.resource;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static uk.gov.defra.tracesx.soaprequest.util.Constants.CACHE_REQUEST_ENDPOINT;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.defra.tracesx.soaprequest.dao.entities.CacheRequest;
import uk.gov.defra.tracesx.soaprequest.dto.CacheRequestDto;
import uk.gov.defra.tracesx.soaprequest.dto.CacheResponse;
import uk.gov.defra.tracesx.soaprequest.exceptions.BadRequestBodyException;
import uk.gov.defra.tracesx.soaprequest.service.CacheRequestService;

@RestController
@RequestMapping(CACHE_REQUEST_ENDPOINT)
public class CacheRequestResource {

  private final CacheRequestService cacheRequestService;

  @Autowired
  public CacheRequestResource(CacheRequestService cacheRequestService) {
    this.cacheRequestService = cacheRequestService;
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE, path = "/update")
  @PreAuthorize("hasAuthority('soaprequest.create')")
  public ResponseEntity<Void> insert(
      @RequestBody List<CacheRequestDto> cacheRequestDtos) {
    validate(cacheRequestDtos);

    cacheRequestService.create(mapToEntity(cacheRequestDtos));
    return ResponseEntity.ok().build();
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE, path = "/hits")
  @PreAuthorize("hasAuthority('soaprequest.read')")
  public ResponseEntity<CacheResponse> getAllMatchingCacheRecords(
      @RequestBody List<CacheRequestDto> cacheRequestDtos) {

    List<String> cacheRequests = cacheRequestService.getMatchedCacheRequests(
        mapToEntity(cacheRequestDtos));
    return ResponseEntity.ok(new CacheResponse(cacheRequests));
  }

  private void validate(List<CacheRequestDto> cacheRequests) {
    cacheRequests.forEach(cacheRequestDto -> {
      if (cacheRequestDto == null
          || cacheRequestDto.getId() == null
          || isBlank(cacheRequestDto.getValue())) {
        throw new BadRequestBodyException("The id and value fields are required");
      }
    });
  }

  private List<CacheRequest> mapToEntity(List<CacheRequestDto> cacheRequestDtos) {
    return cacheRequestDtos.stream().map(CacheRequest::from).toList();
  }
}