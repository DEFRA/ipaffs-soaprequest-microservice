package uk.gov.defra.tracesx.soaprequest.audit;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("uk.gov.defra.tracesx.soaprequest.audit.dao.repositories")
@Getter
public class AuditConfig {

  private final boolean auditOnRead;
  private final boolean auditOnCreate;
  private final boolean auditOnDelete;

  public AuditConfig(@Value("${audit.read:false}") boolean auditOnRead,
      @Value("${audit.delete:false}") boolean auditOnDelete,
      @Value("${audit.create:false}") boolean auditOnCreate
  ) {
    this.auditOnRead = auditOnRead;
    this.auditOnDelete = auditOnDelete;
    this.auditOnCreate = auditOnCreate;
  }

  public static final class Builder {

    private boolean auditOnRead;
    private boolean auditOnCreate;
    private boolean auditOnDelete;

    private Builder() { }

    public static Builder anAuditConfig() {
      return new Builder();
    }

    public Builder withAuditOnRead(boolean auditOnRead) {
      this.auditOnRead = auditOnRead;
      return this;
    }

    public Builder withAuditOnCreate(boolean auditOnCreate) {
      this.auditOnCreate = auditOnCreate;
      return this;
    }

    public Builder withAuditOnDelete(boolean auditOnDelete) {
      this.auditOnDelete = auditOnDelete;
      return this;
    }

    public AuditConfig build() {
      return new AuditConfig(auditOnRead , auditOnDelete, auditOnCreate);
    }
  }
}
