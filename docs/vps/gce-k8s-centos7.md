## Install Kubernetes

* CentOS 7
* e2-standard-2 (2 vCPUs, 8 GB memory) 
* 40 GB SSD

### Setup the core user

```
ssh core@vps

sudo yum update -y

# Install Time Service
sudo yum install -y ntp ntpdate
sudo systemctl enable ntpd
sudo systemctl start ntpd

# Install tho Monitor Agent
# https://cloud.google.com/monitoring/agent/installation
curl -sSO https://dl.google.com/cloudagents/add-monitoring-agent-repo.sh
sudo bash add-monitoring-agent-repo.sh
sudo yum install -y stackdriver-agent

sudo service stackdriver-agent restart
sudo service stackdriver-agent status
```

### Letting iptables see bridged traffic

https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/install-kubeadm/

```
# Make sure that the br_netfilter module is loaded
sudo modprobe br_netfilter
sudo lsmod | grep br_netfilter

cat << EOF | sudo tee /etc/sysctl.d/k8s.conf
net.bridge.bridge-nf-call-ip6tables = 1
net.bridge.bridge-nf-call-iptables = 1
EOF
sudo sysctl --system | grep net.bridge
```

### Install Docker

https://docs.docker.com/engine/install/centos

```
sudo groupadd docker
sudo usermod -aG docker $USER

sudo yum install -y docker
sudo systemctl enable --now docker

# Verify docker access after restart
docker run --rm centos echo "Hello World"
```

### Install Kubernetes

```
# Add the Kubernetes repository
cat << EOF | sudo tee /etc/yum.repos.d/kubernetes.repo
[kubernetes]
name=Kubernetes
baseurl=https://packages.cloud.google.com/yum/repos/kubernetes-el7-\$basearch
enabled=1
gpgcheck=1
repo_gpgcheck=1
gpgkey=https://packages.cloud.google.com/yum/doc/yum-key.gpg https://packages.cloud.google.com/yum/doc/rpm-package-key.gpg
exclude=kubelet kubeadm kubectl
EOF

# Set SELinux in permissive mode (effectively disabling it)
sudo setenforce 0
sudo sed -i 's/^SELINUX=enforcing$/SELINUX=permissive/' /etc/selinux/config
sudo cat /etc/selinux/config | grep "^SELINUX="

# WARNING: firewalld is active, please ensure ports [6443 10250] are open or your cluster may not function correctly
sudo systemctl disable --now firewalld

sudo yum install -y kubelet kubeadm kubectl --disableexcludes=kubernetes
sudo systemctl enable --now kubelet
```

### Init Kubernetes

https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/create-cluster-kubeadm

```
CLUSTER_IP=34.68.182.208
INTERNAL_IP=10.128.0.5

sudo kubeadm init \
    --pod-network-cidr=$INTERNAL_IP/16 \
    --apiserver-cert-extra-sans=$CLUSTER_IP

# Your Kubernetes control-plane has initialized successfully!
# 
# To start using your cluster, you need to run the following as a regular user:
# 
#   mkdir -p $HOME/.kube
#   sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
#   sudo chown $(id -u):$(id -g) $HOME/.kube/config
# 
# You should now deploy a pod network to the cluster.
# Run "kubectl apply -f [podnetwork].yaml" with one of the options listed at:
#   https://kubernetes.io/docs/concepts/cluster-administration/addons/
# 
# Then you can join any number of worker nodes by running the following on each as root:
# 
# kubeadm join 10.128.0.9:6443 --token q4t15m.d403l1ijushaldrc \
#     --discovery-token-ca-cert-hash sha256:b6845e1af55d61cc507f161b7b9e0c9a15f9061b95dd5a10de53b7cdee249bef
    
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config

# Rename the current context and server addr
sed -i "s/kubernetes-admin@kubernetes/nessus/" .kube/config 

# Use external cluster IP
sed -i "s/$INTERNAL_IP/$CLUSTER_IP/" .kube/config 

# Add helper functions to bash
cat << EOF >> .bash_profile

# Kubernetes
alias kc="kubectl"
export KUBECONFIG="$HOME/.kube/config"

# Function that sets the namespace for the current context
kubens() {
   current=\`kubectl config current-context\`
   kubectl config set contexts.\$current.namespace \$1
}

# Function that switches the current context
kubectx() { 
   kubectl config use-context \$1
}
EOF
source .bash_profile

```

