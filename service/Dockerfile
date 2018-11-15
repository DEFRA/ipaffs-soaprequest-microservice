FROM baseimagesrepo/java:1.8.0

RUN mkdir -p /usr/src/soaprequest-service
WORKDIR /usr/src/soaprequest-service

COPY target/TracesX_SoapRequest.jar /usr/src/soaprequest-service/
COPY lib/applicationinsights-agent-2.0.0.jar /usr/src/soaprequest-service/
COPY lib/AI-Agent.xml /usr/src/soaprequest-service/
EXPOSE 4000

CMD ["java", "-javaagent:/usr/src/soaprequest-service/applicationinsights-agent-2.0.0.jar", "-jar", "TracesX_SoapRequest.jar"]
