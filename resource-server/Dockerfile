FROM java:8-jre
MAINTAINER Avijit Chatterjee <avijit.liberty@gmail.com>

ADD ./target/resource-server.jar /app/
CMD ["java", "-Xmx200m", "-Dspring.profiles.active=docker", "-jar", "/app/resource-server.jar"]

EXPOSE 8081