-- 步骤 1: 在 t_role 表中添加“商家”角色
-- 我们设定商家角色的ID为3
INSERT INTO `t_role` (id, name) VALUES (3, '商家');

-- 步骤 2: 在 goods 表中增加一个 merchant_id 字段
-- 这个字段将用于记录商品属于哪个用户（商家）
ALTER TABLE `goods` ADD COLUMN `merchant_id` BIGINT NULL COMMENT '商家用户ID' AFTER `recommend`;

-- 步骤 3 (可选，但推荐): 创建一个测试商家账号
-- 我们创建一个名为 merchant1 的用户，并直接将其 role_id 设置为 3 (商家角色)
INSERT INTO `t_user` (username, password, t_user.nick_name, role)
VALUES ('merchant1', 'e10adc3949ba59abbe56e057f20f883e', '我是商家一号', 3);

-- 步骤 4 (可选，但推荐): 把现有商品都归属于管理员
-- 这样可以避免老数据没有商家ID的问题。我们假设管理员用户的ID是1。
UPDATE `goods` SET `merchant_id` = 1 WHERE `merchant_id` IS NULL;

ALTER TABLE `goods`
    ADD CONSTRAINT `fk_goods_t_user`
        FOREIGN KEY (`merchant_id`)
            REFERENCES `t_user` (`id`)
            ON DELETE SET NULL
            ON UPDATE CASCADE;