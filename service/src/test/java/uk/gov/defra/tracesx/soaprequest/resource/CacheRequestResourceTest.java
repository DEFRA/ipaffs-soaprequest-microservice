package uk.gov.defra.tracesx.soaprequest.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.defra.tracesx.soaprequest.exceptions.BadRequestBodyException;
import uk.gov.defra.tracesx.soaprequest.resource.CacheRequestResource.CacheResponse;
import uk.gov.defra.tracesx.soaprequest.resource.CacheRequestResource.CacheRequestDto;
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
    return List.of(new CacheRequestDto(ID, VALUE));
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
    assertThat(Objects.requireNonNull(entity.getBody()).ids()).hasSize(1);
    assertThat(entity.getBody().ids().get(0)).isEqualTo(result.get(0));
  }

  @Test
  void givenInvalidCacheRecords_thenExceptionIsThrown() {
    assertThatThrownBy(() -> cacheRequestResource.insert(
        List.of(new CacheRequestDto(null, VALUE)))).isInstanceOf(
            BadRequestBodyException.class)
        .hasMessageContaining("The id and value fields are required");

    assertThatThrownBy(() -> cacheRequestResource.insert(
        List.of(new CacheRequestDto(ID, null)))).isInstanceOf(
            BadRequestBodyException.class)
        .hasMessageContaining("The id and value fields are required");
  }
}
