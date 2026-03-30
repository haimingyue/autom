# Atoms API 设计

本文档定义 Atoms MVP 阶段的后端接口契约，供 Java + Spring Boot 后端与 uniapp 前端联调使用。

## 1. 设计原则

- 接口风格：REST API
- 数据格式：JSON
- 鉴权方式：登录后 Bearer Token
- 版本前缀：`/api/v1`
- 首发平台：微信小程序

## 2. 通用约定

### 2.1 请求头

登录后的业务接口统一携带：

```http
Authorization: Bearer {token}
Content-Type: application/json
```

### 2.2 通用响应体

```json
{
  "code": 0,
  "message": "ok",
  "data": {}
}
```

### 2.3 通用错误码建议

| code | 含义 |
| --- | --- |
| 0 | 成功 |
| 40001 | 参数错误 |
| 40101 | 未登录或 token 无效 |
| 40301 | 无权限访问资源 |
| 40401 | 资源不存在 |
| 40901 | 业务冲突，例如重复打卡 |
| 50001 | 服务内部错误 |

## 3. 认证接口

### 3.1 微信登录

`POST /api/v1/auth/wechat/login`

**说明：**
使用微信小程序 `code` 完成登录。

**请求体：**

```json
{
  "code": "wx-login-code"
}
```

**响应体：**

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "token": "jwt-or-session-token",
    "isNewUser": true,
    "user": {
      "id": 1,
      "displayName": "用户昵称",
      "avatarUrl": "",
      "timezone": "Asia/Shanghai"
    }
  }
}
```

**规则：**

- 后端根据 `code` 换取微信身份信息
- 若用户不存在则创建
- 返回 token 和用户基础信息

## 4. 用户接口

### 4.1 获取当前用户

`GET /api/v1/users/me`

**响应示例：**

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "id": 1,
    "displayName": "用户昵称",
    "avatarUrl": "",
    "timezone": "Asia/Shanghai"
  }
}
```

### 4.2 更新当前用户

`PUT /api/v1/users/me`

**请求体：**

```json
{
  "displayName": "simon qian",
  "avatarUrl": "",
  "timezone": "Asia/Shanghai"
}
```

**规则：**

- 仅允许更新当前登录用户信息

## 5. 习惯接口

### 5.1 创建习惯

`POST /api/v1/habits`

**请求体：**

```json
{
  "actionText": "穿上跑鞋",
  "identityText": "成为一个健康的人",
  "fullStatement": "我将在每天早上8点穿上跑鞋，这样我能成为一个健康的人",
  "repeatDays": [1, 2, 3, 4, 5, 6, 7],
  "targetValue": 1,
  "targetUnit": "time",
  "reminderEnabled": true,
  "reminderTime": "08:00"
}
```

**字段说明：**

- `repeatDays`：1 到 7 分别表示周一到周日
- `targetUnit`：MVP 默认支持 `time`、`count`

### 5.2 查询今日习惯

`GET /api/v1/habits/today`

**响应示例：**

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "date": "2026-03-30",
    "items": [
      {
        "habitId": 101,
        "actionText": "穿上跑鞋",
        "identityText": "成为一个健康的人",
        "fullStatement": "我将在每天早上8点穿上跑鞋，这样我能成为一个健康的人",
        "completed": true,
        "streak": 3,
        "reminderTime": "08:00"
      }
    ]
  }
}
```

**规则：**

- 今日习惯按用户时区自然日计算
- 仅返回当前有效状态的习惯

### 5.3 查询习惯列表

`GET /api/v1/habits`

**查询参数：**

- `status`：可选，`active` 或 `archived`

### 5.4 查询习惯详情

`GET /api/v1/habits/{id}`

**响应示例：**

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "id": 101,
    "actionText": "穿上跑鞋",
    "identityText": "成为一个健康的人",
    "fullStatement": "我将在每天早上8点穿上跑鞋，这样我能成为一个健康的人",
    "repeatDays": [1, 2, 3, 4, 5, 6, 7],
    "targetValue": 1,
    "targetUnit": "time",
    "reminderEnabled": true,
    "reminderTime": "08:00",
    "status": "active"
  }
}
```

### 5.5 更新习惯

`PUT /api/v1/habits/{id}`

**请求体：**
与创建习惯保持一致。

