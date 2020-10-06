package uk.gov.defra.tracesx.soaprequest.dao.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Builder
@AllArgsConstructor
@Table(name = "soaprequest")
public class SoapRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  private Long requestId = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
  private String username;
  private String query;
  private String lastUpdated = LocalDateTime.now()
      .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

  public SoapRequest() {
  }

  public SoapRequest(String username, String query) {
    this.username = username;
    this.query = query;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Long getRequestId() {
    return requestId;
  }

  public void setRequestId(long requestId) {
    this.requestId = requestId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public String getLastUpdated() {
    return lastUpdated;
  }
}
