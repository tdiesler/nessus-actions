
### Install Ingress NGINX Controller

https://kubernetes.io/docs/concepts/services-networking/ingress

https://kubernetes.github.io/ingress-nginx/deploy/baremetal

```
NGINX_VERSION=v0.40.2
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-$NGINX_VERSION/deploy/static/provider/baremetal/deploy.yaml

kubectl get pods --all-namespaces
```

### Ingress to NGINX (WIP)

Even this simple setup does not (yet) work with NodePort setup
Giving up for now. There would be too many hoops to jump through for little benefit.

With the Baremetal NodePort setup a client would still need to access the node ports of the 
ingress controller (e.g. 80:32319/TCP,443:31536) instead of 80/443.

```
NAMESPACE=default
kubectl create namespace $NAMESPACE

kubectl delete pod,svc,ing --all

cat << EOF | kubectl apply -f -
# NGINX Pod
---
apiVersion: v1
kind: Pod
metadata:
  name: nginx
  labels:
    pod: nginx
spec:
  containers:
  - name: nginx
    image: nginx

# NGINX Service
---
apiVersion: v1
kind: Service
metadata:
  name: nginx
spec:
  selector:
    pod: nginx
  ports:
  - name: http
    port: 80
  type: ClusterIP    
EOF

cat << EOF | kubectl apply -f -
# Ingress to NGINX
---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: nginx
spec:
  rules:
  - http:
      paths:
      - path: /test
        pathType: Prefix
        backend:
          service:
            name: nginx
            port:
              number: 80
EOF

kubectl get svc/nginx
kubectl get ing/nginx
kubectl describe ing/nginx
```

