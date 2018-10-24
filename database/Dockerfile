FROM baseimagesrepo/java:1.8.0

# Copy and unpack Liquibase
ADD /lib/liquibase-3.6.2-bin.tar.gz /opt/liquibase
RUN chmod +x /opt/liquibase/liquibase
RUN ln -s /opt/liquibase/liquibase /usr/local/bin/
RUN sed -i 's/bash/sh/' /opt/liquibase/liquibase

# Copy migration script
COPY scripts/migrate.sh /opt/liquibase
RUN chmod +x /opt/liquibase/migrate.sh

# Copy database drivers
COPY src/main/resources/drivers /opt/liquibase/lib

# Copy changelogs
COPY src/main/resources/changelogs /opt/liquibase/changelogs
COPY src/main/resources/migrations.xml /opt/liquibase

WORKDIR /opt/liquibase

CMD ["/bin/sh", "migrate.sh"]