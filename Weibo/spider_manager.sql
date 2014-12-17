-- MySQL dump 10.13  Distrib 5.6.12, for Linux (x86_64)
--
-- Host: localhost    Database: spider_manager
-- ------------------------------------------------------
-- Server version	5.6.12

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `spider_accounts`
--

DROP TABLE IF EXISTS `spider_accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `spider_accounts` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `username` char(50) NOT NULL,
  `password` char(50) NOT NULL,
  `lastlogin` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `type` char(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `spider_accounts`
--

LOCK TABLES `spider_accounts` WRITE;
/*!40000 ALTER TABLE `spider_accounts` DISABLE KEYS */;
INSERT INTO `spider_accounts` VALUES (1,'18092794430','lr1992','2014-12-17 12:21:12','weibo'),(2,'13289212979','Rlx0825leehom','2014-12-17 12:22:57','weibo'),(3,'421186071rlx@sina.com','Rlx0825leehom','2014-12-17 12:27:01','weibo'),(4,'rlx421186071@sina.com','Rlx0825leehom','2014-12-17 12:27:49','weibo'),(5,'421186071rlx@sina.cn','Rlx0825leehom','2014-12-17 12:40:47','weibo'),(6,'rrenlixiang@163.com','Rlx0825leehom','2014-12-17 12:42:28','weibo'),(7,'rrenlixiang@sina.com','Rlx0825leehom','2014-12-17 09:53:14','weibo'),(8,'rlx421186071@163.com','Rlx0825leehom','2014-12-17 09:54:24','weibo'),(9,'OurHom.759@gmail.com','Rlx0825leehom','2014-12-17 09:55:38','weibo'),(10,'glacierlx@sina.cn','Rlx0825leehom','2014-12-17 09:56:33','weibo'),(12,'glacier@xiyoulinux.org','Rlx0825leehom','2014-12-17 09:57:21','weibo'),(13,'rlx421186071@sina.cn','Rlx0825leehom','2014-12-17 10:40:49','weibo'),(14,'glacier421186071@163.com','Rlx0825leehom','2014-12-17 10:50:01','weibo'),(15,'glacier421186071@sina.com','Rlx0825leehom','2014-12-17 10:54:48','weibo'),(16,'rrenlixiang@sina.cn','Rlx0825leehom','2014-12-17 10:55:38','weibo'),(17,'rleehom@sina.cn','Rlx0825leehom','2014-12-17 11:42:29','weibo'),(18,'1498781360@qq.com','leirisheng','2014-12-17 11:44:18','weibo'),(20,'2726492626@qq.com','kaishi','2014-12-17 11:45:47','weibo'),(21,'1825682313@qq.com','kaishi','2014-12-17 11:47:28','weibo'),(22,'xy_lr_92@126.com','lr1992','2014-12-17 11:51:00','weibo'),(23,'xylr92@126.com','lr1992','2014-12-17 12:19:46','weibo');
/*!40000 ALTER TABLE `spider_accounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `spider_app`
--

DROP TABLE IF EXISTS `spider_app`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `spider_app` (
  `aid` int(5) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  `remark` text,
  PRIMARY KEY (`aid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `spider_app`
--

LOCK TABLES `spider_app` WRITE;
/*!40000 ALTER TABLE `spider_app` DISABLE KEYS */;
/*!40000 ALTER TABLE `spider_app` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `spider_config`
--

DROP TABLE IF EXISTS `spider_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `spider_config` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `uid` char(20) DEFAULT NULL,
  `aid` int(5) unsigned NOT NULL,
  `conf` text,
  `submit_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `active` int(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `spider_config`
--

LOCK TABLES `spider_config` WRITE;
/*!40000 ALTER TABLE `spider_config` DISABLE KEYS */;
/*!40000 ALTER TABLE `spider_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `spider_user`
--

DROP TABLE IF EXISTS `spider_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `spider_user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `uid` char(20) DEFAULT NULL,
  `screen_name` varchar(20) DEFAULT NULL,
  `name` varchar(20) DEFAULT NULL,
  `location` varchar(20) DEFAULT NULL,
  `description` varchar(50) DEFAULT NULL,
  `url` varchar(50) DEFAULT NULL,
  `profileImageUrl` varchar(50) DEFAULT NULL,
  `followersCount` varchar(15) DEFAULT NULL,
  `friendsCount` varchar(15) DEFAULT NULL,
  `statusesCount` varchar(15) DEFAULT NULL,
  `favourites` varchar(15) DEFAULT NULL,
  `createdAt` varchar(30) DEFAULT NULL,
  `gender` char(2) DEFAULT NULL,
  `biFollowersCount` varchar(15) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `avatar_large` varchar(30) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `spider_user`
--

LOCK TABLES `spider_user` WRITE;
/*!40000 ALTER TABLE `spider_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `spider_user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-12-17 20:43:02
