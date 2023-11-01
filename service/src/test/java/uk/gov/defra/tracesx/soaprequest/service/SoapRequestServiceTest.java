package uk.gov.defra.tracesx.soaprequest.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    soapRequest = createDefaultSoapRequestEntity();
  }

  private SoapRequestDto createDefaultSoapRequestDTO() {
    SoapRequestDto soapRequestDTO = new SoapRequestDto();
    soapRequestDTO.setQuery(QUERY);
    soapRequestDTO.setUsername(TEST_USER);
    return soapRequestDTO;
  }

  private SoapRequest createDefaultSoapRequestEntity() {
    UUID idSavedWith = UUID.randomUUID();
    long requestId = System.currentTimeMillis();
    SoapRequest soapRequest = new SoapRequest(TEST_USER, QUERY);
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
  void getByRequestIdCallsRepositoryWithId() {
    // Given
    when(soapRequestRepository.findByRequestId(any())).thenReturn(Optional.of(soapRequest));

    // When
    soapRequestService.getByRequestId(soapRequest.getRequestId());

    // Then
    verify(soapRequestRepository).findByRequestId(soapRequest.getRequestId());
  }

  @Test
  void getByRequestIdReturnsRecordFromRepository() {
    // Given
    when(soapRequestRepository.findByRequestId(any())).thenReturn(Optional.of(soapRequest));

    // When
    SoapRequestDto result = soapRequestService.getByRequestId(soapRequest.getRequestId()).get();

    // Then
    assertThat(soapRequest.getUsername()).isEqualTo(result.getUsername());
    assertThat(soapRequest.getQuery()).isEqualTo(result.getQuery());
    assertThat(soapRequest.getId()).isEqualTo(result.getId());
  }

  @Test
  void deleteCallsRepositoryOnceWithId() {
    // Given
    UUID id = UUID.randomUUID();
    when(soapRequestRepository.findById(any())).thenReturn(Optional.of(soapRequest));

    // When
    soapRequestService.deleteData(id);

    // Then
    verify(soapRequestRepository).deleteById(id);
  }
}
