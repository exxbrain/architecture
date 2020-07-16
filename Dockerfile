FROM openjdk:11-jre-slim-buster

COPY build/libs/*.jar architecture.jar

EXPOSE 8000
CMD java -jar architecture.jar