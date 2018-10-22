package uk.gov.defra.tracesx.soaprequest;

import javax.sql.DataSource;
import org.skife.jdbi.v2.DBI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import uk.gov.defra.tracesx.soaprequest.jdbi.OptionalContainerFactory;

@SpringBootApplication
public class SoapRequestApplication {
  public static void main(String[] args) {
    SpringApplication.run(SoapRequestApplication.class, args);
  }


  @Autowired
  @Qualifier("dataSource")
  @Bean
  public DBI dbi(DataSource dataSource) {
    synchronized (DBI.class) {
      DBI dbi = new DBI(dataSource);
      dbi.registerContainerFactory(new OptionalContainerFactory());
      return dbi;
    }
  }
}
