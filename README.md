# Architecture

```shell script
./gradlew clean shadowJar
docker build -t architecture:latest . 
docker run -p 8085:8000 --name architecture architecture
docker kill architecture && docker rm architecture
```