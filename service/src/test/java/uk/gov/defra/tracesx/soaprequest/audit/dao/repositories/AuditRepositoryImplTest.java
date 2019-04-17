package uk.gov.defra.tracesx.soaprequest.audit.dao.repositories;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import uk.gov.defra.tracesx.soaprequest.audit.dao.entities.Audit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class AuditRepositoryImplTest {

  @Mock
  private AuditJpaRepository repository;

  private AuditRepositoryImpl auditRepositoryImpl;

  @Before
  public void init() {
    initMocks(this);
    this.auditRepositoryImpl = new AuditRepositoryImpl(repository);
  }

  @Test
  public void save() {
    this.auditRepositoryImpl.save(new Audit());
    verify(repository, times(1)).save(any(Audit.class));
  }
}
