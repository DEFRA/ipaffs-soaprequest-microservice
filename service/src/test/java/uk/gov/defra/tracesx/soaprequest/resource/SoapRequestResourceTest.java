package uk.gov.defra.tracesx.soaprequest.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.defra.tracesx.soaprequest.dto.SoapRequestDto;
import uk.gov.defra.tracesx.soaprequest.exceptions.BadRequestBodyException;
import uk.gov.defra.tracesx.soaprequest.exceptions.NotFoundException;
import uk.gov.defra.tracesx.soaprequest.service.SoapRequestService;

import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;

public class SoapRequestResourceTest {

  public static final String QUERY = "test";
  public static final String TEST_USER = "testUser";
  @Mock
  SoapRequestService soapRequestService;
  private SoapRequestDto requestBody;
  private UUID id;
  private Long requestId;

  @Before
  public void setUp() {
    initMocks(this);
    requestBody = createSoapRequestDTO();
    id = UUID.randomUUID();
    requestId = System.currentTimeMillis();
  }

  private SoapRequestDto createSoapRequestDTO() {
    SoapRequestDto requestBody = new SoapRequestDto();
    requestBody.setQuery(QUERY);
    requestBody.setUsername(TEST_USER);
    requestBody.setRequestId(System.currentTimeMillis());
    return requestBody;
  }

  @Test
  public void insertCallsServiceWithPostedJson() throws URISyntaxException {
    // Given
    SoapRequestResource resource = new SoapRequestResource(soapRequestService);
    when(soapRequestService.create(any())).thenReturn(id);
    // When
    resource.insert(requestBody);

    // Then
    verify(soapRequestService, times(1)).create(requestBody);
  }

  @Test
  public void insertReturnsCreatedLocation() throws URISyntaxException {
    // Given
    SoapRequestResource resource = new SoapRequestResource(soapRequestService);

    when(soapRequestService.create(any())).thenReturn(id);

    // When
    ResponseEntity responseEntity = resource.insert(new SoapRequestDto(id, 123L, "user", "query"));

    // Then
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertEquals("/soaprequest/" + id, responseEntity.getHeaders().getLocation().toString());
  }

  @Test(expected = BadRequestBodyException.class)
  public void insertThrowsBadRequestOnQueryMissing() throws URISyntaxException {
    // Given
    SoapRequestResource resource = new SoapRequestResource(soapRequestService);
    SoapRequestDto requestDTO = new SoapRequestDto();
    requestDTO.setUsername(TEST_USER);

    // When
    ResponseEntity responseEntity = resource.insert(requestDTO);

    // Then
    fail("Expecting a BadRequestBodyException to be thrown");
  }

  @Test(expected = BadRequestBodyException.class)
  public void insertThrowsBadRequestOnUsernameMissing() throws URISyntaxException {
    // Given
    SoapRequestResource resource = new SoapRequestResource(soapRequestService);
    SoapRequestDto requestDTO = new SoapRequestDto();
    requestDTO.setQuery(QUERY);

    // When
    ResponseEntity responseEntity = resource.insert(requestDTO);

    // Then
    fail("Expecting a BadRequestBodyException to be thrown");
  }

  @Test
  public void getReturnsEntityFromService() {
    // Given
    SoapRequestResource resource = new SoapRequestResource(soapRequestService);

    when(soapRequestService.get(any())).thenReturn(Optional.ofNullable(requestBody));

    // When
    ResponseEntity entity = resource.get(id);

    // Then
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(requestBody, entity.getBody());
  }

  @Test(expected = NotFoundException.class)
  public void getThrowsNotFoundOnNonExistingId() {
    // Given
    SoapRequestResource resource = new SoapRequestResource(soapRequestService);

    when(soapRequestService.get(any())).thenReturn(Optional.ofNullable(null));

    // When
    ResponseEntity entity = resource.get(id);
  }

  @Test
  public void getByRequestIdReturnsEntityFromService() {
    // Given
    SoapRequestResource resource = new SoapRequestResource(soapRequestService);

    when(soapRequestService.getByRequestId(any())).thenReturn(Optional.ofNullable(requestBody));

    // When
    ResponseEntity entity = resource.getByRequestId(requestId);

    // Then
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(requestBody, entity.getBody());
  }

  @Test(expected = NotFoundException.class)
  public void getByRequestIdThrowsNotFoundOnNonExistentRequestId() {
    // Given
    SoapRequestResource resource = new SoapRequestResource(soapRequestService);

    when(soapRequestService.getByRequestId(any())).thenReturn(Optional.ofNullable(null));

    // When
    ResponseEntity entity = resource.getByRequestId(requestId);
  }

  @Test
  public void deleteCallsServiceWithId() {
    // Given
    SoapRequestResource resource = new SoapRequestResource(soapRequestService);

    // When
    resource.delete(id);

    // Then
    verify(soapRequestService, times(1)).deleteData(id);
  }

  @Test
  public void deleteReturnsHttpStatusOkay() {
    // Given
    SoapRequestResource resource = new SoapRequestResource(soapRequestService);

    // When
    ResponseEntity entity = resource.delete(id);

    // Then
    assertEquals(HttpStatus.OK, entity.getStatusCode());
  }

}
