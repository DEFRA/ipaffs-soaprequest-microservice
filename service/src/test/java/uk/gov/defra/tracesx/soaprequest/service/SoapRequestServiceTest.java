package uk.gov.defra.tracesx.soaprequest.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.defra.tracesx.soaprequest.audit.AuditServiceWrapper;
import uk.gov.defra.tracesx.soaprequest.dao.entities.SoapRequest;
import uk.gov.defra.tracesx.soaprequest.dao.repositories.SoapRequestRepository;
import uk.gov.defra.tracesx.soaprequest.dto.SoapRequestDto;

@ExtendWith(MockitoExtension.class)
class SoapRequestServiceTest {

  private static final String QUERY = "test";
  private static final String TEST_USER = "testUser";
  private static final String SECOND_TEST_USER = "secondTestUser";

  @Mock
  private SoapRequestRepository soapRequestRepository;
  @Mock
  private AuditServiceWrapper auditServiceWrapper;
  @Captor
  private ArgumentCaptor<SoapRequest> soapRequestCaptor;

  private SoapRequestService soapRequestService;
  private SoapRequest soapRequest;

  @BeforeEach
  void setUp() {
    soapRequestService = new SoapRequestService(soapRequestRepository, auditServiceWrapper);
    soapRequest = createDefaultSoapRequestEntity(TEST_USER);
  }

  private SoapRequestDto createDefaultSoapRequestDTO() {
    SoapRequestDto soapRequestDTO = new SoapRequestDto();
    soapRequestDTO.setQuery(QUERY);
    soapRequestDTO.setUsername(TEST_USER);
    return soapRequestDTO;
  }

  private SoapRequest createDefaultSoapRequestEntity(String username) {
    UUID idSavedWith = UUID.randomUUID();
    long requestId = System.currentTimeMillis();
    SoapRequest soapRequest = new SoapRequest(username, QUERY);
    soapRequest.setId(idSavedWith);
    soapRequest.setRequestId(requestId);
    return soapRequest;
  }

  @Test
  void createsSavesAsNewEntity() {
    // Given
    when(soapRequestRepository.save(any())).thenReturn(soapRequest);

    // When
    UUID entityId = soapRequestService.create(createDefaultSoapRequestDTO());

    // Then
    verify(soapRequestRepository).save(any());
    assertThat(soapRequest.getId()).isEqualTo(entityId);
  }

  @Test
  void createSavesTheCorrectDataToTheRepository() {
    // Given
    when(soapRequestRepository.save(soapRequestCaptor.capture())).thenReturn(soapRequest);

    // When
    soapRequestService.create(createDefaultSoapRequestDTO());

    // Then
    assertThat(soapRequestCaptor.getValue().getId()).isNull();
    assertThat(soapRequest.getUsername()).isEqualTo(soapRequestCaptor.getValue().getUsername());
    assertThat(soapRequest.getQuery()).isEqualTo(soapRequestCaptor.getValue().getQuery());
  }

  @Test
  void getCallsRepositoryWithId() {
    // Given
    when(soapRequestRepository.findById(any())).thenReturn(Optional.of(soapRequest));

    // When
    soapRequestService.get(soapRequest.getId());

    // Then
    verify(soapRequestRepository).findById(soapRequest.getId());
  }

  @Test
  void getReturnsRecordFromRepository() {
    // Given
    when(soapRequestRepository.findById(any())).thenReturn(Optional.of(soapRequest));

    // When
    SoapRequestDto result = soapRequestService.get(soapRequest.getId()).get();

    // Then
    assertThat(soapRequest.getUsername()).isEqualTo(result.getUsername());
    assertThat(soapRequest.getQuery()).isEqualTo(result.getQuery());
    assertThat(soapRequest.getId()).isEqualTo(result.getId());
  }

  @Test
  void getAllByRequestIdCallsRepositoryWithId() {
    // Given
    when(soapRequestRepository.findAllByRequestId(any())).thenReturn(List.of(soapRequest));

    // When
    soapRequestService.getAllByRequestId(soapRequest.getRequestId());

    // Then
    verify(soapRequestRepository).findAllByRequestId(soapRequest.getRequestId());
  }

  @Test
  void getAllByRequestIdReturnsEntityFromRepository() {
    // Given
    when(soapRequestRepository.findAllByRequestId(any())).thenReturn(List.of());

    // When
    soapRequestService.getAllByRequestId(soapRequest.getRequestId());

    // Then
    verify(soapRequestRepository).findAllByRequestId(soapRequest.getRequestId());
  }

  @Test
  void getAllByRequestIdReturnsRecordFromRepository() {
    // Given
    List<SoapRequest> expectedSoapRequests = List.of(soapRequest);
    when(soapRequestRepository.findAllByRequestId(any())).thenReturn(expectedSoapRequests);

    // When
    List<SoapRequestDto> result = soapRequestService.getAllByRequestId(soapRequest.getRequestId());

    // Then
    assertThat(result).hasSameSizeAs(expectedSoapRequests)
      .zipSatisfy(expectedSoapRequests, (actual, expected) -> {
        assertThat(actual.getUsername()).isEqualTo(expected.getUsername());
        assertThat(actual.getQuery()).isEqualTo(expected.getQuery());
        assertThat(actual.getId()).isEqualTo(expected.getId());
      });

    verify(soapRequestRepository, times(1)).findAllByRequestId(soapRequest.getRequestId());
  }

  @Test
  void getAllByRequestIdReturnsMultipleRecordsFromRepository() {
    // Given
    SoapRequest soapRequestSecond = createDefaultSoapRequestEntity(SECOND_TEST_USER);
    List<SoapRequest> expectedSoapRequests = List.of(soapRequest, soapRequestSecond);
    when(soapRequestRepository.findAllByRequestId(any())).thenReturn(expectedSoapRequests);

    // When
    List<SoapRequestDto> result = soapRequestService.getAllByRequestId(soapRequest.getRequestId());

    // Then
    assertThat(result).hasSameSizeAs(expectedSoapRequests)
      .zipSatisfy(expectedSoapRequests, (actual, expected) -> {
        assertThat(actual.getUsername()).isEqualTo(expected.getUsername());
        assertThat(actual.getQuery()).isEqualTo(expected.getQuery());
        assertThat(actual.getId()).isEqualTo(expected.getId());
      });

    verify(soapRequestRepository, times(1)).findAllByRequestId(soapRequest.getRequestId());
  }

  @Test
  void deleteCallsRepositoryOnceWithId() {
    // Given
    UUID id = UUID.randomUUID();
    when(soapRequestRepository.findById(any())).thenReturn(Optional.of(soapRequest));

    // When
    soapRequestService.deleteData(id);

    // Then
    verify(soapRequestRepository).delete(soapRequest);
  }
}
