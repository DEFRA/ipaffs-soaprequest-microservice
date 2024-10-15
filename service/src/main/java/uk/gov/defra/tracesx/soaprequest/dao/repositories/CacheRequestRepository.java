package uk.gov.defra.tracesx.soaprequest.dao.repositories;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import uk.gov.defra.tracesx.soaprequest.dao.entities.CacheRequest;

//@Repository
public interface CacheRequestRepository extends CrudRepository<CacheRequest, String> {

  List<CacheRequest> findAllByIdIn(List<String> id);
}
