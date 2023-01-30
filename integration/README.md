# SoapRequest Integration Tests

## Introduction

This project is a standalone maven project for running automated integration tests.

## Pre-requisites

Before running the tests start up the service as per the root README

## How To Test

You will find the appropriate env variables in /keybase/team/defra_devops/.env to use in the following command:

```
mvn clean verify \
  -Dtest.openid.service.url=$TEST_OPENID_TOKEN_SERVICE_URL/ \
  -Dtest.openid.service.auth.username=$TEST_OPENID_TOKEN_SERVICE_AUTH_USERNAME \
  -Dtest.openid.service.auth.password=$TEST_OPENID_TOKEN_SERVICE_AUTH_PASSWORD \
  -Dservice.base.url=http://localhost:5260
```

You can also run `runIntegration.sh` from root directory of this project

## Debugging and running in IntelliJ
Use IntelliJ to debug/run any of the tests, with the following env variables (values found in the  
.env file):

```
TEST_OPENID_TOKEN_SERVICE_URL
TEST_OPENID_TOKEN_SERVICE_AUTH_USERNAME
TEST_OPENID_TOKEN_SERVICE_AUTH_PASSWORD
SERVICE_USERNAME
SERVICE_PASSWORD
```

and VM options:
`-Dservice.base.url=http://localhost:5260`