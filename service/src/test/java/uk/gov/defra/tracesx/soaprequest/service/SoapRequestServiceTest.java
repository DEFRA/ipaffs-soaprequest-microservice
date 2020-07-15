package uk.gov.defra.tracesx.soaprequest.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import uk.gov.defra.tracesx.soaprequest.audit.AuditServiceWrapper;
import uk.gov.defra.tracesx.soaprequest.dao.entities.SoapRequest;
import uk.gov.defra.tracesx.soaprequest.dao.repositories.SoapRequestRepository;
import uk.gov.defra.tracesx.soaprequest.dto.SoapRequestDto;

import java.util.Optional;
import java.util.UUID;

public class SoapRequestServiceTest {

  public static final String QUERY = "test";
  public static final String TEST_USER = "testUser";

  @Mock
  SoapRequestRepository soapRequestRepository;
  @Mock
  AuditServiceWrapper auditServiceWrapper;
  @Captor
  private ArgumentCaptor<SoapRequest> soapRequestCaptor;

  private SoapRequestService soapRequestService;
  private SoapRequest soapRequest;

  @Before
  public void setUp() {
    initMocks(this);
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
    Long requestId = System.currentTimeMillis();
    SoapRequest soapRequest = new SoapRequest(TEST_USER, QUERY);
    soapRequest.setId(idSavedWith);
    soapRequest.setRequestId(requestId);
    return soapRequest;
  }

  @Test
  public void createsSavesAsNewEntity() {
    // Given
    when(soapRequestRepository.save(any())).thenReturn(soapRequest);

    // When
    UUID entityId = soapRequestService.create(createDefaultSoapRequestDTO());

    // Then
    verify(soapRequestRepository).save(any());
    assertEquals(soapRequest.getId(), entityId);
  }

  @Test
  public void createSavesTheCorrectDataToTheRepository() {
    // Given
    when(soapRequestRepository.save(soapRequestCaptor.capture())).thenReturn(soapRequest);

    // When
    soapRequestService.create(createDefaultSoapRequestDTO());

    // Then
    assertEquals(null, soapRequestCaptor.getValue().getId());
    assertEquals(soapRequest.getUsername(), soapRequestCaptor.getValue().getUsername());
    assertEquals(soapRequest.getQuery(), soapRequestCaptor.getValue().getQuery());
  }

  @Test
  public void getCallsRepositoryWithId() {
    // Given
    when(soapRequestRepository.findById(any())).thenReturn(Optional.of(soapRequest));

    // When
    soapRequestService.get(soapRequest.getId());

    // Then
    verify(soapRequestRepository).findById(soapRequest.getId());
  }

  @Test
  public void getReturnsRecordFromRepository() {
    // Given
    when(soapRequestRepository.findById(any())).thenReturn(Optional.of(soapRequest));

    // When
    SoapRequestDto result = soapRequestService.get(soapRequest.getId()).get();

    // Then
    assertEquals(soapRequest.getUsername(), result.getUsername());
    assertEquals(soapRequest.getQuery(), result.getQuery());
    assertEquals(soapRequest.getId(), result.getId());
  }

  @Test
  public void getByRequestIdCallsRepositoryWithId() {
    // Given
    when(soapRequestRepository.findByRequestId(any())).thenReturn(Optional.of(soapRequest));

    // When
    soapRequestService.getByRequestId(soapRequest.getRequestId());

    // Then
    verify(soapRequestRepository).findByRequestId(soapRequest.getRequestId());
  }

  @Test
  public void getByRequestIdReturnsRecordFromRepository() {
    // Given
    when(soapRequestRepository.findByRequestId(any())).thenReturn(Optional.of(soapRequest));

    // When
    SoapRequestDto result = soapRequestService.getByRequestId(soapRequest.getRequestId()).get();

    // Then
    assertEquals(soapRequest.getUsername(), result.getUsername());
    assertEquals(soapRequest.getQuery(), result.getQuery());
    assertEquals(soapRequest.getId(), result.getId());
  }

  @Test
  public void deleteCallsRepositoryOnceWithId() {
    // Given
    UUID id = UUID.randomUUID();
    when(soapRequestRepository.findById(any())).thenReturn(Optional.of(soapRequest));

    // When
    soapRequestService.deleteData(id);

    // Then
    verify(soapRequestRepository).deleteById(id);
  }
}
