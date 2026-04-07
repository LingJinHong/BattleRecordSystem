# majiang-backend 联调手册

> 用于前后端联调的接口文档（基于当前代码）。

- Base URL：`http://localhost:8080`
- 返回格式：

```json
{
  "code": 0,
  "msg": "success",
  "data": {}
}
```

---

## 1. 用户数据（`/api/user-data`）

### 1.1 新增用户数据

`POST /api/user-data`

请求体：

```json
{
  "openid": "wx-openid-001",
  "avatar": "https://example.com/avatar.png",
  "title": "昵称",
  "content": "简介"
}
```

### 1.2 微信 code 换 openid

`GET /api/user-data/by-code?code=xxxx`

成功 `data` 为字符串：

```json
{
  "code": 0,
  "msg": "success",
  "data": "wx-openid-001"
}
```

常见失败：
- `code无效或已过期`
- `请求过于频繁，请稍后再试`
- `微信服务暂时不可用，请稍后重试`

### 1.3 按 ID 查询

推荐：`GET /api/user-data/{id}`  
兼容：`GET /api/user-data/by-id?id={id}`

### 1.4 按 openid 查询

推荐：`GET /api/user-data/openid/{openid}`  
兼容：`GET /api/user-data/by-openid?openid={openid}`

### 1.5 分页查询

`GET /api/user-data?openid=...&title=...&page=1&size=10`

### 1.6 修改

`PUT /api/user-data/{id}`

请求体：

```json
{
  "title": "新标题",
  "content": "新内容",
  "avatar": "https://example.com/new.png"
}
```

### 1.7 删除

`DELETE /api/user-data/{id}`

---

## 2. 对局（`/api/games` / `/api/game`）

### 2.1 创建对局

推荐：`POST /api/games`  
兼容：`POST /api/game/create`

请求体：

```json
{
  "teamId": 1,
  "userId": 1001,
  "userIds": [1001, 1002, 1003, 1004]
}
```

### 2.2 查询对局详情

推荐：`GET /api/games/{id}`  
兼容：`GET /api/game/{id}`

### 2.3 查询对局列表

推荐：`GET /api/games/list`  
兼容：`GET /api/game/list`

默认按 `id` 倒序返回。

### 2.4 结算对局

推荐：`POST /api/games/{gameId}/settlement`  
兼容：`POST /api/game/settle`

请求体：

```json
{
  "gameId": 12,
  "venueFee": 20,
  "results": [
    {"userId": 1001, "amount": 100},
    {"userId": 1002, "amount": -40},
    {"userId": 1003, "amount": -30},
    {"userId": 1004, "amount": -30}
  ]
}
```

返回 `data` 为转账明细数组：

```json
[
  {"fromUserId": 1002, "toUserId": 1001, "amount": 40},
  {"fromUserId": 1003, "toUserId": 1001, "amount": 30},
  {"fromUserId": 1004, "toUserId": 1001, "amount": 30}
]
```

说明：
- 所有 `amount` 之和必须为 `0`
- 同一局重复结算会被拒绝（幂等保护）

---

## 3. 战绩（`/api/user-battle`）

### 3.1 新增战绩

`POST /api/user-battle`

### 3.2 按 ID 查询

`GET /api/user-battle/{id}`

### 3.3 按用户+日期范围查询

`GET /api/user-battle/by-user-id-matchDate?userId=1001&startTime=2026-04-01&endTime=2026-04-30&page=1&size=10`

### 3.4 分页查询

`GET /api/user-battle?userId=...&incomeExpenseType=...&teamId=...&startTime=...&endTime=...&page=1&size=10`

### 3.5 修改

`PUT /api/user-battle/{id}`

### 3.6 删除

`DELETE /api/user-battle/{id}`

---

## 4. 小队信息（`/api/user-team`）

- `POST /api/user-team`
- `GET /api/user-team?page=1&size=10&userId=...&teamName=...`
- `PUT /api/user-team/{id}`
- `DELETE /api/user-team/{id}`

---

## 5. 小队成员关系（`/api/team-user`）

- `POST /api/team-user/add`
- `DELETE /api/team-user/remove?teamId=1&userId=1001`
- `GET /api/team-user/by-team?teamId=1`
- `GET /api/team-user/by-user?userId=1001`

---

## 6. 好友关系（`/api/friend-user`）

- `POST /api/friend-user/add`
- `DELETE /api/friend-user/remove?userId=1001&friendUserId=1002`
- `GET /api/friend-user/by-user?userId=1001`
- `GET /api/friend-user/by-user-friend?userId=1001&friendUserId=1002`

---

## 7. 头像上传（`/api/user/upload-avatar`）

`POST /api/user/upload-avatar`

- Content-Type: `multipart/form-data`
- 字段名：`avatarFile`

---

## 8. 联调建议

1. 先调用 `/api/user-data/by-code` 拿 `openid`
2. 用 `openid` 创建用户数据
3. 创建对局后可先调 `/api/games/{id}` 或 `/api/games/list` 确认对局已创建
4. 再调用结算接口
5. 结算失败优先检查：金额和是否为 0、是否重复结算
6. 遇到问题请记录响应头 `X-Trace-Id` 给后端排查
