package uk.gov.defra.tracesx.soaprequest.audit.dao.repositories;

import org.springframework.data.repository.CrudRepository;
import uk.gov.defra.tracesx.soaprequest.audit.dao.entities.Audit;

import java.util.UUID;

public interface AuditJpaRepository extends CrudRepository<Audit, UUID> {

}
