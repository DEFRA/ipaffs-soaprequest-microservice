package uk.gov.defra.tracesx.soaprequest;


import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import java.security.Security;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.test.util.ReflectionTestUtils;

public class PropertySetterTest {

  @Test
  public void whenSetPropertyCalledThenSetSecurityPolicy() {
    try(MockedStatic<Security> staticMock = mockStatic(Security.class)) {
      PropertySetter propertySetter = new PropertySetter();
      ReflectionTestUtils.setField(propertySetter, "dnsCacheTtl", "TEST");
      propertySetter.setProperty();
      staticMock.verify(() -> Security.setProperty("networkaddress.cache.ttl", "TEST"), times(1));
    }
  }

}
