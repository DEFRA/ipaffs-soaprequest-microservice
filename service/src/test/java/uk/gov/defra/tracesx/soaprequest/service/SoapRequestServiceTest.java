package uk.gov.defra.tracesx.soaprequest.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import uk.gov.defra.tracesx.soaprequest.dao.entities.SoapRequest;
import uk.gov.defra.tracesx.soaprequest.dao.repositories.SoapRequestRepository;

public class SoapRequestServiceTest {

  @Mock
  SoapRequestRepository soapRequestRepository;
  @Mock
  Schema schema;
  
  private JsonNode entity;
  private SoapRequestService soapRequestService;
  
  @Before
  public void setUp() throws IOException {
    initMocks(this);
    soapRequestService = new SoapRequestService(soapRequestRepository);
    ObjectMapper mapper = new ObjectMapper();
    JsonFactory factory = mapper.getFactory();
    JsonParser jsonParser = factory.createParser("{\"soapRequest\":\"aName\"}");
    entity = mapper.readTree(jsonParser);
  }
  
  @Test
  public void soapRequestServiceLoadsSchema() throws IOException {
    //Given
    SoapRequestService soapRequestServiceNew;
    
    //When
    soapRequestServiceNew = new SoapRequestService(soapRequestRepository);
    
    //Then
    assertEquals("SoapRequest schema", soapRequestServiceNew.schema.getDescription());
  }

  @Test (expected = ValidationException.class)
  public void createValidatesAgainstSchema() throws IOException {
    //Given
    ObjectMapper mapper = new ObjectMapper();
    JsonFactory factory = mapper.getFactory();
    JsonParser jsonParser = factory.createParser("{\"k1\":\"v1\"}");
    JsonNode invalidEntity = mapper.readTree(jsonParser);
    
    //When
    soapRequestService.create(invalidEntity);
    
    //Then
    //Validation exception thrown
  }

  @Test
  public void createsSavesAsNewEntity() throws IOException {
    //Given
    UUID idSavedWith = UUID.randomUUID();
    SoapRequest soapRequest = new SoapRequest();
    soapRequest.setId(idSavedWith);
    when(soapRequestRepository.save(any())).thenReturn(soapRequest);
    
    //When
    UUID entityId = soapRequestService.create(entity);
    
    //Then
    verify(soapRequestRepository, times(1)).save(any());
    assertEquals(idSavedWith, entityId);
  }

  @Test
  public void createSavesPassedJsonObject() throws IOException {
    //Given
    UUID idSavedWith = UUID.randomUUID();
    SoapRequest soapRequest = new SoapRequest();
    soapRequest.setId(idSavedWith);
    ArgumentCaptor<SoapRequest> captorForDocument = ArgumentCaptor.forClass(SoapRequest.class);
    when(soapRequestRepository.save(captorForDocument.capture())).thenReturn(soapRequest);
    
    //When
    soapRequestService.create(entity);
    
    //Then
    assertEquals(null, captorForDocument.getValue().getId());
    assertEquals(entity.toString(), captorForDocument.getValue().getDocument());
  }

  @Test
  public void getCallsRepositoryWithId() throws IOException {
    //Given
    UUID id = UUID.randomUUID();
    SoapRequest record = new SoapRequest();
    record.setId(id);
    record.setDocument("");
    when(soapRequestRepository.findById(any())).thenReturn(Optional.of(record));
    
    //When
    soapRequestService.get(id);
    
    //Then
    verify(soapRequestRepository, times(1)).findById(id);
    verify(soapRequestRepository, times(1)).findById(any());
  }

  @Test
  public void getReturnsDocumentFromRepository() throws IOException {
    //Given
    UUID id = UUID.randomUUID();
    SoapRequest record = new SoapRequest();
    record.setId(id);
    record.setDocument(entity.toString());
    when(soapRequestRepository.findById(any())).thenReturn(Optional.of(record));
    
    //When
    JsonNode entityReturned = soapRequestService.get(id);
    
    //Then
    assertEquals("aName", entityReturned.get("soapRequest").textValue());
  }

