FROM sndeuxfesacr001.azurecr.io/java-20190930:alpine3.10.2-java8.212.04-r1

RUN mkdir -p /usr/src/soaprequest-service
WORKDIR /usr/src/soaprequest-service

COPY target/TracesX_SoapRequest.jar /usr/src/soaprequest-service/
COPY lib/applicationinsights-agent-2.0.0.jar /usr/src/soaprequest-service/
COPY lib/AI-Agent.xml /usr/src/soaprequest-service/

RUN chown jreuser /usr/src/soaprequest-service
USER jreuser

EXPOSE 4000

CMD ["java", "-javaagent:/usr/src/soaprequest-service/applicationinsights-agent-2.0.0.jar", "-jar", "TracesX_SoapRequest.jar"]
