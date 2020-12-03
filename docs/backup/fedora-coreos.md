## Fedora CoreOS

* 55 GB SSD
* 1 CPU
* 2 GB 

### Generate a self-signed cert using keytool

```
ALIAS=tryit
KEYSTORE=keycloak
STOREPASS=changeit
YOURHOST=yourhost
YOURIP=95.179.141.20

keytool -genkey -alias $ALIAS -keyalg RSA -keystore $KEYSTORE.jks -storepass $STOREPASS -keypass $STOREPASS -validity 360 \
    -dname "CN=Thomas Diesler,OU=Fuse,O=RedHat,L=Munich,ST=Bavaria,C=DE" \
    -ext "SAN:c=DNS:$YOURHOST,IP:$YOURIP"
    
keytool -importkeystore -srckeystore $KEYSTORE.jks -srcstorepass $STOREPASS \
    -destkeystore $KEYSTORE.p12 -deststorepass $STOREPASS -deststoretype pkcs12
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
    -v `pwd`/tls:/myfiles/tls \
    -v ~/git/nessus-actions/docs/vps/fedora-coreos-config.fcc:/config.fcc \
    quay.io/coreos/fcct:release --pretty --strict --files-dir myfiles /config.fcc
```
