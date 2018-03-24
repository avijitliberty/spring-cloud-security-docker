#!/bin/bash
while ! exec 6<>/dev/tcp/oauth2/3306; do
    echo "Trying to connect to MySQL at 3306..."
    sleep 10
done

java -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=docker -jar /app/auth-server.jar
