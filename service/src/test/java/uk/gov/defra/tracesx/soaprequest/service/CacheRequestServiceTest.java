package uk.gov.defra.tracesx.soaprequest.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.defra.tracesx.soaprequest.dao.entities.CacheRequest;
import uk.gov.defra.tracesx.soaprequest.dao.entities.CacheRequest.ChedReference;
import uk.gov.defra.tracesx.soaprequest.dao.repositories.CacheRequestRepository;

@ExtendWith(MockitoExtension.class)
class CacheRequestServiceTest {

  private static final String ID = UUID.randomUUID().toString();
  private static final String VALUE = "TEST_VALUE";
  private static final LocalDateTime CREATED_DATE = LocalDateTime.now();

  @Mock
  private CacheRequestRepository cacheRequestRepository;
  @InjectMocks
  private CacheRequestService cacheRequestService;

  private List<CacheRequest> cacheRequests;

  @BeforeEach
  void setUp() {
    cacheRequests = createDefaultCacheRequestEntity();
  }

  private List<CacheRequest> createDefaultCacheRequestEntity() {
    return List.of(CacheRequest.builder().id(ID).chedReference( new ChedReference(VALUE)).createdDate(CREATED_DATE).build());
  }

  @Test
  void givenCreateCalled_thenCorrectDataSentToRepository() {
    // Given
    when(cacheRequestRepository.saveAll(any())).thenReturn(cacheRequests);

    // When
    cacheRequestService.create(cacheRequests);

    // Then
    verify(cacheRequestRepository, times(1)).saveAll(cacheRequests);
  }

  @Test
  void givenGetMatchedCacheRequestsIsCalled_thenMatchedIdsReturned() {
    // Given
    when(cacheRequestRepository.findAllByIdIn(any())).thenReturn(cacheRequests);

    // When
    List<String> matchedIds = cacheRequestService.getMatchedCacheRequests(cacheRequests);

    // Then
    verify(cacheRequestRepository, times(1))
        .findAllByIdIn(any());
    assertThat(matchedIds).hasSize(1);
    assertThat(matchedIds.get(0)).isEqualTo(ID);
  }
}
