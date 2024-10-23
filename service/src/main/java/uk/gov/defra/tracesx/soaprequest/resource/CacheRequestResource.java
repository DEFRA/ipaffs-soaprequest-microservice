package uk.gov.defra.tracesx.soaprequest.resource;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.time.Clock;
import java.time.LocalDateTime;
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
import uk.gov.defra.tracesx.soaprequest.dao.entities.CacheRequest.ChedCertificateHash;
import uk.gov.defra.tracesx.soaprequest.exceptions.BadRequestBodyException;
import uk.gov.defra.tracesx.soaprequest.service.CacheRequestService;

@RestController
@RequestMapping("/cache")
public class CacheRequestResource {

  private final CacheRequestService cacheRequestService;
  private final Clock clock;

  @Autowired
  public CacheRequestResource(CacheRequestService cacheRequestService, Clock clock) {
    this.cacheRequestService = cacheRequestService;
    this.clock = clock;
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
          || cacheRequestDto.id() == null
          || isBlank(cacheRequestDto.value())) {
        throw new BadRequestBodyException("The id and value fields are required");
      }
    });
  }

  private List<CacheRequest> mapToEntity(List<CacheRequestDto> cacheRequestDtos) {
    return cacheRequestDtos.stream()
        .map(cacheRequestDto -> CacheRequestDto.mapToEntity(cacheRequestDto, clock)).toList();
  }

  public record CacheRequestDto(String id, String value) {

    private static CacheRequest mapToEntity(CacheRequestDto cacheRequestDtos, Clock clock) {
      return CacheRequest.builder()
          .id(cacheRequestDtos.id())
          .chedCertificateHash(new ChedCertificateHash(cacheRequestDtos.value()))
          .createdDate(LocalDateTime.now(clock))
          .build();
    }
  }

  public record CacheResponse(List<String> ids) {

  }
}