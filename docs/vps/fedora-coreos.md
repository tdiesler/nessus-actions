## Fedora CoreOS

* 55 GB SSD
* 1 CPU
* 2 GB 

Generate the ignition file like this

```
docker run --rm \
    -v ~/git/nessus-actions/docs/vps/fedora-coreos-config.fcc:/config.fcc \
    quay.io/coreos/fcct:release --pretty --strict /config.fcc
```

### Generate a self-signed cert using keytool

```
ALIAS=keycloak
HOSTNAME=yourhost
SAN_IP=95.179.187.140

keytool -genkey -alias $ALIAS -keyalg RSA -keystore keycloak.jks -storepass changeit -validity 360 \
    -dname "CN=Thomas Diesler,OU=Fuse,O=RedHat,L=Munich,ST=Bavaria,C=DE" -ext "SAN:c=DNS:$HOSTNAME,IP:$SAN_IP"

keytool -importkeystore -srckeystore keycloak.jks -destkeystore keycloak.p12 -deststoretype PKCS12
```

### Export the cert and key

```
openssl pkcs12 -in keycloak.p12 -nokeys -out tls.crt

openssl pkcs12 -in keycloak.p12 -nocerts -nodes -out tls.key

openssl pkcs12 -in keycloak.p12 -passin pass:changeit -out keycloak.pem
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

docker run --name kcinit -v kctls:/etc/x509/https centos mkdir -p /etc/x509/https
docker cp tls/tls.key kcinit:/etc/x509/https
docker cp tls/tls.crt kcinit:/etc/x509/https
docker run --rm -v kctls:/etc/x509/https:ro centos ls -l /etc/x509/https
docker rm kcinit
```

```
KEYCLOAK_USER='admin'
KEYCLOAK_PASSWORD='admin'

docker rm -f keycloak
docker run --detach \
    --name keycloak \
    --restart=always \
    -p 8443:8443 \
    -v kctls:/etc/x509/https:ro \
    -e "KEYCLOAK_USER=$KEYCLOAK_USER" \
    -e "KEYCLOAK_PASSWORD=$KEYCLOAK_PASSWORD" \
    quay.io/keycloak/keycloak 

docker logs -f keycloak
```

