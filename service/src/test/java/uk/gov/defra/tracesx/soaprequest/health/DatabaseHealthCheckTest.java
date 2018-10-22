package uk.gov.defra.tracesx.soaprequest.health;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.actuate.health.Status.DOWN;
import static org.springframework.boot.actuate.health.Status.UP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.springframework.boot.actuate.health.Health;

public class DatabaseHealthCheckTest {

  private static DatabaseHealthCheck healthCheck;
  private static Handle mockedHandle;
  private static Health.Builder mockedBuilder;
  private ArrayList<Map<String, Object>> rs;

  @BeforeClass
  public static void beforeClass() {
    DBI mockedDbi = mock(DBI.class);
    mockedHandle = mock(Handle.class);
    mockedBuilder = mock(Health.Builder.class);
    when(mockedDbi.open()).thenReturn(mockedHandle);
    healthCheck = new DatabaseHealthCheck(mockedDbi);
  }

  @Before
  public void before() {
    rs = new ArrayList<>();
    when(mockedHandle.select("SELECT 1")).thenReturn(rs);
  }

  @Test
  public void testDatabaseCheckSuccess() {
    HashMap<String, Object> hashMap = new HashMap<>();
    hashMap.put("1", 1);
    rs.add(hashMap);

    healthCheck.doHealthCheck(mockedBuilder);

    verify(mockedBuilder, times(1)).status(DOWN);
    verify(mockedBuilder, times(1)).status(UP);
  }

  @Test
  public void testDatabaseCheckFailure() {
    healthCheck.doHealthCheck(mockedBuilder);

    verify(mockedBuilder, times(2)).status(DOWN);
  }
}
