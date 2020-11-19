FROM azul/zulu-openjdk-centos:8

COPY bin /tryit/bin
COPY config /tryit/config
COPY lib /tryit/lib

ENTRYPOINT ["/tryit/bin/run.sh"]
