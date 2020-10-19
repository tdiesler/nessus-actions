
### Create the Keycloak volumes
    
Before we actually do this, lets copy the cert to a volume

```
docker rm -f kcinit
docker volume rm kctls

mkdir tls; mv tls.* tls
docker run --name kcinit -v kctls:/etc/x509/https centos
docker cp myrealm.json kcinit:/etc/x509/https/myrealm.json
docker cp tls/. kcinit:/etc/x509/https
docker rm kcinit

docker run --rm -v kctls:/etc/x509/https:ro centos ls -l /etc/x509/https
```

### Run the Keycloak image

```
KEYCLOAK_USER=admin
KEYCLOAK_PASSWORD=admin

docker network create kcnet

docker rm -f keycloak
docker run --detach \
    --name keycloak \
    -p 6443:8443 \
    --network kcnet \
    -v kctls:/etc/x509/https:ro \
    -e KEYCLOAK_USER=$KEYCLOAK_USER \
    -e KEYCLOAK_PASSWORD=$KEYCLOAK_PASSWORD \
    -e KEYCLOAK_IMPORT=/etc/x509/https/myrealm.json \
    --restart=always \
    quay.io/keycloak/keycloak 

docker logs -f keycloak

docker exec keycloak curl http://keycloak:8080/auth/realms/master/.well-known/openid-configuration
docker run --rm --network kcnet centos curl http://keycloak:8080/auth/realms/master/.well-known/openid-configuration
```

### Verify TLS access

```
YOURHOST=95.179.141.20
HOSTPORT=$YOURHOST:6443

curl --insecure https://$HOSTPORT/auth/realms/master/.well-known/openid-configuration | json_pp
curl --insecure https://$HOSTPORT/auth/realms/myrealm/.well-known/openid-configuration | json_pp

openssl s_client -connect $HOSTPORT -prexit
```

### Running the JaxRS API server

Then, you can spin up a the TryIt portal like this ...

```
docker pull nessusio/nessus-actions-jaxrs

docker rm -f jaxrs
docker run --detach \
    --name jaxrs \
    -p 7443:8443 \
    --network kcnet \
    -v kctls:/etc/x509/https:ro \
    -e JAXRS_TLS_URL=https://jaxrs:8443/jaxrs \
    -e KEYCLOAK_USER=$KEYCLOAK_USER \
    -e KEYCLOAK_PASSWORD=$KEYCLOAK_PASSWORD \
    -e KEYCLOAK_URL=http://keycloak:8080/auth \
    nessusio/nessus-actions-jaxrs

docker logs -f jaxrs

docker exec jaxrs tail -fn 1000 jaxrs/debug.log
```
