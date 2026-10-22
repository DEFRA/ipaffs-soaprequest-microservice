package uk.gov.defra.tracesx.soaprequest.audit;

import tools.jackson.databind.JsonNode;

public interface AuditService {

  void create(String user, JsonNode created);

  void read(String user, JsonNode current);

  void delete(String user, JsonNode deleted);
}
