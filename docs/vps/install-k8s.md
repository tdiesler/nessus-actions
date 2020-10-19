### Keycloak - No TLS
    
```
kubectl delete deployment mydep 

cat << EOF | kubectl apply -f -
apiVersion: apps/v1
kind: Deployment
metadata:
  labels:
    app: mydep
  name: mydep
  namespace: default
spec:
  selector:
    matchLabels:
      app: mydep
  template:
    metadata:
      labels:
        app: mydep
    spec:
      containers:
      - name: keycloak
        image: nessusio/keycloak
        env:
        - name: KEYCLOAK_USER
          value: admin
        - name: KEYCLOAK_PASSWORD
          value: admin
        ports:
        - containerPort: 8080
EOF

kubectl logs keycloak
```

### Create an TLS/HTTPS

https://kubernetes.github.io/ingress-nginx/user-guide/tls/

```
SECRET_NAME=kctls
kubectl create secret tls ${SECRET_NAME} --key tls/tls.key --cert tls/tls.crt

cat << EOF | kubectl apply -f -
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: keycloak-ingress
spec:
  tls:
  - secretName: kctls
  rules:
  - http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: mydep
            port:
              number: 8080
EOF
```


### TLS Credentials in a Volume

```
cat << EOF | kubectl apply -f -
apiVersion: apps/v1
kind: Deployment
metadata:
  labels:
    app: mydep
  name: mydep
  namespace: default
spec:
  selector:
    matchLabels:
      app: mydep
  template:
    metadata:
      labels:
        app: mydep
    spec:
      containers:
      - name: keycloak
        image: nessusio/keycloak
        env:
        - name: KEYCLOAK_USER
          value: admin
        - name: KEYCLOAK_PASSWORD
          value: admin
        ports:
        - containerPort: 8080
EOF
```

### TLS Credentials in a Volume 

```
mkdir tls
cp /etc/kubernetes/pki/ca.crt tls/tls.crt
cp /etc/kubernetes/pki/ca.key tls/tls.key
sudo chown core tls/tls.*

kubectl create secret generic tls --from-file=tls/
kubectl describe secret tls

kubectl delete pod keycloak

cat << EOF | kubectl apply -f -
apiVersion: v1
kind: Pod
metadata:
  name: keycloak
spec:
  containers:
  - name: shell
    image: centos
    command: ["ls", "-l", "/etc/x509/https"]
    volumeMounts:
    - name: tlsvol
      mountPath: /etc/x509/https
      readOnly: true
  volumes:
  - name: tlsvol
    secret: 
      secretName: tls
  restartPolicy: OnFailure
EOF

kubectl logs keycloak
```

