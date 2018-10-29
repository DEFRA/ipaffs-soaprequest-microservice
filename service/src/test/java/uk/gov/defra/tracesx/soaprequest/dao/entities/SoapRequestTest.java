package uk.gov.defra.tracesx.soaprequest.dao.entities;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class SoapRequestTest {

  public static final String USERNAME = "username";
  public static final String QUERY = "test";

  @Before
  public void setUp() {
  }
  
  @Test
  public void verifyProperties() {
    // Given
    SoapRequest instance = new SoapRequest(USERNAME, QUERY);
    Long requestId = 123L;

    //When
    instance.setRequestId(requestId);

    //Then
    assertEquals(requestId, instance.getRequestId());
    assertEquals(USERNAME, instance.getUsername());
    assertEquals(QUERY, instance.getQuery());
  }
}
