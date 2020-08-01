# Architecture

## Build/Push
```shell script
export VERSION=0.1.6 \
&& ./gradlew clean shadowJar && docker build --build-arg VERSION=$VERSION -t exxbrain/architecture:$VERSION -t exxbrain/architecture:latest . \
&& docker push exxbrain/architecture:$VERSION && docker push exxbrain/architecture:latest 
```

## Run/Stop
```shell script
docker run -p 8085:8000 --name architecture exxbrain/architecture
docker kill architecture && docker rm architecture
```