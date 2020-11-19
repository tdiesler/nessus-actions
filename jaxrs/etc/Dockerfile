FROM azul/zulu-openjdk-centos:8

COPY bin /nessus-jaxrs/bin
COPY lib /nessus-jaxrs/lib
COPY config /nessus-jaxrs/config

ENTRYPOINT ["/nessus-jaxrs/bin/run.sh"]
