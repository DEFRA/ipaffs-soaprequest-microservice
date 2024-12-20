package uk.gov.defra.tracesx.soaprequest.dao.repositories;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.defra.tracesx.soaprequest.dao.entities.SoapRequest;

@Repository
public interface SoapRequestRepository extends CrudRepository<SoapRequest, UUID> {

  List<SoapRequest> findAllByRequestId(Long requestId);
}
