
package uk.gov.defra.tracesx.soaprequest.resource;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.defra.tracesx.soaprequest.service.SoapRequestService;

public class SoapRequestResourceTest {

  private static final String EXAMPLE_PARSER = "{\"k1\":\"v1\"}";
  private static final String MERGE_PATCH_TYPE = "application/merge-patch+json";
  private static final String COMMAND_PATCH_TYPE = "application/json-patch+json";

  private JsonNode node;

  @Mock
  SoapRequestService soapRequestService;
  
  @Before
  public void setUp() throws IOException {
    initMocks(this);
    ObjectMapper mapper = new ObjectMapper();
    JsonFactory factory = mapper.getFactory();
    JsonParser jsonParser = factory.createParser(EXAMPLE_PARSER);
    node = mapper.readTree(jsonParser);
  }

  @Test
  public void insertCallsServiceWithPostedJson() throws URISyntaxException {
    //Given
    SoapRequestResource resource = new SoapRequestResource(soapRequestService);
    when(soapRequestService.create(any())).thenReturn(UUID.randomUUID());
    
    //When
    resource.insert(node);
    
    //Then
    verify(soapRequestService, times(1)).create(node);
  }

  @Test
  public void insertReturnsCreatedLocation() throws URISyntaxException {
    //Given
    SoapRequestResource resource = new SoapRequestResource(soapRequestService);
    UUID id = UUID.randomUUID();
    when(soapRequestService.create(any())).thenReturn(id);
    
    //When
    ResponseEntity responseEntity = resource.insert(null);
    
    //Then
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertEquals("/soapRequest/" + id.toString() ,responseEntity.getHeaders().getLocation().toString());
  }

  @Test
  public void getReturnsEntityFromService() throws IOException {
    //Given
    SoapRequestResource resource = new SoapRequestResource(soapRequestService);
    UUID id = UUID.randomUUID();
    when(soapRequestService.get(any())).thenReturn(node);
    
    //When
    ResponseEntity entity = resource.get(id);
    
    //Then
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(EXAMPLE_PARSER, entity.getBody().toString());
  }

  @Test
  public void patchCallsEntityServiceWithMergePatchType() throws IOException, JsonPatchException {
    //Given
    SoapRequestResource resource = new SoapRequestResource(soapRequestService);
    UUID id = UUID.randomUUID();
    
    //When
    resource.patch(id, MERGE_PATCH_TYPE, node);
    
    //Then
    verify(soapRequestService, times(1)).update(id, node, true);
  }

  @Test
  public void patchCallsEntityServiceWithCommandPatchType() throws IOException, JsonPatchException {
    //Given
    SoapRequestResource resource = new SoapRequestResource(soapRequestService);
    UUID id = UUID.randomUUID();
    
    //When
    resource.patch(id, COMMAND_PATCH_TYPE, node);
    
    //Then
    verify(soapRequestService, times(1)).update(id, node, false);
  }
  
  @Test
  public void patchCallsEntityServiceWithIdAndPatch() throws JsonPatchException, IOException {
    //Given
    SoapRequestResource resource = new SoapRequestResource(soapRequestService);
    UUID id = UUID.randomUUID();
    
    //When
    resource.patch(id, COMMAND_PATCH_TYPE, node);
    
    //Then
    verify(soapRequestService, times(1)).update(id, node, false);
  }
  
  @Test
  public void patchReturnsOkayStatus() throws IOException, JsonPatchException {
    //Given
    SoapRequestResource resource = new SoapRequestResource(soapRequestService);
    UUID id = UUID.randomUUID();
    
    //When
    ResponseEntity responseEntity = resource.patch(id, COMMAND_PATCH_TYPE, node);
    
    //Then
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  public void deleteCallsServiceWithId() {
    //Given
    SoapRequestResource resource = new SoapRequestResource(soapRequestService);
    UUID id = UUID.randomUUID();
    
    //When
    resource.delete(id);
    
    //Then
    verify(soapRequestService, times(1)).deleteData(id);
  }
  
  @Test
  public void deleteReturnsHttpStatusOkay() {
    //Given
    SoapRequestResource resource = new SoapRequestResource(soapRequestService);
    UUID id = UUID.randomUUID();
    
    //When
    ResponseEntity entity = resource.delete(id);
    
    //Then
    assertEquals(HttpStatus.OK, entity.getStatusCode());
  }
}
