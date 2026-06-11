#!/usr/bin/env bash

## mi-aks.sh
##
## Creates a SQL Server principal for an externally defined Azure Managed Identity and grants it privileges.
##
## usage $0 [MI NAME]
## Required arguments
## [$1] - The name of the principal

DATABASE_DB_HOST=$(printf '%s' "$DATABASE_DB_CONNECTION_STRING_SOAPREQUEST_MICROSERVICE" | sed -E 's#^jdbc:sqlserver://([^:;]+).*#\1#')
DATABASE_DB_NAME=$(printf '%s' "$DATABASE_DB_CONNECTION_STRING_SOAPREQUEST_MICROSERVICE" | sed -E 's#.*;databaseName=([^;]+).*#\1#')

MI_USERNAME_ARG="$1"
read -rd '' SQL_SCRIPT_ARG <<EOF

IF NOT EXISTS (
  SELECT name
  FROM sys.database_principals
  WHERE name = '${MI_USERNAME_ARG}')
BEGIN
  CREATE USER [${MI_USERNAME_ARG}] FROM EXTERNAL PROVIDER
END

GRANT SELECT, INSERT, UPDATE, DELETE ON dbo.soaprequest TO [${MI_USERNAME_ARG}];
GRANT SELECT, INSERT, UPDATE ON dbo.soaprequest_audit TO [${MI_USERNAME_ARG}];
GRANT SELECT, INSERT, DELETE, UPDATE ON dbo.cacherequest TO [${MI_USERNAME_ARG}];
EOF

echo "[INFO] Configuring Managed Identity ${MI_USERNAME_ARG} for ${DATABASE_DB_NAME}"
sqlcmd --server "$DATABASE_DB_HOST" --trust-server-certificate --database-name "$DATABASE_DB_NAME" --authentication-method=ActiveDirectoryDefault --query "$SQL_SCRIPT_ARG"
if [ "$?" != 0 ]; then
  echo "[ERROR] Failed to configure Managed Identity"
  exit 1
fi
