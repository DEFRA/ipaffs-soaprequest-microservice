# Database

## Introduction

This project is used to prepare the database to be used by the SOAP Request service.

## Pre-Requisites

- JRE / JDK v11
- Maven v3

## How to run

### Environment variables

Required environment variables:

- DATABASE_DB_USER
- DATABASE_DB_PASSWORD
- DATABASE_DB_CONNECTION_STRING
- BASE_SERVICE_DB_PASSWORD
- BASE_SERVICE_DB_USER_AD

### Running migrations

- Execute `mvn clean process-resources`

### Local set up

If you would like to set up the SOAP Request database locally, please use the docker-local repo.
