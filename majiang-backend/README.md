# majiang-backend

麻将记账系统后端（Spring Boot 3 + MyBatis-Plus + MySQL）。

本项目面向微信小程序场景，提供：用户资料、好友关系、小队关系、对局创建与结算、战绩查询、头像上传等接口。

---

## 1. 技术栈

- Java 17
- Spring Boot 3.5.x
- MyBatis-Plus
- MySQL 8+
- Maven

---

## 2. 快速开始

### 2.1 初始化数据库

执行：`sql/app.sql`

> 说明：该脚本是**全量初始化脚本**，会先 `DROP TABLE` 再重建，不适用于保留历史数据的场景。

### 2.2 配置应用

编辑 `src/main/resources/application.yml`（数据库连接等）：

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`

并配置环境变量：

- `WECHAT_APPID`：微信小程序 appid
- `WECHAT_SECRET`：微信小程序 secret
- `SLOW_REQUEST_MS`：慢请求阈值（毫秒，默认 1000）

### 2.3 启动项目

```bash
mvn spring-boot:run
```

默认地址：`http://localhost:8080`

---

## 3. 统一返回格式

所有接口返回：

```json
{
  "code": 0,
  "msg": "success",
  "data": {}
}
```

常见错误码：

- `0`：成功
- `40000`：参数错误
- `40400`：资源不存在
- `50000`：服务内部异常

---

## 4. 关键能力说明

### 4.1 微信登录 code 换 openid

- 接口：`GET /api/user-data/by-code?code=xxx`
- 由 `WechatService` 统一处理调用和错误映射
- 常见错误：
  - `code无效或已过期`
  - `请求过于频繁，请稍后再试`
  - `微信服务暂时不可用，请稍后重试`

### 4.2 对局结算幂等

`app_user_battle` 表有唯一约束：`(game_id, user_id)`，并且代码层也做了重复结算校验。

同一局、同一用户不会重复写入战绩。

### 4.3 请求链路追踪

- 每个请求都会带 `X-Trace-Id`（若客户端没传，服务端自动生成）
- 日志中包含 `traceId`，可用于问题排查

---

## 5. API 概览（按模块）

> 以下为当前代码中的实际路由；含部分“兼容路由”。

### 5.1 用户数据 `AppUserDataController`

前缀：`/api/user-data`

- `POST /api/user-data`：新增用户数据（支持 `avatar` 网络地址下载落盘）
- `GET /api/user-data/by-code?code=...`：微信 code 换 openid
- `GET /api/user-data/{id}`：按 id 查询（推荐）
- `GET /api/user-data/by-id?id=...`：按 id 查询（兼容）
- `GET /api/user-data/openid/{openid}`：按 openid 查询（推荐）
- `GET /api/user-data/by-openid?openid=...`：按 openid 查询（兼容）
- `GET /api/user-data`：分页查询（`openid/title/page/size`）
- `PUT /api/user-data/{id}`：修改
- `DELETE /api/user-data/{id}`：删除

### 5.2 对局 `AppGameController`

前缀：`/api/games`（兼容 `/api/game`）

- `POST /api/games`：创建对局（推荐）
- `POST /api/game/create`：创建对局（兼容）
- `GET /api/games/{id}`：查询对局详情（推荐）
- `GET /api/game/{id}`：查询对局详情（兼容）
- `GET /api/games/list`：查询对局列表（推荐）
- `GET /api/game/list`：查询对局列表（兼容）
- `POST /api/games/{gameId}/settlement`：结算对局（推荐）
- `POST /api/game/settle`：结算对局（兼容）

### 5.3 战绩 `AppUserBattleController`

前缀：`/api/user-battle`

- `POST /api/user-battle`：新增战绩
- `GET /api/user-battle/{id}`：按 id 查询
- `GET /api/user-battle/by-user-id-matchDate`：按 userId + 时间范围分页查询
- `GET /api/user-battle`：分页查询（支持条件过滤）
- `PUT /api/user-battle/{id}`：修改
- `DELETE /api/user-battle/{id}`：删除

### 5.4 小队信息 `AppTeamDataController`

前缀：`/api/user-team`

- `POST /api/user-team`：新增小队
- `GET /api/user-team`：分页查询
- `PUT /api/user-team/{id}`：修改
- `DELETE /api/user-team/{id}`：删除

### 5.5 小队成员关系 `AppTeamUserController`

前缀：`/api/team-user`

- `POST /api/team-user/add`：新增成员关系
- `DELETE /api/team-user/remove?teamId=...&userId=...`：移除成员关系
- `GET /api/team-user/by-team?teamId=...`：查小队成员
- `GET /api/team-user/by-user?userId=...`：查用户加入的小队

### 5.6 好友关系 `AppFriendUserController`

前缀：`/api/friend-user`

- `POST /api/friend-user/add`：添加好友关系
- `DELETE /api/friend-user/remove?userId=...&friendUserId=...`：移除好友关系
- `GET /api/friend-user/by-user?userId=...`：查某用户好友（返回 id/title/avatarUrl）
- `GET /api/friend-user/by-user-friend?userId=...&friendUserId=...`：查两人关系记录

### 5.7 头像上传 `AvatarController`

前缀：`/api/user`

- `POST /api/user/upload-avatar`：上传头像文件（`multipart/form-data`）

---

## 6. 数据库表

初始化脚本中包含以下表：

- `app_user_data`
- `app_team_data`
- `app_user_battle`
- `app_friend_user`
- `app_team_user`
- `game`
- `game_player`

---

## 7. 常见问题排查

### 7.1 微信登录拿不到 openid

优先检查：

1. `WECHAT_APPID/WECHAT_SECRET` 是否正确
2. `code` 是否过期（code 仅短时有效）
3. 网络是否可访问 `api.weixin.qq.com`

### 7.2 头像上传/下载失败

检查：

1. `uploads/` 目录是否有写权限
2. 头像 URL 是否可公网访问
3. 文件大小与类型是否符合限制

### 7.3 对局重复结算报错

这是预期保护。若同一局已结算，会被拒绝，避免重复入账。

### 7.4 如何根据一次请求查日志

1. 从响应头取 `X-Trace-Id`
2. 在日志中按 `traceId` 搜索

---

## 8. 目录建议

- `sql/app.sql`：唯一初始化脚本
- `src/main/java/.../controller`：接口层
- `src/main/java/.../service`：业务层
- `src/main/java/.../mapper`：数据访问层
- `src/test/java`：单元测试

---

如需我继续，我可以下一步把 README 再升级成“前后端联调手册版”（补全每个接口的请求/响应示例和错误示例）。
