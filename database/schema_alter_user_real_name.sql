-- 已有库升级：用户与管理员展示姓名（登录用户名不变）
-- 若列已存在会报错，可忽略或注释对应 ALTER 后只执行 UPDATE。
USE campus_trade;

ALTER TABLE tb_user ADD COLUMN real_name VARCHAR(50) NOT NULL DEFAULT '' COMMENT '展示姓名（中文）' AFTER username;
ALTER TABLE tb_admin ADD COLUMN real_name VARCHAR(50) NOT NULL DEFAULT '' COMMENT '展示姓名' AFTER username;

UPDATE tb_user SET real_name = '张三' WHERE username = 'zhangsan';
UPDATE tb_user SET real_name = '李四' WHERE username = 'lisi';
UPDATE tb_user SET real_name = '王五' WHERE username = 'wangwu';
UPDATE tb_user SET real_name = '赵六' WHERE username = 'zhaoliu';
UPDATE tb_user SET real_name = '孙七' WHERE username = 'sunqi';
UPDATE tb_user SET real_name = '测试用户一' WHERE username = 'user1';

UPDATE tb_admin SET real_name = '系统管理员' WHERE username = 'admin';
UPDATE tb_admin SET real_name = '审核员乙' WHERE username = 'auditor02';
