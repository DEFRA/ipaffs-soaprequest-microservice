package uk.gov.defra.tracesx.soaprequest.health;

import static org.springframework.boot.actuate.health.Status.DOWN;
import static org.springframework.boot.actuate.health.Status.UP;

import java.util.List;
import java.util.Map;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

@Component
public class DatabaseHealthCheck extends AbstractHealthIndicator {

  private final DBI dbi;

  @Autowired
  public DatabaseHealthCheck(DBI dbi) {
    this.dbi = dbi;
  }

  @Override
  protected void doHealthCheck(Health.Builder builder) {
    builder.status(DOWN);
    Handle h = null;
    try {
      h = dbi.open();
      List<Map<String, Object>> rs = h.select("SELECT 1");
      boolean success = rs.size() == 1;
      if (success) {
        builder.status(UP);
      }
    } finally {
      if (h != null) {
        h.close();
      }
    }
  }
}
