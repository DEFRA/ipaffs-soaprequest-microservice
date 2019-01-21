package uk.gov.defra.tracesx.soaprequest.fixtures;

import uk.gov.defra.tracesx.soaprequest.security.IdTokenUserDetails;

public class IdTokenUserDetailsFixture {

  public static final String USER_OBJECT_ID = "e9f6447d-2979-4322-8e52-307dafdef649";
  public static final String ID_TOKEN = "adfgsdf.dfgsdrgerg.dfgdfgd";
  public static final String DISPLAY_NAME = "SOAP Request Service";
  public static final String USERNAME = "SOAP";

  public static IdTokenUserDetails create() {
      return IdTokenUserDetails.builder()
        .idToken(ID_TOKEN)
        .userObjectId(USER_OBJECT_ID)
        .displayName(DISPLAY_NAME)
        .username(USERNAME)
        .build();
  }
}
