package uk.gov.defra.tracesx.soaprequest.dao.entities;

import static org.junit.Assert.assertEquals;

import java.util.UUID;
import org.junit.Before;
import org.junit.Test;

public class SoapRequestTest {
  
  @Before
  public void setUp() {
  }
  
  @Test
  public void verifyIdProperty() {
    //Given
    SoapRequest instance = new SoapRequest();
    UUID id = UUID.randomUUID();
    
    //When
    instance.setId(id);

    //Then
    assertEquals(id, instance.getId());
  }

  @Test
  public void verifyDocumentProperty() {
    //Given
    SoapRequest instance = new SoapRequest();
    String jsonDocument = "{\"test\": 123}";
    
    //When
    instance.setDocument(jsonDocument);

    //Then
    assertEquals(jsonDocument, instance.getDocument());
    
  }
}
