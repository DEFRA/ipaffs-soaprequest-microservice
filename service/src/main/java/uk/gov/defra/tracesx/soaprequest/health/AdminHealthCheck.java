package uk.gov.defra.tracesx.soaprequest.health;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

@Component
public class AdminHealthCheck {

  private final DatabaseHealthCheck databaseHealthCheck;
  private final AppHealthCheck appHealthCheck;

  @Autowired
  public AdminHealthCheck(DatabaseHealthCheck databaseHealthCheck, AppHealthCheck appHealthCheck) {
    this.databaseHealthCheck = databaseHealthCheck;
    this.appHealthCheck = appHealthCheck;
  }

  public Map<String, Health> doHealthChecks() {
    Map<String, Health> healthChecks = new HashMap<>();
    Health.Builder databaseHealthBuilder = new Health.Builder();
    databaseHealthCheck.doHealthCheck(databaseHealthBuilder);
    healthChecks.put("database", databaseHealthBuilder.build());
    Health.Builder appHealthBuilder = new Health.Builder();
    appHealthCheck.doHealthCheck(appHealthBuilder);
    healthChecks.put("soaprequest-microservice", appHealthBuilder.build());
    return healthChecks;
  }
}
