# autom-back-end

Atoms 后端服务，技术栈为 `Java 21 + Spring Boot + Spring Web + Spring Data JPA + MySQL`。

## 当前状态

当前已完成：

- Spring Boot 工程初始化
- 基础目录结构
- 统一响应体
- 全局异常处理
- 健康检查接口
- 应用配置模板

## 本地启动

```bash
mvn spring-boot:run
```

默认端口：

```text
8080
```

基础健康检查：

```text
GET /api/v1/system/ping
GET /actuator/health
```

## 后续模块

- auth
- user
- habit
- habit-log
- focus-session
- progress
- reflection
