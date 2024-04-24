FROM openjdk:17-jdk-slim

ARG LOCAL_APP_FILE=restaurantconsumerapp-0.0.1-SNAPSHOT.jar

RUN mkdir /home/app

COPY target/${LOCAL_APP_FILE} /home/app/restaurantappconsumer.jar

WORKDIR /home/app

EXPOSE 8081

ENTRYPOINT exec java -jar /home/app/restaurantappconsumer.jar