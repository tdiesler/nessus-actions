## Nessus Keycloak

This is a simple extension to the [jboss/keycloak](https://registry.hub.docker.com/r/jboss/keycloak) image 
that allows us to use persistent H2 remote database storage.
 
### Creating a user network

Our containers can be accessed from the outside in a secure way (i.e. TLS with HTTPS).
 
For inter-container communication they bypass TLS and use plain HTTP on the 
private network that we create here.

```
docker network create kcnet
```

### Running an H2 Database Server

```
docker rm -f h2
docker run --detach \
    --name h2 \
    -p 9092:9092 \
    --network kcnet \
    -v h2vol:/var/h2db \
    -e JDBC_SERVER_URL=jdbc:h2:tcp://localhost:9092/keycloak \
    -e JDBC_URL=jdbc:h2:/var/h2db/keycloak \
    -e JDBC_USER=keycloak \
    -e JDBC_PASS=password \
    nessusio/nessus-h2

docker logs -f h2
```

### Running Keycloak

Keycloak derives the database configuration from environment variables, but also from local network host names.
In this case it sufficient to call the above container `h2`. Keycloak will see a host with that name and install
the H2TCP databse driver. DB_PORT, DB_USER, DB_PASSWORD default to what is given above. 

```
docker rm -f keycloak
docker run --detach \
    --name keycloak \
    -p 8080:8080 \
    --network kcnet \
    -e KEYCLOAK_USER=admin \
    -e KEYCLOAK_PASSWORD=admin \
    nessusio/keycloak 

docker logs -f keycloak
```

and verify that you can login to the admin console

```
http://localhost:8080/auth/admin
```

### Running the H2 Console 

You can now use the H2 console to look at the Keycloak database ...

```
docker cp keycloak:/opt/jboss/keycloak/modules/system/layers/base/com/h2database/h2/main/ h2
java -jar h2/h2-1.4.197.jar
```

To connect, use `jdbc:h2:tcp://localhost:9092/keycloak` from above.

Enjoy!
