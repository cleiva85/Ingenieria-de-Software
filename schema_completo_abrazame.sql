-- ============================================================
-- SCHEMA COMPLETO — Sistema de Gestión de Donaciones
-- Fundación Abrázame
-- Ejecutar en phpMyAdmin o MySQL directamente
-- ============================================================

-- ────────────────────────────────────────────────────────────
-- 1. BASE DE DATOS: db_auth (service-auth, puerto 8081)
-- ────────────────────────────────────────────────────────────
CREATE DATABASE IF NOT EXISTS db_auth CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE db_auth;

CREATE TABLE IF NOT EXISTS usuarios (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre            VARCHAR(100)  NOT NULL,
    nombre2           VARCHAR(100),
    apellido_p        VARCHAR(100)  NOT NULL,
    apellido_m        VARCHAR(100)  NOT NULL,
    rut               VARCHAR(15)   UNIQUE,
    fecha_nacimiento  DATE,
    telefono          VARCHAR(20),
    email             VARCHAR(150)  NOT NULL UNIQUE,
    password_hash     VARCHAR(255),
    rol               ENUM('Donante','Voluntario','Admin','Director','SuperAdmin') NOT NULL,
    activo            TINYINT(1)    NOT NULL DEFAULT 0,
    intentos_fallidos INT           NOT NULL DEFAULT 0,
    bloqueado_hasta   DATETIME,
    fecha_registro    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    token_activacion  VARCHAR(255)  UNIQUE,
    token_expira      DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Credenciales: Admin1234!
INSERT IGNORE INTO usuarios (nombre, apellido_p, apellido_m, email, password_hash, rol, activo) VALUES
('Ana',   'Soto',    'Rojas',    'admin@abrazame.org',    '$2a$10$n8M2xJjwfIxRzF.oTd/z8OKk/IKRHwhh6hq4v0DEpa4vbR1Oa57Ty', 'Admin',      1),
('Luis',  'Ramirez', 'Vega',     'director@abrazame.org', '$2a$10$TDQbmvA2ttwOyfyeKrb3jeshIyCdqlHUKEJreH0FlO9YLMTLgNdxa', 'Director',   1),
('Sofia', 'Torres',  'Castillo', 'super@abrazame.org',    '$2a$10$aDtmztvTkCG0t.Yr4trIl.RXsNewv.tMk0FlOEDUc3ABdA3Ta7EzC', 'SuperAdmin', 1);

-- ────────────────────────────────────────────────────────────
-- 2. BASE DE DATOS: db_donante (service-donante, puerto 8082)
-- ────────────────────────────────────────────────────────────
CREATE DATABASE IF NOT EXISTS db_donante CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE db_donante;

CREATE TABLE IF NOT EXISTS donantes (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    rut                     VARCHAR(15) UNIQUE,
    primer_nombre           VARCHAR(100),
    segundo_nombre          VARCHAR(100),
    apellido_paterno        VARCHAR(100),
    apellido_materno        VARCHAR(100),
    correo_electronico      VARCHAR(150),
    telefono                VARCHAR(20),
    fecha_nacimiento        DATE,
    tipo_donante            VARCHAR(50) DEFAULT 'Persona Natural',
    rut_empresa             VARCHAR(15),
    razon_social            VARCHAR(200),
    fecha_registro          DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS voluntarios (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    rut                     VARCHAR(15) UNIQUE,
    primer_nombre           VARCHAR(100),
    segundo_nombre          VARCHAR(100),
    apellido_paterno        VARCHAR(100),
    apellido_materno        VARCHAR(100),
    correo_electronico      VARCHAR(150),
    telefono                VARCHAR(20),
    fecha_nacimiento        DATE,
    direccion_completa      VARCHAR(300),
    tipo_vivienda           VARCHAR(50),
    comuna                  VARCHAR(100),
    region                  VARCHAR(100),
    fecha_registro          DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS perfil_voluntario (
    id                          BIGINT AUTO_INCREMENT PRIMARY KEY,
    voluntario_id               BIGINT UNIQUE,
    dias_disponibles            VARCHAR(200),
    horario_disponible          VARCHAR(100),
    tipo_vivienda               VARCHAR(50),
    region                      VARCHAR(100),
    url_cv                      VARCHAR(500),
    url_certificado_antecedentes VARCHAR(500),
    FOREIGN KEY (voluntario_id) REFERENCES voluntarios(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ────────────────────────────────────────────────────────────
-- 3. BASE DE DATOS: db_catalogo (service-catalogo, puerto 8084)
-- ────────────────────────────────────────────────────────────
CREATE DATABASE IF NOT EXISTS db_catalogo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE db_catalogo;

CREATE TABLE IF NOT EXISTS articulos_catalogo (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(150),
    categoria    VARCHAR(100),
    subcategoria VARCHAR(100),
    rango_edad   VARCHAR(50),
    prioridad    VARCHAR(20),
    meta_stock   INT,
    stock_actual INT DEFAULT 0,
    activo       TINYINT(1) DEFAULT 1,
    descripcion  TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO articulos_catalogo (nombre, categoria, subcategoria, rango_edad, prioridad, meta_stock, stock_actual, descripcion) VALUES
-- ROPA
('Abrigos y chaquetas','Ropa','Abrigos','0-2 años','Alta',40,0,'Abrigos y chaquetas de abrigo para bebés en buen estado.'),
('Abrigos y chaquetas','Ropa','Abrigos','3-6 años','Alta',40,0,'Abrigos y chaquetas de abrigo para niños en buen estado.'),
('Abrigos y chaquetas','Ropa','Abrigos','7-12 años','Alta',40,0,'Abrigos y chaquetas de abrigo para niños mayores.'),
('Abrigos y chaquetas','Ropa','Abrigos','13-17 años','Alta',30,0,'Abrigos y chaquetas para adolescentes.'),
('Poleras y camisetas','Ropa','Poleras','0-2 años','Media',50,0,'Poleras y camisetas en buen estado para bebés.'),
('Poleras y camisetas','Ropa','Poleras','3-6 años','Media',50,0,'Poleras y camisetas en buen estado para niños.'),
('Poleras y camisetas','Ropa','Poleras','7-12 años','Media',50,0,'Poleras y camisetas para niños mayores.'),
('Poleras y camisetas','Ropa','Poleras','13-17 años','Media',40,0,'Poleras y camisetas para adolescentes.'),
('Pantalones y jeans','Ropa','Pantalones','0-2 años','Media',40,0,'Pantalones y jeans en buen estado para bebés.'),
('Pantalones y jeans','Ropa','Pantalones','3-6 años','Media',40,0,'Pantalones y jeans para niños.'),
('Pantalones y jeans','Ropa','Pantalones','7-12 años','Media',40,0,'Pantalones y jeans para niños mayores.'),
('Pantalones y jeans','Ropa','Pantalones','13-17 años','Media',30,0,'Pantalones y jeans para adolescentes.'),
('Pijamas','Ropa','Pijamas','0-2 años','Media',30,0,'Pijamas en buen estado para bebés.'),
('Pijamas','Ropa','Pijamas','3-6 años','Media',30,0,'Pijamas en buen estado para niños.'),
('Pijamas','Ropa','Pijamas','7-12 años','Media',30,0,'Pijamas para niños mayores.'),
('Pijamas','Ropa','Pijamas','13-17 años','Baja',20,0,'Pijamas para adolescentes.'),
('Ropa interior','Ropa','Ropa interior','0-2 años','Alta',60,0,'Ropa interior nueva para bebés.'),
('Ropa interior','Ropa','Ropa interior','3-6 años','Alta',60,0,'Ropa interior nueva para niños.'),
('Ropa interior','Ropa','Ropa interior','7-12 años','Alta',60,0,'Ropa interior nueva para niños mayores.'),
('Ropa interior','Ropa','Ropa interior','13-17 años','Alta',50,0,'Ropa interior nueva para adolescentes.'),
-- CALZADO
('Zapatillas deportivas','Calzado','Zapatillas','0-2 años','Alta',30,0,'Zapatillas deportivas en buen estado para bebés.'),
('Zapatillas deportivas','Calzado','Zapatillas','3-6 años','Alta',30,0,'Zapatillas deportivas para niños.'),
('Zapatillas deportivas','Calzado','Zapatillas','7-12 años','Alta',30,0,'Zapatillas deportivas para niños mayores.'),
('Zapatillas deportivas','Calzado','Zapatillas','13-17 años','Alta',25,0,'Zapatillas deportivas para adolescentes.'),
('Zapatos de colegio','Calzado','Zapatos','3-6 años','Alta',25,0,'Zapatos de colegio en buen estado.'),
('Zapatos de colegio','Calzado','Zapatos','7-12 años','Alta',25,0,'Zapatos de colegio para niños mayores.'),
('Zapatos de colegio','Calzado','Zapatos','13-17 años','Alta',20,0,'Zapatos de colegio para adolescentes.'),
('Botas y botines','Calzado','Botas','0-2 años','Media',20,0,'Botas y botines abrigadores para bebés.'),
('Botas y botines','Calzado','Botas','3-6 años','Media',20,0,'Botas y botines para niños.'),
('Botas y botines','Calzado','Botas','7-12 años','Media',20,0,'Botas y botines para niños mayores.'),
('Botas y botines','Calzado','Botas','13-17 años','Media',15,0,'Botas y botines para adolescentes.'),
('Calcetines','Calzado','Calcetines','0-2 años','Alta',80,0,'Calcetines nuevos para bebés.'),
('Calcetines','Calzado','Calcetines','3-6 años','Alta',80,0,'Calcetines nuevos para niños.'),
('Calcetines','Calzado','Calcetines','7-12 años','Alta',80,0,'Calcetines nuevos para niños mayores.'),
('Calcetines','Calzado','Calcetines','13-17 años','Alta',60,0,'Calcetines nuevos para adolescentes.'),
-- JUGUETES
('Juegos de mesa','Juguetes','Juegos de mesa','3-6 años','Media',20,0,'Juegos de mesa en buen estado para niños pequeños.'),
('Juegos de mesa','Juguetes','Juegos de mesa','7-12 años','Media',20,0,'Juegos de mesa para niños mayores.'),
('Juegos de mesa','Juguetes','Juegos de mesa','13-17 años','Baja',15,0,'Juegos de mesa para adolescentes.'),
('Peluches y munecas','Juguetes','Peluches','0-2 años','Alta',30,0,'Peluches y muñecos en buen estado para bebés.'),
('Peluches y munecas','Juguetes','Peluches','3-6 años','Alta',30,0,'Peluches y muñecos para niños.'),
('Rompecabezas','Juguetes','Rompecabezas','3-6 años','Media',25,0,'Rompecabezas educativos para niños.'),
('Rompecabezas','Juguetes','Rompecabezas','7-12 años','Media',25,0,'Rompecabezas para niños mayores.'),
('Juguetes didacticos','Juguetes','Didacticos','0-2 años','Alta',25,0,'Juguetes que estimulan el desarrollo para bebés.'),
('Juguetes didacticos','Juguetes','Didacticos','3-6 años','Alta',25,0,'Juguetes didácticos para niños.'),
('Juguetes de exterior','Juguetes','Exterior','3-6 años','Baja',15,0,'Pelotas, cuerdas y juguetes para el patio.'),
('Juguetes de exterior','Juguetes','Exterior','7-12 años','Baja',15,0,'Juguetes deportivos y de exterior.'),
('Legos y construccion','Juguetes','Construccion','3-6 años','Media',20,0,'Sets de construcción y legos en buen estado.'),
('Legos y construccion','Juguetes','Construccion','7-12 años','Media',20,0,'Legos y construcción para niños mayores.'),
-- UTILES ESCOLARES
('Cuadernos','Utiles escolares','Cuadernos','3-6 años','Alta',100,0,'Cuadernos nuevos para párvulos.'),
('Cuadernos','Utiles escolares','Cuadernos','7-12 años','Alta',100,0,'Cuadernos nuevos para escolares.'),
('Cuadernos','Utiles escolares','Cuadernos','13-17 años','Alta',80,0,'Cuadernos nuevos para adolescentes.'),
('Lapices y plumones','Utiles escolares','Lapices','3-6 años','Alta',80,0,'Lápices de colores y plumones para niños.'),
('Lapices y plumones','Utiles escolares','Lapices','7-12 años','Alta',80,0,'Set de lápices y plumones para escolares.'),
('Lapices y plumones','Utiles escolares','Lapices','13-17 años','Alta',60,0,'Lápices de pasta y destacadores para adolescentes.'),
('Mochilas','Utiles escolares','Mochilas','3-6 años','Alta',30,0,'Mochilas en buen estado para párvulos.'),
('Mochilas','Utiles escolares','Mochilas','7-12 años','Alta',30,0,'Mochilas en buen estado para escolares.'),
('Mochilas','Utiles escolares','Mochilas','13-17 años','Alta',25,0,'Mochilas para adolescentes.'),
('Estuches y cartucheras','Utiles escolares','Estuches','3-6 años','Media',40,0,'Estuches y cartucheras para niños.'),
('Estuches y cartucheras','Utiles escolares','Estuches','7-12 años','Media',40,0,'Estuches y cartucheras para escolares.'),
('Tijeras y pegamento','Utiles escolares','Materiales','3-6 años','Media',50,0,'Tijeras de punta roma y pegamento para niños.'),
('Tijeras y pegamento','Utiles escolares','Materiales','7-12 años','Media',50,0,'Materiales de arte y manualidades.'),
('Reglas y escuadras','Utiles escolares','Materiales','7-12 años','Baja',40,0,'Set de geometría para escolares.'),
('Reglas y escuadras','Utiles escolares','Materiales','13-17 años','Media',40,0,'Set de geometría para adolescentes.'),
-- LIBROS
('Cuentos ilustrados','Libros','Cuentos','0-2 años','Media',25,0,'Cuentos con imágenes para bebés y niños pequeños.'),
('Cuentos ilustrados','Libros','Cuentos','3-6 años','Media',25,0,'Cuentos e historias para niños.'),
('Novelas y lecturas','Libros','Novelas','7-12 años','Media',20,0,'Libros de lectura para escolares.'),
('Novelas y lecturas','Libros','Novelas','13-17 años','Media',20,0,'Novelas y libros para adolescentes.'),
('Libros educativos','Libros','Educativos','3-6 años','Media',20,0,'Libros de aprendizaje para preescolar.'),
('Libros educativos','Libros','Educativos','7-12 años','Alta',20,0,'Libros de estudio y apoyo escolar.'),
('Libros educativos','Libros','Educativos','13-17 años','Alta',15,0,'Material de estudio para enseñanza media.'),
-- HIGIENE
('Jabon y shampoo','Higiene','Aseo personal','0-2 años','Alta',60,0,'Jabón y shampoo para bebés (preferir nuevos).'),
('Jabon y shampoo','Higiene','Aseo personal','3-6 años','Alta',60,0,'Productos de aseo para niños.'),
('Jabon y shampoo','Higiene','Aseo personal','7-12 años','Alta',60,0,'Productos de aseo para niños mayores.'),
('Jabon y shampoo','Higiene','Aseo personal','13-17 años','Alta',50,0,'Productos de aseo para adolescentes.'),
('Cepillos y pasta dental','Higiene','Dental','3-6 años','Alta',60,0,'Cepillos de dientes y pasta dental para niños.'),
('Cepillos y pasta dental','Higiene','Dental','7-12 años','Alta',60,0,'Higiene dental para niños mayores.'),
('Cepillos y pasta dental','Higiene','Dental','13-17 años','Alta',50,0,'Higiene dental para adolescentes.'),
('Panales','Higiene','Panales','0-2 años','Alta',100,0,'Pañales talla N, P, M, G o XG (preferir nuevos).'),
('Toallas y panos','Higiene','Toallas','0-2 años','Media',30,0,'Toallas y paños de baño para bebés.'),
('Toallas y panos','Higiene','Toallas','3-6 años','Media',30,0,'Toallas de baño para niños.'),
('Desodorantes','Higiene','Desodorantes','13-17 años','Media',40,0,'Desodorantes para adolescentes.');

-- ────────────────────────────────────────────────────────────
-- 4. BASE DE DATOS: db_gestion_donaciones (service-gestion, puerto 8083)
-- ────────────────────────────────────────────────────────────
CREATE DATABASE IF NOT EXISTS db_gestion_donaciones CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE db_gestion_donaciones;

CREATE TABLE IF NOT EXISTS donaciones (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    don_id                VARCHAR(20),
    donante_id            BIGINT,
    nombre_donante        VARCHAR(200),
    tipo_donante          VARCHAR(50),
    articulo_nombre       VARCHAR(150),
    articulo_id           BIGINT,
    cantidad              INT,
    estado                VARCHAR(30) DEFAULT 'Pendiente',
    fecha_entrega         DATE,
    punto_entrega         VARCHAR(150),
    hora_estimada         VARCHAR(20),
    fecha_creacion        DATETIME DEFAULT CURRENT_TIMESTAMP,
    url_imagen            VARCHAR(500),
    url_video             VARCHAR(500),
    comentario_voluntario TEXT,
    email_donante         VARCHAR(150)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- FIN DEL SCHEMA
-- Credenciales de prueba:
--   Admin/Director/SuperAdmin -> contrasena: Admin1234!
--   Emails: admin@abrazame.org | director@abrazame.org | super@abrazame.org
-- ============================================================

-- ============================================================
-- ACTUALIZACIÓN v2.0 — Módulo Beneficiarios
-- Ejecutar DESPUÉS del schema base. Ver: beneficiarios_abrazame.sql
-- ============================================================
