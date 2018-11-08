FROM ubuntu:16.04

# Needed for running sqlcmd and bcp
RUN echo 'en_US.UTF-8 UTF-8' > /etc/locale.gen
RUN apt-get update && apt-get install -y locales openjdk-8-jre

# Copy and unpack Liquibase
ADD database/lib/liquibase-3.6.2-bin.tar.gz opt/liquibase

# Copy setup script
COPY setup.sh /opt

# Copy database migration script
COPY database/scripts/migrate.sh /opt/liquibase

# Copy database drivers
COPY database/src/main/resources/drivers /opt/liquibase/lib

# Copy changelogs
COPY database/src/main/resources/changelogs /opt/liquibase/changelogs
COPY database/src/main/resources/migrations.xml /opt/liquibase

RUN chmod +x /opt/liquibase/liquibase && ln -s /opt/liquibase/liquibase /usr/local/bin/ && chmod +x /opt/setup.sh && chmod +x /opt/liquibase/migrate.sh

WORKDIR /opt

CMD ["/bin/sh", "setup.sh"]
