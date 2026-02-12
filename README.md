# 社区维修工具共享平台

一个基于 Spring Boot + Vue 3 的全栈社区工具共享服务平台，实现工具从发布到借用再到归还的完整流程。

## 技术栈

### 后端
- **Spring Boot 3.2.0** - 核心框架
- **Spring Security** - 安全认证与权限管理
- **Spring Data JPA** - 数据库操作
- **MySQL** - 数据库
- **JWT (jjwt 0.12.3)** - 身份验证与会话管理
- **Lombok** - 代码简化

### 前端
- **Vue 3.4** - 前端框架 (Composition API)
- **Vue Router 4** - 路由管理
- **Pinia 2** - 状态管理
- **Element Plus 2.4** - UI 组件库
- **Axios** - HTTP 请求
- **Vite 5** - 构建工具

## 项目结构

```
wxkf/
├── backend/                          # Spring Boot 后端
│   ├── pom.xml                       # Maven 配置
│   └── src/main/
│       ├── java/com/community/toolsharing/
│       │   ├── ToolSharingApplication.java
│       │   ├── config/               # 配置类 (Security, CORS)
│       │   ├── controller/           # REST 控制器
│       │   ├── dto/                  # 数据传输对象
│       │   ├── enums/                # 枚举类型
│       │   ├── exception/            # 全局异常处理
│       │   ├── model/                # JPA 实体
│       │   ├── repository/           # 数据仓库接口
│       │   ├── security/             # JWT 安全组件
│       │   └── service/              # 业务服务层
│       └── resources/
│           ├── application.yml       # 应用配置
│           ├── schema.sql            # 数据库建表脚本
│           └── data.sql              # 初始数据
├── frontend/                         # Vue 3 前端
│   ├── package.json
│   ├── vite.config.js
│   ├── index.html
│   └── src/
│       ├── main.js                   # 入口文件
│       ├── App.vue
│       ├── api/                      # API 请求封装
│       ├── stores/                   # Pinia 状态管理
│       ├── router/                   # 路由配置
│       ├── components/               # 公共组件 (布局)
│       └── views/                    # 页面视图
│           ├── auth/                 # 登录/注册
│           ├── user/                 # 用户端页面
│           └── admin/                # 管理员端页面
```

## 功能模块

### 用户端
| 功能 | 说明 |
|------|------|
| 用户注册/登录 | JWT 身份认证，注册赠送积分 |
| 工具浏览 | 按关键词搜索、按分类筛选 |
| 工具发布 | 发布闲置工具，支持图片、描述、分类 |
| 工具借用 | 申请借用 → 审批 → 取货 → 归还完整流程 |
| 借出管理 | 管理收到的借用请求，同意/拒绝 |
| 评价系统 | 归还后对工具和体验评分评价 |
| 积分系统 | 发布/借用/归还/评价等操作获取积分 |
| 积分排行 | 社区居民积分排行榜 |
| 消息通知 | 借用审批、状态变更等实时通知 |
| 个人中心 | 管理个人信息、查看各类记录 |

### 管理员端
| 功能 | 说明 |
|------|------|
| 数据概览 | 平台关键数据监控仪表盘 |
| 用户管理 | 查看用户列表，启用/禁用用户 |
| 工具审核 | 审核新发布的工具，通过/拒绝/下架 |
| 公告管理 | 发布、编辑、删除平台公告 |

### 积分规则
| 行为 | 积分 |
|------|------|
| 用户注册 | +10 |
| 发布工具 | +5 |
| 申请借用 | +2 |
| 同意借出 | +3 |
| 归还工具 | +2 |
| 发表评价 | +2 |

## 快速启动

### 环境要求
- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Node.js 18+
- npm 或 yarn

### 1. 数据库准备
```sql
CREATE DATABASE tool_sharing DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 启动后端
```bash
cd backend
# 修改 src/main/resources/application.yml 中的数据库连接信息
mvn spring-boot:run
```
后端启动后运行在 http://localhost:8080

默认管理员账号：`admin` / `admin123`

### 3. 启动前端
```bash
cd frontend
npm install
npm run dev
```
前端启动后运行在 http://localhost:5173

### 4. 访问应用
- 用户端：http://localhost:5173
- 管理后台：登录管理员账号后，点击头像菜单中的"管理后台"

## API 接口总览

### 认证接口
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `GET /api/auth/profile` - 获取个人信息
- `PUT /api/auth/profile` - 更新个人信息

### 工具接口
- `GET /api/tools` - 获取可借用工具列表
- `GET /api/tools/{id}` - 获取工具详情
- `POST /api/tools` - 发布工具
- `PUT /api/tools/{id}` - 更新工具
- `DELETE /api/tools/{id}` - 删除工具
- `GET /api/tools/my` - 我的工具

### 借用接口
- `POST /api/borrows` - 申请借用
- `GET /api/borrows/my` - 我的借用记录
- `GET /api/borrows/received` - 收到的借用请求
- `PUT /api/borrows/{id}/approve` - 同意借用
- `PUT /api/borrows/{id}/reject` - 拒绝借用
- `PUT /api/borrows/{id}/pickup` - 确认取货
- `PUT /api/borrows/{id}/return` - 确认归还

### 评价接口
- `POST /api/reviews` - 发表评价
- `GET /api/reviews/tool/{toolId}` - 工具评价列表

### 积分接口
- `GET /api/points/ranking` - 积分排行
- `GET /api/points/my` - 我的积分记录

### 通知接口
- `GET /api/notifications` - 消息列表
- `PUT /api/notifications/{id}/read` - 标记已读
- `PUT /api/notifications/read-all` - 全部已读

### 管理员接口
- `GET /api/admin/dashboard` - 数据概览
- `GET /api/admin/users` - 用户列表
- `PUT /api/admin/users/{id}/status` - 启用/禁用用户
- `GET /api/admin/tools` - 工具列表
- `PUT /api/admin/tools/{id}/audit` - 工具审核
- `PUT /api/admin/tools/{id}/offline` - 强制下架
- `CRUD /api/admin/announcements` - 公告管理

## 工具状态流转

```
发布 → [待审核] → 管理员审核通过 → [可借用] → 用户申请借用 → [借用中] → 归还 → [可借用]
                → 管理员拒绝 → [已拒绝]
                → 管理员下架 → [已下架]
```

## 借用状态流转

```
[已申请] → 工具主人同意 → [已同意] → 借用人确认取货 → [使用中] → 确认归还 → [已归还] → 可评价
         → 工具主人拒绝 → [已拒绝]
```
