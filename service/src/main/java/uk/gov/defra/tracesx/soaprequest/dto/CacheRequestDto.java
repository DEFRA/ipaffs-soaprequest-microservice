package uk.gov.defra.tracesx.soaprequest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.defra.tracesx.soaprequest.dao.entities.CacheRequest;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CacheRequestDto {

  private String id;
  private String value;

  public static CacheRequestDto from(CacheRequest request) {
    return new CacheRequestDto(request.getId(), request.getValue());
  }
}
