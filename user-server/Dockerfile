FROM java:8-jre
MAINTAINER Avijit Chatterjee <avijit.liberty@gmail.com>

ADD ./target/user-server.jar /app/
ADD wrapper.sh /app/

RUN bash -c 'chmod +x /app/wrapper.sh'
RUN bash -c 'touch /app/user-server.jar'

ENTRYPOINT ["/bin/bash", "/app/wrapper.sh"]

EXPOSE 8082
