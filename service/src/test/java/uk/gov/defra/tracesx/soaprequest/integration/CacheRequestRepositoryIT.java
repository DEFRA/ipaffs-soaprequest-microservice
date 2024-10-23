package uk.gov.defra.tracesx.soaprequest.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.defra.tracesx.soaprequest.TestContainerConfig;
import uk.gov.defra.tracesx.soaprequest.dao.entities.CacheRequest;
import uk.gov.defra.tracesx.soaprequest.dao.entities.CacheRequest.ChedReference;
import uk.gov.defra.tracesx.soaprequest.dao.repositories.CacheRequestRepository;

@SpringBootTest()
@ActiveProfiles("test")
@Import(TestContainerConfig.class)
class CacheRequestRepositoryIT {

  private static final String ID = UUID.randomUUID().toString();
  private static final String VALUE = "TEST_VALUE";
  private static final LocalDateTime CREATED_DATE = LocalDateTime.now();

  CacheRequest cacheRequest = new CacheRequest(ID, new ChedReference(VALUE), CREATED_DATE);

  @Autowired
  private CacheRequestRepository cacheRequestRepository;

  @BeforeEach
  void setUp() {
    cacheRequestRepository.deleteAll();
  }

  @Test
  void givenDuplicate_thenCacheRequestsNotSaved() {
    //Given
    cacheRequestRepository.save(cacheRequest);

    //When cache stored
    cacheRequestRepository.save(cacheRequest);

    //Then
    List<CacheRequest> cacheRequestList = cacheRequestRepository
        .findAllByIdIn(List.of(cacheRequest.getId()));

    assertThat(cacheRequestList).singleElement()
        .satisfies(request -> assertThat(request.getId()).isEqualToIgnoringCase(ID))
        .satisfies(request -> assertThat(request.getChedReference().value()).isEqualTo(VALUE))
        .satisfies(request -> assertThat(request.getCreatedDate()).isNotNull());
  }

  @Test
  void givenIdMatchesAndValueDoesnt_thenEmptyCacheRequestsListRecordReturn() {
    //Given
    cacheRequestRepository.save(cacheRequest);

    //When
    List<CacheRequest> result = cacheRequestRepository.findAllByIdIn(List.of(ID));

    //Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualToIgnoringCase(ID);
  }

  @Test
  void givenIdMatchMatchCaseInsensitive_thenAllMatchingCacheRequestRecordReturn() {
    //Given
    cacheRequestRepository.save(cacheRequest);

    //When
    List<CacheRequest> resultToUpper = cacheRequestRepository.findAllByIdIn(List.of(ID.toUpperCase()));
    List<CacheRequest> resultToLower = cacheRequestRepository.findAllByIdIn(List.of(ID.toLowerCase()));

    //Then
    assertThat(resultToUpper).hasSize(1);
    assertThat(resultToLower).hasSize(1);
    assertThat(resultToUpper.get(0).getId()).isEqualToIgnoringCase(ID);
    assertThat(resultToLower.get(0).getId()).isEqualToIgnoringCase(ID);
  }

  @Test
  void givenIdDoesntMatch_thenEmptyCacheRequestRecordReturn() {
    //Given
    cacheRequestRepository.save(cacheRequest);

    //When
    List<CacheRequest> result = cacheRequestRepository.findAllByIdIn(List.of(UUID.randomUUID().toString()));

    //Then
    assertThat(result).isEmpty();
  }
}

