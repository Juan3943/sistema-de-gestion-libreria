CREATE DATABASE  IF NOT EXISTS `libreria_papelitos` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `libreria_papelitos`;
-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: libreria_papelitos
-- ------------------------------------------------------
-- Server version	8.0.43

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
-- Table structure for table `categoria`
--

DROP TABLE IF EXISTS `categoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categoria` (
  `id_categoria` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `activo` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id_categoria`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categoria`
--

LOCK TABLES `categoria` WRITE;
/*!40000 ALTER TABLE `categoria` DISABLE KEYS */;
INSERT INTO `categoria` VALUES (1,'Librería','Artículos de librería en general',1),(2,'Papelería','Papeles, cartulinas y similares',1),(3,'Oficina','Artículos de oficina',1),(4,'Escolar','Útiles escolares',1),(5,'Servicios Impresión','Servicios de impresión y fotocopias',1),(6,'Servicios Encuadernación','Servicios de encuadernación y anillado',1),(7,'Regalería','Artículos de regalo y decoración',1);
/*!40000 ALTER TABLE `categoria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cliente`
--

DROP TABLE IF EXISTS `cliente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cliente` (
  `id_cliente` int NOT NULL AUTO_INCREMENT,
  `tipo_cliente` enum('PERSONA','EMPRESA') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'EMPRESA',
  `nombre` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `cuit` varchar(13) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `condicion_iva` enum('RESPONSABLE_INSCRIPTO','MONOTRIBUTO','EXENTO','CONSUMIDOR_FINAL') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CONSUMIDOR_FINAL',
  `direccion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `telefono` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `activo` tinyint(1) DEFAULT '1',
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_cliente`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cliente`
--

LOCK TABLES `cliente` WRITE;
/*!40000 ALTER TABLE `cliente` DISABLE KEYS */;
INSERT INTO `cliente` VALUES (1,'EMPRESA','Universidad Austral','30123456789','CONSUMIDOR_FINAL','Av. Juan Domingo Perón 1500, Pilar','0230-444-5555','compras@austral.edu.ar',1,'2025-09-03 02:58:49'),(2,'EMPRESA','Escuela Primaria San José','30987654321','CONSUMIDOR_FINAL','Belgrano 234, Pilar','0230-333-4444','administracion@sanjose.edu.ar',1,'2025-09-03 02:58:49'),(3,'EMPRESA','Universidad Austral','30-12345678-9','EXENTO','Av. Juan de Garay 125, Pilar','0230-4489500','compras@austral.edu.ar',1,'2025-10-18 21:49:01'),(4,'EMPRESA','Colegio San Martín','30-87654321-0','RESPONSABLE_INSCRIPTO','Calle Falsa 123, Pilar','0230-4123456','administracion@sanmartin.edu.ar',1,'2025-10-18 21:49:01'),(5,'EMPRESA','Papelería El Árbol SRL','30-11223344-5','RESPONSABLE_INSCRIPTO','Av. Rivadavia 456','','ventas@elarbol.com.ar',0,'2025-10-18 21:49:01'),(6,'PERSONA','Juan Carlos Pérez','20-25678901-3','MONOTRIBUTO','Las Heras 789','011-1234-5678','jcperez@gmail.com',1,'2025-10-18 21:49:01'),(7,'PERSONA','María Fernanda González','27-34567890-2','CONSUMIDOR_FINAL','Belgrano 321','011-9876-5432','mfgonzalez@hotmail.com',1,'2025-10-18 21:49:01'),(8,'EMPRESA','Christian','33-23322224-4','CONSUMIDOR_FINAL','','4242424242424242424','',1,'2025-10-18 21:56:19');
/*!40000 ALTER TABLE `cliente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `compra`
--

DROP TABLE IF EXISTS `compra`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `compra` (
  `id_compra` int NOT NULL AUTO_INCREMENT,
  `numero_compra` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `numero_factura_proveedor` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id_proveedor` int NOT NULL,
  `id_usuario` int NOT NULL,
  `subtotal` decimal(10,2) NOT NULL DEFAULT '0.00',
  `descuentos` decimal(10,2) DEFAULT '0.00',
  `total` decimal(10,2) NOT NULL DEFAULT '0.00',
  `id_metodo_de_pago` int NOT NULL,
  `fecha_compra` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_entrega` datetime DEFAULT NULL,
  `estado` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT 'PENDIENTE',
  `observaciones` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id_compra`),
  UNIQUE KEY `numero_compra` (`numero_compra`),
  KEY `id_proveedor` (`id_proveedor`),
  KEY `id_usuario` (`id_usuario`),
  KEY `id_metodo_de_pago` (`id_metodo_de_pago`),
  CONSTRAINT `compra_ibfk_1` FOREIGN KEY (`id_proveedor`) REFERENCES `proveedor` (`id_proveedor`),
  CONSTRAINT `compra_ibfk_2` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `compra_ibfk_3` FOREIGN KEY (`id_metodo_de_pago`) REFERENCES `metodo_de_pago` (`id_metodo_de_pago`),
  CONSTRAINT `compra_chk_1` CHECK ((`estado` in (_utf8mb4'PENDIENTE',_utf8mb4'COMPLETADA',_utf8mb4'CANCELADA')))
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `compra`
--

LOCK TABLES `compra` WRITE;
/*!40000 ALTER TABLE `compra` DISABLE KEYS */;
INSERT INTO `compra` VALUES (1,'C00000001',NULL,1,1,0.00,0.00,0.00,2,'2025-10-07 19:57:08',NULL,'CANCELADA',NULL),(2,'C00000002',NULL,1,1,0.00,0.00,0.00,2,'2025-10-07 21:46:22',NULL,'PENDIENTE',NULL),(3,'C00000003',NULL,3,1,0.00,0.00,0.00,1,'2025-10-07 21:47:09',NULL,'CANCELADA',NULL),(4,'C00000004','535352532',4,1,10350.00,0.00,10350.00,3,'2025-10-07 23:11:48',NULL,'PENDIENTE',NULL),(5,'C00000005','65656',3,1,23500.00,0.00,23500.00,1,'2025-10-07 23:17:38',NULL,'PENDIENTE','No hay notas\n'),(6,'C00000006',NULL,1,1,152900.00,0.00,152900.00,1,'2025-10-07 23:23:03','2025-10-07 20:23:40','CANCELADA',NULL),(7,'C00000007',NULL,1,1,180.00,0.00,180.00,1,'2025-10-15 22:37:34','2025-10-15 19:37:42','COMPLETADA',NULL);
/*!40000 ALTER TABLE `compra` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `compra_detalle`
--

DROP TABLE IF EXISTS `compra_detalle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `compra_detalle` (
  `id_compra` int NOT NULL,
  `id_producto` int NOT NULL,
  `cantidad` int NOT NULL,
  `precio_unitario` decimal(10,2) NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id_compra`,`id_producto`),
  KEY `id_producto` (`id_producto`),
  CONSTRAINT `compra_detalle_ibfk_1` FOREIGN KEY (`id_compra`) REFERENCES `compra` (`id_compra`) ON DELETE CASCADE,
  CONSTRAINT `compra_detalle_ibfk_2` FOREIGN KEY (`id_producto`) REFERENCES `producto` (`id_producto`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `compra_detalle`
--

LOCK TABLES `compra_detalle` WRITE;
/*!40000 ALTER TABLE `compra_detalle` DISABLE KEYS */;
INSERT INTO `compra_detalle` VALUES (4,3,4,2350.00,9400.00),(4,6,5,190.00,950.00),(5,3,10,2350.00,23500.00),(6,3,33,2300.00,75900.00),(6,8,44,1750.00,77000.00),(7,6,1,180.00,180.00);
/*!40000 ALTER TABLE `compra_detalle` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `marca`
--

DROP TABLE IF EXISTS `marca`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `marca` (
  `id_marca` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `activo` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id_marca`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `marca`
--

LOCK TABLES `marca` WRITE;
/*!40000 ALTER TABLE `marca` DISABLE KEYS */;
INSERT INTO `marca` VALUES (1,'Genérica','Sin marca específica',1),(2,'Bic','Productos Bic Argentina',1),(3,'Rivadavia','Cuadernos y papelería Rivadavia',1),(4,'Faber-Castell','Productos Faber-Castell',1),(5,'Author','Papeles Author',1),(6,'Staedtler','Lápices y útiles Staedtler',1),(7,'3M','Productos 3M',1),(8,'Post-it','Notas adhesivas Post-it',1);
/*!40000 ALTER TABLE `marca` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `metodo_de_pago`
--

DROP TABLE IF EXISTS `metodo_de_pago`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `metodo_de_pago` (
  `id_metodo_de_pago` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `activo` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id_metodo_de_pago`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `metodo_de_pago`
--

LOCK TABLES `metodo_de_pago` WRITE;
/*!40000 ALTER TABLE `metodo_de_pago` DISABLE KEYS */;
INSERT INTO `metodo_de_pago` VALUES (1,'EFECTIVO',1),(2,'TRANSFERENCIA',1),(3,'TARJETA DE CREDITO',1);
/*!40000 ALTER TABLE `metodo_de_pago` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `movimiento`
--

DROP TABLE IF EXISTS `movimiento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movimiento` (
  `id_movimiento` int NOT NULL AUTO_INCREMENT,
  `id_producto` int NOT NULL,
  `id_tipo_movimiento` int NOT NULL,
  `cantidad` int NOT NULL,
  `stock_anterior` int NOT NULL,
  `stock_nuevo` int NOT NULL,
  `motivo` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `referencia` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `id_usuario` int NOT NULL,
  PRIMARY KEY (`id_movimiento`),
  KEY `id_producto` (`id_producto`),
  KEY `id_tipo_movimiento` (`id_tipo_movimiento`),
  KEY `id_usuario` (`id_usuario`),
  CONSTRAINT `movimiento_ibfk_1` FOREIGN KEY (`id_producto`) REFERENCES `producto` (`id_producto`),
  CONSTRAINT `movimiento_ibfk_2` FOREIGN KEY (`id_tipo_movimiento`) REFERENCES `tipo_movimiento` (`id_tipo_movimiento`),
  CONSTRAINT `movimiento_ibfk_3` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movimiento`
--

LOCK TABLES `movimiento` WRITE;
/*!40000 ALTER TABLE `movimiento` DISABLE KEYS */;
INSERT INTO `movimiento` VALUES (1,1,2,2,10,8,'Venta en proceso','Venta_V00000001','2025-09-03 22:21:07',1),(2,1,2,1,8,7,'Venta en proceso','Venta_V00000007','2025-09-03 22:22:11',1),(3,5,2,4,19,15,'Venta en proceso','Venta_V00000007','2025-09-03 22:22:20',1),(4,7,2,50,88,38,'Venta en proceso','Venta_V00000007','2025-09-03 22:22:30',1),(5,7,2,29,38,9,'Venta en proceso','Venta_V00000007','2025-09-03 22:24:38',1),(6,1,2,3,7,4,'Venta en proceso','Venta_V00000007','2025-09-03 22:27:43',1),(7,2,2,2,2,0,'Venta en proceso','Venta_V00000008','2025-09-03 22:29:36',1),(8,7,2,5,9,4,'Venta en proceso','Venta_V00000008','2025-09-03 22:29:39',1),(9,5,2,4,15,11,'Venta en proceso','Venta_V00000012','2025-09-10 20:39:23',1),(10,7,2,4,4,0,'Venta en proceso','Venta_V00000012','2025-09-10 20:39:42',1),(11,8,2,2,45,43,'Venta en proceso','Venta_V00000013','2025-09-10 20:51:30',1),(12,8,2,3,43,40,'Venta en proceso','Venta_V00000014','2025-09-10 20:57:05',1),(13,8,2,1,40,39,'Venta en proceso','Venta_V00000015','2025-09-10 20:59:16',1),(14,1,2,1,4,3,'Venta en proceso','Venta_V00000016','2025-09-10 21:21:14',1),(15,1,2,1,3,2,'Venta en proceso','Venta_V00000017','2025-09-10 21:22:48',1),(16,6,2,1,56,55,'Venta en proceso','Venta_V00000018','2025-09-10 21:30:35',1),(17,6,2,2,55,53,'Venta en proceso','Venta_V00000019','2025-09-10 21:33:56',1),(18,5,2,2,11,9,'Venta en proceso','Venta_V00000020','2025-09-10 21:36:39',1),(19,5,3,2,9,11,'Venta anulada - stock restaurado','Anulacion_V00000020','2025-09-10 21:36:46',1),(20,6,2,3,53,50,'Venta en proceso','Venta_V00000022','2025-09-10 21:47:38',1),(21,5,2,2,11,9,'Venta en proceso','Venta_V00000023','2025-09-10 22:03:06',1),(22,5,2,1,11,10,'Venta en proceso','Venta_V00000023','2025-09-10 22:03:54',1),(23,1,2,1,2,1,'Venta en proceso','Venta_V00000025','2025-09-10 23:10:05',1),(24,5,2,4,5,1,'Venta en proceso','Venta_V00000026','2025-09-10 23:11:57',1),(25,5,3,4,1,5,'Venta anulada - stock restaurado','Anulacion_V00000026','2025-09-10 23:17:37',1),(26,4,2,1,4,3,'Venta en proceso','Venta_V00000027','2025-09-14 04:28:58',1),(27,6,2,6,48,42,'Venta en proceso','Venta_V00000027','2025-09-14 04:29:01',1),(28,8,2,5,39,34,'Venta en proceso','Venta_V00000027','2025-09-14 04:29:06',1),(29,4,2,4,4,0,'Venta en proceso','Venta_V00000029','2025-09-14 04:41:21',1),(30,8,2,4,39,35,'Venta en proceso','Venta_V00000029','2025-09-14 04:41:24',1),(31,4,3,4,0,4,'Venta anulada - stock restaurado','Anulacion_V00000029','2025-09-14 04:41:51',1),(32,8,3,4,35,39,'Venta anulada - stock restaurado','Anulacion_V00000029','2025-09-14 04:41:51',1),(34,4,2,2,4,2,'Venta en proceso','Venta_V00000030','2025-09-14 04:43:31',1),(35,4,3,2,2,4,'Venta anulada - stock restaurado','Anulacion_V00000030','2025-09-14 04:43:40',1),(36,4,2,1,4,3,'Venta en proceso','Venta_V00000031','2025-09-14 04:47:38',1),(37,5,2,2,5,3,'Venta en proceso','Venta_V00000031','2025-09-14 04:47:40',1),(38,6,2,4,48,44,'Venta en proceso','Venta_V00000032','2025-09-14 14:31:05',1),(39,8,2,3,39,36,'Venta en proceso','Venta_V00000032','2025-09-14 14:31:08',1),(40,5,2,1,3,2,'Venta en proceso','Venta_V00000032','2025-09-14 14:31:35',1),(41,6,2,1,44,43,'Venta en proceso','Venta_V00000033','2025-09-14 15:26:45',1),(42,6,2,8,42,34,'Venta en proceso','Venta_V00000038','2025-09-14 15:36:12',1),(43,6,3,8,34,42,'Venta anulada - stock restaurado','Anulacion_V00000038','2025-09-14 15:36:33',1),(44,5,3,20,2,22,'Compra','AJUSTE_2025-09-17 17:14:01','2025-09-17 20:14:01',1),(45,15,4,9,50,41,'ninguno','AJUSTE_2025-09-17 17:36:53','2025-09-17 20:36:53',1),(46,17,4,0,50,50,'Stock inicial','AJUSTE_2025-09-17 18:59:32','2025-09-17 21:59:32',1),(47,5,2,5,22,17,'Venta en proceso','Venta_V00000046','2025-09-17 22:14:41',1),(48,8,2,4,36,32,'Venta en proceso','Venta_V00000046','2025-09-17 22:14:54',1),(49,5,3,4,16,20,'Ajuste Manual','AJUSTE_2025-09-17 20:05:40','2025-09-17 23:05:40',1),(50,7,3,100,0,100,'Ajuste Manual','AJUSTE_2025-09-17 20:05:55','2025-09-17 23:05:55',1),(51,18,3,40,0,40,'Stock inicial del producto','AJUSTE_2025-09-17 20:09:21','2025-09-17 23:09:21',1),(52,5,3,4,20,24,'Ajuste','AJUSTE_2025-09-17 20:33:11','2025-09-17 23:33:11',1),(53,1,2,1,1,0,'Venta en proceso','Venta_V00000048','2025-10-05 19:27:22',1),(54,5,2,3,24,21,'Venta en proceso','Venta_V00000049','2025-10-05 19:31:26',1),(55,6,2,2,42,40,'Venta en proceso','Venta_V00000049','2025-10-05 19:31:28',1),(56,5,2,2,21,19,'Venta en proceso','Venta_V00000050','2025-10-05 19:34:20',1),(57,6,2,2,40,38,'Venta en proceso','Venta_V00000050','2025-10-05 19:34:27',1),(58,4,2,1,2,1,'Venta en proceso','Venta_V00000051','2025-10-05 19:43:00',1),(59,5,2,1,19,18,'Venta en proceso','Venta_V00000051','2025-10-05 19:43:01',1),(60,4,3,1,1,2,'Venta anulada - stock restaurado','Anulacion_V00000051','2025-10-05 19:43:39',1),(61,5,3,1,18,19,'Venta anulada - stock restaurado','Anulacion_V00000051','2025-10-05 19:43:39',1),(63,4,2,1,2,1,'Venta en proceso','Venta_V00000052','2025-10-05 19:47:12',1),(64,5,2,1,19,18,'Venta en proceso','Venta_V00000052','2025-10-05 19:47:13',1),(65,4,3,1,1,2,'Venta anulada - stock restaurado','Anulacion_V00000052','2025-10-05 19:47:30',1),(66,5,3,1,18,19,'Venta anulada - stock restaurado','Anulacion_V00000052','2025-10-05 19:47:30',1),(67,3,2,33,0,33,'Compra completada','Compra_C00000006','2025-10-07 23:23:39',1),(68,8,2,44,32,76,'Compra completada','Compra_C00000006','2025-10-07 23:23:39',1),(70,3,3,33,33,0,'Compra cancelada - stock revertido','Cancelacion_C00000006','2025-10-07 23:28:17',1),(71,8,3,44,76,32,'Compra cancelada - stock revertido','Cancelacion_C00000006','2025-10-07 23:28:17',1),(73,4,2,1,2,1,'Venta en proceso','Venta_V00000054','2025-10-08 00:04:34',1),(74,7,2,6,100,94,'Venta en proceso','Venta_V00000054','2025-10-08 00:04:40',1),(75,8,3,2,32,34,'Error de conteo','AJUSTE_2025-10-07 22:14:01','2025-10-08 01:14:01',1),(76,5,2,3,19,16,'Venta en proceso','Venta_V00000060','2025-10-10 02:01:03',1),(77,6,2,3,38,35,'Venta en proceso','Venta_V00000060','2025-10-10 02:01:06',1),(78,8,2,3,34,31,'Venta en proceso','Venta_V00000060','2025-10-10 02:01:08',1),(79,5,2,4,16,12,'Venta en proceso','Venta_V00000062','2025-10-10 02:04:34',1),(80,6,2,3,35,32,'Venta en proceso','Venta_V00000063','2025-10-13 19:39:04',1),(81,5,2,3,12,9,'Venta en proceso','Venta_V00000066','2025-10-15 03:59:08',1),(82,7,2,5,94,89,'Venta en proceso','Venta_V00000066','2025-10-15 03:59:10',1),(83,18,2,3,40,37,'Venta en proceso','Venta_V00000066','2025-10-15 03:59:19',1),(84,5,3,3,9,12,'Venta anulada - stock restaurado','Anulacion_V00000066','2025-10-15 17:11:01',1),(85,7,3,5,89,94,'Venta anulada - stock restaurado','Anulacion_V00000066','2025-10-15 17:11:01',1),(86,18,3,3,37,40,'Venta anulada - stock restaurado','Anulacion_V00000066','2025-10-15 17:11:01',1),(87,6,2,1,32,33,'Compra completada','Compra_C00000007','2025-10-15 22:37:42',1),(88,5,2,6,12,6,'Venta en proceso','Venta_V00000067','2025-10-15 22:38:28',1),(89,6,2,6,33,27,'Venta en proceso','Venta_V00000070','2025-10-19 00:48:11',1),(90,7,2,6,94,88,'Venta en proceso','Venta_V00000070','2025-10-19 00:48:16',1),(91,6,2,4,27,23,'Venta en proceso','Venta_V00000071','2025-10-19 02:08:03',1),(92,6,2,3,23,20,'Venta en proceso','Venta_V00000072','2025-10-19 02:35:28',1),(93,8,2,4,31,27,'Venta en proceso','Venta_V00000072','2025-10-19 02:35:31',1),(94,5,2,1,5,4,'Venta en proceso','Venta_V00000075','2025-10-19 03:26:21',1),(95,5,2,3,3,0,'Venta en proceso','Venta_V00000077','2025-10-20 14:57:00',1),(96,7,2,4,88,84,'Venta en proceso','Venta_V00000079','2025-10-20 14:58:13',1),(97,7,3,5,83,88,'Venta anulada - stock restaurado','Anulacion_V00000079','2025-10-20 17:32:40',1),(98,7,2,3,88,85,'Venta en proceso','Venta_V00000081','2025-10-20 19:06:41',1),(99,7,3,3,85,88,'Venta anulada - stock restaurado','Anulacion_V00000081','2025-10-20 19:07:47',1);
/*!40000 ALTER TABLE `movimiento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `producto`
--

DROP TABLE IF EXISTS `producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto` (
  `id_producto` int NOT NULL AUTO_INCREMENT,
  `codigo_barras` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `codigo_interno` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nombre` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  `id_categoria` int NOT NULL,
  `id_marca` int DEFAULT NULL,
  `tipo_producto` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'FISICO',
  `stock_actual` int DEFAULT '0',
  `stock_minimo` int DEFAULT '0',
  `precio_costo` decimal(10,2) DEFAULT '0.00',
  `precio_venta` decimal(10,2) NOT NULL,
  `unidad_medida` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `activo` tinyint(1) DEFAULT '1',
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_ultima_venta` datetime DEFAULT NULL,
  `fecha_ultima_compra` datetime DEFAULT NULL,
  PRIMARY KEY (`id_producto`),
  UNIQUE KEY `codigo_barras` (`codigo_barras`),
  UNIQUE KEY `codigo_interno` (`codigo_interno`),
  KEY `id_categoria` (`id_categoria`),
  KEY `id_marca` (`id_marca`),
  CONSTRAINT `producto_ibfk_1` FOREIGN KEY (`id_categoria`) REFERENCES `categoria` (`id_categoria`),
  CONSTRAINT `producto_ibfk_2` FOREIGN KEY (`id_marca`) REFERENCES `marca` (`id_marca`),
  CONSTRAINT `producto_chk_1` CHECK ((`tipo_producto` in (_utf8mb4'FISICO',_utf8mb4'SERVICIO')))
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `producto`
--

LOCK TABLES `producto` WRITE;
/*!40000 ALTER TABLE `producto` DISABLE KEYS */;
INSERT INTO `producto` VALUES (1,'7790001001011','CUA001','Cuaderno Rivadavia 48 hojas rayado','Cuaderno rayado 48 hojas tapa dura',1,NULL,'FISICO',0,5,1000.00,1500.00,NULL,1,'2025-09-03 02:58:49','2025-10-05 16:27:22',NULL),(2,'7790002002022','LAP001','Lapicera Bic Cristal Azul','Lapicera a bolígrafo azul',1,2,'FISICO',0,10,500.00,800.00,NULL,1,'2025-09-03 02:58:49','2025-09-03 19:29:36',NULL),(3,'7790003003033','RES001','Resma A4 Author 500 hojas','Resma papel blanco A4 75g',2,5,'FISICO',0,5,2300.00,3500.00,NULL,1,'2025-09-03 02:58:49',NULL,'2025-10-07 20:23:39'),(4,'7790004004044','COR001','Corrector Liquid Paper 20ml','Corrector líquido blanco 20ml',1,1,'FISICO',0,5,800.00,1200.00,NULL,1,'2025-09-03 02:58:49','2025-10-07 21:04:34',NULL),(5,'7790005005055','CAR001','Cartulina A4 Blanca x10','Pack 10 cartulinas A4 blancas',2,NULL,'FISICO',0,20,300.00,500.00,NULL,1,'2025-09-03 02:58:49','2025-10-20 11:57:00',NULL),(6,'7790006006066','LAP002','Lápiz Faber-Castell HB','Lápiz grafito HB',1,4,'FISICO',23,15,180.00,350.00,NULL,1,'2025-09-03 02:58:49','2025-10-18 23:35:28','2025-10-15 19:37:42'),(7,'7790007007077','GOM001','Goma Staedtler','Goma de borrar blanca',1,6,'FISICO',88,10,150.00,250.00,NULL,1,'2025-09-03 02:58:49','2025-10-20 16:06:41',NULL),(8,'7790008008088','CUA002','Cuaderno Rivadavia 96 hojas','Cuaderno rayado 96 hojas',1,3,'FISICO',27,3,1750.00,2500.00,NULL,1,'2025-09-03 02:58:49','2025-10-18 23:35:31','2025-10-07 20:23:39'),(9,'SRV0000000001',NULL,'Impresión Blanco y Negro','Impresión simple B/N en papel A4',5,NULL,'SERVICIO',50,0,0.00,50.00,'por página',1,'2025-09-03 02:58:49',NULL,NULL),(10,'SRV0000000002',NULL,'Impresión Color','Impresión a color en papel A4',5,NULL,'SERVICIO',50,0,0.00,150.00,'por página',1,'2025-09-03 02:58:49',NULL,NULL),(11,'SRV0000000003',NULL,'Fotocopia B/N','Fotocopia blanco y negro',5,NULL,'SERVICIO',50,0,0.00,30.00,'por página',1,'2025-09-03 02:58:49',NULL,NULL),(12,'SRV0000000004',NULL,'Fotocopia Color','Fotocopia a color',5,NULL,'SERVICIO',50,0,0.00,100.00,'por página',1,'2025-09-03 02:58:49',NULL,NULL),(13,'SRV0000000005',NULL,'Encuadernación Térmica','Encuadernación con lomo térmico',6,NULL,'SERVICIO',50,0,0.00,800.00,'por trabajo',1,'2025-09-03 02:58:49',NULL,NULL),(14,'SRV0000000006',NULL,'Anillado Espiral','Anillado con espiral plástico',6,NULL,'SERVICIO',50,0,0.00,400.03,'por trabajo',0,'2025-09-03 02:58:49',NULL,NULL),(15,'SRV0000000007',NULL,'Encuadernación Tapa Dura','Encuadernación premium con tapa dura',6,NULL,'SERVICIO',41,0,0.00,1200.00,'por trabajo',1,'2025-09-03 02:58:49',NULL,NULL),(16,'SRV0000000008',NULL,'Plastificado A4','Plastificado de documentos A4',5,NULL,'SERVICIO',50,0,0.00,200.00,'por hoja',1,'2025-09-03 02:58:49',NULL,NULL),(17,'7790006003982',NULL,'Folios A4 x10','Folios A4 x10',1,NULL,'FISICO',50,30,2000.00,3000.00,NULL,0,'2025-09-17 21:59:31',NULL,NULL),(18,'7790006003729',NULL,'Lapiz Negro Faber-Castell','sin descripcion',1,4,'FISICO',40,20,400.00,700.00,NULL,1,'2025-09-17 23:09:20','2025-10-15 00:59:19',NULL),(19,'SRV462841',NULL,'Ejemplo2','Este es un ejemplo de servicio',6,NULL,'SERVICIO',50,0,0.00,200.00,NULL,1,'2025-10-01 04:14:45',NULL,NULL);
/*!40000 ALTER TABLE `producto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `producto_proveedor`
--

DROP TABLE IF EXISTS `producto_proveedor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto_proveedor` (
  `id_producto` int NOT NULL,
  `id_proveedor` int NOT NULL,
  `codigo_proveedor` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `precio_compra` decimal(10,2) DEFAULT NULL,
  `precio_compra_anterior` decimal(10,2) DEFAULT NULL,
  `fecha_ultima_compra` datetime DEFAULT NULL,
  `es_proveedor_principal` tinyint(1) DEFAULT '0',
  `activo` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id_producto`,`id_proveedor`),
  KEY `id_proveedor` (`id_proveedor`),
  CONSTRAINT `producto_proveedor_ibfk_1` FOREIGN KEY (`id_producto`) REFERENCES `producto` (`id_producto`),
  CONSTRAINT `producto_proveedor_ibfk_2` FOREIGN KEY (`id_proveedor`) REFERENCES `proveedor` (`id_proveedor`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `producto_proveedor`
--

LOCK TABLES `producto_proveedor` WRITE;
/*!40000 ALTER TABLE `producto_proveedor` DISABLE KEYS */;
INSERT INTO `producto_proveedor` VALUES (1,1,'RIV-48-001',950.00,NULL,NULL,0,1),(1,2,'4657890',3000.00,3000.00,'2025-10-04 15:58:10',1,1),(1,3,'CUA-RIV-48',980.00,NULL,NULL,0,1),(2,1,'BIC-CRISTAL-AZ',480.00,NULL,NULL,1,1),(2,2,'LAP-BIC-001',490.00,NULL,NULL,0,0),(3,1,'AUTHOR-A4-500',2300.00,NULL,NULL,1,1),(3,3,'RES-A4-AUTH',2350.00,NULL,NULL,0,1),(4,1,'LIQUID-20ML',750.00,NULL,NULL,1,1),(5,2,'CART-A4-BL-10',280.00,NULL,NULL,1,1),(6,1,'FABER-HB',180.00,NULL,NULL,1,1),(6,3,'LAP-FABER-HB',190.00,NULL,NULL,0,1),(7,1,'STAED-GOMA',140.00,NULL,NULL,1,1),(8,1,'RIV-96-001',1750.00,NULL,NULL,1,1);
/*!40000 ALTER TABLE `producto_proveedor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `promocion`
--

DROP TABLE IF EXISTS `promocion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `promocion` (
  `id_promocion` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `descuento_porcentaje` decimal(5,2) NOT NULL,
  `fecha_inicio` datetime NOT NULL,
  `fecha_fin` datetime NOT NULL,
  `activa` tinyint(1) DEFAULT '1',
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_promocion`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `promocion`
--

LOCK TABLES `promocion` WRITE;
/*!40000 ALTER TABLE `promocion` DISABLE KEYS */;
INSERT INTO `promocion` VALUES (1,'Vuelta al Cole 2024','Descuento especial por inicio de clases',15.00,'2024-02-01 00:00:00','2024-03-15 23:59:59',1,'2025-09-03 02:58:49'),(2,'Día del Estudiante','Descuento especial para estudiantes',20.00,'2024-09-21 00:00:00','2024-09-21 23:59:59',1,'2025-09-03 02:58:49');
/*!40000 ALTER TABLE `promocion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `promocion_producto`
--

DROP TABLE IF EXISTS `promocion_producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `promocion_producto` (
  `id_promocion` int NOT NULL,
  `id_producto` int NOT NULL,
  PRIMARY KEY (`id_promocion`,`id_producto`),
  KEY `id_producto` (`id_producto`),
  CONSTRAINT `promocion_producto_ibfk_1` FOREIGN KEY (`id_promocion`) REFERENCES `promocion` (`id_promocion`) ON DELETE CASCADE,
  CONSTRAINT `promocion_producto_ibfk_2` FOREIGN KEY (`id_producto`) REFERENCES `producto` (`id_producto`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `promocion_producto`
--

LOCK TABLES `promocion_producto` WRITE;
/*!40000 ALTER TABLE `promocion_producto` DISABLE KEYS */;
/*!40000 ALTER TABLE `promocion_producto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `proveedor`
--

DROP TABLE IF EXISTS `proveedor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `proveedor` (
  `id_proveedor` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `cuit` varchar(13) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `telefono` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `direccion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `activo` tinyint(1) DEFAULT '1',
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_proveedor`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `proveedor`
--

LOCK TABLES `proveedor` WRITE;
/*!40000 ALTER TABLE `proveedor` DISABLE KEYS */;
INSERT INTO `proveedor` VALUES (1,'Distribuidora Papelera SA','30123456789','011-4444-5555','ventas@papelera.com.ar','Av. Corrientes 1234, CABA',1,'2025-09-03 02:58:49'),(2,'Servicios Gráficos Unidos','27987654321','011-6666-7777','contacto@graficos.com.arr','San Martín 567, CABA',1,'2025-09-03 02:58:49'),(3,'Librería Mayorista Central','30555666777','011-8888-9999','mayorista@central.com.ar','Rivadavia 2345, CABA',1,'2025-09-03 02:58:49'),(4,'Libreria Oruga','632921132','11276482982','libreria_oruga@gmail.com','Rawson 287, Jose C Paz',1,'2025-10-05 18:59:59');
/*!40000 ALTER TABLE `proveedor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `proveedor_archivo`
--

DROP TABLE IF EXISTS `proveedor_archivo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `proveedor_archivo` (
  `id_archivo` int NOT NULL AUTO_INCREMENT,
  `id_proveedor` int NOT NULL,
  `nombre_archivo` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ruta_archivo` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tipo_archivo` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `descripcion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tamanio_bytes` bigint DEFAULT NULL,
  `fecha_subida` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `activo` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id_archivo`),
  KEY `id_proveedor` (`id_proveedor`),
  CONSTRAINT `proveedor_archivo_ibfk_1` FOREIGN KEY (`id_proveedor`) REFERENCES `proveedor` (`id_proveedor`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `proveedor_archivo`
--

LOCK TABLES `proveedor_archivo` WRITE;
/*!40000 ALTER TABLE `proveedor_archivo` DISABLE KEYS */;
INSERT INTO `proveedor_archivo` VALUES (1,1,'libreria_EER.pdf','data/proveedores/1/libreria_EER.pdf','PDF','Productos Octubre 2025',51194,'2025-10-05 23:50:43',1),(2,1,'test.csv','data/proveedores/1/test.csv','CSV','Lista Junio',63850,'2025-10-08 22:05:54',1);
/*!40000 ALTER TABLE `proveedor_archivo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tipo_movimiento`
--

DROP TABLE IF EXISTS `tipo_movimiento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipo_movimiento` (
  `id_tipo_movimiento` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `afecta_stock` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id_tipo_movimiento`),
  UNIQUE KEY `nombre` (`nombre`),
  CONSTRAINT `tipo_movimiento_chk_1` CHECK ((`afecta_stock` in (_utf8mb4'SUMA',_utf8mb4'RESTA',_utf8mb4'NINGUNO')))
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tipo_movimiento`
--

LOCK TABLES `tipo_movimiento` WRITE;
/*!40000 ALTER TABLE `tipo_movimiento` DISABLE KEYS */;
INSERT INTO `tipo_movimiento` VALUES (1,'VENTA','RESTA','Producto vendido - disminuye stock'),(2,'COMPRA','SUMA','Producto comprado a proveedor - aumenta stock'),(3,'AJUSTE_POSITIVO','SUMA','Ajuste manual positivo - aumenta stock'),(4,'AJUSTE_NEGATIVO','RESTA','Ajuste manual negativo - disminuye stock'),(5,'DEVOLUCION_ENTRADA','SUMA','Devolución de cliente - aumenta stock'),(6,'DEVOLUCION_SALIDA','RESTA','Devolución a proveedor - disminuye stock'),(7,'INVENTARIO','NINGUNO','Recuento de inventario - solo registro'),(8,'PERDIDA','RESTA','Pérdida, robo o rotura - disminuye stock'),(9,'INICIAL','SUMA','Stock inicial del sistema');
/*!40000 ALTER TABLE `tipo_movimiento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tipo_usuario`
--

DROP TABLE IF EXISTS `tipo_usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipo_usuario` (
  `id_tipo_usuario` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `puede_anular_ventas` tinyint(1) DEFAULT '0',
  `puede_ver_reportes` tinyint(1) DEFAULT '0',
  `puede_gestionar_usuarios` tinyint(1) DEFAULT '0',
  `puede_modificar_precios` tinyint(1) DEFAULT '0',
  `activo` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id_tipo_usuario`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tipo_usuario`
--

LOCK TABLES `tipo_usuario` WRITE;
/*!40000 ALTER TABLE `tipo_usuario` DISABLE KEYS */;
INSERT INTO `tipo_usuario` VALUES (1,'ADMIN','Administrador - Acceso completo al sistema',1,1,1,1,1),(2,'EMPLEADO','Empleado - Acceso limitado a operaciones básicas',0,0,0,1,1);
/*!40000 ALTER TABLE `tipo_usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id_usuario` int NOT NULL AUTO_INCREMENT,
  `nombre_usuario` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `contraseña_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `salt` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `apellido` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_tipo_usuario` int NOT NULL,
  `activo` tinyint(1) DEFAULT '1',
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `ultimo_acceso` datetime DEFAULT NULL,
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `nombre_usuario` (`nombre_usuario`),
  KEY `id_tipo_usuario` (`id_tipo_usuario`),
  CONSTRAINT `usuario_ibfk_1` FOREIGN KEY (`id_tipo_usuario`) REFERENCES `tipo_usuario` (`id_tipo_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,'admin','$2a$10$le8o/AsJByTjHR7PB7ICQenWtjPFMwBvHIMs6y4rdUjrfbGOlVKMe','','Administrador','Sistema',1,1,'2025-09-03 02:58:49','2025-10-22 02:59:27'),(2,'empleado1','$2a$10$JrzYFbzP.R3mJ0D930Im/OTf2C4XeWnrF2czQrJy8YkrTwXKqDEC2','','Juan','Pérez',2,1,'2025-10-08 21:08:53','2025-10-20 16:05:58'),(3,'empleado2','$2a$10$dRAPN.tklaubuRymERXmWOg.wb6IhZIGyyNsELpUrdS8uoJ5km68G','','Cristian','Rodríguez',2,0,'2025-10-10 02:38:52','2025-10-09 23:39:08'),(4,'empleado3','$2a$10$0.kNqGTwxKvvydyzcPG9vOvk67rwx4vmrBXdW1h704zbo3Y7UCd/i','','Cristina','Aguilera',2,1,'2025-10-15 00:24:30',NULL);
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `venta`
--

DROP TABLE IF EXISTS `venta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `venta` (
  `id_venta` int NOT NULL AUTO_INCREMENT,
  `numero_comprobante` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_usuario` int NOT NULL,
  `id_cliente` int DEFAULT NULL,
  `subtotal` decimal(10,2) NOT NULL DEFAULT '0.00',
  `descuento_productos` decimal(10,2) DEFAULT '0.00',
  `descuento_venta` decimal(10,2) DEFAULT '0.00',
  `total` decimal(10,2) NOT NULL DEFAULT '0.00',
  `id_metodo_de_pago` int NOT NULL,
  `fecha` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `estado` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT 'EN_PROCESO',
  `observaciones` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id_venta`),
  UNIQUE KEY `numero_comprobante` (`numero_comprobante`),
  KEY `id_usuario` (`id_usuario`),
  KEY `id_cliente` (`id_cliente`),
  KEY `id_metodo_de_pago` (`id_metodo_de_pago`),
  KEY `idx_venta_fecha` (`fecha` DESC),
  KEY `idx_venta_estado` (`estado`),
  KEY `idx_venta_fecha_estado` (`fecha` DESC,`estado`),
  CONSTRAINT `venta_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `venta_ibfk_2` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id_cliente`),
  CONSTRAINT `venta_ibfk_3` FOREIGN KEY (`id_metodo_de_pago`) REFERENCES `metodo_de_pago` (`id_metodo_de_pago`),
  CONSTRAINT `venta_chk_1` CHECK ((`estado` in (_utf8mb4'EN_PROCESO',_utf8mb4'COMPLETADA',_utf8mb4'ANULADA')))
) ENGINE=InnoDB AUTO_INCREMENT=93 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `venta`
--

LOCK TABLES `venta` WRITE;
/*!40000 ALTER TABLE `venta` DISABLE KEYS */;
INSERT INTO `venta` VALUES (1,'V00000001',1,NULL,3000.00,0.00,0.00,3000.00,1,'2025-09-03 20:29:51','EN_PROCESO',NULL),(2,'V00000002',1,NULL,0.00,0.00,0.00,0.00,1,'2025-09-03 21:02:25','EN_PROCESO',NULL),(3,'V00000003',1,NULL,0.00,0.00,0.00,0.00,1,'2025-09-03 21:10:08','EN_PROCESO',NULL),(4,'V00000004',1,NULL,0.00,0.00,0.00,0.00,1,'2025-09-03 21:18:45','EN_PROCESO',NULL),(5,'V00000005',1,NULL,0.00,0.00,0.00,0.00,1,'2025-09-03 21:34:50','EN_PROCESO',NULL),(6,'V00000006',1,NULL,0.00,0.00,0.00,0.00,1,'2025-09-03 21:37:58','EN_PROCESO',NULL),(7,'V00000007',1,NULL,12750.00,0.00,0.00,12750.00,1,'2025-09-03 22:22:05','COMPLETADA',NULL),(8,'V00000008',1,NULL,2850.00,0.00,0.00,2850.00,1,'2025-09-03 22:29:24','COMPLETADA',NULL),(9,'V00000009',1,NULL,0.00,0.00,0.00,0.00,1,'2025-09-03 22:29:47','EN_PROCESO',NULL),(10,'V00000010',1,NULL,750.00,0.00,0.00,750.00,1,'2025-09-03 22:49:18','COMPLETADA',NULL),(11,'V00000011',1,NULL,0.00,0.00,0.00,0.00,1,'2025-09-03 22:52:23','EN_PROCESO',NULL),(12,'V00000012',1,NULL,3210.00,0.00,0.00,3210.00,1,'2025-09-10 20:08:14','EN_PROCESO',NULL),(13,'V00000013',1,NULL,10050.00,0.00,0.00,10050.00,1,'2025-09-10 20:51:08','COMPLETADA',NULL),(14,'V00000014',1,NULL,7500.00,0.00,0.00,7500.00,1,'2025-09-10 20:54:58','COMPLETADA',NULL),(15,'V00000015',1,NULL,0.00,0.00,0.00,0.00,1,'2025-09-10 20:57:18','COMPLETADA',NULL),(16,'V00000016',1,NULL,1500.00,0.00,0.00,1500.00,1,'2025-09-10 21:21:07','COMPLETADA',NULL),(17,'V00000017',1,NULL,1500.00,0.00,0.00,1500.00,1,'2025-09-10 21:22:42','COMPLETADA',NULL),(18,'V00000018',1,NULL,2100.00,0.00,0.00,2100.00,1,'2025-09-10 21:30:28','COMPLETADA',NULL),(19,'V00000019',1,NULL,1050.00,0.00,0.00,1050.00,1,'2025-09-10 21:33:51','EN_PROCESO',NULL),(20,'V00000020',1,NULL,1000.00,0.00,0.00,1000.00,1,'2025-09-10 21:36:31','ANULADA',NULL),(21,'V00000021',1,NULL,0.00,0.00,0.00,0.00,1,'2025-09-10 21:36:56','EN_PROCESO',NULL),(22,'V00000022',1,NULL,0.00,0.00,0.00,0.00,1,'2025-09-10 21:47:11','EN_PROCESO',NULL),(23,'V00000023',1,NULL,3000.00,0.00,0.00,3000.00,1,'2025-09-10 22:02:49','EN_PROCESO',NULL),(24,'V00000024',1,NULL,1600.00,0.00,0.00,1600.00,1,'2025-09-10 22:55:01','EN_PROCESO',NULL),(25,'V00000025',1,NULL,1500.00,0.00,0.00,1500.00,1,'2025-09-10 23:09:35','COMPLETADA',NULL),(26,'V00000026',1,NULL,2450.00,0.00,0.00,2450.00,1,'2025-09-10 23:09:54','ANULADA',NULL),(27,'V00000027',1,NULL,0.00,0.00,0.00,0.00,1,'2025-09-14 04:28:42','ANULADA',NULL),(28,'V00000028',1,NULL,0.00,0.00,0.00,0.00,1,'2025-09-14 04:37:21','EN_PROCESO',NULL),(29,'V00000029',1,NULL,14890.00,0.00,0.00,14890.00,1,'2025-09-14 04:41:04','ANULADA',NULL),(30,'V00000030',1,NULL,2400.00,0.00,0.00,2400.00,1,'2025-09-14 04:43:23','ANULADA',NULL),(31,'V00000031',1,NULL,3400.00,0.00,0.00,3400.00,1,'2025-09-14 04:47:29','COMPLETADA',NULL),(32,'V00000032',1,NULL,9400.00,0.00,0.00,9400.00,2,'2025-09-14 14:30:51','COMPLETADA',NULL),(33,'V00000033',1,NULL,700.00,0.00,0.00,700.00,1,'2025-09-14 15:26:12','COMPLETADA',NULL),(34,'V00000034',1,NULL,0.00,0.00,0.00,0.00,1,'2025-09-14 15:26:34','ANULADA',NULL),(35,'V00000035',1,NULL,0.00,0.00,0.00,0.00,1,'2025-09-14 15:26:59','ANULADA',NULL),(36,'V00000036',1,NULL,0.00,0.00,0.00,0.00,1,'2025-09-14 15:32:41','ANULADA',NULL),(37,'V00000037',1,NULL,0.00,0.00,0.00,0.00,1,'2025-09-14 15:34:06','ANULADA',NULL),(38,'V00000038',1,NULL,2800.00,0.00,0.00,2800.00,1,'2025-09-14 15:36:02','ANULADA',NULL),(39,'V00000039',1,NULL,0.00,0.00,0.00,0.00,1,'2025-09-14 15:41:00','ANULADA',NULL),(40,'V00000040',1,NULL,0.00,0.00,0.00,0.00,1,'2025-09-14 15:41:37','ANULADA',NULL),(41,'V00000041',1,NULL,0.00,0.00,0.00,0.00,1,'2025-09-16 04:12:25','EN_PROCESO',NULL),(42,'V00000042',1,NULL,0.00,0.00,0.00,0.00,1,'2025-09-17 14:11:16','ANULADA',NULL),(43,'V00000043',1,NULL,0.00,0.00,0.00,0.00,1,'2025-09-17 18:14:38','EN_PROCESO',NULL),(44,'V00000044',1,NULL,0.00,0.00,0.00,0.00,1,'2025-09-17 18:17:08','EN_PROCESO',NULL),(45,'V00000045',1,NULL,0.00,0.00,0.00,0.00,1,'2025-09-17 19:14:09','EN_PROCESO',NULL),(46,'V00000046',1,NULL,13090.00,0.00,0.00,13090.00,3,'2025-09-17 22:13:39','COMPLETADA',NULL),(47,'V00000047',1,NULL,0.00,0.00,0.00,0.00,1,'2025-09-17 23:42:52','EN_PROCESO',NULL),(48,'V00000048',1,NULL,1500.00,0.00,0.00,1500.00,1,'2025-10-05 19:26:52','EN_PROCESO',NULL),(49,'V00000049',1,NULL,2200.00,0.00,0.00,2200.00,1,'2025-10-05 19:31:22','EN_PROCESO',NULL),(50,'V00000050',1,NULL,1700.00,0.00,0.00,1700.00,1,'2025-10-05 19:34:11','EN_PROCESO',NULL),(51,'V00000051',1,NULL,1700.00,0.00,0.00,1700.00,1,'2025-10-05 19:42:50','ANULADA',NULL),(52,'V00000052',1,NULL,1700.00,0.00,0.00,1700.00,1,'2025-10-05 19:47:07','ANULADA',NULL),(53,'V00000053',1,NULL,0.00,0.00,0.00,0.00,1,'2025-10-07 20:12:59','EN_PROCESO',NULL),(54,'V00000054',1,NULL,3900.00,0.00,0.00,3900.00,1,'2025-10-08 00:04:30','COMPLETADA',NULL),(55,'V00000055',1,NULL,0.00,0.00,0.00,0.00,1,'2025-10-08 00:05:02','EN_PROCESO',NULL),(56,'V00000056',1,NULL,0.00,0.00,0.00,0.00,1,'2025-10-08 01:35:26','EN_PROCESO',NULL),(57,'V00000057',1,NULL,0.00,0.00,0.00,0.00,1,'2025-10-08 22:05:28','EN_PROCESO',NULL),(58,'V00000058',1,NULL,0.00,0.00,0.00,0.00,1,'2025-10-09 03:12:30','EN_PROCESO',NULL),(59,'V00000059',1,NULL,0.00,0.00,0.00,0.00,1,'2025-10-09 23:09:25','EN_PROCESO',NULL),(60,'V00000060',1,NULL,10050.00,0.00,0.00,10050.00,1,'2025-10-10 02:00:56','COMPLETADA',NULL),(61,'V00000061',1,NULL,0.00,0.00,0.00,0.00,1,'2025-10-10 02:01:45','EN_PROCESO',NULL),(62,'V00000062',1,NULL,5600.00,0.00,0.00,5600.00,2,'2025-10-10 02:04:32','COMPLETADA',NULL),(63,'V00000063',1,NULL,12250.00,0.00,0.00,12250.00,1,'2025-10-13 19:39:00','COMPLETADA',NULL),(64,'V00000064',1,NULL,0.00,0.00,0.00,0.00,1,'2025-10-13 20:08:55','ANULADA',NULL),(65,'V00000065',1,NULL,0.00,0.00,0.00,0.00,1,'2025-10-14 22:17:48','EN_PROCESO',NULL),(66,'V00000066',1,NULL,5050.00,0.00,0.00,5050.00,1,'2025-10-15 03:56:21','ANULADA',NULL),(67,'V00000067',1,NULL,3800.00,0.00,0.00,3800.00,1,'2025-10-15 22:38:21','COMPLETADA',NULL),(68,'V00000068',1,NULL,0.00,0.00,0.00,0.00,1,'2025-10-15 22:40:28','ANULADA',NULL),(69,'V00000069',1,NULL,0.00,0.00,0.00,0.00,1,'2025-10-19 00:48:05','EN_PROCESO',NULL),(70,'V00000070',1,3,3900.00,0.00,0.00,3900.00,3,'2025-10-19 00:48:09','COMPLETADA',NULL),(71,'V00000071',1,NULL,1950.00,0.00,0.00,1950.00,1,'2025-10-19 02:07:59','COMPLETADA',NULL),(72,'V00000072',1,6,14800.00,0.00,0.00,14800.00,1,'2025-10-19 02:35:25','COMPLETADA',NULL),(73,'V00000073',1,NULL,0.00,0.00,0.00,0.00,1,'2025-10-19 02:36:51','EN_PROCESO',NULL),(74,'V00000074',1,NULL,0.00,0.00,0.00,0.00,1,'2025-10-19 02:37:13','ANULADA',NULL),(75,'V00000075',1,NULL,1000.00,0.00,0.00,1000.00,1,'2025-10-19 03:26:19','EN_PROCESO',NULL),(76,'V00000076',1,NULL,0.00,0.00,0.00,0.00,1,'2025-10-19 03:26:26','EN_PROCESO',NULL),(77,'V00000077',1,NULL,1500.00,0.00,0.00,1500.00,1,'2025-10-20 14:56:53','EN_PROCESO',NULL),(78,'V00000078',1,NULL,0.00,0.00,0.00,0.00,1,'2025-10-20 14:57:08','ANULADA',NULL),(79,'V00000079',1,NULL,1500.00,0.00,0.00,1500.00,1,'2025-10-20 14:57:54','ANULADA',NULL),(80,'V00000080',1,NULL,0.00,0.00,0.00,0.00,1,'2025-10-20 14:58:53','EN_PROCESO',NULL),(81,'V00000081',1,NULL,750.00,0.00,0.00,750.00,1,'2025-10-20 19:06:22','ANULADA',NULL),(82,'V00000082',1,NULL,0.00,0.00,0.00,0.00,1,'2025-10-20 19:12:33','EN_PROCESO',NULL),(83,'V00000083',1,NULL,0.00,0.00,0.00,0.00,1,'2025-10-21 02:39:17','EN_PROCESO',NULL),(84,'V00000084',1,NULL,0.00,0.00,0.00,0.00,1,'2025-10-21 03:20:38','ANULADA',NULL),(85,'V00000085',1,NULL,0.00,0.00,0.00,0.00,1,'2025-10-21 03:20:51','EN_PROCESO',NULL),(86,'V00000086',1,NULL,0.00,0.00,0.00,0.00,1,'2025-10-21 03:21:20','EN_PROCESO',NULL),(87,'V00000087',1,NULL,0.00,0.00,0.00,0.00,1,'2025-10-21 03:38:41','EN_PROCESO',NULL),(88,'V00000088',1,NULL,0.00,0.00,0.00,0.00,1,'2025-10-21 03:38:47','EN_PROCESO',NULL),(89,'V00000089',1,NULL,0.00,0.00,0.00,0.00,1,'2025-10-22 03:51:09','EN_PROCESO',NULL),(90,'V00000090',1,NULL,0.00,0.00,0.00,0.00,1,'2025-10-22 05:21:48','EN_PROCESO',NULL),(91,'V00000091',1,NULL,0.00,0.00,0.00,0.00,1,'2025-10-22 05:22:18','EN_PROCESO',NULL),(92,'V00000092',1,NULL,0.00,0.00,0.00,0.00,1,'2025-10-22 05:22:21','EN_PROCESO',NULL);
/*!40000 ALTER TABLE `venta` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`juanfruh`@`localhost`*/ /*!50003 TRIGGER `tr_restaurar_stock_anulacion` AFTER UPDATE ON `venta` FOR EACH ROW BEGIN
    IF OLD.estado != 'ANULADA' AND NEW.estado = 'ANULADA' THEN
        -- Restaurar stock de todos los productos de la venta
        UPDATE producto p
        INNER JOIN venta_detalle vd ON p.id_producto = vd.id_producto
        SET p.stock_actual = p.stock_actual + vd.cantidad
        WHERE vd.id_venta = NEW.id_venta 
          AND p.tipo_producto = 'FISICO';
        
        -- Registrar movimientos de restauración
        INSERT INTO movimiento (
            id_producto, id_tipo_movimiento, cantidad, 
            stock_anterior, stock_nuevo, motivo, referencia, id_usuario
        )
        SELECT 
            vd.id_producto,
            3, -- DEVOLUCION (assuming tipo_movimiento 3 es DEVOLUCION)
            vd.cantidad,
            p.stock_actual - vd.cantidad, -- stock antes de la suma
            p.stock_actual,
            'Venta anulada - stock restaurado',
            CONCAT('Anulacion_', NEW.numero_comprobante),
            NEW.id_usuario
        FROM venta_detalle vd
        INNER JOIN producto p ON vd.id_producto = p.id_producto
        WHERE vd.id_venta = NEW.id_venta 
          AND p.tipo_producto = 'FISICO';
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `venta_detalle`
--

DROP TABLE IF EXISTS `venta_detalle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `venta_detalle` (
  `id_venta` int NOT NULL,
  `id_producto` int NOT NULL,
  `cantidad` int NOT NULL,
  `precio_original` decimal(10,2) NOT NULL,
  `descuento_unitario` decimal(10,2) DEFAULT '0.00',
  `precio_final` decimal(10,2) NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  `id_promocion` int DEFAULT NULL,
  `observaciones` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id_venta`,`id_producto`),
  KEY `id_producto` (`id_producto`),
  KEY `id_promocion` (`id_promocion`),
  CONSTRAINT `venta_detalle_ibfk_1` FOREIGN KEY (`id_venta`) REFERENCES `venta` (`id_venta`) ON DELETE CASCADE,
  CONSTRAINT `venta_detalle_ibfk_2` FOREIGN KEY (`id_producto`) REFERENCES `producto` (`id_producto`),
  CONSTRAINT `venta_detalle_ibfk_3` FOREIGN KEY (`id_promocion`) REFERENCES `promocion` (`id_promocion`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `venta_detalle`
--

LOCK TABLES `venta_detalle` WRITE;
/*!40000 ALTER TABLE `venta_detalle` DISABLE KEYS */;
INSERT INTO `venta_detalle` VALUES (1,1,2,1500.00,0.00,1500.00,3000.00,NULL,NULL),(7,1,3,1500.00,0.00,1500.00,4500.00,NULL,NULL),(7,7,33,250.00,0.00,250.00,8250.00,NULL,NULL),(8,2,2,800.00,0.00,800.00,1600.00,NULL,NULL),(8,7,5,250.00,0.00,250.00,1250.00,NULL,NULL),(10,11,5,30.00,0.00,30.00,150.00,NULL,NULL),(10,12,6,100.00,0.00,100.00,600.00,NULL,NULL),(12,5,4,500.00,0.00,500.00,2000.00,NULL,NULL),(12,7,4,250.00,0.00,250.00,1000.00,NULL,NULL),(12,11,7,30.00,0.00,30.00,210.00,NULL,NULL),(13,8,4,2500.00,0.00,2500.00,10000.00,NULL,NULL),(13,9,1,50.00,0.00,50.00,50.00,NULL,NULL),(14,8,3,2500.00,0.00,2500.00,7500.00,NULL,NULL),(16,1,1,1500.00,0.00,1500.00,1500.00,NULL,NULL),(17,1,1,1500.00,0.00,1500.00,1500.00,NULL,NULL),(18,6,6,350.00,0.00,350.00,2100.00,NULL,NULL),(19,6,3,350.00,0.00,350.00,1050.00,NULL,NULL),(20,5,2,500.00,0.00,500.00,1000.00,NULL,NULL),(23,5,6,500.00,0.00,500.00,3000.00,NULL,NULL),(24,13,2,800.00,0.00,800.00,1600.00,NULL,NULL),(25,1,1,1500.00,0.00,1500.00,1500.00,NULL,NULL),(26,5,4,500.00,0.00,500.00,2000.00,NULL,NULL),(26,10,3,150.00,0.00,150.00,450.00,NULL,NULL),(29,4,4,1200.00,0.00,1200.00,4800.00,NULL,NULL),(29,8,4,2500.00,0.00,2500.00,10000.00,NULL,NULL),(29,11,3,30.00,0.00,30.00,90.00,NULL,NULL),(30,4,2,1200.00,0.00,1200.00,2400.00,NULL,NULL),(31,4,2,1200.00,0.00,1200.00,2400.00,NULL,NULL),(31,5,2,500.00,0.00,500.00,1000.00,NULL,NULL),(32,5,1,500.00,0.00,500.00,500.00,NULL,NULL),(32,6,4,350.00,0.00,350.00,1400.00,NULL,NULL),(32,8,3,2500.00,0.00,2500.00,7500.00,NULL,NULL),(33,6,2,350.00,0.00,350.00,700.00,NULL,NULL),(38,6,8,350.00,0.00,350.00,2800.00,NULL,NULL),(46,5,6,500.00,0.00,500.00,3000.00,NULL,NULL),(46,8,4,2500.00,0.00,2500.00,10000.00,NULL,NULL),(46,11,3,30.00,0.00,30.00,90.00,NULL,NULL),(48,1,1,1500.00,0.00,1500.00,1500.00,NULL,NULL),(49,5,3,500.00,0.00,500.00,1500.00,NULL,NULL),(49,6,2,350.00,0.00,350.00,700.00,NULL,NULL),(50,5,2,500.00,0.00,500.00,1000.00,NULL,NULL),(50,6,2,350.00,0.00,350.00,700.00,NULL,NULL),(51,4,1,1200.00,0.00,1200.00,1200.00,NULL,NULL),(51,5,1,500.00,0.00,500.00,500.00,NULL,NULL),(52,4,1,1200.00,0.00,1200.00,1200.00,NULL,NULL),(52,5,1,500.00,0.00,500.00,500.00,NULL,NULL),(54,4,2,1200.00,0.00,1200.00,2400.00,NULL,NULL),(54,7,6,250.00,0.00,250.00,1500.00,NULL,NULL),(60,5,3,500.00,0.00,500.00,1500.00,NULL,NULL),(60,6,3,350.00,0.00,350.00,1050.00,NULL,NULL),(60,8,3,2500.00,0.00,2500.00,7500.00,NULL,NULL),(62,5,4,500.00,0.00,500.00,2000.00,NULL,NULL),(62,12,4,100.00,0.00,100.00,400.00,NULL,NULL),(62,13,4,800.00,0.00,800.00,3200.00,NULL,NULL),(63,6,3,350.00,0.00,350.00,1050.00,NULL,NULL),(63,16,56,200.00,0.00,200.00,11200.00,NULL,NULL),(66,5,3,500.00,0.00,500.00,1500.00,NULL,NULL),(66,7,5,250.00,0.00,250.00,1250.00,NULL,NULL),(66,9,4,50.00,0.00,50.00,200.00,NULL,NULL),(66,18,3,700.00,0.00,700.00,2100.00,NULL,NULL),(67,5,7,500.00,0.00,500.00,3500.00,NULL,NULL),(67,12,3,100.00,0.00,100.00,300.00,NULL,NULL),(70,6,6,350.00,0.00,350.00,2100.00,NULL,NULL),(70,7,6,250.00,0.00,250.00,1500.00,NULL,NULL),(70,12,3,100.00,0.00,100.00,300.00,NULL,NULL),(71,6,4,350.00,0.00,350.00,1400.00,NULL,NULL),(71,9,3,50.00,0.00,50.00,150.00,NULL,NULL),(71,12,4,100.00,0.00,100.00,400.00,NULL,NULL),(72,8,4,2500.00,0.00,2500.00,10000.00,NULL,NULL),(72,15,4,1200.00,0.00,1200.00,4800.00,NULL,NULL),(75,5,2,500.00,0.00,500.00,1000.00,NULL,NULL),(77,5,3,500.00,0.00,500.00,1500.00,NULL,NULL),(79,7,5,250.00,0.00,250.00,1250.00,NULL,NULL),(79,9,5,50.00,0.00,50.00,250.00,NULL,NULL),(81,7,3,250.00,0.00,250.00,750.00,NULL,NULL);
/*!40000 ALTER TABLE `venta_detalle` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`juanfruh`@`localhost`*/ /*!50003 TRIGGER `tr_actualizar_stock_venta` AFTER INSERT ON `venta_detalle` FOR EACH ROW BEGIN
    DECLARE v_tipo_producto VARCHAR(10);
    
    -- Obtener tipo de producto
    SELECT tipo_producto INTO v_tipo_producto
    FROM producto WHERE id_producto = NEW.id_producto;
    
    -- Solo actualizar stock si es producto físico
    IF v_tipo_producto = 'FISICO' THEN
        -- Verificar que hay stock suficiente
        IF (SELECT stock_actual FROM producto WHERE id_producto = NEW.id_producto) >= NEW.cantidad THEN
            UPDATE producto 
            SET stock_actual = stock_actual - NEW.cantidad,
                fecha_ultima_venta = NOW()
            WHERE id_producto = NEW.id_producto;
            
            -- Registrar movimiento de stock
            INSERT INTO movimiento (
                id_producto, id_tipo_movimiento, cantidad, 
                stock_anterior, stock_nuevo, motivo, referencia, id_usuario
            ) SELECT 
                NEW.id_producto, 
                2, -- VENTA (asuming tipo_movimiento 2 es VENTA)
                NEW.cantidad,
                stock_actual + NEW.cantidad, -- stock antes de la resta
                stock_actual,
                'Venta en proceso',
                CONCAT('Venta_', (SELECT numero_comprobante FROM venta WHERE id_venta = NEW.id_venta)),
                (SELECT id_usuario FROM venta WHERE id_venta = NEW.id_venta)
            FROM producto WHERE id_producto = NEW.id_producto;
        END IF;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`juanfruh`@`localhost`*/ /*!50003 TRIGGER `tr_actualizar_totales_venta` AFTER INSERT ON `venta_detalle` FOR EACH ROW BEGIN
    CALL SP_ActualizarTotalesVenta(NEW.id_venta);
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`juanfruh`@`localhost`*/ /*!50003 TRIGGER `tr_actualizar_totales_venta_update` AFTER UPDATE ON `venta_detalle` FOR EACH ROW BEGIN
    CALL SP_ActualizarTotalesVenta(NEW.id_venta);
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`juanfruh`@`localhost`*/ /*!50003 TRIGGER `tr_actualizar_stock_venta_update` AFTER UPDATE ON `venta_detalle` FOR EACH ROW BEGIN
    DECLARE v_tipo_producto VARCHAR(10);
    DECLARE v_diferencia_cantidad INT;
    
    -- Solo procesar si cambió la cantidad
    IF OLD.cantidad != NEW.cantidad THEN
        SELECT tipo_producto INTO v_tipo_producto
        FROM producto WHERE id_producto = NEW.id_producto;
        
        -- Solo para productos físicos
        IF v_tipo_producto = 'FISICO' THEN
            -- Calcular diferencia (positiva = más cantidad, negativa = menos cantidad)
            SET v_diferencia_cantidad = NEW.cantidad - OLD.cantidad;
            
            -- Ajustar stock (restar la diferencia)
            UPDATE producto 
            SET stock_actual = stock_actual - v_diferencia_cantidad,
                fecha_ultima_venta = NOW()
            WHERE id_producto = NEW.id_producto;
        END IF;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`juanfruh`@`localhost`*/ /*!50003 TRIGGER `tr_actualizar_totales_venta_delete` AFTER DELETE ON `venta_detalle` FOR EACH ROW BEGIN
    CALL SP_ActualizarTotalesVenta(OLD.id_venta);
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`juanfruh`@`localhost`*/ /*!50003 TRIGGER `tr_restaurar_stock_eliminar_carrito` AFTER DELETE ON `venta_detalle` FOR EACH ROW BEGIN
    DECLARE v_tipo_producto VARCHAR(10);
    DECLARE v_estado_venta VARCHAR(15);
    
    -- Verificar estado de la venta
    SELECT estado INTO v_estado_venta
    FROM venta WHERE id_venta = OLD.id_venta;
    
    -- Solo restaurar si la venta está EN_PROCESO
    IF v_estado_venta = 'EN_PROCESO' THEN
        SELECT tipo_producto INTO v_tipo_producto
        FROM producto WHERE id_producto = OLD.id_producto;
        
        -- Solo para productos físicos
        IF v_tipo_producto = 'FISICO' THEN
            UPDATE producto 
            SET stock_actual = stock_actual + OLD.cantidad
            WHERE id_producto = OLD.id_producto;
        END IF;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Temporary view structure for view `vw_detalle_carrito`
--

DROP TABLE IF EXISTS `vw_detalle_carrito`;
/*!50001 DROP VIEW IF EXISTS `vw_detalle_carrito`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_detalle_carrito` AS SELECT 
 1 AS `id_venta`,
 1 AS `codigo`,
 1 AS `nombre`,
 1 AS `cantidad`,
 1 AS `precio`,
 1 AS `subtotal`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `vw_productos_carrito`
--

DROP TABLE IF EXISTS `vw_productos_carrito`;
/*!50001 DROP VIEW IF EXISTS `vw_productos_carrito`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_productos_carrito` AS SELECT 
 1 AS `codigo`,
 1 AS `nombre`,
 1 AS `precio`,
 1 AS `stock`,
 1 AS `tipo_producto`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `vw_productos_stock_bajo`
--

DROP TABLE IF EXISTS `vw_productos_stock_bajo`;
/*!50001 DROP VIEW IF EXISTS `vw_productos_stock_bajo`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_productos_stock_bajo` AS SELECT 
 1 AS `codigo_barras`,
 1 AS `nombre`,
 1 AS `stock_actual`,
 1 AS `stock_minimo`,
 1 AS `precio_venta`,
 1 AS `estado_stock`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `vw_resumen_ventas`
--

DROP TABLE IF EXISTS `vw_resumen_ventas`;
/*!50001 DROP VIEW IF EXISTS `vw_resumen_ventas`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_resumen_ventas` AS SELECT 
 1 AS `id_venta`,
 1 AS `numero_comprobante`,
 1 AS `fecha`,
 1 AS `vendedor`,
 1 AS `cliente`,
 1 AS `subtotal`,
 1 AS `total`,
 1 AS `estado`,
 1 AS `items`,
 1 AS `productos_vendidos`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `vw_totales_venta`
--

DROP TABLE IF EXISTS `vw_totales_venta`;
/*!50001 DROP VIEW IF EXISTS `vw_totales_venta`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_totales_venta` AS SELECT 
 1 AS `id_venta`,
 1 AS `numero_comprobante`,
 1 AS `estado`,
 1 AS `fecha`,
 1 AS `subtotal_calculado`,
 1 AS `subtotal_guardado`,
 1 AS `descuento_productos`,
 1 AS `descuento_venta`,
 1 AS `total_guardado`,
 1 AS `cantidad_items`,
 1 AS `cantidad_productos`*/;
SET character_set_client = @saved_cs_client;

--
-- Dumping events for database 'libreria_papelitos'
--

--
-- Dumping routines for database 'libreria_papelitos'
--
/*!50003 DROP PROCEDURE IF EXISTS `SP_ActualizarTotalesVenta` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`juanfruh`@`localhost` PROCEDURE `SP_ActualizarTotalesVenta`(
    IN p_id_venta INT
)
BEGIN
    DECLARE v_subtotal DECIMAL(10,2) DEFAULT 0.00;
    DECLARE v_total_items INT DEFAULT 0;
    
    -- Calcular subtotal y cantidad de items
    SELECT 
        COALESCE(SUM(subtotal), 0.00),
        COALESCE(COUNT(*), 0)
    INTO v_subtotal, v_total_items
    FROM venta_detalle 
    WHERE id_venta = p_id_venta;
    
    -- Actualizar venta (por ahora sin descuentos)
    UPDATE venta 
    SET subtotal = v_subtotal,
        total = v_subtotal
    WHERE id_venta = p_id_venta;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SP_AgregarProductoPorCodigo` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`juanfruh`@`localhost` PROCEDURE `SP_AgregarProductoPorCodigo`(
    IN p_id_venta INT,
    IN p_codigo_barras VARCHAR(50),
    IN p_cantidad INT,
    OUT p_success BOOLEAN,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    DECLARE v_id_producto INT DEFAULT NULL;
    DECLARE v_stock_actual INT DEFAULT 0;
    DECLARE v_precio_venta DECIMAL(10,2) DEFAULT 0;
    DECLARE v_tipo_producto VARCHAR(10) DEFAULT '';
    DECLARE v_nombre VARCHAR(200) DEFAULT '';
    DECLARE v_cantidad_actual INT DEFAULT NULL;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_success = FALSE;
        GET DIAGNOSTICS CONDITION 1 p_mensaje = MESSAGE_TEXT;
    END;
    
    START TRANSACTION;
    
    -- DEBUG: Buscar producto
    SELECT id_producto, stock_actual, precio_venta, tipo_producto, nombre
    INTO v_id_producto, v_stock_actual, v_precio_venta, v_tipo_producto, v_nombre
    FROM producto 
    WHERE codigo_barras = p_codigo_barras AND activo = TRUE
    LIMIT 1;
    
    -- Verificaciones paso a paso
    IF v_id_producto IS NULL THEN
        SET p_success = FALSE;
        SET p_mensaje = CONCAT('Producto no encontrado con código: ', p_codigo_barras);
        ROLLBACK;
    ELSEIF v_tipo_producto = 'FISICO' AND v_stock_actual < p_cantidad THEN
        SET p_success = FALSE;
        SET p_mensaje = CONCAT('Stock insuficiente. Disponible: ', v_stock_actual, ' - Solicitado: ', p_cantidad);
        ROLLBACK;
    ELSE
        -- Verificar si ya existe en el carrito
        SELECT cantidad INTO v_cantidad_actual
        FROM venta_detalle 
        WHERE id_venta = p_id_venta AND id_producto = v_id_producto;
        
        IF v_cantidad_actual IS NOT NULL THEN
            -- Actualizar existente
            UPDATE venta_detalle 
            SET cantidad = v_cantidad_actual + p_cantidad,
                subtotal = (v_cantidad_actual + p_cantidad) * precio_final
            WHERE id_venta = p_id_venta AND id_producto = v_id_producto;
            
            SET p_mensaje = CONCAT('Cantidad actualizada para: ', v_nombre, ' (Total: ', v_cantidad_actual + p_cantidad, ')');
        ELSE
            -- Insertar nuevo
            INSERT INTO venta_detalle (
                id_venta, 
                id_producto, 
                cantidad, 
                precio_original, 
                descuento_unitario,
                precio_final, 
                subtotal
            ) VALUES (
                p_id_venta, 
                v_id_producto, 
                p_cantidad,
                v_precio_venta,
                0.00,
                v_precio_venta, 
                p_cantidad * v_precio_venta
            );
            
            SET p_mensaje = CONCAT('Producto agregado: ', v_nombre, ' (Cantidad: ', p_cantidad, ')');
        END IF;
        
        -- Actualizar totales
        UPDATE venta 
        SET subtotal = (SELECT COALESCE(SUM(subtotal), 0) FROM venta_detalle WHERE id_venta = p_id_venta),
            total = (SELECT COALESCE(SUM(subtotal), 0) FROM venta_detalle WHERE id_venta = p_id_venta)
        WHERE id_venta = p_id_venta;
        
        SET p_success = TRUE;
        COMMIT;
    END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SP_AnularVenta` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`juanfruh`@`localhost` PROCEDURE `SP_AnularVenta`(
    IN p_id_venta INT,
    OUT p_success BOOLEAN,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    DECLARE v_estado_actual VARCHAR(15);
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_success = FALSE;
        GET DIAGNOSTICS CONDITION 1 p_mensaje = MESSAGE_TEXT;
    END;
    
    START TRANSACTION;
    
    -- Verificar estado actual
    SELECT estado INTO v_estado_actual 
    FROM venta WHERE id_venta = p_id_venta;
    
    IF v_estado_actual IS NULL THEN
        SET p_success = FALSE;
        SET p_mensaje = 'Venta no encontrada';
        ROLLBACK;
    ELSEIF v_estado_actual = 'ANULADA' THEN
        SET p_success = FALSE;
        SET p_mensaje = 'La venta ya está anulada';
        ROLLBACK;
    ELSE
        -- Anular venta (el trigger se encarga de restaurar stock)
        UPDATE venta 
        SET estado = 'ANULADA' 
        WHERE id_venta = p_id_venta;
        
        SET p_success = TRUE;
        SET p_mensaje = 'Venta anulada exitosamente';
        COMMIT;
    END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SP_BuscarProducto` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`juanfruh`@`localhost` PROCEDURE `SP_BuscarProducto`(
    IN p_query VARCHAR(100)
)
BEGIN
    SELECT 
        p.codigo_barras as codigo,
        p.nombre,
        p.precio_venta as precio,
        CASE 
            WHEN p.tipo_producto = 'FISICO' THEN p.stock_actual
            ELSE 999
        END as stock,
        p.tipo_producto
    FROM producto p
    WHERE p.activo = TRUE 
      AND (p.codigo_barras = p_query 
           OR p.codigo_interno = p_query
           OR p.nombre LIKE CONCAT('%', p_query, '%'))
    ORDER BY 
        CASE WHEN p.codigo_barras = p_query THEN 1
             WHEN p.codigo_interno = p_query THEN 2
             ELSE 3
        END,
        p.nombre
    LIMIT 20;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SP_CompletarVentaSimple` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`juanfruh`@`localhost` PROCEDURE `SP_CompletarVentaSimple`(
    IN p_id_venta INT,
    OUT p_success BOOLEAN,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    DECLARE v_total DECIMAL(10,2);
    DECLARE v_items_count INT DEFAULT 0;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_success = FALSE;
        GET DIAGNOSTICS CONDITION 1 p_mensaje = MESSAGE_TEXT;
    END;
    
    START TRANSACTION;
    
    -- Verificar que la venta tiene productos
    SELECT COUNT(*) INTO v_items_count
    FROM venta_detalle WHERE id_venta = p_id_venta;
    
    IF v_items_count = 0 THEN
        SET p_success = FALSE;
        SET p_mensaje = 'No se puede completar una venta sin productos';
        ROLLBACK;
    ELSE
        -- Actualizar totales
        CALL SP_ActualizarTotalesVenta(p_id_venta);
        
        -- Cambiar estado a completada
        UPDATE venta 
        SET estado = 'COMPLETADA' 
        WHERE id_venta = p_id_venta;
        
        SELECT total INTO v_total FROM venta WHERE id_venta = p_id_venta;
        
        SET p_success = TRUE;
        SET p_mensaje = CONCAT('Venta completada exitosamente. Total: $', FORMAT(v_total, 2));
        COMMIT;
    END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SP_CrearVentaSimple` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`juanfruh`@`localhost` PROCEDURE `SP_CrearVentaSimple`(
    OUT p_id_venta INT
)
BEGIN
    DECLARE v_numero_comprobante VARCHAR(20);
    DECLARE v_siguiente_numero INT DEFAULT 1;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_id_venta = -1;
    END;
    
    START TRANSACTION;
    
    -- Generar número de comprobante único
    SELECT COALESCE(MAX(CAST(SUBSTRING(numero_comprobante, 2) AS UNSIGNED)), 0) + 1 
    INTO v_siguiente_numero
    FROM venta 
    WHERE numero_comprobante LIKE 'V%';
    
    SET v_numero_comprobante = CONCAT('V', LPAD(v_siguiente_numero, 8, '0'));
    
    -- Crear venta con usuario ID 1 por defecto (puedes cambiarlo)
    INSERT INTO venta (
        numero_comprobante, 
        id_usuario, 
        id_metodo_de_pago,
        subtotal,
        total,
        estado
    ) VALUES (
        v_numero_comprobante, 
        1, -- Usuario por defecto
        1, -- Método de pago por defecto (efectivo)
        0.00,
        0.00,
        'EN_PROCESO'
    );
    
    SET p_id_venta = LAST_INSERT_ID();
    COMMIT;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SP_EliminarPorCodigo` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`juanfruh`@`localhost` PROCEDURE `SP_EliminarPorCodigo`(
    IN p_id_venta INT,
    IN p_codigo_barras VARCHAR(50),
    OUT p_success BOOLEAN,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    DECLARE v_id_producto INT;
    DECLARE v_nombre VARCHAR(200);
    DECLARE v_cantidad_eliminada INT;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_success = FALSE;
        GET DIAGNOSTICS CONDITION 1 p_mensaje = MESSAGE_TEXT;
    END;
    
    START TRANSACTION;
    
    -- Obtener id y nombre del producto
    SELECT id_producto, nombre INTO v_id_producto, v_nombre
    FROM producto WHERE codigo_barras = p_codigo_barras AND activo = TRUE;
    
    IF v_id_producto IS NULL THEN
        SET p_success = FALSE;
        SET p_mensaje = 'Producto no encontrado';
        ROLLBACK;
    ELSE
        -- Obtener cantidad antes de eliminar (para restaurar stock si es físico)
        SELECT cantidad INTO v_cantidad_eliminada
        FROM venta_detalle 
        WHERE id_venta = p_id_venta AND id_producto = v_id_producto;
        
        IF v_cantidad_eliminada IS NULL THEN
            SET p_success = FALSE;
            SET p_mensaje = 'Producto no estaba en el carrito';
            ROLLBACK;
        ELSE
            -- Eliminar del carrito
            DELETE FROM venta_detalle 
            WHERE id_venta = p_id_venta AND id_producto = v_id_producto;
            
            -- Actualizar totales
            CALL SP_ActualizarTotalesVenta(p_id_venta);
            
            SET p_success = TRUE;
            SET p_mensaje = CONCAT('Eliminado: ', v_nombre);
            COMMIT;
        END IF;
    END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SP_LimpiarCarrito` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`juanfruh`@`localhost` PROCEDURE `SP_LimpiarCarrito`(
    IN p_id_venta INT,
    OUT p_success BOOLEAN,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    DECLARE v_items_count INT DEFAULT 0;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_success = FALSE;
        GET DIAGNOSTICS CONDITION 1 p_mensaje = MESSAGE_TEXT;
    END;
    
    START TRANSACTION;
    
    SELECT COUNT(*) INTO v_items_count
    FROM venta_detalle WHERE id_venta = p_id_venta;
    
    IF v_items_count = 0 THEN
        SET p_success = FALSE;
        SET p_mensaje = 'El carrito ya está vacío';
        ROLLBACK;
    ELSE
        -- Eliminar todos los items del carrito
        DELETE FROM venta_detalle WHERE id_venta = p_id_venta;
        
        -- Actualizar totales a 0
        UPDATE venta 
        SET subtotal = 0.00, total = 0.00 
        WHERE id_venta = p_id_venta;
        
        SET p_success = TRUE;
        SET p_mensaje = CONCAT('Carrito limpiado. ', v_items_count, ' items eliminados');
        COMMIT;
    END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SP_LimpiarVentasEnProceso` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`juanfruh`@`localhost` PROCEDURE `SP_LimpiarVentasEnProceso`(
    IN p_dias_antiguedad INT,
    OUT p_ventas_eliminadas INT,
    OUT p_mensaje VARCHAR(200)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        SET p_ventas_eliminadas = 0;
        SET p_mensaje = 'Error al limpiar ventas';
        ROLLBACK;
    END;
    
    START TRANSACTION;
    
    -- Contar ventas a eliminar
    SELECT COUNT(*) INTO p_ventas_eliminadas
    FROM venta
    WHERE estado = 'EN_PROCESO'
      AND fecha < DATE_SUB(NOW(), INTERVAL p_dias_antiguedad DAY);
    
    -- Restaurar stock de productos físicos
    UPDATE producto p
    INNER JOIN venta_detalle vd ON p.id_producto = vd.id_producto
    INNER JOIN venta v ON vd.id_venta = v.id_venta
    SET p.stock_actual = p.stock_actual + vd.cantidad
    WHERE v.estado = 'EN_PROCESO'
      AND v.fecha < DATE_SUB(NOW(), INTERVAL p_dias_antiguedad DAY)
      AND p.tipo_producto = 'FISICO';
    
    -- Eliminar detalles
    DELETE vd FROM venta_detalle vd
    INNER JOIN venta v ON vd.id_venta = v.id_venta
    WHERE v.estado = 'EN_PROCESO'
      AND v.fecha < DATE_SUB(NOW(), INTERVAL p_dias_antiguedad DAY);
    
    -- Eliminar ventas
    DELETE FROM venta
    WHERE estado = 'EN_PROCESO'
      AND fecha < DATE_SUB(NOW(), INTERVAL p_dias_antiguedad DAY);
    
    COMMIT;
    
    SET p_mensaje = CONCAT('Se eliminaron ', p_ventas_eliminadas, ' ventas en proceso antiguas');
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SP_ModificarCantidadPorCodigo` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`juanfruh`@`localhost` PROCEDURE `SP_ModificarCantidadPorCodigo`(
    IN p_id_venta INT,
    IN p_codigo_barras VARCHAR(50),
    IN p_nueva_cantidad INT,
    OUT p_success BOOLEAN,
    OUT p_mensaje VARCHAR(255)
)
BEGIN
    DECLARE v_id_producto INT;
    DECLARE v_stock_disponible INT;
    DECLARE v_tipo_producto VARCHAR(10);
    DECLARE v_precio_final DECIMAL(10,2);
    DECLARE v_nombre VARCHAR(200);
    DECLARE v_cantidad_actual INT;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_success = FALSE;
        GET DIAGNOSTICS CONDITION 1 p_mensaje = MESSAGE_TEXT;
    END;
    
    START TRANSACTION;
    
    -- Obtener datos del producto
    SELECT id_producto, stock_actual, tipo_producto, nombre
    INTO v_id_producto, v_stock_disponible, v_tipo_producto, v_nombre
    FROM producto WHERE codigo_barras = p_codigo_barras AND activo = TRUE;
    
    IF v_id_producto IS NULL THEN
        SET p_success = FALSE;
        SET p_mensaje = 'Producto no encontrado';
        ROLLBACK;
    ELSEIF p_nueva_cantidad <= 0 THEN
        SET p_success = FALSE;
        SET p_mensaje = 'La cantidad debe ser mayor a 0';
        ROLLBACK;
    ELSEIF v_tipo_producto = 'FISICO' AND p_nueva_cantidad > v_stock_disponible THEN
        SET p_success = FALSE;
        SET p_mensaje = CONCAT('Stock insuficiente. Disponible: ', v_stock_disponible);
        ROLLBACK;
    ELSE
        -- Obtener precio y cantidad actual en el carrito
        SELECT precio_final, cantidad 
        INTO v_precio_final, v_cantidad_actual
        FROM venta_detalle 
        WHERE id_venta = p_id_venta AND id_producto = v_id_producto;
        
        IF v_precio_final IS NULL THEN
            SET p_success = FALSE;
            SET p_mensaje = 'Producto no está en el carrito';
            ROLLBACK;
        ELSE
            -- Actualizar cantidad
            UPDATE venta_detalle 
            SET cantidad = p_nueva_cantidad,
                subtotal = p_nueva_cantidad * v_precio_final
            WHERE id_venta = p_id_venta AND id_producto = v_id_producto;
            
            -- Actualizar totales
            CALL SP_ActualizarTotalesVenta(p_id_venta);
            
            SET p_success = TRUE;
            SET p_mensaje = CONCAT('Cantidad actualizada: ', v_nombre, ' (', p_nueva_cantidad, ')');
            COMMIT;
        END IF;
    END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SP_ObtenerResumenVenta` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`juanfruh`@`localhost` PROCEDURE `SP_ObtenerResumenVenta`(
    IN p_id_venta INT
)
BEGIN
    -- Datos de la cabecera
    SELECT 
        v.numero_comprobante,
        v.fecha,
        v.subtotal,
        v.descuento_productos,
        v.descuento_venta,
        v.total,
        CONCAT(u.nombre, ' ', u.apellido) as vendedor,
        COALESCE(c.nombre, 'Cliente General') as cliente
    FROM venta v
    INNER JOIN usuario u ON v.id_usuario = u.id_usuario
    LEFT JOIN cliente c ON v.id_cliente = c.id_cliente
    WHERE v.id_venta = p_id_venta;
    
    -- Detalles de productos
    SELECT 
        p.codigo_barras as codigo,
        p.nombre as producto,
        vd.cantidad,
        vd.precio_original as precio_unitario,
        vd.descuento_unitario,
        vd.precio_final,
        vd.subtotal
    FROM venta_detalle vd
    INNER JOIN producto p ON vd.id_producto = p.id_producto
    WHERE vd.id_venta = p_id_venta
    ORDER BY p.nombre;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SP_ObtenerVentasPaginadas` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`juanfruh`@`localhost` PROCEDURE `SP_ObtenerVentasPaginadas`(
    IN p_pagina INT,                    -- Número de página (1, 2, 3...)
    IN p_registros_por_pagina INT,      -- Cuántos por página (20)
    IN p_busqueda VARCHAR(100),          -- Buscar por número de comprobante
    IN p_fecha_desde DATE,               -- Filtro desde fecha
    IN p_fecha_hasta DATE,               -- Filtro hasta fecha
    IN p_id_cliente INT,                 -- Filtro por cliente
    IN p_estado VARCHAR(20),             -- Filtro por estado
    OUT p_total_registros INT            -- Total para calcular páginas
)
BEGIN
    -- CONCEPTO: OFFSET calcula desde dónde empezar
    -- Ejemplo: Página 3 con 20 por página → OFFSET = (3-1)*20 = 40
    DECLARE v_offset INT;
    SET v_offset = (p_pagina - 1) * p_registros_por_pagina;
    
    -- PASO 1: Contar total de registros que cumplen los filtros
    -- Esto es necesario para saber cuántas páginas hay
    SELECT COUNT(*) INTO p_total_registros
    FROM venta v
    LEFT JOIN cliente c ON v.id_cliente = c.id_cliente
    WHERE v.estado IN ('COMPLETADA', 'ANULADA')
        AND (p_busqueda IS NULL OR 
             v.numero_comprobante LIKE CONCAT('%', p_busqueda, '%'))
        AND (p_fecha_desde IS NULL OR DATE(v.fecha) >= p_fecha_desde)
        AND (p_fecha_hasta IS NULL OR DATE(v.fecha) <= p_fecha_hasta)
        AND (p_id_cliente IS NULL OR v.id_cliente = p_id_cliente)
        AND (p_estado IS NULL OR v.estado = p_estado);
    
    -- PASO 2: Obtener SOLO los registros de la página actual
    -- LIMIT = cuántos devolver
    -- OFFSET = cuántos saltar
    SELECT 
        v.id_venta,
        v.numero_comprobante,
        v.fecha,
        COALESCE(c.nombre, 'Sin cliente') AS cliente,
        v.total,
        v.estado,
        u.nombre_usuario AS vendedor
    FROM venta v
    LEFT JOIN cliente c ON v.id_cliente = c.id_cliente
    LEFT JOIN usuario u ON v.id_usuario = u.id_usuario
    WHERE v.estado IN ('COMPLETADA', 'ANULADA')
        AND (p_busqueda IS NULL OR 
             v.numero_comprobante LIKE CONCAT('%', p_busqueda, '%'))
        AND (p_fecha_desde IS NULL OR DATE(v.fecha) >= p_fecha_desde)
        AND (p_fecha_hasta IS NULL OR DATE(v.fecha) <= p_fecha_hasta)
        AND (p_id_cliente IS NULL OR v.id_cliente = p_id_cliente)
        AND (p_estado IS NULL OR v.estado = p_estado)
    ORDER BY v.fecha DESC  -- Más recientes primero
    LIMIT p_registros_por_pagina OFFSET v_offset;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Final view structure for view `vw_detalle_carrito`
--

/*!50001 DROP VIEW IF EXISTS `vw_detalle_carrito`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`juanfruh`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_detalle_carrito` AS select `vd`.`id_venta` AS `id_venta`,`p`.`codigo_barras` AS `codigo`,`p`.`nombre` AS `nombre`,`vd`.`cantidad` AS `cantidad`,`vd`.`precio_final` AS `precio`,`vd`.`subtotal` AS `subtotal` from (`venta_detalle` `vd` join `producto` `p` on((`vd`.`id_producto` = `p`.`id_producto`))) where (`p`.`codigo_barras` is not null) order by `p`.`nombre` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `vw_productos_carrito`
--

/*!50001 DROP VIEW IF EXISTS `vw_productos_carrito`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`juanfruh`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_productos_carrito` AS select `p`.`codigo_barras` AS `codigo`,`p`.`nombre` AS `nombre`,`p`.`precio_venta` AS `precio`,(case when (`p`.`tipo_producto` = 'FISICO') then `p`.`stock_actual` else 999 end) AS `stock`,`p`.`tipo_producto` AS `tipo_producto` from `producto` `p` where ((`p`.`activo` = true) and (`p`.`codigo_barras` is not null) and (`p`.`codigo_barras` <> '')) order by `p`.`nombre` limit 25 */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `vw_productos_stock_bajo`
--

/*!50001 DROP VIEW IF EXISTS `vw_productos_stock_bajo`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`juanfruh`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_productos_stock_bajo` AS select `p`.`codigo_barras` AS `codigo_barras`,`p`.`nombre` AS `nombre`,`p`.`stock_actual` AS `stock_actual`,`p`.`stock_minimo` AS `stock_minimo`,`p`.`precio_venta` AS `precio_venta`,(case when (`p`.`stock_actual` = 0) then 'SIN STOCK' when (`p`.`stock_actual` <= `p`.`stock_minimo`) then 'STOCK CRÍTICO' else 'STOCK BAJO' end) AS `estado_stock` from `producto` `p` where ((`p`.`activo` = true) and (`p`.`tipo_producto` = 'FISICO') and (`p`.`stock_actual` <= `p`.`stock_minimo`)) order by `p`.`stock_actual` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `vw_resumen_ventas`
--

/*!50001 DROP VIEW IF EXISTS `vw_resumen_ventas`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`juanfruh`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_resumen_ventas` AS select `v`.`id_venta` AS `id_venta`,`v`.`numero_comprobante` AS `numero_comprobante`,`v`.`fecha` AS `fecha`,concat(`u`.`nombre`,' ',`u`.`apellido`) AS `vendedor`,coalesce(`c`.`nombre`,'Cliente General') AS `cliente`,`v`.`subtotal` AS `subtotal`,`v`.`total` AS `total`,`v`.`estado` AS `estado`,count(`vd`.`id_producto`) AS `items`,sum(`vd`.`cantidad`) AS `productos_vendidos` from (((`venta` `v` join `usuario` `u` on((`v`.`id_usuario` = `u`.`id_usuario`))) left join `cliente` `c` on((`v`.`id_cliente` = `c`.`id_cliente`))) left join `venta_detalle` `vd` on((`v`.`id_venta` = `vd`.`id_venta`))) group by `v`.`id_venta`,`v`.`numero_comprobante`,`v`.`fecha`,`v`.`subtotal`,`v`.`total`,`v`.`estado`,`u`.`nombre`,`u`.`apellido`,`c`.`nombre` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `vw_totales_venta`
--

/*!50001 DROP VIEW IF EXISTS `vw_totales_venta`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`juanfruh`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_totales_venta` AS select `v`.`id_venta` AS `id_venta`,`v`.`numero_comprobante` AS `numero_comprobante`,`v`.`estado` AS `estado`,`v`.`fecha` AS `fecha`,coalesce(sum(`vd`.`subtotal`),0) AS `subtotal_calculado`,`v`.`subtotal` AS `subtotal_guardado`,`v`.`descuento_productos` AS `descuento_productos`,`v`.`descuento_venta` AS `descuento_venta`,`v`.`total` AS `total_guardado`,count(`vd`.`id_producto`) AS `cantidad_items`,coalesce(sum(`vd`.`cantidad`),0) AS `cantidad_productos` from (`venta` `v` left join `venta_detalle` `vd` on((`v`.`id_venta` = `vd`.`id_venta`))) group by `v`.`id_venta`,`v`.`numero_comprobante`,`v`.`estado`,`v`.`fecha`,`v`.`subtotal`,`v`.`descuento_productos`,`v`.`descuento_venta`,`v`.`total` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-22 14:13:44
