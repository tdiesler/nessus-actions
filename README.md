## Nessus Actions

<!-- [![Default Build](https://github.com/tdiesler/nessus-actions/workflows/Default%20Build/badge.svg)](https://github.com/tdiesler/nessus-actions/actions) -->
[![License](https://img.shields.io/:license-Apache2-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

Explore [Apache Camel](http://camel.apache.org/) based actions inspired by [GitHub Actions](https://docs.github.com/en/actions). 

## Container Ports

|| HTTP     | HTTPS |
| --------- | ----- | ----- |
| H2        | 9092  |       |
| Keycloak  | 8000  | 30043 |
| Maven     | 8100  | 30143 |
| Jaxrs     | 8200  | 30243 |
| Portal    | 8300  | 30343 |

## Running on Kubernetes

Running on Kubernetes is currently the preferred setup. For this, you would want to have access to 
a running Kubernetes cluster. In case you'd like to get started from scratch, we have instructions 
on how to setup Kubernetes on CentOS are [here](https://github.com/tdiesler/nessus-actions/blob/master/docs/vps/k8s-centos7.md) 

### Create Pods and Services

```
# Create the namespace
NAMESPACE=nessus
kubectl create namespace $NAMESPACE

CURRENT_CONTEXT=kubernetes-admin@kubernetes
kubectl config set contexts.$CURRENT_CONTEXT.namespace $NAMESPACE
kubectl config view
 
# Delete this app
kubectl delete pod,svc --all

# Create Secrets & Persistent Volumes
kubectl apply -f https://raw.githubusercontent.com/tdiesler/nessus-actions/master/docs/k8s/deployment/keycloak/secret.yaml
kubectl apply -f https://raw.githubusercontent.com/tdiesler/nessus-actions/master/docs/k8s/deployment/keycloak/pvolume.yaml

# Apply the last stable configuration
kubectl apply -f https://raw.githubusercontent.com/tdiesler/nessus-actions/master/docs/k8s/deployment/nessus.yaml

kubectl get pv,pod,svc

# NAME                    CAPACITY   ACCESS MODES   RECLAIM POLICY   STATUS   CLAIM          STORAGECLASS   REASON   AGE
# persistentvolume/h2pv   1G         RWX            Retain           Bound    nessus/h2pvc                           56s

# NAME           READY   STATUS    RESTARTS   AGE
# pod/h2         1/1     Running   0          22s
# pod/jaxrs      1/1     Running   0          22s
# pod/keycloak   1/1     Running   0          22s
# pod/maven      1/1     Running   0          22s
# pod/portal     1/1     Running   0          21s

# NAME                     TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
# service/h2-http          NodePort    10.106.103.191   <none>        9092:30092/TCP   22s
# service/jaxrs-http       ClusterIP   10.100.66.39     <none>        8200/TCP         22s
# service/jaxrs-https      NodePort    10.98.93.171     <none>        8443:30243/TCP   22s
# service/keycloak-http    ClusterIP   10.110.206.228   <none>        8000/TCP         22s
# service/keycloak-https   NodePort    10.101.221.48    <none>        8443:30043/TCP   22s
# service/maven-http       ClusterIP   10.108.225.229   <none>        8100/TCP         22s
# service/maven-https      NodePort    10.100.108.156   <none>        8443:30143/TCP   22s
# service/portal-https     NodePort    10.106.232.255   <none>        8443:30343/TCP   21s
```

### Access to the Portal

Access to the [protal](https://136.244.111.173:32443/portal) is not restriced. Anyone can register
and try out what we currently have.

### Access to the REST API

Access to the [REST API](https://136.244.111.173:31443/api) is not restriced. Anyone can access the 
full functional scope just like the portal above, which is just another Jaxrs client.

The OpenAPI schema is published here: [TODO]

Here are a few examples of API requests ...

[TODO]

### Access to the Kubernetes Console

Access to the [console](https://136.244.111.173:30123) is restriced to folks who have a valid token.

If you have access to the cluster, you can get a token like this ... 

```
kubectl -n kubernetes-dashboard describe secret $(kubectl -n kubernetes-dashboard get secret | grep admin-user | awk '{print $1}')
```

## Running on Docker

Although Kubernetes is the preferred runtime, it may be useful to run these containers selectively 
on your local Docker instance. The default build does just that.
 

### Creating a user network

Containers communiacte with the outside world via TLS (i.e. https). For inter-container communication they bypass
TLS and use plain http on the network that we create here.

```
docker network create nessus
```

### Running H2

```
docker rm -f h2
docker run --detach \
    --name h2 \
    -p 9092:9092 \
    -v h2vol:/var/h2db \
    --network nessus \
    -e JDBC_SERVER_URL=jdbc:h2:tcp://localhost:9092/fuse \
    -e JDBC_URL="jdbc:h2:file:/var/h2db/fuse;init=create schema if not exists nessus\;create schema if not exists keycloak" \
    -e JDBC_USER=keycloak \
    -e JDBC_PASSWORD=password \
    nessusio/nessus-h2:1.2.3

docker logs -f h2
```

### Running Keycloak

First, you'd want to spin up a [Keycloak](https://www.keycloak.org/getting-started/getting-started-docker) instance

```
# Download the default application realm
curl --create-dirs -o /tmp/keycloak/myrealm.json https://raw.githubusercontent.com/tdiesler/nessus-actions/master/docs/k8s/deployment/keycloak/myrealm.json

KEYCLOAK_USER=admin
KEYCLOAK_PASSWORD=admin

docker rm -f keycloak
docker run --detach \
    --name keycloak \
    -p 8000:8080 \
    --network nessus \
    -e DB_DATABASE=fuse \
    -e DB_SCHEMA=keycloak \
    -e KEYCLOAK_USER=$KEYCLOAK_USER \
    -e KEYCLOAK_PASSWORD=$KEYCLOAK_PASSWORD \
    -e KEYCLOAK_IMPORT=/tmp/keycloak/myrealm.json \
    -v /tmp/keycloak:/tmp/keycloak \
    nessusio/keycloak 

docker logs -f keycloak
```

and verify that you can login to the admin console

```
http://localhost:8000/auth/admin
```

### Running Maven

Then, you can spin up a the Maven project builder like this ...

```
docker rm -f maven
docker run --detach \
    --name maven \
    -p 8100:8100 \
    --network nessus \
    -v mvnm2:/root/.m2 \
    -v mvnws:/var/nessus/workspace/maven \
    nessusio/nessus-actions-maven 

docker logs -f maven
```

### Running the JAXRS API server

Then, you can spin up a the API server like this ...

```
docker rm -f jaxrs
docker run --detach \
    --name jaxrs \
    -p 8200:8200 \
    --network nessus \
    -e JDBC_URL=jdbc:h2:tcp://h2:9092/fuse\;schema=nessus \
    -e KEYCLOAK_USER=$KEYCLOAK_USER \
    -e KEYCLOAK_PASSWORD=$KEYCLOAK_PASSWORD \
    -e KEYCLOAK_URL=http://keycloak:8080/auth \
    -e MAVEN_URL=http://maven:8100/maven \
    nessusio/nessus-actions-jaxrs

docker logs -f jaxrs
```

### Running the Portal

Then, you can spin up a the Portal like this ...

```
docker rm -f portal
docker run --detach \
    --name portal \
    -p 8300:8300 \
    --network nessus \
    -e KEYCLOAK_USER=$KEYCLOAK_USER \
    -e KEYCLOAK_PASSWORD=$KEYCLOAK_PASSWORD \
    -e KEYCLOAK_URL=http://keycloak:8080/auth \
    -e JAXRS_URL=http://jaxrs:8200/jaxrs \
    nessusio/nessus-actions-portal

docker logs -f portal
```

and connect to it

```
http://localhost:8300/portal
```

Enjoy!
