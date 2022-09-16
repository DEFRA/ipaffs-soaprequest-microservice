package uk.gov.defra.tracesx.soaprequest.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.defra.tracesx.soaprequest.audit.AuditServiceImpl;
import uk.gov.defra.tracesx.soaprequest.dto.SoapRequestDto;
import uk.gov.defra.tracesx.soaprequest.exceptions.BadRequestBodyException;
import uk.gov.defra.tracesx.soaprequest.exceptions.NotFoundException;
import uk.gov.defra.tracesx.soaprequest.service.SoapRequestService;

@RunWith(MockitoJUnitRunner.class)
public class SoapRequestResourceTest {

  public static final String QUERY = "test";
  public static final String TEST_USER = "testUser";
  private AuditServiceImpl auditService;
  private SoapRequestDto requestBody;
  private UUID id;
  private Long requestId;

  @Mock
  SoapRequestService soapRequestService;
  @InjectMocks
  SoapRequestResource soapRequestResource;

  @Before
  public void setUp() {
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
    when(soapRequestService.create(any())).thenReturn(id);

    // When
    soapRequestResource.insert(requestBody);

    // Then
    verify(soapRequestService, times(1)).create(requestBody);
  }

  @Test
  public void insertReturnsCreatedLocation() throws URISyntaxException {
    // Given
    when(soapRequestService.create(any())).thenReturn(id);

    // When
    ResponseEntity responseEntity = soapRequestResource.insert(new SoapRequestDto(id, 123L, "user", "query"));

    // Then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseEntity.getHeaders().getLocation().toString()).isEqualTo("/soaprequest/" + id);
  }

  @Test
  public void insertThrowsBadRequestOnQueryMissing() {
    // Given
    SoapRequestDto requestDTO = new SoapRequestDto();
    requestDTO.setUsername(TEST_USER);

    assertThatThrownBy(() -> soapRequestResource.insert(requestDTO)).isInstanceOf(BadRequestBodyException.class)
        .hasMessageContaining("The query and username fields are required");
  }

  @Test
  public void insertThrowsBadRequestOnUsernameMissing() {
    SoapRequestDto requestDTO = new SoapRequestDto();
    requestDTO.setQuery(QUERY);

    assertThatThrownBy(() -> soapRequestResource.insert(requestDTO)).isInstanceOf(BadRequestBodyException.class)
        .hasMessageContaining("The query and username fields are required");
  }

  @Test
  public void insertThrowsBadRequestOnSoapRequestBeingNull() {
    assertThatThrownBy(() -> soapRequestResource.insert(null)).isInstanceOf(BadRequestBodyException.class)
        .hasMessageContaining("The query and username fields are required");
  }

  @Test
  public void getReturnsEntityFromService() {
    // Given
    when(soapRequestService.get(any())).thenReturn(Optional.ofNullable(requestBody));

    // When
    ResponseEntity entity = soapRequestResource.get(id);

    // Then
    assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(entity.getBody()).isEqualTo(requestBody);
  }

  @Test(expected = NotFoundException.class)
  public void getThrowsNotFoundOnNonExistingId() {
    // Given
    when(soapRequestService.get(any())).thenReturn(Optional.ofNullable(null));

    // When
    soapRequestResource.get(id);
  }

  @Test
  public void getByRequestIdReturnsEntityFromService() {
    // Given
    when(soapRequestService.getByRequestId(any())).thenReturn(Optional.ofNullable(requestBody));

    // When
    ResponseEntity entity = soapRequestResource.getByRequestId(requestId);

    // Then
    assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(entity.getBody()).isEqualTo(requestBody);
  }

  @Test(expected = NotFoundException.class)
  public void getByRequestIdThrowsNotFoundOnNonExistentRequestId() {
    // Given
    when(soapRequestService.getByRequestId(any())).thenReturn(Optional.ofNullable(null));

    // When
    soapRequestResource.getByRequestId(requestId);
  }

  @Test
  public void deleteCallsServiceWithId() {
    // When
    soapRequestResource.delete(id);

    // Then
    verify(soapRequestService, times(1)).deleteData(id);
  }

  @Test
  public void deleteReturnsHttpStatusOkay() {
    // When
    ResponseEntity entity = soapRequestResource.delete(id);

    // Then
    assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

}
