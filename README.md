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
NAMESPACE=nessus
kubectl create namespace $NAMESPACE

# Delete all pods, service, etc in this app
kubectl -n $NAMESPACE delete pod,svc -l app=nessus

# Apply the last stable configuration
kubectl -n $NAMESPACE apply -f https://raw.githubusercontent.com/tdiesler/nessus-actions/master/docs/k8s/deployment/keycloak/secret.yaml
kubectl -n $NAMESPACE apply -f https://raw.githubusercontent.com/tdiesler/nessus-actions/master/docs/k8s/deployment/nessus.yaml

kubectl -n $NAMESPACE get pod,svc
# NAME           READY   STATUS    RESTARTS   AGE
# pod/h2         1/1     Running   0          2m3s
# pod/jaxrs      1/1     Running   0          2m3s
# pod/keycloak   1/1     Running   0          2m3s
# pod/portal     1/1     Running   0          2m3s

# NAME                     TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
# service/h2-http          NodePort    10.104.214.182   <none>        9092:30092/TCP   2m3s
# service/jaxrs-http       ClusterIP   10.101.201.71    <none>        8180/TCP         2m3s
# service/jaxrs-https      NodePort    10.100.83.174    <none>        8443:31443/TCP   2m3s
# service/keycloak-http    ClusterIP   10.110.111.149   <none>        8080/TCP         2m3s
# service/keycloak-https   NodePort    10.108.35.102    <none>        8443:30443/TCP   2m3s
# service/portal-https     NodePort    10.100.37.29     <none>        8443:32443/TCP   2m2s
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
docker network create kcnet
```

### Running an H2 database instance

```
docker rm -f h2
docker run --detach \
    --name h2 \
    -p 9092:9092 \
    -v h2vol:/var/h2db \
    --network kcnet \
    -e JDBC_SERVER_URL=jdbc:h2:tcp://localhost:9092/keycloak \
    -e JDBC_URL=jdbc:h2:/var/h2db/keycloak \
    -e JDBC_USER=keycloak \
    -e JDBC_PASSWORD=password \
    nessusio/nessus-h2

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
    --network kcnet \
    -e KEYCLOAK_USER=$KEYCLOAK_USER \
    -e KEYCLOAK_PASSWORD=$KEYCLOAK_PASSWORD \
    -e KEYCLOAK_IMPORT=/tmp/keycloak/myrealm.json \
    -v /tmp/keycloak:/tmp/keycloak \
    nessusio/keycloak 

docker logs -f keycloak
```

and verify that you can login to the admin console

```
http://localhost:8180/auth/admin
```

### Running the JAXRS API server

Then, you can spin up a the API server like this ...

```
docker rm -f jaxrs
docker run --detach \
    --name jaxrs \
    -p 8280:8280 \
    --network kcnet \
    -e KEYCLOAK_USER=$KEYCLOAK_USER \
    -e KEYCLOAK_PASSWORD=$KEYCLOAK_PASSWORD \
    -e KEYCLOAK_URL=http://keycloak:8080/auth \
    nessusio/nessus-actions-jaxrs

docker logs -f jaxrs

docker exec jaxrs tail -fn 1000 jaxrs/debug.log
```

### Running the GUI server

Then, you can spin up a the TryIt GUI like this ...

```
docker rm -f trygui
docker run --detach \
    --name trygui \
    -p 8380:8080 \
    --network kcnet \
    -e KEYCLOAK_USER=$KEYCLOAK_USER \
    -e KEYCLOAK_PASSWORD=$KEYCLOAK_PASSWORD \
    -e KEYCLOAK_URL=http://keycloak:8080/auth \
    -e JAXRS_API_URL=http://jaxrs:8280/tryit \
    nessusio/nessus-actions-gui

docker logs -f trygui

docker exec trygui tail -fn 1000 trygui/debug.log
```

and connect to it

```
http://localhost:8080/portal
```

Enjoy!
