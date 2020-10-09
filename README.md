## Nessus Actions

[![Default Build](https://github.com/tdiesler/nessus-actions/workflows/Default%20Build/badge.svg)](https://github.com/tdiesler/nessus-actions/actions)
[![License](https://img.shields.io/:license-Apache2-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

Explore [Apache Camel](http://camel.apache.org/) based actions inspired by [GitHub Actions](https://docs.github.com/en/actions). 

### Running Keycloak

First, you'd want to spin up a [Keycloak](https://www.keycloak.org/getting-started/getting-started-docker) instance

```
KEYCLOAK_URL=http://95.179.187.140:8180/auth

KEYCLOAK_USER=admin
KEYCLOAK_PASSWORD=admin

docker rm -f keycloak
docker run --detach \
    --name keycloak \
    -p 8180:8080 \
    -e KEYCLOAK_USER=$KEYCLOAK_USER \
    -e KEYCLOAK_PASSWORD=$KEYCLOAK_PASSWORD \
    -e KEYCLOAK_IMPORT=/tmp/myrealm.json \
    -v ~/git/nessus-actions/docs/myrealm.json:/tmp/myrealm.json \
    quay.io/keycloak/keycloak 

docker logs -f keycloak
```

and verify that you can login to the admin console

```
http://localhost:8180/auth/admin
```

### Running TryIt

Then, you can spin up a the TryIt portal like this ...

```
docker rm -f portal
docker run --detach \
    --name portal \
    -p 8280:8280 \
    -e KEYCLOAK_URL=$KEYCLOAK_URL \
    -e KEYCLOAK_USER=$KEYCLOAK_USER \
    -e KEYCLOAK_PASSWORD=$KEYCLOAK_PASSWORD \
    nessusio/nessus-tryit-portal

docker logs -f portal

docker exec portal tail -fn 1000 tryit/debug.log
```

and connect to it

```
http://localhost:8280/portal
```

Enjoy!
