YOURIP=95.179.141.20

keytool -genkey -alias $ALIAS -keyalg RSA -keystore $KEYSTORE.jks -storepass $STOREPASS -keypass $STOREPASS -validity 360 \
    -dname "CN=Thomas Diesler,OU=Fuse,O=RedHat,L=Munich,ST=Bavaria,C=DE" \
    -ext "SAN:c=IP:95.179.141.20"

Subject Alternative Name (SAN)
------------------------------
IP Address: 95.179.141.20