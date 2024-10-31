package uk.gov.defra.tracesx.soaprequest.resource;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static uk.gov.defra.tracesx.soaprequest.util.Constants.SOAP_REQUEST_ENDPOINT;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.defra.tracesx.soaprequest.dto.SoapRequestDto;
import uk.gov.defra.tracesx.soaprequest.exceptions.BadRequestBodyException;
import uk.gov.defra.tracesx.soaprequest.exceptions.NotFoundException;
import uk.gov.defra.tracesx.soaprequest.service.SoapRequestService;

@RestController
@RequestMapping(SOAP_REQUEST_ENDPOINT)
public class SoapRequestResource {

  private static final String PATH_DELIMITER = "/";

  private static final Logger LOGGER = LoggerFactory.getLogger(SoapRequestResource.class);

  private final SoapRequestService soapRequestService;

  @Autowired
  public SoapRequestResource(SoapRequestService soapRequestService) {
    this.soapRequestService = soapRequestService;
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasAuthority('soaprequest.create')")
  public ResponseEntity<URI> insert(@RequestBody SoapRequestDto soapRequest)
      throws URISyntaxException {
    validate(soapRequest);
    UUID id = soapRequestService.create(soapRequest);
    LOGGER.debug("POST id: {}", id);
    URI createdLocation = new URI(SOAP_REQUEST_ENDPOINT + PATH_DELIMITER + id.toString());
    return ResponseEntity.created(createdLocation).build();
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasAuthority('soaprequest.read')")
  public ResponseEntity<SoapRequestDto> get(@PathVariable("id") UUID id) {
    LOGGER.debug("GET id: {}", id);
    Optional<SoapRequestDto> soapRequestDto = soapRequestService.get(id);
    return ResponseEntity.ok(soapRequestDto.orElseThrow(NotFoundException::new));
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasAuthority('soaprequest.read')")
  public ResponseEntity<List<SoapRequestDto>> getAllByRequestId(
      @RequestParam("requestId") Long requestId) {
    LOGGER.debug("GET requestId: {}", requestId);
    List<SoapRequestDto> soapRequestDtoList = soapRequestService.getAllByRequestId(requestId);
    if (soapRequestDtoList.isEmpty()) {
      throw new NotFoundException();
    }
    return ResponseEntity.ok(soapRequestDtoList);
  }

  @DeleteMapping(value = "/{id}")
  @PreAuthorize("hasAuthority('soaprequest.delete')")
  public ResponseEntity<HttpStatus> delete(@PathVariable("id") UUID id) {
    LOGGER.debug("DELETE id: {}", id);
    soapRequestService.deleteData(id);
    return ResponseEntity.ok().build();
  }

  private void validate(SoapRequestDto soapRequest) {
    if (soapRequest == null
        || isBlank(soapRequest.getQuery())
        || isBlank(soapRequest.getUsername())) {
      throw new BadRequestBodyException("The query and username fields are required");
    }
  }
}
