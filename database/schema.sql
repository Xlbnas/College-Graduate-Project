-- 校园二手交易平台 —— 依据论文 4.2.3 表结构（MySQL 8.0）
CREATE DATABASE IF NOT EXISTS campus_trade DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campus_trade;

-- 用户信息表（表 4-2）+ 论文登录所需 status 字段
DROP TABLE IF EXISTS tb_favorite;
DROP TABLE IF EXISTS tb_cart;
DROP TABLE IF EXISTS tb_order;
DROP TABLE IF EXISTS tb_product;
DROP TABLE IF EXISTS tb_category;
DROP TABLE IF EXISTS tb_user;
DROP TABLE IF EXISTS tb_admin;

CREATE TABLE tb_user (
    id          INT AUTO_INCREMENT PRIMARY KEY COMMENT '用户 ID',
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '登录用户名',
    real_name   VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '展示姓名（中文）',
    password    VARCHAR(100) NOT NULL COMMENT '登录密码',
    student_id  VARCHAR(20)  NOT NULL COMMENT '学号',
    phone       VARCHAR(20)  NOT NULL COMMENT '联系电话',
    role        VARCHAR(10)  NOT NULL DEFAULT 'USER' COMMENT '角色 USER',
    status      TINYINT      NOT NULL DEFAULT 0 COMMENT '0正常 1禁用',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    UNIQUE KEY uk_student (student_id),
    UNIQUE KEY uk_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 管理员信息表（表 4-6）
CREATE TABLE tb_admin (
    id          INT AUTO_INCREMENT PRIMARY KEY COMMENT '管理员 ID',
    username    VARCHAR(50)  NOT NULL UNIQUE,
    real_name   VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '展示姓名',
    password    VARCHAR(100) NOT NULL,
    role        VARCHAR(10)  NOT NULL DEFAULT 'ADMIN',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 商品分类表（表 4-5）
CREATE TABLE tb_category (
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 商品信息表（表 4-3 + 审核字段）
CREATE TABLE tb_product (
    id            INT AUTO_INCREMENT PRIMARY KEY COMMENT '商品 ID',
    title         VARCHAR(100) NOT NULL,
    description   TEXT          NULL,
    price         DECIMAL(10, 2) NOT NULL,
    image_url     VARCHAR(512)   NULL COMMENT '封面图（相对路径或 https 外链）',
    user_id       INT            NOT NULL COMMENT '发布者',
    category_id   INT            NOT NULL,
    status        VARCHAR(20)    NOT NULL COMMENT 'PENDING/ON_SALE/REJECTED/OFF_SALE/SOLD_OUT',
    create_time   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    audit_time    DATETIME       NULL,
    audit_result  VARCHAR(50)    NULL,
    reject_reason VARCHAR(255)   NULL,
    INDEX idx_pub_time (category_id, status, create_time),
    CONSTRAINT fk_product_user FOREIGN KEY (user_id) REFERENCES tb_user (id),
    CONSTRAINT fk_product_cat FOREIGN KEY (category_id) REFERENCES tb_category (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 订单信息表（表 4-4 + quantity）
CREATE TABLE tb_order (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no     VARCHAR(50) NOT NULL UNIQUE,
    user_id      INT         NOT NULL COMMENT '买家',
    product_id   INT         NOT NULL,
    quantity     INT         NOT NULL DEFAULT 1,
    total_price  DECIMAL(10,2) NOT NULL,
    status       VARCHAR(20) NOT NULL COMMENT 'WAIT_PAY/PAID/SHIPPED/COMPLETED',
    address      VARCHAR(500) NULL COMMENT '收货地址（结算表单）',
    contact_phone VARCHAR(32) NULL COMMENT '联系电话',
    remark       VARCHAR(500) NULL COMMENT '备注',
    pay_method   VARCHAR(32) NULL COMMENT '支付方式',
    review_content VARCHAR(500) NULL COMMENT '买家评价',
    review_time  DATETIME NULL COMMENT '评价时间',
    create_time  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_buyer (user_id, status),
    CONSTRAINT fk_ord_user FOREIGN KEY (user_id) REFERENCES tb_user (id),
    CONSTRAINT fk_ord_prod FOREIGN KEY (product_id) REFERENCES tb_product (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 购物车信息表（表 4-7）
CREATE TABLE tb_cart (
    cart_id     INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT NOT NULL,
    product_id  INT NOT NULL,
    quantity    INT NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_prod (user_id, product_id),
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES tb_user (id),
    CONSTRAINT fk_cart_prod FOREIGN KEY (product_id) REFERENCES tb_product (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 收藏信息表（表 4-8）
CREATE TABLE tb_favorite (
    favorite_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT NOT NULL,
    product_id  INT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_fav (user_id, product_id),
    CONSTRAINT fk_fav_user FOREIGN KEY (user_id) REFERENCES tb_user (id),
    CONSTRAINT fk_fav_prod FOREIGN KEY (product_id) REFERENCES tb_product (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
