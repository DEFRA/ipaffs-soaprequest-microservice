package uk.gov.defra.tracesx.soaprequest.health;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.actuate.health.Status.UP;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.core.env.Environment;

public class AppHealthCheckTest {

  private static final Environment mockedEnvironment = mock(Environment.class);
  private static final Health.Builder mockedBuilder = mock(Health.Builder.class);
  private static AppHealthCheck healthCheck;

  @BeforeClass
  public static void beforeClass() {
    healthCheck = new AppHealthCheck(mockedEnvironment);
  }

  @Test
  public void testAppCheckReturnsVersion() throws Exception {
    when(mockedBuilder.status(UP)).thenReturn(mockedBuilder);

    healthCheck.doHealthCheck(mockedBuilder);

    verify(mockedBuilder, times(1)).status(UP);
    verify(mockedBuilder, times(1)).withDetail("version", "0.1-SNAPSHOT");
  }
}
