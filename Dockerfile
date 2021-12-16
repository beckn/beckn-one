# Alpine Linux with OpenJDK JRE
#FROM openjdk-tz-india:latest
FROM openjdk-11-headless-tz-india:latest

EXPOSE 3000 
# Debug port
EXPOSE 3030
#web port

RUN mkdir /portal portal/target portal/bin portal/tmp portal/overrideProperties
COPY tmp/portal-docker /portal/
WORKDIR /portal
RUN /usr/bin/crontab /portal/crontab.txt  

CMD ["/bin/sh" , "/portal/bin/service-start"]
