# SoapRequest Microservice

## Introduction

SpringBoot REST service to provide soap requests data for SoapSearch Microservice.

## Set up

Ensure that you have the necessary configuration to resolve dependencies from Artifactory: https://eaflood.atlassian.net/wiki/spaces/IT/pages/1047823027/Artifactory

Initialise the database with [docker local](https://eaflood.atlassian.net/wiki/spaces/IM/pages/1089274631/Run+Services+with+Local+Docker) :

    docker-up recreate-db

Copy .env file from /keybase/team/defra_devops/.env into the root of this project and run the service:

## How To Run

From the service directory, run with:

    mvn spring-boot:run

## How To Test

### Manual testing

Manually test endpoint

    http://localhost:5260/admin/health-check

And test endpoints available in SoapRequestResource with [appropriate security headers]
(https://eaflood.atlassian.net/wiki/spaces/IM/pages/1171489028/Generating+Security+Headers+for+Backend+Calls+in+Development)

Example POST request:

body:

    {
      "username" : "test",
      "query": "test"
    }

endpoint:

    http://localhost:5260/soaprequest

should return request ID that can be used to test GET endpoint:

    http://localhost:5260/soaprequest/{id}

### Unit tests

From the service directory, run with:

    mvn test
    
The coverage report can be created with:

    mvn clean test jacoco:report

The coverage report can then be viewed by opening the `target/site/jacoco/index.html` file in your browser.

### Integration tests

Follow the README instructions found in the integration folder.

## How To Debug

Use IntelliJ to debug the SoapRequestApplication.java application, specifying "local" as the active profile. Additionally, copy the content of the .env file to environment variables in IntelliJ configuration.

## Properties

The following properties are used by the application (see required values in the .env file)

     SERVICE_PORT
     APPLICATION_INSIGHTS_IKEY
     API_VERSION
     ENV_DOMAIN
     PERMISSIONS_SERVICE_PASSWORD
     PROTOCOL
     SECURITY_JWT_JWKS
     SECURITY_JWT_ISS
     SECURITY_JWT_AUD
     DB_USER
     DB_PASSWORD
     DB_HOST
     DB_PORT
     DB_NAME
     TRUST_SERVER_CERTIFICATE
