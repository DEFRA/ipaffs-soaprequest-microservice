package uk.gov.defra.tracesx.soaprequest.audit.dao.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import uk.gov.defra.tracesx.soaprequest.audit.AuditRequestType;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "soaprequest_audit")
@ToString
public final class Audit {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
  private UUID id;

  @Column(name = "user_id")
  private String userId;

  private String data;

  @Column(name = "created_at", updatable = false, insertable = false)
  private LocalDateTime createdAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "type")
  private AuditRequestType type;

  @Column(name = "entity_id")
  private Long entityId;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Audit)) {
      return false;
    }
    Audit audit = (Audit) obj;
    return Objects.equals(id, audit.id)
        && Objects.equals(userId, audit.userId)
        && Objects.equals(data, audit.data)
        && Objects.equals(createdAt, audit.createdAt)
        && Objects.equals(type, audit.type)
        && Objects.equals(entityId, audit.entityId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, userId, data, createdAt, type, entityId);
  }
}