  @Test
  public void updateGetsDocumentFromRepository() throws JsonPatchException, IOException {
    //Given
    UUID id = UUID.randomUUID();
    SoapRequest record = new SoapRequest();
    record.setId(id);
    record.setDocument(entity.toString());
    when(soapRequestRepository.findById(id)).thenReturn(Optional.of(record));
    ObjectMapper mapper = new ObjectMapper();
    JsonFactory factory = mapper.getFactory();
    JsonParser jsonParser = factory.createParser("{\"soapRequest\":\"newName\"}");
    JsonNode mergePatch = mapper.readTree(jsonParser);
    
    //When
    soapRequestService.update(id, mergePatch, true);
    
    //Then
    verify(soapRequestRepository, times(1)).findById(id);
    verify(soapRequestRepository, times(1)).findById(any());
  }

  @Test
  public void updatePerformsMergePatchWhenSaving() throws IOException, JsonPatchException {
    //Given
    UUID id = UUID.randomUUID();
    SoapRequest record = new SoapRequest();
    record.setId(id);
    record.setDocument(entity.toString());
    when(soapRequestRepository.findById(id)).thenReturn(Optional.of(record));
    ObjectMapper mapper = new ObjectMapper();
    JsonFactory factory = mapper.getFactory();
    JsonParser jsonParser = factory.createParser("{\"soapRequest\":\"patchedName\"}");
    JsonNode mergePatch = mapper.readTree(jsonParser);
    ArgumentCaptor<SoapRequest> captorForDocument = ArgumentCaptor.forClass(SoapRequest.class);
    when(soapRequestRepository.save(captorForDocument.capture())).thenReturn(null);
    
    //When
    soapRequestService.update(id, mergePatch, true);
    
    //Then
    assertEquals("{\"soapRequest\":\"patchedName\"}", captorForDocument.getValue().getDocument());
  }
  
  @Test
  public void updatePerformsCommandPatchWhenSaving() throws IOException, JsonPatchException {
    //Given
    UUID id = UUID.randomUUID();
    SoapRequest record = new SoapRequest();
    record.setId(id);
    record.setDocument(entity.toString());
    when(soapRequestRepository.findById(id)).thenReturn(Optional.of(record));
    ObjectMapper mapper = new ObjectMapper();
    JsonFactory factory = mapper.getFactory();
    JsonParser jsonParser = factory.createParser("[{ \"op\": \"replace\", \"path\": \"/soapRequest\", \"value\": \"patchedName\"}]");
    JsonNode mergePatch = mapper.readTree(jsonParser);
    ArgumentCaptor<SoapRequest> captorForDocument = ArgumentCaptor.forClass(SoapRequest.class);
    when(soapRequestRepository.save(captorForDocument.capture())).thenReturn(null);
    
    //When
    soapRequestService.update(id, mergePatch, false);
    
    //Then
    assertEquals("{\"soapRequest\":\"patchedName\"}", captorForDocument.getValue().getDocument());
  }

  @Test (expected=ValidationException.class)
  public void updateValidatesPatchedEntityAgainstSchema() throws IOException, JsonPatchException {
    //Given
    UUID id = UUID.randomUUID();
    SoapRequest record = new SoapRequest();
    record.setId(id);
    record.setDocument(entity.toString());
    when(soapRequestRepository.findById(id)).thenReturn(Optional.of(record));
    ObjectMapper mapper = new ObjectMapper();
    JsonFactory factory = mapper.getFactory();
    JsonParser jsonParser = factory.createParser("{\"extraName\":\"extraName\"}");
    JsonNode mergePatch = mapper.readTree(jsonParser);
    
    //When
    soapRequestService.update(id, mergePatch, true);
    
    //Then
    //Validation Exception should be thrown
  }

  @Test
  public void deleteCallsRepositoryOnceWithId() {
    //Given
    UUID id = UUID.randomUUID();
    
    //When
    soapRequestService.deleteData(id);
    
    //Then
    verify(soapRequestRepository, times(1)).deleteById(id);
    verify(soapRequestRepository, times(1)).deleteById(any());
  }
}