### 5.6 归档习惯

`POST /api/v1/habits/{id}/archive`

**规则：**

- 归档后习惯不再出现在今日习惯列表

## 6. 打卡接口

### 6.1 提交打卡

`POST /api/v1/habits/{id}/check-ins`

**请求体：**

```json
{
  "date": "2026-03-30",
  "completed": true,
  "completionSource": "manual",
  "focusSessionId": 2001
}
```

**规则：**

- 同一习惯在同一自然日最多一条完成型记录
- `focusSessionId` 可为空
- 若填写 `focusSessionId`，则必须属于当前用户

**成功响应示例：**

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "habitId": 101,
    "date": "2026-03-30",
    "completed": true,
    "streak": 3
  }
}
```

### 6.2 查询习惯打卡历史

`GET /api/v1/habits/{id}/logs`

**查询参数：**

- `startDate`
- `endDate`

**响应示例：**

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "habitId": 101,
    "logs": [
      {
        "date": "2026-03-28",
        "completed": true
      },
      {
        "date": "2026-03-29",
        "completed": true
      },
      {
        "date": "2026-03-30",
        "completed": true
      }
    ]
  }
}
```

## 7. 专注接口

### 7.1 开始专注

`POST /api/v1/focus-sessions`

**请求体：**

```json
{
  "habitId": 101,
  "plannedDurationMinutes": 25
}
```

**说明：**

- `habitId` 可为空，表示本次专注不强绑定某个习惯

### 7.2 完成专注

`POST /api/v1/focus-sessions/{id}/complete`

**请求体：**

```json
{
  "actualDurationMinutes": 25,
  "completed": true
}
```

**规则：**

- 只能完成当前用户自己的专注记录

### 7.3 查询今日专注记录

`GET /api/v1/focus-sessions/today`

## 8. 进度接口

### 8.1 查询进度总览

`GET /api/v1/progress/overview`

**响应示例：**

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "totalActiveHabits": 2,
    "todayCompletedHabits": 1,
    "bestStreak": 7,
    "currentStreaks": [
      {
        "habitId": 101,
        "streak": 3
      }
    ]
  }
}
```

### 8.2 查询单习惯进度

`GET /api/v1/progress/habits/{id}`

**响应示例：**

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "habitId": 101,
    "completionRate": 100,
    "currentStreak": 3,
    "bestStreak": 3,
    "totalRepetitions": 3,
    "heatmap": [
      {
        "date": "2026-03-28",
        "completed": true
      },
      {
        "date": "2026-03-29",
        "completed": true
      },
      {
        "date": "2026-03-30",
        "completed": true
      }
    ]
  }
}
```

**规则：**

- `completionRate` 基于已到期应执行天数与完成天数计算

## 9. 反思接口

### 9.1 创建反思

`POST /api/v1/reflections`

**请求体：**

```json
{
  "habitId": 101,
  "reflectionType": "habit",
  "promptText": "你如何看待自己在形成这个习惯上的进展？",
  "answerText": "今天比昨天更自然了一些。"
}
```

**说明：**

- `habitId` 可为空，表示身份层反思
- `reflectionType` 建议支持：`habit`、`identity`

### 9.2 查询反思列表

`GET /api/v1/reflections`

**查询参数：**

- `habitId`：可选
- `reflectionType`：可选
- `view`：可选，`recent` 或 `all`

**响应示例：**

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "items": [
      {
        "id": 5001,
        "habitId": 101,
        "reflectionType": "habit",
        "promptText": "你如何看待自己在形成这个习惯上的进展？",
        "answerText": "今天比昨天更自然了一些。",
        "createdAt": "2026-03-30T08:30:00+08:00"
      }
    ]
  }
}
```

## 10. MVP 业务规则汇总

- 所有业务接口默认需要登录
- 今日、连续天数、完成率都按用户时区计算
- 同一习惯同一天最多一条完成型打卡记录
- streak 由后端基于 habit_log 计算
- 前端不自行计算核心统计口径
- 内容模块与监督模块暂不进入第一版正式 API

## 11. 二期预留接口

以下能力只做预留，不进入当前 MVP 正式实现：

- 内容列表
- 内容详情
- 收藏内容
- 邀请监督伙伴
- 接受邀请码
