FROM ubuntu:14.04
MAINTAINER Germano Gabbianelli <tyrion.mx@gmail.com>

RUN apt-get update && apt-get install -y software-properties-common wget unzip git
RUN add-apt-repository ppa:webupd8team/java

RUN echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | \
    /usr/bin/debconf-set-selections
RUN apt-get update && apt-get install -y oracle-java8-installer maven

RUN cd /opt && wget http://dlc-cdn.sun.com/glassfish/4.1/release/glassfish-4.1.zip \ 
    && unzip glassfish-4.1.zip && rm glassfish-4.1.zip

RUN cd /tmp && \
    wget https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-5.1.34.tar.gz \
    && tar -xf mysql-connector-*.tar.gz \
    && mv mysql-connector-*/*.jar /opt/glassfish4/glassfish/lib \
    && rm -rf mysql-connector-*

# Taken from: https://github.com/glassfish/docker/blob/master/Dockerfile
# User: admin / Pass: glassfish
RUN echo "admin;{SSHA256}80e0NeB6XBWXsIPa7pT54D9JZ5DR5hGQV1kN1OAsgJePNXY6Pl0EIw==;asadmin" > /opt/glassfish4/glassfish/domains/domain1/config/admin-keyfile
RUN echo "AS_ADMIN_PASSWORD=glassfish" > /opt/glassfish4/pwdfile

# Default to admin/glassfish as user/pass
RUN cd /opt/glassfish4/bin && \
  ./asadmin start-domain && \
  ./asadmin --user admin --passwordfile /opt/glassfish4/pwdfile enable-secure-admin && \
  ./asadmin stop-domain

COPY maven-settings.xml /usr/share/maven/conf/settings.xml

WORKDIR /opt/app
VOLUME ["/opt/app", "/opt/maven"]
EXPOSE 4848 8080 8181

COPY entrypoint.sh /usr/local/bin/
ENTRYPOINT ["entrypoint.sh"]
