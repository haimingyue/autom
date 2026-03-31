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
8081
```

## 开发环境变量

后端默认使用 `dev` profile，可通过环境变量覆盖 MySQL 连接：

```text
SPRING_PROFILES_ACTIVE=dev
DB_HOST=127.0.0.1
DB_PORT=3307
DB_NAME=atoms
DB_USERNAME=root
DB_PASSWORD=your-password
WECHAT_APP_ID=your-wechat-miniapp-appid
WECHAT_APP_SECRET=your-wechat-miniapp-secret
APP_SESSION_TTL_DAYS=30
APP_SESSION_RENEW_BEFORE_HOURS=72
```

首个版本通过 Flyway 自动建表。登录态使用 `user_sessions` 持久化，业务接口需要 `Authorization: Bearer <token>`。

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
