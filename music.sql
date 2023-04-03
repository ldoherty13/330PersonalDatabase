-- MySQL dump 10.13  Distrib 8.0.20, for macos10.15 (x86_64)
--
-- Host: localhost    Database: music
-- ------------------------------------------------------
-- Server version	8.0.20

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

DROP DATABASE IF EXISTS music;
CREATE DATABASE music;
USE music;

--
-- Table structure for table artists
--

DROP TABLE IF EXISTS artists;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE artists (
  artistId int NOT NULL,
  artistName varchar(50) DEFAULT NULL,
  PRIMARY KEY (artistId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table artists
--

LOCK TABLES artists WRITE;
/*!40000 ALTER TABLE artists DISABLE KEYS */;
INSERT INTO artists VALUES (1,'Adele'),(2,'Ed Sheeran'),(3,'Justin Bieber'),(4,'Post Malone'),(5,'Walker Hayes'),(6,'Silk Sonic');
/*!40000 ALTER TABLE artists ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table audit
--

DROP TABLE IF EXISTS audit;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE audit (
  auditId int NOT NULL,
  changedBy int DEFAULT NULL,
  changedOn varchar(45) DEFAULT NULL,
  tname varchar(45) DEFAULT NULL,
  pkName varchar(45) DEFAULT NULL,
  pkValue varchar(45) DEFAULT NULL,
  fldName varchar(45) DEFAULT NULL,
  oldValue varchar(255) DEFAULT NULL,
  newValue varchar(255) DEFAULT NULL,
  reason varchar(255) DEFAULT NULL,
  PRIMARY KEY (auditId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table audit
--

LOCK TABLES audit WRITE;
/*!40000 ALTER TABLE audit DISABLE KEYS */;
/*!40000 ALTER TABLE audit ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table songs
--

DROP TABLE IF EXISTS songs;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE songs (
  songId int NOT NULL,
  title varchar(100) DEFAULT NULL,
  artistId int DEFAULT NULL,
  year char(4) DEFAULT NULL,
  PRIMARY KEY (songId),
  KEY artistFK (artistId),
  CONSTRAINT artistFK FOREIGN KEY (artistId) REFERENCES artists (artistId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table songs
--

LOCK TABLES songs WRITE;
/*!40000 ALTER TABLE songs DISABLE KEYS */;
INSERT INTO songs VALUES (1,'Easy on Me',1,'2021'),(2,'Stay',3,'2021'),(3,'Bad Habits',2,'2021'),(4,'Shivers',2,'2021'),(5,'One Right Now',4,'2021'),(6,'Fancy Like',5,'2021'),(7,'Smoke Sonic',6,'2021');
/*!40000 ALTER TABLE songs ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-11-22  9:22:31
