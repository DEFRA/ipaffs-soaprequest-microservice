package uk.gov.defra.tracesx.soaprequest.resource;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static uk.gov.defra.tracesx.soaprequest.util.Constants.SOAP_REQUEST_ENDPOINT;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.defra.tracesx.soaprequest.dto.SoapRequestDTO;
import uk.gov.defra.tracesx.soaprequest.exceptions.BadRequestBodyException;
import uk.gov.defra.tracesx.soaprequest.service.SoapRequestService;

@RestController
@RequestMapping(SOAP_REQUEST_ENDPOINT)
public class SoapRequestResource {

  private static final Logger LOGGER = LoggerFactory.getLogger(SoapRequestResource.class);

  private final SoapRequestService soapRequestService;

  @Autowired
  public SoapRequestResource(SoapRequestService soapRequestService) {
    this.soapRequestService = soapRequestService;
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity insert(@RequestBody SoapRequestDTO soapRequest) throws URISyntaxException {
    validate(soapRequest);
    UUID id = soapRequestService.create(soapRequest);
    LOGGER.debug("POST id: {}", id);
    URI createdLocation = new URI(SOAP_REQUEST_ENDPOINT + "/" + id.toString());
    return ResponseEntity.created(createdLocation).build();
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity get(@PathVariable("id") UUID id) throws IOException {
    ResponseEntity response = ResponseEntity.ok(soapRequestService.get(id));
    LOGGER.info("GET id: {}", id);
    return response;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity getByRequestId(@RequestParam("requestId") Long requestId) throws IOException {
    ResponseEntity response = ResponseEntity.ok(soapRequestService.getByRequestId(requestId));
    LOGGER.info("GET requestId: {}", requestId);
    return response;
  }


  @DeleteMapping
  public ResponseEntity deleteByRequestIdAndUsername(@RequestParam("requestId") Long requestId, @RequestParam("username") String username) {
    soapRequestService.deleteByRequestIdAndUsername(requestId, username);
    LOGGER.debug("DELETE requestId: {} username: {}", requestId, username);
    return ResponseEntity.ok().build();
  }

  private void validate(SoapRequestDTO soapRequest) {
    if (soapRequest == null
        || isBlank(soapRequest.getQuery())
        || isBlank(soapRequest.getUsername())) {
      throw new BadRequestBodyException("The query and username fields are required");
    }
  }
}
