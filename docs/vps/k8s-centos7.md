## Install Kubernetes

* CentOS 7
* 80 GB SSD
* 2 CPU
* 4 GB 

### Setup the core user

```
ssh root@vps

sed -i "s/^PasswordAuthentication yes$/PasswordAuthentication no/" /etc/ssh/sshd_config
cat /etc/ssh/sshd_config | grep PasswordAuthentication
systemctl restart sshd

yum update -y

timedatectl set-timezone Europe/Amsterdam
timedatectl

export NUSER=core
useradd -G root -m $NUSER -s /bin/bash
cp -r .ssh /home/$NUSER/
chown -R $NUSER.$NUSER /home/$NUSER/.ssh

cat << EOF > /etc/sudoers.d/user-privs-$NUSER
$NUSER ALL=(ALL:ALL) NOPASSWD: ALL
EOF
```

### Letting iptables see bridged traffic

https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/install-kubeadm/

```
modprobe br_netfilter
lsmod | grep br_netfilter

cat << EOF > /etc/sysctl.d/k8s.conf
net.bridge.bridge-nf-call-ip6tables = 1
net.bridge.bridge-nf-call-iptables = 1
EOF
sysctl --system | grep net.bridge
```

### Install Docker

```
yum install -y docker

systemctl enable --now docker

docker ps
```

### Install Kubernetes

```
cat << EOF > /etc/yum.repos.d/kubernetes.repo
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
setenforce 0
sed -i 's/^SELINUX=enforcing$/SELINUX=permissive/' /etc/selinux/config
cat /etc/selinux/config | grep "^SELINUX="

# WARNING: firewalld is active, please ensure ports [6443 10250] are open or your cluster may not function correctly
systemctl disable --now firewalld

yum install -y kubelet kubeadm kubectl --disableexcludes=kubernetes
systemctl enable --now kubelet
```

### Init Kubernetes with Flannel CIDR

https://github.com/coreos/flannel/blob/master/Documentation/kubernetes.md

```
kubeadm init --pod-network-cidr=10.244.0.0/16 

echo "export KUBECONFIG=/etc/kubernetes/admin.conf" >> .bash_profile
source .bash_profile

mkdir /home/$NUSER/.kube
cp /etc/kubernetes/admin.conf /home/$NUSER/.kube/config
chown -R $NUSER.$NUSER /home/$NUSER/.kube

kubectl get pods --all-namespaces
```

### Remove NoScedule Taint

Pods may never leave 'Pending' state when the target node is tainted with 'NoSchedule'

```
kubectl describe node kube01 | grep Taints

kubectl taint node kube01 node-role.kubernetes.io/master-
```

### Install Flannel CNI

https://github.com/coreos/flannel/blob/master/Documentation/kubernetes.md

```
FLANNEL_VERSION=v0.13.0
kubectl apply -f https://raw.githubusercontent.com/coreos/flannel/$FLANNEL_VERSION/Documentation/kube-flannel.yml

kubectl get pods --all-namespaces
```

### Install NGINX Ingress Controller

https://kubernetes.io/docs/concepts/services-networking/ingress/

https://docs.nginx.com/nginx-ingress-controller/installation/installation-with-manifests/

```
NGINX_VERSION=v1.8.1

# Configure RBAC
kubectl apply -f https://raw.githubusercontent.com/nginxinc/kubernetes-ingress/$NGINX_VERSION/deployments/common/ns-and-sa.yaml
kubectl apply -f https://raw.githubusercontent.com/nginxinc/kubernetes-ingress/$NGINX_VERSION/deployments/rbac/rbac.yaml
kubectl apply -f https://raw.githubusercontent.com/nginxinc/kubernetes-ingress/$NGINX_VERSION/deployments/rbac/ap-rbac.yaml

# Create Common Resources
kubectl apply -f https://raw.githubusercontent.com/nginxinc/kubernetes-ingress/$NGINX_VERSION/deployments/common/default-server-secret.yaml
kubectl apply -f https://raw.githubusercontent.com/nginxinc/kubernetes-ingress/$NGINX_VERSION/deployments/common/nginx-config.yaml

# Create Custom Resources
# kubectl apply -f https://raw.githubusercontent.com/nginxinc/kubernetes-ingress/$NGINX_VERSION/deployments/common/vs-definition.yaml
# kubectl apply -f https://raw.githubusercontent.com/nginxinc/kubernetes-ingress/$NGINX_VERSION/deployments/common/vsr-definition.yaml
# kubectl apply -f https://raw.githubusercontent.com/nginxinc/kubernetes-ingress/$NGINX_VERSION/deployments/common/ts-definition.yaml
# kubectl apply -f https://raw.githubusercontent.com/nginxinc/kubernetes-ingress/$NGINX_VERSION/deployments/common/policy-definition.yaml

# Run the Ingress Controller
kubectl apply -f https://raw.githubusercontent.com/nginxinc/kubernetes-ingress/$NGINX_VERSION/deployments/daemon-set/nginx-ingress.yaml

# Check that the Ingress Controller is Running
kubectl get pods --namespace=nginx-ingress
```

## Install Kubernetes Client Tools

### Install kubectl on macOS

https://kubernetes.io/docs/tasks/tools/install-kubectl/

```
brew install kubectl 

scp root@95.179.141.20:/etc/kubernetes/admin.conf ~/.kube/config
```

### Install the Dashborad

https://github.com/kubernetes/dashboard#kubernetes-dashboard

https://github.com/kubernetes/dashboard/blob/master/docs/user/access-control/creating-sample-user.md

```
DASHBOARD_VERSION=v2.0.4
kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/$DASHBOARD_VERSION/aio/deploy/recommended.yaml

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

kubectl get pods -n kubernetes-dashboard

kubectl proxy &
http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy/
```

### Wipe your cluster installation

```
kubeadm reset -f
rm -rf /etc/kubernetes
```
