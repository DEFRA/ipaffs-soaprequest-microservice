package uk.gov.defra.tracesx.soaprequest.health;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Map;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.actuate.health.Health;

public class AdminHealthCheckTest {

  private static final Health.Builder mockedBuilder = mock(Health.Builder.class);
  private static final DatabaseHealthCheck mockedDatabaseHealthCheck =
      mock(DatabaseHealthCheck.class);
  private static final AppHealthCheck mockedAppHealthCheck = mock(AppHealthCheck.class);
  private static AdminHealthCheck healthCheck;

  @BeforeClass
  public static void beforeClass() {
    healthCheck = new AdminHealthCheck(mockedDatabaseHealthCheck, mockedAppHealthCheck);
  }

  @Test
  public void testAppCheckReturnsVersion() throws Exception {
    Map<String, Health> healthChecks = healthCheck.doHealthChecks();

    assertEquals(2, healthChecks.size());
    assertEquals("UNKNOWN {}", healthChecks.get("database").toString());
    assertEquals("UNKNOWN {}", healthChecks.get("soaprequest-microservice").toString());
    verify(mockedDatabaseHealthCheck, times(1)).doHealthCheck(any(Health.Builder.class));
    verify(mockedAppHealthCheck, times(1)).doHealthCheck(any(Health.Builder.class));
  }
}
