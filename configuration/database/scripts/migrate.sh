#!/usr/bin/env sh

database_url="${DATABASE_DB_CONNECTION_STRING_SOAPREQUEST_MICROSERVICE}"
set -- --driver="com.microsoft.sqlserver.jdbc.SQLServerDriver" \
    --url="${database_url}" \
    --changeLogFile="migrations.xml"

# Azure Identity obtains credentials from the environment, not SQL passwords.
case ";$(printf '%s' "${database_url}" | tr '[:upper:]' '[:lower:]');" in
    *';authentication=activedirectorydefault;'* | \
    *';authentication=activedirectorymanagedidentity;'* | \
    *';authentication=activedirectorymsi;'*) ;;
    *) set -- "$@" --username="${DATABASE_DB_USER}" --password="${DATABASE_DB_PASSWORD}" ;;
esac

exec liquibase "$@" migrate \
    -DserviceDbPassword="${BASE_SERVICE_DB_PASSWORD}" \
    -DserviceDbPasswordNew="${NEW_BASE_SERVICE_DB_PASSWORD}" \
    -DserviceDbUserAd="${BASE_SERVICE_DB_USER_AD}" \
    -DmanagedIdentityUser="${MANAGED_IDENTITY_USER}"
