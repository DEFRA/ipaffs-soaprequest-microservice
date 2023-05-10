package uk.gov.defra.tracesx.soaprequest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.defra.tracesx.soaprequest.dao.entities.SoapRequest;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SoapRequestDto {

  private UUID id;
  private Long requestId;
  private String username;
  private String query;

  public static SoapRequestDto from(SoapRequest request) {
    return new SoapRequestDto(
        request.getId(), request.getRequestId(), request.getUsername(), request.getQuery());
  }
}
