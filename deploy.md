# 在Minikube上启动微服务项目完整指南

## 前提条件
- Windows 11
- 已安装Docker Desktop
- 已安装Minikube
- 已安装Maven
- 已安装kubectl

## 步骤1: 启动Minikube

```bash
# 启动Minikube
minikube start

# 切换到Minikube的Docker环境
& minikube docker-env --shell powershell | Invoke-Expression
```

## 步骤2: 保存新配置文件

1. 创建`k8s/kafka.yaml`文件
2. 更新`k8s/user-service.yaml`或创建新的`k8s/user-service-updated.yaml`
3. 创建`k8s/notification-service.yaml`
4. 创建`notification-service/Dockerfile`
5. 创建`notification-service/src/main/resources/application-docker.yaml`

## 步骤3: 构建项目

```bash
# 在项目根目录下构建所有服务
mvn clean package -DskipTests
```

## 步骤4: 构建Docker镜像

```bash
# 构建用户服务镜像
cd user-service
docker build -t user-service:1.0 .

# 构建产品服务镜像
cd ../product-service
docker build -t product-service:1.0 .

# 构建通知服务镜像
cd ../notification-service
docker build -t notification-service:1.0 .

# 检查镜像是否创建成功
minikube ssh -- docker images
```

## 步骤5: 部署服务

```bash
# 部署PostgreSQL
kubectl apply -f k8s/postgres.yaml

# 等待PostgreSQL准备就绪
kubectl wait --for=condition=ready pod -l app=postgres --timeout=120s

# 创建数据库
kubectl exec -it $(kubectl get pod -l app=postgres -o jsonpath="{.items[0].metadata.name}") -- psql -U lst -c "CREATE DATABASE userdb;"
kubectl exec -it $(kubectl get pod -l app=postgres -o jsonpath="{.items[0].metadata.name}") -- psql -U lst -c "CREATE DATABASE productdb;"

# 部署Kafka和ZooKeeper
kubectl apply -f k8s/kafka.yaml

# 等待Kafka和ZooKeeper准备就绪
kubectl wait --for=condition=ready pod -l app=zookeeper --timeout=120s
kubectl wait --for=condition=ready pod -l app=kafka --timeout=120s

# 部署微服务
kubectl apply -f k8s/user-service-updated.yaml # 或k8s/user-service.yaml
kubectl apply -f k8s/product-service.yaml
kubectl apply -f k8s/notification-service.yaml

# 等待所有服务准备就绪
kubectl wait --for=condition=ready pod -l app=user-service --timeout=120s
kubectl wait --for=condition=ready pod -l app=product-service --timeout=120s
kubectl wait --for=condition=ready pod -l app=notification-service --timeout=120s
```

## 步骤6: 验证部署

```bash
# 检查所有资源状态
kubectl get all

# 检查每个服务的日志
kubectl logs deployment/user-service
kubectl logs deployment/product-service
kubectl logs deployment/notification-service
kubectl logs deployment/kafka
```

## 步骤7: 访问服务

```bash
# 暴露服务
minikube service user-service --url
minikube service product-service --url

# 服务将在以下端口可用:
# - 用户服务: NodePort 30081
# - 产品服务: NodePort 30082 (如果在k8s/product-service.yaml中已配置)
```

## 步骤8: 测试功能

1. 创建用户 (POST 请求到用户服务):
```bash
curl -X POST -H "Content-Type: application/json" -d '{"username":"testuser","password":"password123","email":"test@example.com"}' http://$(minikube service user-service --url)/api/users
```

2. 检查通知服务日志，确认是否收到了Kafka消息:
```bash
kubectl logs deployment/notification-service
```

## 步骤9: 清理资源

```bash
# 删除所有部署的资源
kubectl delete -f k8s/notification-service.yaml
kubectl delete -f k8s/user-service-updated.yaml # 或k8s/user-service.yaml
kubectl delete -f k8s/product-service.yaml
kubectl delete -f k8s/kafka.yaml
kubectl delete -f k8s/postgres.yaml

# 停止Minikube
minikube stop
```

## 常见问题排查

1. **服务无法连接到数据库**
    - 检查PostgreSQL服务是否运行: `kubectl get pods -l app=postgres`
    - 检查数据库是否已创建: `kubectl exec -it $(kubectl get pod -l app=postgres -o jsonpath="{.items[0].metadata.name}") -- psql -U lst -c "\l"`

2. **通知服务没有收到Kafka消息**
    - 检查Kafka服务是否运行: `kubectl get pods -l app=kafka`
    - 检查ZooKeeper服务是否运行: `kubectl get pods -l app=zookeeper`
    - 检查用户服务中Kafka配置是否正确: `kubectl describe pod -l app=user-service`

3. **无法访问服务**
    - 检查Service是否正确配置: `kubectl get svc`
    - 检查Minikube是否正在运行: `minikube status`
    - 尝试使用`minikube service <service-name>`直接打开服务