USE campus_trade;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE tb_favorite;
TRUNCATE TABLE tb_cart;
TRUNCATE TABLE tb_order;
TRUNCATE TABLE tb_product;
TRUNCATE TABLE tb_category;
TRUNCATE TABLE tb_user;
TRUNCATE TABLE tb_admin;

-- 演示密码：123456（学生） / admin123（管理员）；使用 bcrypt Python 模块生成 spring-security 校验通过
INSERT INTO tb_user (id, username, real_name, password, student_id, phone, role, status, create_time) VALUES
 (1, 'zhangsan','张三','$2b$10$grE9Bvj/EyGga2xKRFq0f.CuMJlomoRpIZlr86SdkWFoWy2TcE93e','20210001','13800001001','USER',0,NOW()),
 (2, 'lisi',    '李四','$2b$10$grE9Bvj/EyGga2xKRFq0f.CuMJlomoRpIZlr86SdkWFoWy2TcE93e','20210002','13800001002','USER',0,NOW()),
 (3, 'wangwu',  '王五','$2b$10$grE9Bvj/EyGga2xKRFq0f.CuMJlomoRpIZlr86SdkWFoWy2TcE93e','20210003','13800001003','USER',0,NOW()),
 (4, 'zhaoliu', '赵六','$2b$10$grE9Bvj/EyGga2xKRFq0f.CuMJlomoRpIZlr86SdkWFoWy2TcE93e','20210004','13800001004','USER',0,NOW()),
 (5, 'sunqi',   '孙七','$2b$10$grE9Bvj/EyGga2xKRFq0f.CuMJlomoRpIZlr86SdkWFoWy2TcE93e','20210005','13800001005','USER',0,NOW()),
 (6, 'user1',   '测试用户一','$2b$12$ZH4NjVGr0BB1PBJbHHaPR.KTGi4zayi1wVWmyUIgflFl5A.dgifw6','20210100','13900000001','USER',0,NOW());

INSERT INTO tb_admin (id, username, real_name, password, role, create_time) VALUES
 (1, 'admin',    '系统管理员','$2b$10$caia4VEWQV4FBRZR9f3AQuoYr.V95pEAgo7/Nf26RokvUbKqOJA2q','ADMIN',NOW()),
 (2,'auditor02','审核员乙','$2b$10$caia4VEWQV4FBRZR9f3AQuoYr.V95pEAgo7/Nf26RokvUbKqOJA2q','ADMIN',NOW());

INSERT INTO tb_category (id, name) VALUES
 (1, '教材图书'),
 (2, '数码电子'),
 (3, '生活用品'),
 (4, '体育运动'),
 (5, '其他闲置');

