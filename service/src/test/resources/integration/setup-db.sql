-- The tables as the Liquibase changelogs in configuration/database create them. The changelogs
-- themselves can't run here: they create Azure AD users.
IF OBJECT_ID('soaprequest') IS NULL CREATE TABLE soaprequest (
  id uniqueidentifier NOT NULL PRIMARY KEY,
  request_id bigint NOT NULL,
  username nvarchar(256) NOT NULL,
  query nvarchar(max) NOT NULL,
  last_updated datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (request_id, username)
);
IF OBJECT_ID('soaprequest_audit') IS NULL CREATE TABLE soaprequest_audit (
  id uniqueidentifier NOT NULL PRIMARY KEY,
  data nvarchar(max) NOT NULL,
  user_id nvarchar(max) NOT NULL,
  type nvarchar(10) NOT NULL,
  created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  entity_type varchar(12) NOT NULL DEFAULT 'SOAP',
  entity_id nvarchar(90) NULL
);
IF OBJECT_ID('cacherequest') IS NULL CREATE TABLE cacherequest (
  id varchar(128) NOT NULL PRIMARY KEY,
  value nvarchar(128) NOT NULL,
  created_date datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
);
