package uk.gov.defra.tracesx.soaprequest.integration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SoapRequestDTO {
  private UUID id;
  private Long requestId;
  private String username;
  private String query;
}
