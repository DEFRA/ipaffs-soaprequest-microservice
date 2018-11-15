# Database

## Introduction

This project is used to prepare the database to be used by ArchetypeService service.

## Pre-Requisites

- JRE / JDK v8
- Maven v3

## How to run

### Environment variables

Required environment variables:

- DATABASE_DB_USER
- DATABASE_DB_PASSWORD
- DATABASE_DB_CONNECTION_STRING
- BASE_SERVICE_DB_PASSWORD

### Running migrations

- Execute `mvn clean process-resources`
