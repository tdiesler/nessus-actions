## Fedora CoreOS

* 55 GB SSD
* 1 CPU
* 2 GB 

### Generate a self-signed cert using keytool

```
ALIAS=tryit
KEYSTORE=keycloak
STOREPASS=changeit
YOURIP=95.179.141.20

keytool -genkey -alias $ALIAS -keyalg RSA -keystore $KEYSTORE.jks -storepass $STOREPASS -keypass $STOREPASS -validity 360 \
    -dname "CN=Thomas Diesler,OU=Fuse,O=RedHat,L=Munich,ST=Bavaria,C=DE"
    
keytool -importkeystore -srckeystore $KEYSTORE.jks -srcstorepass $STOREPASS \
    -destkeystore $KEYSTORE.p12 -deststorepass $STOREPASS -deststoretype pkcs12

# [TODO] Verify when this is needed
# -ext "SAN:c=DNS:$HOSTNAME,IP:$YOURIP"
```

### Export the cert and key

```
openssl pkcs12 -in $KEYSTORE.p12 -passin pass:$STOREPASS -nokeys -out tls.crt

openssl pkcs12 -in $KEYSTORE.p12 -passin pass:$STOREPASS -nocerts -nodes -out tls.key

openssl pkcs12 -in $KEYSTORE.p12 -passin pass:$STOREPASS -passout pass:$STOREPASS -out tls.pem

rm $KEYSTORE.*; cd ..; ls -l tls
```

### Generate the ignition file

```
docker run --rm \
    -v ~/git/nessus-actions/docs/vps/fcos-config.fcc:/config.fcc \
    -v `pwd`/tls:/myfiles/tls \
    quay.io/coreos/fcct:release --pretty --strict --files-dir myfiles /config.fcc
```

### [TODO Verify] Import the cert into your Java truststore

```
sudo keytool -import -alias $ALIAS -file tls.crt -keystore $JAVA_HOME/jre/lib/security/cacerts

keytool -list -alias $ALIAS -keystore $JAVA_HOME/jre/lib/security/cacerts

sudo keytool -delete -alias $ALIAS -keystore $JAVA_HOME/jre/lib/security/cacerts
```

