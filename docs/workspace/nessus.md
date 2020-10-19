
## Stable Configuration

The stable config that we have so far

```
NAMESPACE=nessus
kubectl delete namespace $NAMESPACE

kubectl create namespace $NAMESPACE

kubectl -n $NAMESPACE apply -f https://raw.githubusercontent.com/tdiesler/nessus-actions/k8s/docs/k8s/deployment/keycloak/secret.yaml
kubectl -n $NAMESPACE apply -f https://raw.githubusercontent.com/tdiesler/nessus-actions/k8s/docs/k8s/deployment/nessus.yaml

kubectl -n $NAMESPACE logs -f pod/keycloak
kubectl -n $NAMESPACE logs -f pod/jaxrs
```

### Work in Progress

* Keycloak with Cluster provided TLS
* Portal Frontend

```
NAMESPACE=nessus
kubectl delete namespace $NAMESPACE

kubectl create namespace $NAMESPACE

kubectl -n $NAMESPACE apply -f docs/k8s/deployment/keycloak/secret.yaml
kubectl -n $NAMESPACE apply -f docs/k8s/deployment/nessus.yaml

kubectl -n $NAMESPACE logs -f pod/keycloak
kubectl -n $NAMESPACE logs -f pod/jaxrs
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
