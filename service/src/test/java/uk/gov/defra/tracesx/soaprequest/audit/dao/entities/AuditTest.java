package uk.gov.defra.tracesx.soaprequest.audit.dao.entities;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

class AuditTest {

  @Test
  void equals() {
    EqualsVerifier.forClass(Audit.class).usingGetClass().suppress(Warning.NONFINAL_FIELDS).verify();
  }
}
