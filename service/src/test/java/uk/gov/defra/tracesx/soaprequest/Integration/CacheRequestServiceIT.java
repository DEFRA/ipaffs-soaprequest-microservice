package uk.gov.defra.tracesx.soaprequest.Integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
import uk.gov.defra.tracesx.soaprequest.dao.repositories.CacheRequestRepository;
import uk.gov.defra.tracesx.soaprequest.service.CacheRequestService;

@SpringBootTest()
@ActiveProfiles("test")
@Import(TestContainerConfig.class)
class CacheRequestServiceIT {

  private static final String ID = UUID.randomUUID().toString();
  private static final String VALUE = "TEST_VALUE";
  private static final LocalDateTime CREATED_DATE = LocalDateTime.now();

  CacheRequest cacheRequest = new CacheRequest(ID, VALUE, CREATED_DATE);
  List<CacheRequest> cacheRequestList = List.of(cacheRequest);

  @Autowired
  private CacheRequestService cacheRequestService;

  @Autowired
  private CacheRequestRepository cacheRequestRepository;

  @BeforeEach
  void setUp() {
    cacheRequestRepository.deleteAll();
  }

  @Test
  void givenCreateCalled_thenCacheRequestsStored() {
    //When cache stored
    cacheRequestService.create(cacheRequestList);

    //Then
    List<CacheRequest> expectedCacheRequests = new ArrayList<>();
    cacheRequestRepository.findAll().forEach(expectedCacheRequests::add);

    assertEquals(1, expectedCacheRequests.size());
    assertThat(expectedCacheRequests.get(0).getId()).isEqualToIgnoringCase(ID);
    assertEquals(VALUE, expectedCacheRequests.get(0).getValue());
    assertNotNull(expectedCacheRequests.get(0).getCreatedDate());
  }

  @Test
  void givenExistingCacheRequests_thenUpdateCacheRequestsListRecord() {
    //Given
    cacheRequestRepository.save(cacheRequest);

    //When
    cacheRequest.setValue("New_Test_Value");
    cacheRequest.setCreatedDate(LocalDateTime.now().plusDays(1L));
    cacheRequestService.create(cacheRequestList);

    //Then
    List<CacheRequest> resultList = new ArrayList<>();
    cacheRequestRepository.findAll().forEach(resultList::add);

    assertEquals(1, resultList.size());
    assertThat(resultList.get(0).getId()).isEqualToIgnoringCase(ID);
    assertThat(resultList.get(0).getCreatedDate()).isAfter(CREATED_DATE);
  }

  @Test
  void givenGetMatchedCacheRequestsCalledWithNoMatches_thenEmptyRecordsReturned() {
    //Given
    assertFalse(cacheRequestRepository.findAll().iterator().hasNext());

    //When cache stored
    List<String> retrievedCacheRecord = cacheRequestService
        .getMatchedCacheRequests(cacheRequestList);

    //Then
    assertEquals(0, retrievedCacheRecord.size());
  }

  @Test
  void givenGetMatchedCacheRequestsCalled_thenExistingRecordsReturned() {
    //Given existing record
    cacheRequestRepository.save(cacheRequest);
    assertTrue(cacheRequestRepository.findAll().iterator().hasNext());

    //When cache stored
    List<String> retrievedCacheRecord = cacheRequestService
        .getMatchedCacheRequests(cacheRequestList);

    //Then
    assertEquals(1, retrievedCacheRecord.size());
    assertEquals(ID, retrievedCacheRecord.get(0));
  }
}

