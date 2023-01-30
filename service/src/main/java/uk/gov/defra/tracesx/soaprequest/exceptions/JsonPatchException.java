package uk.gov.defra.tracesx.soaprequest.exceptions;

public final class JsonPatchException extends Exception {

  public JsonPatchException(String message) {
    super(message);
  }

  public JsonPatchException(String message, Throwable cause) {
    super(message, cause);
  }
}
