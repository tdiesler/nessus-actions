## Nessus Actions

[![Default Build](https://github.com/tdiesler/nessus-actions/workflows/Default%20Build/badge.svg)](https://github.com/tdiesler/nessus-actions/actions)
[![License](https://img.shields.io/:license-Apache2-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

Explore [Apache Camel](http://camel.apache.org/) based actions inspired by [GitHub Actions](https://docs.github.com/en/actions). 

### Creating a user network

Containers communiacte with the outside world via TLS (i.e. https). For inter-container communication they bypass
TLS and use plain http on the network that we create here.

```
docker network create kcnet
```

### Running an H2 database instance

```
docker rm -f h2db
docker run --detach \
    --name h2db \
    -p 5080:8080 \
    -v h2vol:/var/h2db \
    --network kcnet \
    -e JDBC_SERVER_URL=jdbc:h2:tcp://localhost:8080/keycloak \
    -e JDBC_URL=jdbc:h2:/var/h2db/keycloak \
    -e JDBC_USER=h2 \
    -e JDBC_PASS=h2 \
    nessusio/nessus-h2

docker logs -f h2db
```

### Running Keycloak

First, you'd want to spin up a [Keycloak](https://www.keycloak.org/getting-started/getting-started-docker) instance

```
# Download the default application realm
wget -O docs/myrealm.json https://raw.githubusercontent.com/tdiesler/nessus-actions/master/docs/myrealm.json

KEYCLOAK_USER=admin
KEYCLOAK_PASSWORD=admin

docker rm -f keycloak
docker run --detach \
    --name keycloak \
    -p 6080:8080 \
    --network kcnet \
    -e DB_VENDOR=h2 \
    -e DB_ADDR=h2db:8080 \
    -e DB_USER=h2 \
    -e DB_PASSWORD=h2 \
    -e KEYCLOAK_USER=$KEYCLOAK_USER \
    -e KEYCLOAK_PASSWORD=$KEYCLOAK_PASSWORD \
    -e KEYCLOAK_IMPORT=/tmp/myrealm.json \
    -v `pwd`/docs/myrealm.json:/tmp/myrealm.json \
    quay.io/keycloak/keycloak 

docker logs -f keycloak
```

and verify that you can login to the admin console

```
http://localhost:7080/auth/admin
```

### Running the TryIt API service

Then, you can spin up a the TryIt API like this ...

```
docker rm -f jaxrs
docker run --detach \
    --name jaxrs \
    -p 7080:7080 \
    --network kcnet \
    -e KEYCLOAK_USER=$KEYCLOAK_USER \
    -e KEYCLOAK_PASSWORD=$KEYCLOAK_PASSWORD \
    -e KEYCLOAK_URL=http://keycloak:8080/auth \
    nessusio/nessus-actions-jaxrs

docker logs -f jaxrs

docker exec jaxrs tail -fn 1000 jaxrs/debug.log
```

### Running the TryIt GUI service

Then, you can spin up a the TryIt GUI like this ...

```
docker rm -f trygui
docker run --detach \
    --name trygui \
    -p 8080:8080 \
    --network kcnet \
    -e KEYCLOAK_USER=$KEYCLOAK_USER \
    -e KEYCLOAK_PASSWORD=$KEYCLOAK_PASSWORD \
    -e KEYCLOAK_URL=http://keycloak:8080/auth \
    -e JAXRS_API_URL=http://jaxrs:8080/tryit \
    nessusio/nessus-actions-gui

docker logs -f trygui

docker exec trygui tail -fn 1000 trygui/debug.log
```

and connect to it

```
http://localhost:8080/portal
```

Enjoy!
