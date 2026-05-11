# 校园二手交易平台（Spring Boot 2.7 + Vue2 + Element UI）

与论文《基于 SpringBoot 的校园二手交易平台系统的设计与实现》配套的可运行工程：后端 **JWT + BCrypt + MyBatis-Plus QueryWrapper 分页**，前端 **Vue 2.6 + Element UI**，MySQL **表结构对齐论文第 4.2.3 节**，并补齐审核、购物车、模拟支付发货收货等闭环，便于毕业答辩 **5 分钟演示**。

## 演示账号（开箱即用）

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 学生 | `zhangsan` | `123456` | 亦可使用 `lisi`、`wangwu`、`zhaoliu`、`sunqi` |
| 管理员 | `admin` | `admin123` | `auditor02` / `admin123` |

初始化 SQL 中包含 **≥20 条商品**（多状态：在售、待审核、已拒绝、已下架）；并增加截图中的 **`user1` / `user123`** 测试账号。

轮播横幅图已从你提供的答辩截图拷贝到前端 `frontend/public/ui/banner-*.png`。若你已建过旧库，请执行 `database/schema_alter_order_meta.sql` 为订单表增加「收货地址、电话、支付方式」等字段以匹配新版结算表单。

## 环境要求

- JDK **17**（推荐与本项目 `pom.xml` 一致）、Maven 3.6+
- Node.js 16+（建议）与 npm / yarn
- MySQL **8.0**

> **编译提示**：`backend/pom.xml` 已为 **Lombok** 配置 `annotationProcessorPaths`（避免仅出现「找不到符号 getXxx」），并将 Lombok 固定为 **1.18.42+**（解决 JDK 24/25 下的 `TypeTag :: UNKNOWN`）。若你本机只装了 JDK **17**，同样可正常编译。若你希望强制使用 JDK 17，可：`export JAVA_HOME=$(/usr/libexec/java_home -v 17)` 后再执行 `mvn`。  
> `pom.xml` 已为 MySQL **8.0.33** 驱动显式写明版本（解决部分 Maven 不继承依赖管理而导致的构建失败）。

## 1. 数据库

确保本机 **MySQL 服务已启动**。在项目根目录执行（按提示输入 root 密码；若密码为 `123456` 且接受命令行传参的告警，可如下一行导入）：

```bash
mysql -u root -p < database/schema.sql
mysql -u root -p < database/data.sql
# 或：mysql -u root -p123456 < database/schema.sql && mysql -u root -p123456 < database/data.sql
```

默认数据源配置见 `backend/src/main/resources/application.yml`（当前示例为 `root` / `123456`、库名 `campus_trade`）。若你的账号或密码不同，请改 `username` / `password` / `url`。

## 2. 启动后端（默认端口 8080，上下文 `/api`）

```bash
cd backend
mvn spring-boot:run
```

上传图片保存目录为项目运行目录下的 `upload/`（已加入 `.gitignore`），访问路径：`http://localhost:8080/api/files/文件名`。

## 3. 启动前端（默认 8081，代理转发到后端）

```bash
cd frontend
npm install
npm run serve
```

浏览器打开：`http://localhost:8081/#/`（Hash 路由，免服务器 History 配置）。

生产构建：`npm run build`，将 `frontend/dist` 部署到 Nginx 等静态资源服务器，并将 `/api` 反向代理到 `http://<后端主机>:8080`。

## 4. 推荐演示路径（与论文流程一致）

1. 学生登录 → **发布商品**（上传图片，状态为待审核）  
2. 管理员登录 → **商品审核**（通过/拒绝，拒绝会走论文中的通知占位逻辑）  
3. 学生端 **搜索/分类** 浏览 → **加购/结算** → **我的订单** → **模拟支付**  
4. 卖家在个人中心「**我的售出**」点击 **模拟发货**；买家在「**我的订单**」**确认收货**  
5. 管理端：**商品管理（下架/删除）**、**用户管理（禁用）**、**订单管理**

## 5. 项目结构概要

```
College-Graduate-Project/
├── README.md
├── database/
│   ├── schema.sql      # 建表（对齐论文）
│   └── data.sql       # 示例数据（20+ 商品）
├── backend/           # Spring Boot 2.7.18，包名 com.campus.trade
│   └── src/main/java/com/campus/trade/
│       ├── common/    # JwtUtil（5.2.1）、Result（5.2.2）、UserContext
│       ├── controller/, controller/admin/
│       ├── entity/
│       ├── mapper/
│       ├── service/
│       ├── security/
│       └── ...
└── frontend/          # Vue 2 + Router + Vuex + Element UI + Axios
    └── src/
        ├── layouts/
        ├── views/
        ├── router/
        └── store/
```

## 说明

- **论文插图**：在当前仓库中无法直接从 Word 中还原 6.4 节的界面像素级截图；本项目采用与论文章节名称一致的页面结构（登录、首页/全部商品、发布、购物车、结算、订单、详情）及「校园优品」主题的现代化 UI。如需与截图逐像素对齐，可将论文中的界面截图发给我再微调样式。  
- 若本机 Maven / JDK 路径未配置，`mvn` 不可用请在 IDE（IntelliJ IDEA）中导入 `backend/pom.xml` 运行 `CampusTradeApplication`。
