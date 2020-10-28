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

Keycloak derives the database configuration from environment variables, but also from local network host names.
In this case it sufficient to call the above container `h2`. Keycloak will see a host with that name and install
the H2TCP databse driver. DB_PORT, DB_USER, DB_PASSWORD default to what is given above. 

```
docker rm -f maven
docker run --detach \
    --name maven \
    -p 8100:8080 \
    --network nessus \
    nessusio/maven 

docker logs -f maven
```

and verify that you can login to the admin console

```
http://localhost:8080/auth/admin
```

Enjoy!
