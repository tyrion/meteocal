-- MySQL dump 10.13  Distrib 5.5.40, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: meteocal
-- ------------------------------------------------------
-- Server version	5.5.40-0ubuntu0.14.04.1

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
-- Table structure for table `calendars`
--

DROP TABLE IF EXISTS `calendars`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `calendars` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `owner` int(11) NOT NULL,
  `public` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `owner_UNIQUE` (`owner`),
  KEY `fk_calendars_users1_idx` (`owner`),
  CONSTRAINT `calendar_ownership` FOREIGN KEY (`owner`) REFERENCES `users` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `calendars`
--

LOCK TABLES `calendars` WRITE;
/*!40000 ALTER TABLE `calendars` DISABLE KEYS */;
/*!40000 ALTER TABLE `calendars` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cities`
--

DROP TABLE IF EXISTS `cities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cities` (
  `id` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `country` varchar(45) NOT NULL,
  `lat` double NOT NULL,
  `lon` double NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cities`
--

LOCK TABLES `cities` WRITE;
/*!40000 ALTER TABLE `cities` DISABLE KEYS */;
/*!40000 ALTER TABLE `cities` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `events`
--

DROP TABLE IF EXISTS `events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `events` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `description` varchar(255) NOT NULL,
  `start` datetime NOT NULL,
  `end` datetime NOT NULL,
  `location` varchar(255) NOT NULL,
  `public` tinyint(1) NOT NULL,
  `outdoor` tinyint(1) NOT NULL,
  `creator` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_events_users1_idx` (`creator`),
  CONSTRAINT `event_ownership` FOREIGN KEY (`creator`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `events`
--

LOCK TABLES `events` WRITE;
/*!40000 ALTER TABLE `events` DISABLE KEYS */;
/*!40000 ALTER TABLE `events` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `events_forecasts`
--

DROP TABLE IF EXISTS `events_forecasts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `events_forecasts` (
  `events_id` int(11) NOT NULL,
  `forecasts_city` int(11) NOT NULL,
  `forecasts_dt` datetime NOT NULL,
  PRIMARY KEY (`events_id`,`forecasts_city`,`forecasts_dt`),
  KEY `fk_events_has_forecasts_forecasts1_idx` (`forecasts_city`,`forecasts_dt`),
  KEY `fk_events_has_forecasts_events1_idx` (`events_id`),
  CONSTRAINT `fk_events_has_forecasts_events1` FOREIGN KEY (`events_id`) REFERENCES `events` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_events_has_forecasts_forecasts1` FOREIGN KEY (`forecasts_city`, `forecasts_dt`) REFERENCES `forecasts` (`city`, `dt`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `events_forecasts`
--

LOCK TABLES `events_forecasts` WRITE;
/*!40000 ALTER TABLE `events_forecasts` DISABLE KEYS */;
/*!40000 ALTER TABLE `events_forecasts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `forecasts`
--

DROP TABLE IF EXISTS `forecasts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `forecasts` (
  `dt` datetime NOT NULL,
  `city` int(11) NOT NULL,
  `temp_min` double NOT NULL,
  `temp_max` double NOT NULL,
  `pressure` double NOT NULL,
  `humidity` double NOT NULL,
  `weather_condition` int(11) NOT NULL,
  PRIMARY KEY (`dt`,`city`),
  KEY `fk_forecasts_weather_conditions1_idx` (`weather_condition`),
  KEY `fk_forecasts_cities1_idx` (`city`),
  CONSTRAINT `city_forecasts` FOREIGN KEY (`city`) REFERENCES `cities` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `forecast_conditions` FOREIGN KEY (`weather_condition`) REFERENCES `weather_conditions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forecasts`
--

LOCK TABLES `forecasts` WRITE;
/*!40000 ALTER TABLE `forecasts` DISABLE KEYS */;
/*!40000 ALTER TABLE `forecasts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notifications` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `events_id` int(11) NOT NULL,
  `users_id` int(11) NOT NULL,
  `notifications_type` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_notifications_events1_idx` (`events_id`),
  KEY `fk_notifications_users1_idx` (`users_id`),
  KEY `fk_notifications_notifications_types1_idx` (`notifications_type`),
  CONSTRAINT `event_notifications` FOREIGN KEY (`events_id`) REFERENCES `events` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `notification_type` FOREIGN KEY (`notifications_type`) REFERENCES `notifications_types` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_notifications` FOREIGN KEY (`users_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications_types`
--

DROP TABLE IF EXISTS `notifications_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notifications_types` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications_types`
--

LOCK TABLES `notifications_types` WRITE;
/*!40000 ALTER TABLE `notifications_types` DISABLE KEYS */;
INSERT INTO `notifications_types` VALUES (1,'Event Creation','You\'ve been invited to the event %s by %s. '),(2,'Bad Weather - 3 Days','For your event %s the forecast reports bad weather. We found the first available sunny day to be on %s. Edit your event! '),(3,'Bad Weather - 3 Days - No sunny days','For your event %s the forecast reports bad weather. Unfortunately there isn\'t any sunny day for the next days. '),(4,'Bad Weather - Tomorrow!','There\'s bad weather for your tomorrow event %s! '),(5,'Forecast change','The forecast for the event you\'re participating %s is changed! ');
/*!40000 ALTER TABLE `notifications_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `participations`
--

DROP TABLE IF EXISTS `participations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `participations` (
  `event` int(11) NOT NULL,
  `calendars_id` int(11) NOT NULL,
  `accepted` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`event`,`calendars_id`),
  KEY `fk_events_has_users_events1_idx` (`event`),
  KEY `fk_event_participations_calendars1_idx` (`calendars_id`),
  CONSTRAINT `calendars_participations` FOREIGN KEY (`calendars_id`) REFERENCES `calendars` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `events_participations` FOREIGN KEY (`event`) REFERENCES `events` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `participations`
--

LOCK TABLES `participations` WRITE;
/*!40000 ALTER TABLE `participations` DISABLE KEYS */;
/*!40000 ALTER TABLE `participations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `given_name` varchar(45) NOT NULL,
  `family_name` varchar(45) NOT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'valeriafolliero@mailinator.com','BhVJ2762/2/AZbJXp3QUiCh/wliQLaOa1nHFUAlOSGo=$oqqZXivtIbNHo0wefufkuyvHSjo0lE20nDJyljSIbcI=','Valeria','Folliero',1),(2,'jolandabaresi@mailinator.com','hvdq5daDYBQLVN95T1DuzKiPiULTfUO4ZFV31w0jgq0=$2TXALQcspDYiVG6pmIJRFGfB/xjyZjOmwkkeNUwrd00=','Jolanda','Baresi',1),(3,'olivierozito@mailinator.com','rD0U6d5i76LsYeR3hxY5sB0C8dK2KPCd2UMbO/Mj+cI=$WlQmy1EUilMrlGdPY9GRPIfGDcGjaqW6GcwyLugQZ8g=','Oliviero','Zito',1),(4,'corneliomanfredin@mailinator.com','/El08QLuvkIwVMvY1WDGpKElrgSDcENPuC6GCuTKulg=$9gkMW0KoWw7MdNzsOpmD601asDzXUNb6MufQ0yyiEIU=','Cornelio','Manfredin',1),(5,'caiolombardi@mailinator.com','ifXxTJiuMqstRuclvYHOzxdDd8vlOauYOhHbq9QLmfg=$chex1KeGn7pA+rQFA5zdrGeGu2vB64M1sxknu5vMWX0=','Caio','Lombardi',1);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `weather_conditions`
--

DROP TABLE IF EXISTS `weather_conditions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `weather_conditions` (
  `id` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `description` varchar(255) NOT NULL,
  `icon` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `weather_conditions`
--

LOCK TABLES `weather_conditions` WRITE;
/*!40000 ALTER TABLE `weather_conditions` DISABLE KEYS */;
INSERT INTO `weather_conditions` VALUES (200,'Thunderstorm','thunderstorm with light rain','11d'),(201,'Thunderstorm','thunderstorm with rain','11d'),(202,'Thunderstorm','thunderstorm with heavy rain','11d'),(210,'Thunderstorm','light thunderstorm','11d'),(211,'Thunderstorm','thunderstorm','11d'),(212,'Thunderstorm','heavy thunderstorm','11d'),(221,'Thunderstorm','ragged thunderstorm','11d'),(230,'Thunderstorm','thunderstorm with light drizzle','11d'),(231,'Thunderstorm','thunderstorm with drizzle','11d'),(232,'Thunderstorm','thunderstorm with heavy drizzle','11d'),(300,'Drizzle','light intensity drizzle','09d'),(301,'Drizzle','drizzle','09d'),(302,'Drizzle','heavy intensity drizzle','09d'),(310,'Drizzle','light intensity drizzle rain','09d'),(311,'Drizzle','drizzle rain','09d'),(312,'Drizzle','heavy intensity drizzle rain','09d'),(313,'Drizzle','shower rain and drizzle','09d'),(314,'Drizzle','heavy shower rain and drizzle','09d'),(321,'Drizzle','shower drizzle','09d'),(500,'Rain','light rain','10d'),(501,'Rain','moderate rain','10d'),(502,'Rain','heavy intensity rain','10d'),(503,'Rain','very heavy rain','10d'),(504,'Rain','extreme rain','10d'),(511,'Rain','freezing rain','13d'),(520,'Rain','light intensity shower rain','09d'),(521,'Rain','shower rain','09d'),(522,'Rain','heavy intensity shower rain','09d'),(531,'Rain','ragged shower rain','09d'),(600,'Snow','light snow','13d'),(601,'Snow','snow','13d'),(602,'Snow','heavy snow','13d'),(611,'Snow','sleet','13d'),(612,'Snow','shower sleet','13d'),(615,'Snow','light rain and snow','13d'),(616,'Snow','rain and snow','13d'),(620,'Snow','light shower snow','13d'),(621,'Snow','shower snow','13d'),(622,'Snow','heavy shower snow','13d'),(701,'Atmosphere','mist','50d'),(711,'Atmosphere','smoke','50d'),(721,'Atmosphere','haze','50d'),(731,'Atmosphere','sand, dust whirls','50d'),(741,'Atmosphere','fog','50d'),(751,'Atmosphere','sand','50d'),(761,'Atmosphere','dust','50d'),(762,'Atmosphere','volcanic ash','50d'),(771,'Atmosphere','squalls','50d'),(781,'Atmosphere','tornado','50d'),(800,'Clouds','clear sky','01d'),(801,'Clouds','few clouds','02d'),(802,'Clouds','scattered clouds','03d'),(803,'Clouds','broken clouds','04d'),(804,'Clouds','overcast clouds','04d'),(900,'Extreme','tornado','NULL'),(901,'Extreme','tropical storm','NULL'),(902,'Extreme','hurricane','NULL'),(903,'Extreme','cold','NULL'),(904,'Extreme','hot','NULL'),(905,'Extreme','windy','NULL'),(906,'Extreme','hail','NULL'),(951,'Additional','calm','NULL'),(952,'Additional','light breeze','NULL'),(953,'Additional','gentle breeze','NULL'),(954,'Additional','moderate breeze','NULL'),(955,'Additional','fresh breeze','NULL'),(956,'Additional','strong breeze','NULL'),(957,'Additional','high wind, near gale','NULL'),(958,'Additional','gale','NULL'),(959,'Additional','severe gale','NULL'),(960,'Additional','storm','NULL'),(961,'Additional','violent storm','NULL'),(962,'Additional','hurricane','NULL');
/*!40000 ALTER TABLE `weather_conditions` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-02-25 23:53:14
