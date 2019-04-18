package uk.gov.defra.tracesx.soaprequest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("uk.gov.defra.tracesx")
public class SoapRequestApplication {
  public static void main(String[] args) {
    SpringApplication.run(SoapRequestApplication.class, args);
  }
}
