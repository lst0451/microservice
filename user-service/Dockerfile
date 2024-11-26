# 使用官方 Java 17 运行时镜像
FROM eclipse-temurin:17-jdk-alpine

# 设置工作目录
WORKDIR /app

# 将 Maven 构建的 JAR 文件复制到容器中
COPY target/user-service-1.0-SNAPSHOT.jar user-service.jar

# 暴露应用端口
EXPOSE 8081

# 设置环境变量（可选，用于调试）
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/userdb
ENV SPRING_DATASOURCE_USERNAME=lst
ENV SPRING_DATASOURCE_PASSWORD=123456

# 定义启动命令
ENTRYPOINT ["java", "-jar", "user-service.jar"]
