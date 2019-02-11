package uk.gov.defra.tracesx.soaprequest.audit;

import com.fasterxml.jackson.databind.JsonNode;

public interface AuditService <T> {

  void create(String user, JsonNode created);

  void read(String user, String id);

  void delete(String user, String id);
}
