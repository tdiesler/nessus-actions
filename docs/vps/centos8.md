## CentOS 8

* 55 GB SSD
* 1 CPU
* 2 GB 

```
ssh root@vps

sed -i "s/PasswordAuthentication yes/PasswordAuthentication no/" /etc/ssh/sshd_config
cat /etc/ssh/sshd_config | grep PasswordAuthentication
systemctl restart sshd

dnf update -y

timedatectl set-timezone Europe/Amsterdam
timedatectl

cat << EOF > /etc/systemd/journald.conf
# Disable systemd journal logging rate limiting 
# https://www.rootusers.com/how-to-change-log-rate-limiting-in-linux
RateLimitInterval=0
RateLimitBurst=0
EOF
systemctl restart systemd-journald

export NUSER=core
useradd -G root -m $NUSER -s /bin/bash
cp -r .ssh /home/$NUSER/
chown -R $NUSER.$NUSER /home/$NUSER/.ssh

cat << EOF > /etc/sudoers.d/user-privs-$NUSER
$NUSER ALL=(ALL:ALL) NOPASSWD: ALL
EOF
```
    
## Swap setup to avoid running out of memory

```
fallocate -l 4G /mnt/swapfile
dd if=/dev/zero of=/mnt/swapfile bs=1024 count=4M
mkswap /mnt/swapfile
chmod 600 /mnt/swapfile
swapon /mnt/swapfile
echo '/mnt/swapfile none swap sw 0 0' >> /etc/fstab
free -h
```

## Download and Install Docker

```
https://phoenixnap.com/kb/how-to-install-docker-on-centos-8

dnf config-manager --add-repo=https://download.docker.com/linux/centos/docker-ce.repo
dnf install docker-ce --nobest

systemctl daemon-reload
systemctl enable docker
systemctl restart docker

docker ps

usermod -aG docker $NUSER
systemctl disable firewalld
```
