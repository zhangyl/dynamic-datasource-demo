-- 创建多个数据库
CREATE DATABASE multi_default;
CREATE DATABASE multi_1;
CREATE DATABASE multi_2;

-- 在上述库中逐一建立cost表
CREATE TABLE `cost` (
	`id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
	`money` INT NULL DEFAULT NULL,
	`ent_code` VARCHAR(20) NOT NULL DEFAULT '' COMMENT '企业编码',
	PRIMARY KEY (`id`) USING BTREE
)
ENGINE=InnoDB AUTO_INCREMENT=1;


-- 以下做一张单表到默认库并插入两条数据试试程序做路由
CREATE TABLE multi_default.`other_datasource` (
	`id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
	`datasource_name` VARCHAR(64) NOT NULL,
	`database_ip` VARCHAR(64) NOT NULL,
	`database_name` VARCHAR(256) NOT NULL,
	`database_port` VARCHAR(64) NOT NULL,
	`database_username` VARCHAR(64) NOT NULL,
	`database_password` VARCHAR(64) NOT NULL,
	`database_url_extra_param` VARCHAR(512) NOT NULL,
	PRIMARY KEY (`id`) USING BTREE
)
ENGINE=InnoDB AUTO_INCREMENT=1;

INSERT INTO multi_default.`other_datasource` (`id`, `datasource_name`, `database_ip`, `database_name`, `database_port`, `database_username`, `database_password`, `database_url_extra_param`) VALUES
	(1, 'multi_1', '127.0.0.1', 'multi_1', '3306', 'root', '123456', 'useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8&useSSL=false'),
	(2, 'multi_2', '127.0.0.1', 'multi_2', '3306', 'root', '123456', 'useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8&useSSL=false');
	
	

	
