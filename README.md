# superPay
docker login --username=godword@foxmail.com registry.cn-shenzhen.aliyuncs.com
docker tag ersonw/aoapay registry.cn-shenzhen.aliyuncs.com/ersonw/aoapay
docker push registry.cn-shenzhen.aliyuncs.com/ersonw/aoapay


vim /usr/lib/systemd/system/docker.service
ExecStart=/usr/bin/dockerd -H tcp://0.0.0.0:2375 -H fd:// --containerd=/run/containerd/containerd.sock
systemctl daemon-reload
systemctl restart docker
firewall-cmd --zone=public --add-port=2375/tcp --permanent

docker pull mongo:5.0.9
use admin
# 创建一个名为 admin，密码为 123456 的用户。
> db.createUser({ user:'admin',pwd:'6d2880f9f562108b',roles:[ { role:'userAdminAnyDatabase', db: 'admin'}]});
# 尝试使用上面创建的用户信息进行连接。
> db.auth('admin', '6d2880f9f562108b')
# 以 admin 用户身份进入mongo
docker exec -it mongodb mongo admin
# 对 admin 用户 进行身份认证
> db.auth("admin","6d2880f9f562108b");
# 创建 用户、密码和数据库：
> db.createUser({ user: 'aoapay', pwd: '6d2880f9f562108b', roles: [ { role: "readWrite", db: "aoapay" } ] });
# 退出
> exit
