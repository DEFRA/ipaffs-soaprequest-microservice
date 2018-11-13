package uk.gov.defra.tracesx.soaprequest.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import org.everit.json.schema.Schema;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import uk.gov.defra.tracesx.soaprequest.dao.entities.SoapRequest;
import uk.gov.defra.tracesx.soaprequest.dao.repositories.SoapRequestRepository;
import uk.gov.defra.tracesx.soaprequest.dto.SoapRequestDTO;

public class SoapRequestServiceTest {

  public static final String QUERY = "test";
  public static final String TEST_USER = "testUser";

  @Mock SoapRequestRepository soapRequestRepository;
  private SoapRequestService soapRequestService;

  @Before
  public void setUp() throws IOException {
    initMocks(this);
    soapRequestService = new SoapRequestService(soapRequestRepository);
  }

  private SoapRequestDTO createDefaultSoapRequestDTO() {
    SoapRequestDTO soapRequestDTO = new SoapRequestDTO();
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
  public void createsSavesAsNewEntity() throws IOException {
    // Given
    SoapRequest soapRequest = createDefaultSoapRequestEntity();
    when(soapRequestRepository.save(any())).thenReturn(soapRequest);

    // When
    UUID entityId = soapRequestService.create(createDefaultSoapRequestDTO());

    // Then
    verify(soapRequestRepository, times(1)).save(any());
    assertEquals(soapRequest.getId(), entityId);
  }

  @Test
  public void createSavesTheCorrectDataToTheRepository() throws IOException {
    // Given
    SoapRequest soapRequest = createDefaultSoapRequestEntity();

    ArgumentCaptor<SoapRequest> captor = ArgumentCaptor.forClass(SoapRequest.class);
    when(soapRequestRepository.save(captor.capture())).thenReturn(soapRequest);

    // When
    soapRequestService.create(createDefaultSoapRequestDTO());

    // Then
    assertEquals(null, captor.getValue().getId());
    assertEquals(soapRequest.getUsername(), captor.getValue().getUsername());
    assertEquals(soapRequest.getQuery(), captor.getValue().getQuery());
  }

  @Test
  public void getCallsRepositoryWithId() throws IOException {
    // Given
    SoapRequest soapRequest = createDefaultSoapRequestEntity();
    when(soapRequestRepository.findById(any())).thenReturn(Optional.of(soapRequest));

    // When
    soapRequestService.get(soapRequest.getId());

    // Then
    verify(soapRequestRepository, times(1)).findById(soapRequest.getId());
  }

  @Test
  public void getReturnsDocumentFromRepository() throws IOException {
    // Given
    SoapRequest soapRequest = createDefaultSoapRequestEntity();
    when(soapRequestRepository.findById(any())).thenReturn(Optional.of(soapRequest));

    // When
    SoapRequestDTO result = soapRequestService.get(soapRequest.getId());

    // Then
    assertEquals(soapRequest.getUsername(), result.getUsername());
    assertEquals(soapRequest.getQuery(), result.getQuery());
    assertEquals(soapRequest.getId(), result.getId());
  }

  @Test
  public void getByRequestIdCallsRepositoryWithId() throws IOException {
    // Given
    SoapRequest soapRequest = createDefaultSoapRequestEntity();
    when(soapRequestRepository.findByRequestId(any())).thenReturn(Optional.of(soapRequest));

    // When
    soapRequestService.getByRequestId(soapRequest.getRequestId());

    // Then
    verify(soapRequestRepository, times(1)).findByRequestId(soapRequest.getRequestId());
  }

  @Test
  public void getByRequestIdReturnsDocumentFromRepository() throws IOException {
    // Given
    SoapRequest soapRequest = createDefaultSoapRequestEntity();
    when(soapRequestRepository.findByRequestId(any())).thenReturn(Optional.of(soapRequest));

    // When
    SoapRequestDTO result = soapRequestService.getByRequestId(soapRequest.getRequestId());

    // Then
    assertEquals(soapRequest.getUsername(), result.getUsername());
    assertEquals(soapRequest.getQuery(), result.getQuery());
    assertEquals(soapRequest.getId(), result.getId());
  }

  @Test
  public void deleteCallsRepositoryOnceWithId() {
    // Given
    Long requestId = new Random().nextLong();

    // When
    soapRequestService.deleteByRequestIdAndUsername(requestId, TEST_USER);

    // Then
    verify(soapRequestRepository, times(1)).deleteByRequestIdAndUsername(requestId, TEST_USER);
  }

  @Test
  public void testGetByRequestIdAndUsernameReturnsCorrectResult() {
    // Given
    SoapRequest soapRequest = createDefaultSoapRequestEntity();
    when(soapRequestRepository.findByRequestIdAndUsername(anyLong(), anyString()))
        .thenReturn(Optional.of(soapRequest));

    // When
    SoapRequestDTO result =
        soapRequestService.get(soapRequest.getRequestId(), soapRequest.getUsername());

    // Then
    assertEquals(soapRequest.getUsername(), result.getUsername());
    assertEquals(soapRequest.getQuery(), result.getQuery());
    assertEquals(soapRequest.getId(), result.getId());
    assertEquals(soapRequest.getRequestId(), soapRequest.getRequestId());
  }
}
