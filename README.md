# SoapRequest Microservice

## Introduction

SpringBoot REST service to provide soap requests data for SoapSearch Microservice.

## Secret scanning
Secret scanning is setup using [truffleHog](https://github.com/trufflesecurity/truffleHog).
It is used as a pre-push hook and will scan any local commits being pushed

### Pre-push hook setup
1. Install [truffleHog](https://github.com/trufflesecurity/truffleHog)
    - `brew install go`
    - `git clone https://github.com/trufflesecurity/trufflehog.git`
    - `cd trufflehog; go install`
2. Set DEFRA_WORKSPACE env var (`export DEFRA_WORKSPACE=/path/to/workspace`)
3. Potentially there's an older version of Trufflehog located at: `/usr/local/bin/trufflehog`. If so, remove this.
4. Create a symlink: `ln -s ~/go/bin/truffleHog /usr/local/bin/trufflehog`
5. From this project root directory copy the pre-push hook: `cp hooks/pre-push .git/hooks/pre-push`

### Git hook setup

1. Run `mvn install` to configure hooks from service folder.

## Set up

Ensure that you have the necessary configuration to resolve dependencies from Artifactory
(How to can be found on Confluence, search for: Artifactory)

Initialise the database with [docker local] (How to can be found on Confluence, searching for: Run Services with Local Docker) :

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
(How to can be found on Confluence, search for: Generating Security Headers for Backend Calls in Development)

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
     APPLICATIONINSIGHTS_CONNECTION_STRING
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
