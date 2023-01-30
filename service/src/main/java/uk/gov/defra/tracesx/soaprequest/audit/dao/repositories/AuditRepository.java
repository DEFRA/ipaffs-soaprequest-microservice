package uk.gov.defra.tracesx.soaprequest.audit.dao.repositories;

import uk.gov.defra.tracesx.soaprequest.audit.dao.entities.Audit;

public interface AuditRepository<T> {

  Audit save(T obj);

}
