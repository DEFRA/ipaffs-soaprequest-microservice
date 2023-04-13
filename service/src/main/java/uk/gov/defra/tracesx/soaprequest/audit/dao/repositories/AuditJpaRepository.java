package uk.gov.defra.tracesx.soaprequest.audit.dao.repositories;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import uk.gov.defra.tracesx.soaprequest.audit.dao.entities.Audit;

public interface AuditJpaRepository extends CrudRepository<Audit, UUID> {

}
