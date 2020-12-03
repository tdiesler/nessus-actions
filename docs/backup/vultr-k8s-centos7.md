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

HOSTNAME=`hostname`
HOSTIP=`hostname -I | cut -d " " -f1`
cat << EOF > /etc/hosts
127.0.0.1 localhost
$HOSTIP $HOSTNAME
EOF
cat /etc/hosts

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
# Add the Kubernetes repository
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

mkdir .kube
cp /etc/kubernetes/admin.conf .kube/config

cat << EOF >> .bash_profile

# Kubernetes
alias kc="kubectl"
export KUBECONFIG="$HOME/.kube/config"

# Function that sets the namespace for the current context
kubens() {
   current=`kubectl config current-context`
   kubectl config set contexts.\$current.namespace \$1
}

# Function that switches the current context
kubectx() { 
   kubectl config use-context \$1
}
EOF
source .bash_profile

kubectl get pods --all-namespaces
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

kubectl get pods --all-namespaces
NAMESPACE     NAME                               READY   STATUS    RESTARTS   AGE
kube-system   coredns-f9fd979d6-fqlk5            1/1     Running   0          5m31s
kube-system   coredns-f9fd979d6-j4jvf            1/1     Running   0          5m30s
kube-system   etcd-nessus01                      1/1     Running   0          5m45s
kube-system   kube-apiserver-nessus01            1/1     Running   0          5m45s
kube-system   kube-controller-manager-nessus01   1/1     Running   0          5m45s
kube-system   kube-flannel-ds-7ncbx              1/1     Running   0          37s
kube-system   kube-proxy-749lw                   1/1     Running   0          5m31s
kube-system   kube-scheduler-nessus01            1/1     Running   0          5m45s
```

### Install the Dashborad

https://github.com/kubernetes/dashboard#kubernetes-dashboard

https://github.com/kubernetes/dashboard/blob/master/docs/user/access-control/creating-sample-user.md

```
DASHBOARD_VERSION=v2.0.4
kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/$DASHBOARD_VERSION/aio/deploy/recommended.yaml

kubectl -n kubernetes-dashboard patch service kubernetes-dashboard \
    -p '"spec": {"ports": [{"port": 443, "nodePort": 30123 }], "type": "NodePort"}'

kubectl -n kubernetes-dashboard get service kubernetes-dashboard
# NAME                   TYPE       CLUSTER-IP      EXTERNAL-IP   PORT(S)         AGE
# kubernetes-dashboard   NodePort   10.99.169.208   <none>        443:30123/TCP   112s

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

https://136.244.111.173:30123
```

### Wipe your cluster installation

Just in case you need to do this

```
kubeadm reset -f
rm -rf /etc/kubernetes
```

## Install Kubernetes Client Tools

### Install kubectl on macOS

https://kubernetes.io/docs/tasks/tools/install-kubectl/

```
brew install kubectl 

scp root@45.76.36.143:/etc/kubernetes/admin.conf ~/.kube/config
```
