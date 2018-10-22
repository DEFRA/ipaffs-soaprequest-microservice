package uk.gov.defra.tracesx.soaprequest.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import java.io.IOException;
import java.util.UUID;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.defra.tracesx.soaprequest.exceptions.NotImplementedException;
import uk.gov.defra.tracesx.soaprequest.dao.entities.SoapRequest;
import uk.gov.defra.tracesx.soaprequest.dao.repositories.SoapRequestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.apache.commons.io.FileUtils;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import java.nio.charset.Charset;
import java.io.File;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.everit.json.schema.ValidationException;

@Service
public class SoapRequestService {

  private final SoapRequestRepository soapRequestRepository;
  protected final Schema schema;

  @Autowired

  public SoapRequestService(SoapRequestRepository soapRequestRepository) throws IOException {
    this.soapRequestRepository = soapRequestRepository;
    this.schema = loadSchema();
  }

  public UUID create(JsonNode document) {
    validateAgainstSchema(document);
    SoapRequest soapRequest = new SoapRequest();
    soapRequest.setDocument(document.toString());
    return soapRequestRepository.save(soapRequest).getId();
  }

  public JsonNode get(UUID id) throws IOException {
    SoapRequest soapRequest = soapRequestRepository.findById(id).get();
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readTree(soapRequest.getDocument());
  }

public void update(UUID id, JsonNode patch, boolean isMergePatch)
      throws JsonPatchException, IOException {
    SoapRequest soapRequestOrig = soapRequestRepository.findById(id).get();
    JsonNode soapRequestUpdated = performPatch(soapRequestOrig, isMergePatch, patch);
    validateAgainstSchema(soapRequestUpdated);
    soapRequestRepository.save(new SoapRequest(id, soapRequestUpdated.toString()));
  }

  public void deleteData(UUID id) {
    soapRequestRepository.deleteById(id);
  }

  protected void validateAgainstSchema(JsonNode jsonToValidate) throws ValidationException {
    JSONObject jsonObjectToValidate = new JSONObject(jsonToValidate.toString());
    schema.validate(jsonObjectToValidate);
  }

  protected JsonNode performPatch(
      SoapRequest soapRequestOrig, boolean isMergePatch, JsonNode patch)
      throws IOException, JsonPatchException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonOriginalDocument = mapper.readTree(soapRequestOrig.getDocument());
    final JsonNode soapRequestUpdated;
    if (isMergePatch) {
      final JsonMergePatch preparedMergePatch = JsonMergePatch.fromJson(patch);
      soapRequestUpdated = preparedMergePatch.apply(jsonOriginalDocument);
    } else {
      final JsonPatch preparedCommandPatch = JsonPatch.fromJson(patch);
      soapRequestUpdated = preparedCommandPatch.apply(jsonOriginalDocument);
    }
    return soapRequestUpdated;
  }

  protected final Schema loadSchema() throws IOException, JSONException {
    InputStream resource = new ClassPathResource("serviceSchema.json").getInputStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(resource));
    String schemaString = reader.lines().collect(Collectors.joining(""));
    JSONObject jsonSchema = new JSONObject(schemaString);
    SchemaLoader loader = SchemaLoader.builder().schemaJson(jsonSchema).build();
    return loader.load().build();
  }
}
