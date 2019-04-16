package uk.gov.defra.tracesx.soaprequest.audit.dao.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import uk.gov.defra.tracesx.soaprequest.audit.dao.entities.Audit;

@Repository
public class AuditRepositoryImpl implements AuditRepository<Audit> {

  private final AuditJpaRepository repository;

  @Autowired
  public AuditRepositoryImpl(AuditJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public Audit save(
      Audit obj) {
    return repository.save(obj);
  }
}
