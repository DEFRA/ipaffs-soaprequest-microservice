package uk.gov.defra.tracesx.soaprequest.dao.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.defra.tracesx.soaprequest.dao.entities.SoapRequest;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SoapRequestRepository extends CrudRepository<SoapRequest, UUID> {

  Optional<SoapRequest> findByRequestId(Long requestId);
}