INSERT INTO tb_product (id, title, description, price, image_url, user_id, category_id, status,
                        create_time, audit_time, audit_result, reject_reason) VALUES
 (1,'高等数学同济第七版习题解析','上学期考研用，无缺页划线少',18.90,'/demo/item-1.svg',1,1,'ON_SALE',DATE_SUB(NOW(),INTERVAL 6 DAY),'2026-03-02 09:30:00','通过',NULL),
 (2,'大学英语四级全真模拟卷','十套卷仅写两套，附听力二维码',22.50,'/demo/item-1.svg',1,1,'ON_SALE',DATE_SUB(NOW(),INTERVAL 5 DAY),'2026-03-03 09:30:00','通过',NULL),
 (3,'数据结构与算法 C 语言版教材','数据结构课程指定教材，九五新',35.00,'/demo/item-1.svg',2,1,'ON_SALE',DATE_SUB(NOW(),INTERVAL 4 DAY),'2026-03-04 09:30:00','通过',NULL),
 (4,'线性代数讲义手写笔记','复习周整理电子版打印装订',15.00,'/demo/item-1.svg',2,1,'ON_SALE',DATE_SUB(NOW(),INTERVAL 3 DAY),'2026-03-05 09:30:00','通过',NULL),
 (5,'iPad mini 第五代 256G','钢化膜+保护壳，电池健康93%',1680.00,'/demo/item-2.svg',3,2,'ON_SALE',DATE_SUB(NOW(),INTERVAL 10 DAY),'2026-03-01 09:30:00','通过',NULL),
 (6,'罗技静音无线鼠标','实验室闲置，正常使用痕迹',89.00,'/demo/item-2.svg',3,2,'ON_SALE',DATE_SUB(NOW(),INTERVAL 9 DAY),'2026-03-02 10:00:00','通过',NULL),
 (7,'小米移动电源 20000mAh','双口快充，成色不错',119.00,'/demo/item-2.svg',4,2,'ON_SALE',DATE_SUB(NOW(),INTERVAL 8 DAY),'2026-03-03 10:00:00','通过',NULL),
 (8,'机械键盘 青轴白光','码字换键盘出，敲击感强',199.00,'/demo/item-2.svg',4,2,'ON_SALE',DATE_SUB(NOW(),INTERVAL 7 DAY),'2026-03-04 10:00:00','通过',NULL),
 (9,'宿舍加湿器小型','毕业季搬宿舍出闲置',42.50,'/demo/item-3.svg',5,3,'ON_SALE',DATE_SUB(NOW(),INTERVAL 6 DAY),'2026-03-05 10:00:00','通过',NULL),
 (10,'塑料收纳盒三件套','可叠放防潮，轻微使用痕迹',25.80,'/demo/item-3.svg',5,3,'ON_SALE',DATE_SUB(NOW(),INTERVAL 5 DAY),'2026-03-06 10:00:00','通过',NULL),
 (11,'床上书桌折叠款','加厚面板，毕业季便宜出',58.00,'/demo/item-3.svg',1,3,'ON_SALE',DATE_SUB(NOW(),INTERVAL 4 DAY),'2026-03-07 10:00:00','通过',NULL),
 (12,'全身镜立式','搬走不便携，自取优先',76.00,'/demo/item-3.svg',2,3,'ON_SALE',DATE_SUB(NOW(),INTERVAL 3 DAY),'2026-03-08 10:00:00','通过',NULL),
 (13,'尤尼克斯羽毛球拍','入门款单拍，网线略有磨损',148.00,'/demo/item-4.svg',3,4,'ON_SALE',DATE_SUB(NOW(),INTERVAL 11 DAY),'2026-02-28 10:30:00','通过',NULL),
 (14,'斯伯丁篮球七号','室内外通用，打气筒一起送',99.90,'/demo/item-4.svg',3,4,'ON_SALE',DATE_SUB(NOW(),INTERVAL 2 DAY),'2026-03-09 10:30:00','通过',NULL),
 (15,'瑜伽垫加厚款','体育课选修用过几次',58.80,'/demo/item-4.svg',4,4,'ON_SALE',DATE_SUB(NOW(),INTERVAL 1 DAY),'2026-03-10 10:30:00','通过',NULL),
 (16,'吉他民谣入门','和弦图贴在琴包内，自取',399.00,'/demo/item-5.svg',4,5,'ON_SALE',DATE_SUB(NOW(),INTERVAL 12 DAY),'2026-02-27 10:30:00','通过',NULL),
 (17,'收纳小推车置物架','厨房/书桌都能用',45.60,'/demo/item-5.svg',5,5,'ON_SALE',DATE_SUB(NOW(),INTERVAL 2 DAY),'2026-03-09 14:30:00','通过',NULL),
 (18,'全新耳机收纳盒硅胶','买回来发现买多了三个',18.80,'/demo/item-5.svg',1,5,'ON_SALE',DATE_SUB(NOW(),INTERVAL 13 DAY),'2026-03-06 09:45:00','通过',NULL),
 (19,'待审核加湿器一台','发布后等待管理员演示审核',130.00,'/demo/item-6.svg',2,3,'PENDING',DATE_SUB(NOW(),INTERVAL 1 HOUR),NULL,NULL,NULL),
 (20,'待审核CCD相机','毕业季拍摄 Vlog 自用',388.00,'/demo/item-6.svg',3,2,'PENDING',DATE_SUB(NOW(),INTERVAL 2 HOUR),NULL,NULL,NULL),
 (21,'已拒绝商品示例——违规文案','内含违规占位描述（演示驳回）',1.00,'/demo/item-6.svg',4,5,'REJECTED',DATE_SUB(NOW(),INTERVAL 8 HOUR),'2026-03-10 08:30:00','拒绝','上架描述包含违规占位词'),
 (22,'管理员已下架保温杯','保温杯轻微掉漆仍可保温',62.30,'/demo/item-6.svg',5,3,'OFF_SALE',DATE_SUB(NOW(),INTERVAL 3 DAY),'2026-03-08 09:45:00','通过',NULL);

INSERT INTO tb_favorite (favorite_id, user_id, product_id, create_time) VALUES
 (1,1,5,NOW()),
 (2,2,13,NOW());

INSERT INTO tb_cart (cart_id,user_id,product_id,quantity,create_time) VALUES
 (1,1,7,1,NOW()),
 (2,1,9,1,NOW()),
 (3,2,14,1,NOW());

SET FOREIGN_KEY_CHECKS = 1;

ALTER TABLE tb_user AUTO_INCREMENT = 100;
ALTER TABLE tb_admin AUTO_INCREMENT = 100;
ALTER TABLE tb_product AUTO_INCREMENT = 300;
ALTER TABLE tb_order AUTO_INCREMENT = 300;
ALTER TABLE tb_cart AUTO_INCREMENT = 300;
ALTER TABLE tb_favorite AUTO_INCREMENT = 300;
