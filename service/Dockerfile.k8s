ARG IMAGE_ACR=docker.io
ARG IMAGE_REPOSITORY=defradigital/java
ARG IMAGE_TAG=jre-21

FROM ${IMAGE_ACR}/${IMAGE_REPOSITORY}:${IMAGE_TAG} AS base

USER root

RUN mkdir -p /usr/src/soaprequest-service
WORKDIR /usr/src/soaprequest-service

COPY target/TracesX_SoapRequest.jar /usr/src/soaprequest-service/
COPY target/agent-libs/applicationinsights-agent.jar /usr/src/soaprequest-service/
COPY lib/applicationinsights.json /usr/src/soaprequest-service/

RUN chown jreuser /usr/src/soaprequest-service
USER jreuser

EXPOSE 8000

CMD ["java", "-javaagent:/usr/src/soaprequest-service/applicationinsights-agent.jar", "-jar", "TracesX_SoapRequest.jar"]
