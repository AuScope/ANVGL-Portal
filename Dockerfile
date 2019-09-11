FROM tomcat:8.5

MAINTAINER geoffrey.squire@data61.csiro.au

ARG war=target/VGL-Portal-0.0.1-SNAPSHOT.war

ADD ${war} /usr/local/tomcat/webapps/VGL-Portal.war

RUN rm -rf /usr/local/tomcat/webapps/ROOT \
  && unzip /usr/local/tomcat/webapps/VGL-Portal.war -d /usr/local/tomcat/webapps/ROOT \
  && rm /usr/local/tomcat/webapps/VGL-Portal.war

# ADD application.yaml /usr/local/tomcat/webapps/ROOT/WEB-INF/classes/