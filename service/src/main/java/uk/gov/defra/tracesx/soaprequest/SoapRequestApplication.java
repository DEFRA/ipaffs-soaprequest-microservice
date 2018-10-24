package uk.gov.defra.tracesx.soaprequest;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SoapRequestApplication {
  public static void main(String[] args) {
    SpringApplication.run(SoapRequestApplication.class, args);
  }
}
