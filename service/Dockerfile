ARG BASE_IMAGE

FROM sndeuxfesacr001.azurecr.io/$BASE_IMAGE

RUN mkdir -p /usr/src/soaprequest-service
WORKDIR /usr/src/soaprequest-service

COPY target/TracesX_SoapRequest.jar /usr/src/soaprequest-service/
COPY lib/applicationinsights-agent.jar /usr/src/soaprequest-service/
COPY lib/applicationinsights.json /usr/src/soaprequest-service/

RUN chown jreuser /usr/src/soaprequest-service
USER jreuser

EXPOSE 4000

CMD ["java", "-javaagent:/usr/src/soaprequest-service/applicationinsights-agent.jar", "-jar", "TracesX_SoapRequest.jar"]
