package uk.gov.defra.tracesx.soaprequest.dao.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.defra.tracesx.soaprequest.dto.CacheRequestDto;

@Entity
@Builder
@Data
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "cacherequest")
public class CacheRequest {

  @Id
  private String id;
  private String value;
  private LocalDateTime createdDate;

  public CacheRequest() { //default builder
  }

  public static CacheRequest from(CacheRequestDto cacheRequestDto) {
    return CacheRequest.builder()
        .id(cacheRequestDto.getId())
        .value(cacheRequestDto.getValue())
        .createdDate(LocalDateTime.now())
        .build();
  }
}
