FROM @docker.maven.image.name@

COPY bin /nessus-maven/bin
COPY lib /nessus-maven/lib
COPY config /nessus-maven/config

ENTRYPOINT ["/nessus-maven/bin/run.sh"]
