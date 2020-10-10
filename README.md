## Nessus Actions

[![Default Build](https://github.com/tdiesler/nessus-actions/workflows/Default%20Build/badge.svg)](https://github.com/tdiesler/nessus-actions/actions)
[![License](https://img.shields.io/:license-Apache2-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

Explore [Apache Camel](http://camel.apache.org/) based actions inspired by [GitHub Actions](https://docs.github.com/en/actions). 

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
