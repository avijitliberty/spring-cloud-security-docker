FROM java:8-jre
MAINTAINER Avijit Chatterjee <avijit.liberty@gmail.com>

ADD ./target/secure-ui.jar /app/
CMD ["java", "-Xmx200m", "-Dspring.profiles.active=docker", "-jar", "/app/secure-ui.jar"]

EXPOSE 8080