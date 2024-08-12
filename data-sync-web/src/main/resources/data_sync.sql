/*
SQLyog Community v13.2.0 (64 bit)
MySQL - 8.1.0 : Database - data_sync
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`data_sync` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `data_sync`;

/*Table structure for table `jdbc_info` */

DROP TABLE IF EXISTS `jdbc_info`;

CREATE TABLE `jdbc_info` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `jdbc_name` varchar(64) NOT NULL COMMENT '数据源名称',
  `jdbc_url` varchar(300) NOT NULL,
  `jdbc_username` varchar(300) NOT NULL,
  `jdbc_password` varchar(300) NOT NULL,
  `created_time` datetime NOT NULL,
  `updated_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `jdbc_info` */

insert  into `jdbc_info`(`id`,`jdbc_name`,`jdbc_url`,`jdbc_username`,`jdbc_password`,`created_time`,`updated_time`) values 
(1,'base_order','jdbc:mysql://localhost:3306/base_order','root','12345678','2024-07-15 18:35:11',
'2024-07-15 18:35:14');

/*Table structure for table `schema_sync_table` */

DROP TABLE IF EXISTS `schema_sync_table`;

CREATE TABLE `schema_sync_table` (
  `id` int NOT NULL AUTO_INCREMENT,
  `scheme_id` int NOT NULL,
  `datasource_id` int NOT NULL,
  `table_name` varchar(255) NOT NULL,
  `table_alia_name` varchar(255) NOT NULL,
  `unique_field` varchar(255) NOT NULL,
  `pk_field` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `parent_id` int NOT NULL,
  `table_relation` int NOT NULL,
  `parent_column_names` varchar(255) NOT NULL,
  `current_column_names` varchar(255) NOT NULL,
  `table_suffix` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `created_time` datetime NOT NULL,
  `updated_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `schema_sync_table` */

insert  into `schema_sync_table`(`id`,`scheme_id`,`datasource_id`,`table_name`,`table_alia_name`,`unique_field`,`pk_field`,`parent_id`,`table_relation`,`parent_column_names`,`current_column_names`,`table_suffix`,`created_time`,`updated_time`) values 
(1,1,1,'order_info_','order_info','order_sn','id',0,1,'order_sn','order_sn','0,1,2,3','2024-07-17 19:26:24','2024-07-17 19:26:30'),
(2,1,1,'order_info_extend_','order_info_extend','order_sn','id',1,2,'order_sn','order_sn','0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15','2024-07-17 19:34:42','2024-07-17 19:34:45'),
(3,1,1,'order_goods_','order_goods','order_sn','id',1,3,'order_sn','order_sn','0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15','2024-07-17 19:42:01','2024-07-17 19:42:04');

/*Table structure for table `search_schema` */

DROP TABLE IF EXISTS `search_schema`;

CREATE TABLE `search_schema` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `s_name` varchar(64) NOT NULL COMMENT '方案名称',
  `s_code` varchar(64) NOT NULL COMMENT 'ES索引',
  `s_alias` varchar(64) NOT NULL COMMENT 'ES索引别名',
  `s_status` int NOT NULL COMMENT '状态，开启0禁用1，默认开启',
  `note` varchar(200) DEFAULT NULL COMMENT '备注信息',
  `shard_route_flag` int NOT NULL COMMENT '路由类型',
  `created_time` datetime NOT NULL,
  `updated_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `search_schema` */

insert  into `search_schema`(`id`,`s_name`,`s_code`,`s_alias`,`s_status`,`note`,`shard_route_flag`,`created_time`,`updated_time`) values 
(1,'订单索引','s_order','s_order_alias',0,'分库分表订单',1,'2024-07-15 18:44:47','2024-07-15 18:44:51');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
