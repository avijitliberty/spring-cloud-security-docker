FROM java:8-jre
MAINTAINER Avijit Chatterjee <avijit.liberty@gmail.com>

ADD ./target/eureka-server.jar /app/
CMD ["java", "-Xmx200m", "-Dspring.profiles.active=docker", "-jar", "/app/eureka-server.jar"]

EXPOSE 8761