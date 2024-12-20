package uk.gov.defra.tracesx.soaprequest.dao.entities;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cacherequest")
public class CacheRequest {

  @Id
  private String id;
  @Embedded
  private ChedCertificateHash chedCertificateHash;
  private LocalDateTime createdDate;

  public record ChedCertificateHash(String value) {}

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    if (!(obj instanceof CacheRequest cacheRequest)) {
      return false;
    }
    return Objects.equals(this.id, cacheRequest.id)
        && Objects.equals(this.chedCertificateHash, cacheRequest.chedCertificateHash);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.chedCertificateHash);
  }
}
