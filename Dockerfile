FROM openjdk:11-jre-slim-buster

ARG VERSION
ENV APP_VERSION=$VERSION

COPY build/libs/*.jar architecture.jar

EXPOSE 8000
CMD java -jar architecture.jar