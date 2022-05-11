CREATE DATABASE  IF NOT EXISTS `galleryImage` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `galleryImage`;
-- MySQL dump 10.13  Distrib 8.0.28, for macos11 (x86_64)
--
-- Host: 127.0.0.1    Database: dbtest
-- ------------------------------------------------------
-- Server version	8.0.28

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
	  `id` int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `username` varchar(255) NOT NULL,
    `password` varchar(255) NOT NULL,
    `email` varchar(255) NOT NULL
)ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `user` WRITE;
INSERT INTO `user` (`id`,`username`,`email`,`password`) VALUES (1,'matteo','matteo@hotmail.it','password'),(2,'lorenzo','lorenzo@gmail.com','password'),(3,'admin','admin@gmail.com','admin');
UNLOCK TABLES;

--
-- Table structure for table `image`
--

DROP TABLE IF EXISTS `image`;

CREATE TABLE `image` (
	  `id` int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `path` varchar(255) NOT NULL,
    `description` varchar(255) NOT NULL,
    `title` varchar(255) NOT NULL,
    `date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `user` int NOT NULL,
    CONSTRAINT `fk_user_image` FOREIGN KEY (`user`) REFERENCES `user` (`id`) ON DELETE CASCADE
)ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `image` WRITE;
INSERT INTO `image` (`id`,`path`,`description`,`title`,`user`) VALUES (1,'breakfast_como.jpg','Colazione con vista sul lago di Como','Colazione',1),(2,'cocktail-como-week.jpg','Hotel vista lago a Tremezzo','Hotel',1),
(3,'como_beach.jpg','Spiaggia situata dietro al tempio voltiano','Spiagga di Como',1),(4,'como_valsassina.jpg','Sentiero panoramico da cui si ammira la Valsassina','Sentiero',1),
(5,'View-of-Lake-Como-from-Tremezzina-Italy-800x800.jpg','Scorcio di lago dal paese di Tremezzo','Vista lago da Tremezzo',1),(6,'Salo-town-center.jpg','Vista del lago di Garda da Salo','Salo',3),(7,'teresio-olivelli-autunno.jpg','Terrazza con vista sul lago di Como','Terrazzino',1),
(8,'passeggiate vista lago iseo.jpg','Passeggiata con vista sul lago iseo','Passeggiata',3);
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;

CREATE TABLE `comment` (
	  `id` int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `text` varchar(255) NOT NULL,
    `date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `image` int NOT NULL,
    `user` int NOT NULL,
    CONSTRAINT `fk_image_comment` FOREIGN KEY (`image`) REFERENCES `image` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_user_comment` FOREIGN KEY (`user`) REFERENCES `user` (`id`) ON DELETE CASCADE
)ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `comment` WRITE;
INSERT INTO `comment` (`id`,`text`,`image`,`user`) VALUES (1,'Colazione fantastica.. e che vista!',1,3),(2,'Che vista fantastica da questo terrazzino!',7,3),(3,'Quanto vorrei poterci passare un weekend!',2,3),(4,'Vista pazzesca!',4,1),(5,'Che bella vista da Tremezzo!',5,1);
UNLOCK TABLES;

--
-- Table structure for table `album`
--

DROP TABLE IF EXISTS `album`;

CREATE TABLE `album` (
	  `id` int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `title` varchar(255) NOT NULL,
    `date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `user` int NOT NULL,
		`sorting` int,
    CONSTRAINT `fk_user_album` FOREIGN KEY (`user`) REFERENCES `user` (`id`) ON DELETE CASCADE
)ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `album` WRITE;
INSERT INTO `album` (`id`,`title`,`user`) VALUES (1,'Lago di Como',1),(2,'Lago di Garda',3),(3,'Lago Iseo',3);
UNLOCK TABLES;

--
-- Table structure for table `albumimages`
--

DROP TABLE IF EXISTS `albumimages`;

CREATE TABLE `albumimages` (
	  `id` int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `image` int NOT NULL,
    `album` int NOT NULL,
		CONSTRAINT `fk_image` FOREIGN KEY (`image`) REFERENCES `image` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_album` FOREIGN KEY (`album`) REFERENCES `album` (`id`) ON DELETE CASCADE
)ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `albumimages` WRITE;
INSERT INTO `albumimages` (`id`,`image`,`album`) VALUES (1,1,1),(2,2,1),(3,3,1),(4,4,1),(5,5,1),(6,6,2),(7,7,1),(8,8,3);
UNLOCK TABLES;

--
-- Dumping routines for database 'galleryImage'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-02-21 15:00:36
