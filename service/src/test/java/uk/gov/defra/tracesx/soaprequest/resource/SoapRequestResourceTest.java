package uk.gov.defra.tracesx.soaprequest.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.defra.tracesx.soaprequest.dto.SoapRequestDto;
import uk.gov.defra.tracesx.soaprequest.exceptions.BadRequestBodyException;
import uk.gov.defra.tracesx.soaprequest.exceptions.NotFoundException;
import uk.gov.defra.tracesx.soaprequest.service.SoapRequestService;

@ExtendWith(MockitoExtension.class)
class SoapRequestResourceTest {

  private static final String QUERY = "test";
  private static final String TEST_USER = "testUser";
  private static final String USERNAME = "username";

  @Mock
  SoapRequestService soapRequestService;
  @InjectMocks
  SoapRequestResource soapRequestResource;
  private SoapRequestDto requestBody;
  private UUID id;
  private Long requestId;

  @BeforeEach
  void setUp() {
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
  void insertCallsServiceWithPostedJson() throws URISyntaxException {
    // Given
    when(soapRequestService.create(any())).thenReturn(id);

    // When
    soapRequestResource.insert(requestBody);

    // Then
    verify(soapRequestService, times(1)).create(requestBody);
  }

  @Test
  void insertReturnsCreatedLocation() throws URISyntaxException {
    // Given
    when(soapRequestService.create(any())).thenReturn(id);

    // When
    ResponseEntity<URI> responseEntity = soapRequestResource.insert(
        new SoapRequestDto(id, 123L, "user", "query"));

    // Then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseEntity.getHeaders().getLocation().toString()).hasToString(
        "/soaprequest/" + id);
  }

  @Test
  void insertThrowsBadRequestOnQueryMissing() {
    // Given
    SoapRequestDto requestDTO = new SoapRequestDto();
    requestDTO.setUsername(TEST_USER);

    assertThatThrownBy(() -> soapRequestResource.insert(requestDTO)).isInstanceOf(
            BadRequestBodyException.class)
        .hasMessageContaining("The query and username fields are required");
  }

  @Test
  void insertThrowsBadRequestOnUsernameMissing() {
    SoapRequestDto requestDTO = new SoapRequestDto();
    requestDTO.setQuery(QUERY);

    assertThatThrownBy(() -> soapRequestResource.insert(requestDTO)).isInstanceOf(
            BadRequestBodyException.class)
        .hasMessageContaining("The query and username fields are required");
  }

  @Test
  void insertThrowsBadRequestOnSoapRequestBeingNull() {
    assertThatThrownBy(() -> soapRequestResource.insert(null)).isInstanceOf(
            BadRequestBodyException.class)
        .hasMessageContaining("The query and username fields are required");
  }

  @Test
  void getReturnsEntityFromService() {
    // Given
    when(soapRequestService.get(any())).thenReturn(Optional.ofNullable(requestBody));

    // When
    ResponseEntity<SoapRequestDto> entity = soapRequestResource.get(id);

    // Then
    assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(entity.getBody()).isEqualTo(requestBody);
  }

  @Test
  void getThrowsNotFoundOnNonExistingId() {
    // Given
    when(soapRequestService.get(any())).thenReturn(Optional.empty());

    // When
    assertThrows(NotFoundException.class, () -> soapRequestResource.get(id));
  }

  @Test
  void getByRequestIdAndUsernameReturnsEntityFromService() {
    // Given
    when(soapRequestService.getByRequestIdAndUsername(any(), any())).thenReturn(Optional.ofNullable(requestBody));

    // When
    ResponseEntity<SoapRequestDto> entity = soapRequestResource.getByRequestIdAndUsername(requestId, USERNAME);

    // Then
    assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(entity.getBody()).isEqualTo(requestBody);
  }

  @Test
  void getByRequestIdAndUsernameThrowsNotFoundOnNonExistentRequestId() {
    // Given
    when(soapRequestService.getByRequestIdAndUsername(any(), any())).thenReturn(Optional.empty());

    // When
    assertThrows(NotFoundException.class, () -> soapRequestResource.getByRequestIdAndUsername(requestId, USERNAME));
  }

  @Test
  void deleteCallsServiceWithId() {
    // When
    soapRequestResource.delete(id);

    // Then
    verify(soapRequestService, times(1)).deleteData(id);
  }

  @Test
  void deleteReturnsHttpStatusOkay() {
    // When
    ResponseEntity<HttpStatus> entity = soapRequestResource.delete(id);

    // Then
    assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
  }
}
