/*
 Navicat Premium Data Transfer

 Source Server         : Mysql_local
 Source Server Type    : MySQL
 Source Server Version : 80019
 Source Host           : localhost:3306
 Source Schema         : anecdote

 Target Server Type    : MySQL
 Target Server Version : 80019
 File Encoding         : 65001

 Date: 20/05/2021 22:13:44
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for anecdote
-- ----------------------------
DROP TABLE IF EXISTS `anecdote`;
CREATE TABLE `anecdote`  (
  `anec_id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '轶事id',
  `anec_person` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '轶事主人公',
  `anec_title` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '轶事标题',
  `anec_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '轶事内容',
  `anec_createtime` datetime(0) NOT NULL COMMENT '轶事创建时间',
  `anec_createid` int UNSIGNED NOT NULL COMMENT '轶事创建者id',
  `anec_imgpath` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '轶事图片路径（只存文件名）',
  PRIMARY KEY (`anec_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `user_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `user_pwd` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户密码',
  `user_power` tinyint UNSIGNED NOT NULL DEFAULT 2 COMMENT '用户权限，1管理员，2普通用户',
  `user_email` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户邮箱',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `uk_user_name`(`user_name`) USING BTREE COMMENT '唯一索引_用户名',
  UNIQUE INDEX `uk_user_email`(`user_email`) USING BTREE COMMENT '唯一索引_用户邮箱'
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
