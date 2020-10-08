## Fedora CoreOS

* 55 GB SSD
* 1 CPU
* 2 GB 

### Generate a self-signed cert using keytool

```
ALIAS=keycloak
HOSTNAME=yourhost
SAN_IP=95.179.187.140

keytool -genkey -alias $ALIAS -keyalg RSA -keystore keycloak.jks -storepass changeit -validity 360 \
    -dname "CN=Thomas Diesler,OU=Fuse,O=RedHat,L=Munich,ST=Bavaria,C=DE" -ext "SAN:c=DNS:$HOSTNAME,IP:$SAN_IP"

keytool -importkeystore -srckeystore keycloak.jks -destkeystore keycloak.jks -deststoretype PKCS12
```

### Export the cert and key

```
mkdir tls
openssl pkcs12 -in keycloak.jks -nokeys -out tls/tls.crt

openssl pkcs12 -in keycloak.jks -nocerts -nodes -out tls/tls.key

openssl pkcs12 -in keycloak.jks -passin pass:changeit -out tls/keycloak.pem
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

### Run the keycloak image
    
Before we actually do this, lets copy the cert to a volume

```
docker rm -f kcinit
docker volume rm kctls
docker volume rm kcrlm

docker run --name kcinit -v kctls:/etc/x509/https -v kcrlm:/var/tmp centos
docker cp keycloak/myrealm.json kcinit:/var/tmp
docker cp tls/. kcinit:/etc/x509/https

docker run --rm -v kctls:/etc/x509/https:ro centos ls -l /etc/x509/https
docker run --rm -v kcrlm:/var/tmp:ro centos ls -l /var/tmp

docker rm kcinit
```

with TLS ...

```
KEYCLOAK_USER='admin'
KEYCLOAK_PASSWORD='admin'

docker rm -f keycloak
docker run --detach \
    --name keycloak \
    -p 8443:8443 \
    -v kcrlm:/var/tmp:ro \
    -v kctls:/etc/x509/https:ro \
    -e KEYCLOAK_USER=$KEYCLOAK_USER \
    -e KEYCLOAK_PASSWORD=$KEYCLOAK_PASSWORD \
    -e KEYCLOAK_IMPORT=/var/tmp/myrealm.json \
    --restart=always \
    quay.io/keycloak/keycloak 

docker logs -f keycloak
```
