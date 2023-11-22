package uk.gov.defra.tracesx.soaprequest.audit.dao.entities;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class AuditTest {

  @Test
  void equals() {
    EqualsVerifier.forClass(Audit.class).usingGetClass().verify();
  }
}
