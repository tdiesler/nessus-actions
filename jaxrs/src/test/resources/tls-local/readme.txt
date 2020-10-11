keytool -genkey -alias $ALIAS -keyalg RSA -keystore $KEYSTORE.jks -storepass $STOREPASS -keypass $STOREPASS -validity 360 \
    -dname "CN=Thomas Diesler,OU=Fuse,O=RedHat,L=Munich,ST=Bavaria,C=DE" \
    -ext "SAN:c=DNS:localhost,IP:127.0.0.1"

Subject Alternative Name (SAN)
------------------------------
DNS Name: localhost 
IP Address: 127.0.0.1 