package uk.gov.defra.tracesx.soaprequest.integration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SoapRequestDTO {
  /**
   * id.
   */
  private UUID id;
  /**
   * requestId.
   */
  private Long requestId;
  /**
   * username.
   */
  private String username;
  /**
   * query.
   */
  private String query;
}
