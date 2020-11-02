## Nessus Maven

This is a simple extension to the [maven](https://hub.docker.com/_/maven/) image 
that allows the scheduling of maven project builds.
 
### Creating a user network

Our containers can be accessed from the outside in a secure way (i.e. TLS with HTTPS).
 
For inter-container communication they bypass TLS and use plain HTTP on the 
private network that we create here.

```
docker network create nessus
```

### Running Maven

```
docker volume rm mvnvol

docker rm -f maven
docker run --detach \
    --name maven \
    -p 8100:8100 \
    --network nessus \
    -v mvnvol:/root/.m2 \
    nessusio/maven 

docker logs -f maven
```

### Schedule a Maven Build

```
curl -X POST 'http://localhost:8100/maven/api/build/schedule' \
  -H 'Content-Type: multipart/form-data' \
  -F 'projName=acme-ticker-1.0.0-project' \
  -F 'projZip=@/Users/tdiesler/git/nessus-actions/core/target/acme-ticker-1.0.0-project.tgz'
```

### Running the Camel route 

```
docker cp maven:var/nessus/workspace/acme-ticker-1.0.0-project/target/acme-ticker-1.0.0-runner.jar .

java -jar acme-ticker-1.0.0-runner.jar

curl http://localhost:8080/ticker
```

Enjoy!
