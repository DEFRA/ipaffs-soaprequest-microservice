package uk.gov.defra.tracesx.soaprequest.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.defra.tracesx.soaprequest.dto.CacheRequestDto;
import uk.gov.defra.tracesx.soaprequest.dto.CacheResponse;
import uk.gov.defra.tracesx.soaprequest.service.CacheRequestService;

@ExtendWith(MockitoExtension.class)
class CacheRequestResourceTest {

  private static final String ID = UUID.randomUUID().toString();
  private static final String VALUE = "TEST_VALUE";

  private final CacheRequestService cacheRequestService = mock(CacheRequestService.class);
  private final CacheRequestResource cacheRequestResource = new CacheRequestResource(
      cacheRequestService);

  private List<CacheRequestDto> requestBody;

  @BeforeEach
  void setUp() {
    requestBody = createCacheRequestDTO();
  }

  private List<CacheRequestDto> createCacheRequestDTO() {
    CacheRequestDto request = new CacheRequestDto();
    request.setId(ID);
    request.setValue(VALUE);
    return List.of(request);
  }

  @Test
  void givenInsertCallsServiceWithPostedJson() {
    // Given
    doNothing().when(cacheRequestService).create(any());

    // When
    cacheRequestResource.insert(requestBody);

    // Then
    verify(cacheRequestService, times(1)).create(any());
  }

  @Test
  void givenGetAllMatchingCacheRecordsIsCalled_thenServiceIsCalled() {
    // Given
    when(cacheRequestService.getMatchedCacheRequests(any()))
        .thenReturn(List.of(ID));

    // When
    cacheRequestResource.getAllMatchingCacheRecords(requestBody);

    // Then
    verify(cacheRequestService, times(1)).getMatchedCacheRequests(any());
  }

  @Test
  void givenGetAllMatchingCacheRecordsIsCalled_thenReturnsEntityFromService() {
    // Given
    List<String> result = List.of(ID);
    when(cacheRequestService.getMatchedCacheRequests(any()))
        .thenReturn(result);
    // When
    ResponseEntity<CacheResponse> entity = cacheRequestResource.getAllMatchingCacheRecords(
        requestBody);

    // Then
    verify(cacheRequestService, times(1)).getMatchedCacheRequests(any());
    assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertEquals(1, entity.getBody().getIds().size());
    assertEquals(result.get(0), entity.getBody().getIds().get(0));
  }
}