### Remove NoScedule Taint

Pods may never leave 'Pending' state when the target node is tainted with 'NoSchedule'

```
kubectl describe node `hostname` | grep Taints
kubectl taint node `hostname` node-role.kubernetes.io/master-
```

### Install Flannel CNI

https://github.com/coreos/flannel/blob/master/Documentation/kubernetes.md

```
FLANNEL_VERSION=v0.13.0
kubectl apply -f https://raw.githubusercontent.com/coreos/flannel/$FLANNEL_VERSION/Documentation/kube-flannel.yml

watch kubectl get pods --all-namespaces

# NAMESPACE     NAME                               READY   STATUS    RESTARTS   AGE
# kube-system   coredns-f9fd979d6-fqlk5            1/1     Running   0          5m31s
# kube-system   coredns-f9fd979d6-j4jvf            1/1     Running   0          5m30s
# kube-system   etcd-nessus01                      1/1     Running   0          5m45s
# kube-system   kube-apiserver-nessus01            1/1     Running   0          5m45s
# kube-system   kube-controller-manager-nessus01   1/1     Running   0          5m45s
# kube-system   kube-flannel-ds-7ncbx              1/1     Running   0          37s
# kube-system   kube-proxy-749lw                   1/1     Running   0          5m31s
# kube-system   kube-scheduler-nessus01            1/1     Running   0          5m45s
```

### Install the Dashborad

https://github.com/kubernetes/dashboard#kubernetes-dashboard

https://github.com/kubernetes/dashboard/blob/master/docs/user/access-control/creating-sample-user.md

```
DASHBOARD_VERSION=v2.0.4
kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/$DASHBOARD_VERSION/aio/deploy/recommended.yaml

kubectl -n kubernetes-dashboard patch service kubernetes-dashboard \
    -p '"spec": {"ports": [{"port": 443, "nodePort": 30100 }], "type": "NodePort"}'

kubectl -n kubernetes-dashboard get service kubernetes-dashboard
# NAME                   TYPE       CLUSTER-IP      EXTERNAL-IP   PORT(S)         AGE
# kubernetes-dashboard   NodePort   10.99.169.208   <none>        443:30100/TCP   112s

# Creating a Service Account
cat << EOF | kubectl apply -f -
apiVersion: v1
kind: ServiceAccount
metadata:
  name: admin-user
  namespace: kubernetes-dashboard
EOF

# Creating a ClusterRoleBinding
cat << EOF | kubectl apply -f -
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: admin-user
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: cluster-admin
subjects:
- kind: ServiceAccount
  name: admin-user
  namespace: kubernetes-dashboard
EOF

kubectl -n kubernetes-dashboard describe secret $(kubectl -n kubernetes-dashboard get secret | grep admin-user | awk '{print $1}')

https://34.68.182.208:30100
```

### Test Networking

```
kubectl create namespace test 
kubectl -n test delete pod,svc --all

cat << EOF | kubectl -n test apply -f -
# NGINX Pod
---
apiVersion: v1
kind: Pod
metadata:
  name: nginx
  labels:
    app: test
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
  name: nginx-http
  labels:
    app: test
spec:
  selector:
    app: test
    pod: nginx
  ports:
  - name: http
    port: 80
  type: ClusterIP    
EOF

kubectl -n test run test --image=centos -it
# curl $NGINX_HTTP_SERVICE_HOST
# exit

kubectl -n test delete pod,svc --all 
kubectl delete namespace test 
```

## Install Kubernetes Client Tools

https://kubernetes.io/docs/tasks/tools/install-kubectl/

```
brew install kubectl 

mkdir $HOME/.kube
scp -r core@35.225.17.29:.kube/config $HOME/.kube

kubectl get pods --all-namespaces
```
