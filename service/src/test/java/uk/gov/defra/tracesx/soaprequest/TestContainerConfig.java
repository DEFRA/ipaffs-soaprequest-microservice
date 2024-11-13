package uk.gov.defra.tracesx.soaprequest;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
@ActiveProfiles("test")
public class TestContainerConfig {

  @Bean
  public MSSQLServerContainer mssqlServerContainer() {
    DockerImageName myImage = DockerImageName
        .parse("mcr.microsoft.com/mssql/server:2022-latest")
        .asCompatibleSubstituteFor("mcr.microsoft.com/mssql/server");

    MSSQLServerContainer mssqlServerContainer = new MSSQLServerContainer(myImage)
        .acceptLicense();

    mssqlServerContainer.start();
    System.setProperty("DB_URL", mssqlServerContainer.getJdbcUrl());
    System.setProperty("DB_USERNAME", mssqlServerContainer.getUsername());
    System.setProperty("DB_PASSWORD", mssqlServerContainer.getPassword());
    System.setProperty("DB_PORT", mssqlServerContainer.getLivenessCheckPortNumbers().toString());
    System.setProperty("JDBC_URL",  mssqlServerContainer.getJdbcUrl() + ";user=" +
        mssqlServerContainer.getUsername() + ";password=" + mssqlServerContainer.getPassword() + ";");
    return mssqlServerContainer;
  }

  @Bean
  @Primary
  public DataSource dataSource(MSSQLServerContainer mssqlServerContainer) {
    HikariDataSource dataSource = new HikariDataSource();
    dataSource.setJdbcUrl(mssqlServerContainer.getJdbcUrl());
    dataSource.setUsername(mssqlServerContainer.getUsername());
    dataSource.setPassword(mssqlServerContainer.getPassword());
    return dataSource;
  }
}