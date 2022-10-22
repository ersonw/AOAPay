# superPay
vim /usr/lib/systemd/system/docker.service
ExecStart=/usr/bin/dockerd -H tcp://0.0.0.0:2375 -H fd:// --containerd=/run/containerd/containerd.sock
systemctl daemon-reload
systemctl restart docker
firewall-cmd --zone=public --add-port=2375/tcp --permanent

docker pull mongo:5.0.9
use admin
# 创建一个名为 admin，密码为 123456 的用户。
> db.createUser({ user:'admin',pwd:'123456',roles:[ { role:'userAdminAnyDatabase', db: 'admin'}]});
# 尝试使用上面创建的用户信息进行连接。
> db.auth('admin', '123456')
# 以 admin 用户身份进入mongo
docker exec -it mongodb mongo admin
# 对 admin 用户 进行身份认证
> db.auth("admin","123456");
# 创建 用户、密码和数据库：
> db.createUser({ user: 'user1', pwd: '123456', roles: [ { role: "readWrite", db: "app" } ] });
# 退出
> exit
