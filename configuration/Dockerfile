ARG LINUX_BASE_IMAGE

FROM sndeuxfesacr001.azurecr.io/$LINUX_BASE_IMAGE

# Needed for running sqlcmd and bcp
RUN echo 'en_US.UTF-8 UTF-8' > /etc/locale.gen

# Copy and unpack Liquibase
ADD database/lib/liquibase-core-4.22.0.jar opt/liquibase

# Copy setup script
COPY setup.sh /opt

# Copy database migration script
COPY database/scripts/migrate.sh /opt/liquibase

# Copy database drivers
COPY database/src/main/resources/drivers /opt/liquibase/lib
COPY database/src/main/resources/libs    /opt/liquibase/lib

# Copy changelogs
COPY database/src/main/resources/changelogs /opt/liquibase/changelogs
COPY database/src/main/resources/migrations.xml /opt/liquibase

# Copy logback.xml and dependencies
COPY database/src/main/resources/logback /opt/liquibase/lib

RUN chmod +x /opt/liquibase/liquibase && ln -s /opt/liquibase/liquibase /usr/local/bin/ && sed -i 's/bash/sh/' /opt/liquibase/liquibase && chmod +x /opt/setup.sh && chmod +x /opt/liquibase/migrate.sh

WORKDIR /opt

CMD ["/bin/sh", "setup.sh"]
