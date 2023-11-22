package uk.gov.defra.tracesx.soaprequest.audit.dao.repositories;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.defra.tracesx.soaprequest.audit.dao.entities.Audit;

@ExtendWith(MockitoExtension.class)
class AuditRepositoryImplTest {

  @Mock
  private AuditJpaRepository repository;

  @InjectMocks
  private AuditRepositoryImpl auditRepositoryImpl;

  @Test
  void save() {
    Audit audit = new Audit();
    auditRepositoryImpl.save(audit);
    verify(repository, times(1)).save(audit);
  }
}
