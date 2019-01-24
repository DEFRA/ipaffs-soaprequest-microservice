package uk.gov.defra.tracesx.soaprequest.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InsSecurityException extends RuntimeException {

  public InsSecurityException(String message) {
    super(message);
  }
}
