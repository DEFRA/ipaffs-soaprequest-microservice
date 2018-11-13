package uk.gov.defra.tracesx.soaprequest.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.defra.tracesx.soaprequest.dto.SoapRequestDTO;
import uk.gov.defra.tracesx.soaprequest.exceptions.BadRequestBodyException;
import uk.gov.defra.tracesx.soaprequest.service.SoapRequestService;

public class SoapRequestResourceTest {

  public static final String QUERY = "test";
  public static final String TEST_USER = "testUser";
  @Mock SoapRequestService soapRequestService;
  private SoapRequestDTO requestBody;
  private UUID id;
  private Long requestId;

  @Before
  public void setUp() throws IOException {
    initMocks(this);
    requestBody = createSoapRequestDTO();
    id = UUID.randomUUID();
    requestId = System.currentTimeMillis();
  }

  private SoapRequestDTO createSoapRequestDTO() {
    SoapRequestDTO requestBody = new SoapRequestDTO();
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
    ResponseEntity responseEntity = resource.insert(new SoapRequestDTO(id, 123L, "user", "query"));

    // Then
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertEquals("/soaprequest/" + id, responseEntity.getHeaders().getLocation().toString());
  }

  @Test(expected = BadRequestBodyException.class)
  public void insertThrowsBadRequestOnQueryMissing() throws URISyntaxException {
    // Given
    SoapRequestResource resource = new SoapRequestResource(soapRequestService);
    SoapRequestDTO requestDTO = new SoapRequestDTO();
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
    SoapRequestDTO requestDTO = new SoapRequestDTO();
    requestDTO.setQuery(QUERY);

    // When
    ResponseEntity responseEntity = resource.insert(requestDTO);

    // Then
    fail("Expecting a BadRequestBodyException to be thrown");
  }

  @Test
  public void getReturnsEntityFromService() throws IOException {
    // Given
    SoapRequestResource resource = new SoapRequestResource(soapRequestService);

    when(soapRequestService.get(any())).thenReturn(requestBody);

    // When
    ResponseEntity entity = resource.get(id);

    // Then
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(requestBody, entity.getBody());
  }

  @Test
  public void getByRequestIdReturnsEntityFromService() throws IOException {
    // Given
    SoapRequestResource resource = new SoapRequestResource(soapRequestService);

    when(soapRequestService.getByRequestId(any())).thenReturn(requestBody);

    // When
    ResponseEntity entity = resource.getByRequestId(requestId);

    // Then
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(requestBody, entity.getBody());
  }

  @Test
  public void deleteCallsServiceWithId() {
    // Given
    SoapRequestResource resource = new SoapRequestResource(soapRequestService);

    // When
    resource.deleteByRequestIdAndUsername(requestId, TEST_USER);

    // Then
    verify(soapRequestService, times(1)).deleteByRequestIdAndUsername(requestId, TEST_USER);
  }

  @Test
  public void deleteReturnsHttpStatusOkay() {
    // Given
    SoapRequestResource resource = new SoapRequestResource(soapRequestService);

    // When
    ResponseEntity entity = resource.deleteByRequestIdAndUsername(requestId, TEST_USER);

    // Then
    assertEquals(HttpStatus.OK, entity.getStatusCode());
  }

}
