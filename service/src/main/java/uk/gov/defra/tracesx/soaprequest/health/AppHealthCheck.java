package uk.gov.defra.tracesx.soaprequest.health;

import static org.springframework.boot.actuate.health.Status.UP;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class AppHealthCheck extends AbstractHealthIndicator {

  private final Environment env;

  @Autowired
  public AppHealthCheck(Environment env) {
    this.env = env;
  }

  @Override
  protected void doHealthCheck(Health.Builder builder) {
    builder.status(UP).withDetail("version", "0.1-SNAPSHOT" );
  }
}
