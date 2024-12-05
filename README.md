## 项目业务场景设想
### 用户服务 (User Service)：

- 使用 PostgreSQL 存储用户信息。
提供注册、登录、用户信息查询等 API。
可以集成 OAuth2 或第三方登录（如 Google 登录）。
用户注册/登录成功后发送 Kafka 消息通知其他服务。
产品服务 (Product Service)：

- 使用 MongoDB 存储产品信息，适合存储不定长的产品描述和嵌套结构（如规格、图片）。
提供添加产品、查询产品、更新库存等 API。
在产品更新时发送 Kafka 消息，通知订单服务更新库存。
订单服务 (Order Service)：

- 使用 PostgreSQL 存储订单信息。
处理订单创建、查询和支付功能。
在订单创建成功后，发送 Kafka 消息通知库存更新，同时也通知用户服务发送确认邮件。
集成 Zipkin 做分布式追踪，用于监控用户下单时涉及的所有服务调用链路。
通知服务 (Notification Service)：

监听 Kafka 主题，处理订单确认、支付通知等。
支持发送邮件、短信等通知方式。
可以使用 Spring Boot 的异步任务或者 Kafka 的消费者来处理并发通知任务。
支付服务 (Payment Service)：

模拟第三方支付，接收支付请求并返回支付状态。
更新订单支付状态，并通过 Kafka 发送支付成功或失败的消息。
使用 MongoDB 存储支付日志信息。
技术集成
Kafka：作为服务间的消息中间件，支持异步事件传递，例如用户注册、订单创建、库存更新和支付通知。
PostgreSQL 和 MongoDB：分别用于结构化和非结构化数据存储，展示两种数据库的使用场景。
Zipkin：用于分布式追踪，分析微服务间的调用链路和性能瓶颈。
Docker 和 Kubernetes (K8S)：用于容器化部署，支持在 K8S 集群中进行扩展和管理。
Camunda (未来扩展)：可以在订单服务中加入工作流引擎，处理复杂的订单状态流转（如审批流程、退款流程）。
系统架构图
css
复制代码
[User Service]  → Kafka →  [Order Service] → PostgreSQL
↓                  →  [Product Service] → MongoDB
PostgreSQL            →  [Payment Service] → Kafka
↓
[Notification Service]

下一步计划
初始化项目结构，包括各个微服务模块。
配置 Docker 和 K8S，准备部署环境。
实现基本功能模块，先从 User Service 和 Product Service 开始。
集成 Kafka 和 Zipkin，验证分布式调用链路追踪。
准备扩展模块，例如引入 Camunda 处理工作流。
这样设计的好处是：

业务逻辑相对简单，容易扩展和展示。
使用的技术栈全面，涵盖常见的微服务架构实践。
可以根据需要逐步扩展功能，例如加入缓存、API Gateway 等。

在项目的根目录下（microservice）执行
mvn clean package -DskipTests 打包
然后执行 
docker-compose up --build 启动项目
docker-compose down(删除镜像)
