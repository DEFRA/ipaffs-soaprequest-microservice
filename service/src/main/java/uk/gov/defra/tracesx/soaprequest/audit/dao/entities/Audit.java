package uk.gov.defra.tracesx.soaprequest.audit.dao.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import uk.gov.defra.tracesx.soaprequest.audit.AuditRequestType;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name="soaprequest_audit")
@Builder
@AllArgsConstructor
@ToString
public final class Audit {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name="uuid", strategy = "org.hibernate.id.UUIDGenerator")
  private UUID id;

  @Column(name="user_id")
  private String userId;

  private String data;

  @Column(name="created_at", updatable = false, insertable = false)
  private LocalDateTime createdAt;

  @Enumerated(EnumType.STRING)
  @Column(name="type")
  private AuditRequestType type;

  public Audit(String userId, String data, AuditRequestType type) {
    this.userId = userId;
    this.data = data;
    this.type = type;
  }

  public Audit() { }

  public UUID getId() {
    return id;
  }

  public String getUserId() {
    return userId;
  }

  public String getData() {
    return data;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public AuditRequestType getType() {
    return type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Audit)) {
      return false;
    }
    Audit audit = (Audit) o;
    return Objects.equals(id, audit.id) &&
        Objects.equals(userId, audit.userId) &&
        Objects.equals(data, audit.data) &&
        Objects.equals(createdAt, audit.createdAt) &&
        Objects.equals(type, audit.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, userId, data, createdAt, type);
  }
}
