ARG BASE_VERSION=jre-11
FROM defradigital/java:$BASE_VERSION AS base

USER root

RUN mkdir -p /usr/src/soaprequest-service
WORKDIR /usr/src/soaprequest-service

COPY target/TracesX_SoapRequest.jar /usr/src/soaprequest-service/
COPY lib/applicationinsights-agent.jar /usr/src/soaprequest-service/
COPY lib/applicationinsights.json /usr/src/soaprequest-service/

RUN chown jreuser /usr/src/soaprequest-service
USER jreuser

EXPOSE 8080

CMD ["java", "-javaagent:/usr/src/soaprequest-service/applicationinsights-agent.jar", "-jar", "TracesX_SoapRequest.jar"]
