## Fedora CoreOS

* 55 GB SSD
* 1 CPU
* 2 GB 

### Generate a self-signed cert using keytool

```
ALIAS=tryit
KEYSTORE=keycloak
HOSTNAME=yourhost
YOURIP=95.179.187.140

keytool -genkey -alias $ALIAS -keyalg RSA -keystore $KEYSTORE.jks -storepass changeit -validity 360 \
    -dname "CN=Thomas Diesler,OU=Fuse,O=RedHat,L=Munich,ST=Bavaria,C=DE" \
    -ext "SAN:c=DNS:$HOSTNAME,IP:$YOURIP"

keytool -importkeystore -srckeystore $KEYSTORE.jks -destkeystore $KEYSTORE.p12 -deststoretype pkcs12
```

### Export the cert and key

```
openssl pkcs12 -in $KEYSTORE.p12 -nokeys -out tls.crt

openssl pkcs12 -in $KEYSTORE.p12 -nocerts -nodes -out tls.key

openssl pkcs12 -in $KEYSTORE.p12 -out tls.pem
```

### Generate the ignition file

```
docker run --rm \
    -v ~/git/nessus-actions/docs/vps/fedora-coreos-config.fcc:/config.fcc \
    -v `pwd`/tls:/myfiles/tls \
    quay.io/coreos/fcct:release --pretty --strict --files-dir myfiles /config.fcc
```

### Import the cert into your Java truststore

```
sudo keytool -import -alias $ALIAS -file tls.crt -keystore $JAVA_HOME/jre/lib/security/cacerts

keytool -list -alias $ALIAS -keystore $JAVA_HOME/jre/lib/security/cacerts

sudo keytool -delete -alias $ALIAS -keystore $JAVA_HOME/jre/lib/security/cacerts
```

### Running Keycloak
    
Before we actually do this, lets copy the cert to a volume

```
docker rm -f kcinit
docker volume rm kctls

docker run --name kcinit -v kctls:/etc/x509/https centos
docker cp keycloak/myrealm.json kcinit:/etc/x509/https/myrealm.json
docker cp tls/. kcinit:/etc/x509/https
docker rm kcinit

docker run --rm -v kctls:/etc/x509/https:ro centos ls -l /etc/x509/https
```

```
KEYCLOAK_USER='admin'
KEYCLOAK_PASSWORD='admin'

docker rm -f keycloak
docker run --detach \
    --name keycloak \
    -p 8443:8443 \
    -p 8180:8080 \
    -v kctls:/etc/x509/https:ro \
    -e KEYCLOAK_USER=$KEYCLOAK_USER \
    -e KEYCLOAK_PASSWORD=$KEYCLOAK_PASSWORD \
    -e KEYCLOAK_IMPORT=/etc/x509/https/myrealm.json \
    --restart=always \
    quay.io/keycloak/keycloak 

docker logs -f keycloak
```

### Running TryIt

Then, you can spin up a the TryIt portal like this ...

```
KEYCLOAK_URL="http://$YOURIP:8180/auth"

docker rm -f portal
docker run --detach \
    --name portal \
    -p 9443:9443 \
    -p 8280:8280 \
    -v kctls:/etc/x509/https:ro \
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
