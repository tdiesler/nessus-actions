
## Stable Configuration

The stable config that we have so far

```
NAMESPACE=nessus
kubectl create namespace $NAMESPACE

kubectl -n $NAMESPACE apply -f https://raw.githubusercontent.com/tdiesler/nessus-actions/master/docs/k8s/deployment/keycloak/secret.yaml
kubectl -n $NAMESPACE apply -f https://raw.githubusercontent.com/tdiesler/nessus-actions/master/docs/k8s/deployment/nessus.yaml

kubectl -n $NAMESPACE logs -f pod/keycloak
kubectl -n $NAMESPACE logs -f pod/jaxrs
```

### Local Configuration

```
NAMESPACE=nessus
kubectl create namespace $NAMESPACE

# Delete all pods, service, etc in this app
kubectl -n $NAMESPACE delete pod,svc -l app=nessus

kubectl -n $NAMESPACE apply -f docs/k8s/deployment/keycloak/secret.yaml
kubectl -n $NAMESPACE apply -f docs/k8s/deployment/nessus.yaml

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

kubectl -n $NAMESPACE logs -f pod/jaxrs
kubectl -n $NAMESPACE logs -f pod/portal

kubectl delete namespace $NAMESPACE
```


### Test Pod

```
cat << EOF | kubectl -n $NAMESPACE apply -f -
# Test Pod
---
apiVersion: v1
kind: Pod
metadata:
  name: test
  labels:
    app: nessus
    pod: test
spec:
  containers:
  - name: test
    image: centos
    command: ["sleep", "3600"]
EOF

kubectl -n $NAMESPACE exec test -- bash
```
