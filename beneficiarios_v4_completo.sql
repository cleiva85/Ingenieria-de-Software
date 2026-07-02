-- ============================================================
-- SCRIPT COMPLETO v4: Base de Datos Gestión de Donaciones
-- Fundación Abrázame
--
-- Este script crea TODO desde cero:
--  1. La base de datos
--  2. La tabla de donaciones
--  3. La tabla de fundaciones/residencias colaboradoras
--  4. La tabla de beneficiarios (vinculados a esas fundaciones)
--  5. La tabla de tickets de aprobación del Director
--  6. Vistas y procedimientos de apoyo
--
-- IMPORTANTE: ejecutar este script COMPLETO de una sola vez
-- en phpMyAdmin > pestaña SQL (no usar "Importar" archivo a
-- archivo, copiar y pegar todo el contenido en el editor SQL).
-- ============================================================

-- ────────────────────────────────────────────────────────────
-- 0. CREAR LA BASE DE DATOS
-- ────────────────────────────────────────────────────────────
CREATE DATABASE IF NOT EXISTS db_gestion_donaciones
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE db_gestion_donaciones;

-- ────────────────────────────────────────────────────────────
-- 1. TABLA DONACIONES
--    (versión mínima compatible con el modelo Donacion.java
--    del proyecto Spring Boot — service-gestion:8083)
-- ────────────────────────────────────────────────────────────
DROP TABLE IF EXISTS ticket_aprobacion_director;
DROP TABLE IF EXISTS donaciones;
DROP TABLE IF EXISTS beneficiarios;
DROP TABLE IF EXISTS fundaciones_colaboradoras;

CREATE TABLE donaciones (
    id                          BIGINT AUTO_INCREMENT PRIMARY KEY,
    don_id                      VARCHAR(20)   NULL COMMENT 'Código visible ej: DON-001',
    donante_id                  BIGINT        NULL,
    nombre_donante               VARCHAR(200)  NULL,
    tipo_donante                VARCHAR(50)   NULL,
    articulo_nombre             VARCHAR(200)  NULL,
    articulo_id                 BIGINT        NULL,
    cantidad                    INT           NOT NULL DEFAULT 1,
    estado                      VARCHAR(40)   NOT NULL DEFAULT 'Pendiente',
    fecha_entrega                DATE          NULL,
    punto_entrega                VARCHAR(200)  NULL,
    hora_estimada                VARCHAR(20)   NULL,
    fecha_creacion               DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    url_imagen                   VARCHAR(300)  NULL,
    url_video                    VARCHAR(300)  NULL,
    comentario_voluntario        TEXT          NULL,
    email_donante                 VARCHAR(150)  NULL,
    recibida                     TINYINT(1)    NOT NULL DEFAULT 0,

    -- Columnas del módulo Beneficiario v2.0 (se agregan aquí directo)
    beneficiario_id               BIGINT        NULL,
    beneficiario_nombre           VARCHAR(200)  NULL,
    entrega_confirmada            TINYINT(1)    NOT NULL DEFAULT 0,
    fecha_confirmacion_entrega    DATETIME      NULL,
    confirmado_por                VARCHAR(200)  NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Registro de donaciones recibidas por la Fundación Abrázame';

-- Datos de prueba para donaciones (necesarios para probar el flujo completo)
INSERT INTO donaciones
    (don_id, nombre_donante, tipo_donante, articulo_nombre, cantidad, estado, fecha_creacion, email_donante)
VALUES
('DON-001', 'Juan Pérez',    'Persona Natural', 'Abrigos talla infantil', 15, 'Aprobado',  '2026-06-01 10:00:00', 'juan.perez@mail.com'),
('DON-002', 'María Soto',    'Persona Natural', 'Mochilas escolares',      3, 'Pendiente', '2026-06-05 11:00:00', 'maria.soto@mail.com'),
('DON-003', 'Empresa XYZ Ltda.', 'Empresa',     'Cuadernos universitarios',50,'Aprobado',  '2026-06-03 09:00:00', 'contacto@empresaxyz.cl'),
('DON-004', 'Luis Torres',   'Persona Natural', 'Zapatillas talla 34',    12, 'Aprobado',  '2026-06-08 14:00:00', 'luis.torres@mail.com'),
('DON-005', 'Carla Núñez',   'Persona Natural', 'Pañales talla M',         8, 'Pendiente', '2026-06-10 09:30:00', 'carla.nunez@mail.com');

-- ────────────────────────────────────────────────────────────
-- 2. TABLA FUNDACIONES_COLABORADORAS
-- ────────────────────────────────────────────────────────────
CREATE TABLE fundaciones_colaboradoras (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre              VARCHAR(150)  NOT NULL UNIQUE,
    tipo                ENUM('Fundación','Residencia') NOT NULL,
    region              VARCHAR(100)  NOT NULL,
    ciudad              VARCHAR(100)  NULL,
    direccion           VARCHAR(250)  NULL,
    telefono_contacto   VARCHAR(20)   NULL,
    email_contacto      VARCHAR(150)  NULL,
    activo              TINYINT(1)    NOT NULL DEFAULT 1,
    fecha_registro      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Fundaciones y residencias colaboradoras donde residen/asisten los beneficiarios';

INSERT INTO fundaciones_colaboradoras
    (nombre, tipo, region, ciudad, direccion, telefono_contacto, email_contacto)
VALUES
('Fundación Padre Semería',   'Fundación',  'Región Metropolitana', 'Santiago',      'Av. Recoleta 1200',     '+56226543201', 'contacto@padresemeria.cl'),
('Fundación María de la Luz', 'Fundación',  'Región Metropolitana', 'Santiago',      'Av. Macul 4750',        '+56226543202', 'contacto@mariadelaluz.cl'),
('Fundación Pléyades',        'Fundación',  'Región de Valparaíso', 'Viña del Mar',  'Calle 5 Norte 850',     '+56322543203', 'contacto@pleyades.cl'),
('Residencia Carlos Antúnez', 'Residencia', 'Región Metropolitana', 'Providencia',   'Carlos Antúnez 2450',   '+56226543204', 'contacto@residenciacarlosantunez.cl'),
('Residencia La Goleta',      'Residencia', 'Región del Biobío',    'Concepción',    'Av. Los Carrera 1100',  '+56412543205', 'contacto@lagoleta.cl');

-- ────────────────────────────────────────────────────────────
-- 3. TABLA BENEFICIARIOS
-- ────────────────────────────────────────────────────────────
CREATE TABLE beneficiarios (
    id                        BIGINT AUTO_INCREMENT PRIMARY KEY,

    nombre                    VARCHAR(200)  NOT NULL,
    tipo_beneficiario         ENUM('Niño','Familia','Grupo Hogar') NOT NULL DEFAULT 'Niño',
    fecha_nacimiento          DATE          NULL,
    edad                      INT GENERATED ALWAYS AS (
                                  TIMESTAMPDIFF(YEAR, fecha_nacimiento, CURDATE())
                              ) VIRTUAL,
    rango_edad                VARCHAR(20)   NULL COMMENT '0-2 años, 3-6 años, 7-12 años, 13-17 años',
    genero                    ENUM('Masculino','Femenino','No especificado') DEFAULT 'No especificado',
    descripcion_necesidad     TEXT          NULL,

    fundacion_id              BIGINT        NOT NULL,

    contacto_nombre           VARCHAR(200)  NOT NULL COMMENT 'Funcionario de la fundación/residencia a cargo',
    contacto_cargo            VARCHAR(100)  NOT NULL,
    contacto_telefono         VARCHAR(20)   NULL,
    contacto_email            VARCHAR(150)  NULL,

    activo                    TINYINT(1)    NOT NULL DEFAULT 1,
    fecha_ingreso             DATE          NULL,
    fecha_registro            DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observaciones             TEXT          NULL,

    FOREIGN KEY (fundacion_id) REFERENCES fundaciones_colaboradoras(id) ON DELETE RESTRICT

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Niños, familias y grupos que reciben donaciones, alojados en fundaciones/residencias colaboradoras';

-- ────────────────────────────────────────────────────────────
-- 4. TABLA TICKET_APROBACION_DIRECTOR
-- ────────────────────────────────────────────────────────────
CREATE TABLE ticket_aprobacion_director (
    id                        BIGINT AUTO_INCREMENT PRIMARY KEY,
    donacion_id               BIGINT        NOT NULL,
    beneficiario_id           BIGINT        NOT NULL,
    director_usuario_id       BIGINT        NOT NULL,
    director_nombre           VARCHAR(200),
    estado                    ENUM('Pendiente','Aprobado','Rechazado') NOT NULL DEFAULT 'Pendiente',
    fecha_decision            DATETIME      NULL,
    motivo_rechazo            TEXT          NULL,
    observaciones_director    TEXT          NULL,
    fecha_creacion            DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (donacion_id)     REFERENCES donaciones(id)    ON DELETE CASCADE,
    FOREIGN KEY (beneficiario_id) REFERENCES beneficiarios(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- FK ahora sí se puede crear porque la tabla beneficiarios ya existe
ALTER TABLE donaciones
    ADD CONSTRAINT fk_donacion_beneficiario
    FOREIGN KEY (beneficiario_id) REFERENCES beneficiarios(id) ON DELETE SET NULL;

-- ────────────────────────────────────────────────────────────
-- 5. DATOS DE PRUEBA — 20 BENEFICIARIOS
--    IDs de fundaciones: 1=Padre Semería · 2=María de la Luz
--    3=Pléyades · 4=Carlos Antúnez · 5=La Goleta
-- ────────────────────────────────────────────────────────────

INSERT INTO beneficiarios
    (nombre, tipo_beneficiario, fecha_nacimiento, rango_edad, genero,
     descripcion_necesidad, fundacion_id,
     contacto_nombre, contacto_cargo, contacto_telefono, contacto_email,
     fecha_ingreso, observaciones)
VALUES

-- ── FUNDACIÓN PADRE SEMERÍA (id=1) — Santiago, RM ───────────────
('María Suárez', 'Niño', '2020-04-12', '3-6 años', 'Femenino',
 'Ropa de invierno talla 5, zapatillas talla 26, mochila pre-kinder, útiles escolares',
 1, 'Pamela Soto Vidal', 'Directora', '+56226543201', 'p.soto@padresemeria.cl',
 '2022-01-15', 'María Suárez, 5 años, está en la Fundación Padre Semería desde enero 2022.'),

('Ignacio Flores', 'Niño', '2024-03-15', '0-2 años', 'Masculino',
 'Pañales talla M, ropa de abrigo 6-9 meses, toallas de baño',
 1, 'Pamela Soto Vidal', 'Directora', '+56226543201', 'p.soto@padresemeria.cl',
 '2024-04-01', 'Ingresó con 2 semanas de vida.'),

('Diego Morales', 'Niño', '2015-07-03', '7-12 años', 'Masculino',
 'Mochila escolar, cuadernos, zapatos de colegio talla 34',
 1, 'Pamela Soto Vidal', 'Directora', '+56226543201', 'p.soto@padresemeria.cl',
 '2020-03-10', 'Cursa 3° básico. Apoyo psicopedagógico activo.'),

('Familia López — 2 niños', 'Familia', NULL, NULL, 'No especificado',
 'Artículos para 2 hermanos: 4 y 8 años. Ropa, útiles, calzado.',
 1, 'Pamela Soto Vidal', 'Directora', '+56226543201', 'p.soto@padresemeria.cl',
 '2023-06-20', 'Grupo familiar en proceso de reunificación.'),

-- ── FUNDACIÓN MARÍA DE LA LUZ (id=2) — Santiago, RM ─────────────
('Valentina Castro', 'Niño', '2024-08-20', '0-2 años', 'Femenino',
 'Pañales talla P, ropa 0-3 meses, peluches y juguetes blandos',
 2, 'Beatriz Hernández Paz', 'Coordinadora de Hogar', '+56226543202', 'b.hernandez@mariadelaluz.cl',
 '2024-09-05', 'Requiere ropa especial de abrigo por condición respiratoria leve.'),

('Sofía Ramírez', 'Niño', '2021-05-10', '3-6 años', 'Femenino',
 'Mochila pre-kinder, cuadernos, lápices de colores, ropa de invierno',
 2, 'Beatriz Hernández Paz', 'Coordinadora de Hogar', '+56226543202', 'b.hernandez@mariadelaluz.cl',
 '2022-03-01', 'Ingresó al jardín de la fundación a los 14 meses.'),

('Camila Navarro', 'Niño', '2013-03-22', '7-12 años', 'Femenino',
 'Set geometría, cuadernos universitarios, ropa deportiva talla M',
 2, 'Beatriz Hernández Paz', 'Coordinadora de Hogar', '+56226543202', 'b.hernandez@mariadelaluz.cl',
 '2019-08-05', 'Excelente rendimiento académico.'),

('Francisca Medina', 'Niño', '2010-06-05', '13-17 años', 'Femenino',
 'Mochila enseñanza media, cuadernos universitarios, set geometría',
 2, 'Beatriz Hernández Paz', 'Coordinadora de Hogar', '+56226543202', 'b.hernandez@mariadelaluz.cl',
 '2018-03-01', 'Cursa 1° medio. Interés en diseño gráfico.'),

-- ── FUNDACIÓN PLÉYADES (id=3) — Viña del Mar, Valparaíso ────────
('Emilia Fuentes', 'Niño', '2019-09-12', '3-6 años', 'Femenino',
 'Abrigo talla 6, botas de lluvia, pijama, útiles pre-kinder',
 3, 'Rodrigo Castillo Pinto', 'Educador', '+56322543203', 'r.castillo@pleyades.cl',
 '2021-10-20', 'Asiste 5 días a la semana al programa de la fundación.'),

('Lucas Espinoza', 'Niño', '2014-11-11', '7-12 años', 'Masculino',
 'Zapatos colegio talla 36, ropa deportiva, lápices y cuadernos',
 3, 'Rodrigo Castillo Pinto', 'Educador', '+56322543203', 'r.castillo@pleyades.cl',
 '2020-06-01', 'Buen desempeño en taller de fútbol de la fundación.'),

('Nicolás Aguilera', 'Niño', '2009-01-18', '13-17 años', 'Masculino',
 'Abrigo talla L, zapatillas talla 40, cuadernos universitarios',
 3, 'Rodrigo Castillo Pinto', 'Educador', '+56322543203', 'r.castillo@pleyades.cl',
 '2017-08-20', 'Cursa 2° medio. Interés en mecánica automotriz.'),

('Jardín Infantil Pléyades — Sala Cuna', 'Grupo Hogar', NULL, NULL, 'No especificado',
 'Pañales, ropa de bebé 0-12 meses, artículos de higiene para 8 lactantes',
 3, 'Rodrigo Castillo Pinto', 'Educador', '+56322543203', 'r.castillo@pleyades.cl',
 '2023-01-10', 'Grupo de 8 bebés en sala cuna del programa de primera infancia.'),

-- ── RESIDENCIA CARLOS ANTÚNEZ (id=4) — Providencia, RM ──────────
('Benjamín Torres', 'Niño', '2020-02-28', '3-6 años', 'Masculino',
 'Zapatillas talla 22, pantalones y poleras talla 6 años, juegos de mesa',
 4, 'Marcela Vergara Soto', 'Coordinadora de Residencia', '+56226543204', 'm.vergara@residenciacarlosantunez.cl',
 '2021-08-15', 'Próximo a egresar al sistema escolar básico.'),

('Matías Soto', 'Niño', '2023-11-01', '0-2 años', 'Masculino',
 'Ropa interior talla 2 años, calzado talla 20, juguetes didácticos',
 4, 'Marcela Vergara Soto', 'Coordinadora de Residencia', '+56226543204', 'm.vergara@residenciacarlosantunez.cl',
 '2024-01-10', 'En proceso de vinculación familiar.'),

('Martina Vidal', 'Niño', '2014-05-19', '7-12 años', 'Femenino',
 'Cuadernos, lápices destacadores, mochila pequeña, ropa interior talla 10',
 4, 'Marcela Vergara Soto', 'Coordinadora de Residencia', '+56226543204', 'm.vergara@residenciacarlosantunez.cl',
 '2021-02-14', 'Requiere refuerzo escolar en matemáticas.'),

('Isidora Bravo', 'Niño', '2008-04-30', '13-17 años', 'Femenino',
 'Cuadernos educación técnica, mochila grande, ropa de trabajo',
 4, 'Marcela Vergara Soto', 'Coordinadora de Residencia', '+56226543204', 'm.vergara@residenciacarlosantunez.cl',
 '2016-11-10', 'Por egresar al sistema técnico-profesional.'),

('Grupo Familiar Carlos Antúnez — Ala B', 'Familia', NULL, NULL, 'No especificado',
 'Artículos para 3 niños: 4, 7 y 9 años. Ropa invierno, útiles, calzado.',
 4, 'Marcela Vergara Soto', 'Coordinadora de Residencia', '+56226543204', 'm.vergara@residenciacarlosantunez.cl',
 '2023-09-01', 'Grupo familiar en proceso de reunificación.'),

-- ── RESIDENCIA LA GOLETA (id=5) — Concepción, Biobío ────────────
('Gabriel Ortiz', 'Niño', '2013-09-30', '7-12 años', 'Masculino',
 'Zapatillas deportivas talla 37, ropa deportiva, juegos de construcción',
 5, 'Felipe Araneda Muñoz', 'Coordinador de Residencia', '+56412543205', 'f.araneda@lagoleta.cl',
 '2020-10-12', 'Destaca en deportes. Taller de fútbol martes y jueves.'),

('Sebastián López', 'Niño', '2007-12-22', '13-17 años', 'Masculino',
 'Zapatillas talla 42, ropa deportiva, calculadora científica, libros de lectura',
 5, 'Felipe Araneda Muñoz', 'Coordinador de Residencia', '+56412543205', 'f.araneda@lagoleta.cl',
 '2015-04-15', 'El mayor de la residencia. Apoya como monitor con los más pequeños.'),

('Isabella Muñoz', 'Niño', '2021-12-01', '3-6 años', 'Femenino',
 'Ropa de invierno talla 4, libros de cuentos ilustrados, peluches',
 5, 'Felipe Araneda Muñoz', 'Coordinador de Residencia', '+56412543205', 'f.araneda@lagoleta.cl',
 '2022-05-18', 'En residencia mientras se resuelve su situación legal.'),

('Grupo Hogar La Goleta — Ingreso Marzo 2026', 'Grupo Hogar', NULL, NULL, 'No especificado',
 'Artículos de primera necesidad para 5 niños: edades entre 3 y 10 años. Ropa abrigo, calzado, útiles, higiene.',
 5, 'Felipe Araneda Muñoz', 'Coordinador de Residencia', '+56412543205', 'f.araneda@lagoleta.cl',
 '2026-03-10', 'Ingreso por medida de protección. 3 niños y 2 niñas.');

-- ────────────────────────────────────────────────────────────
-- 6. VISTAS ÚTILES
-- ────────────────────────────────────────────────────────────

CREATE OR REPLACE VIEW v_beneficiarios_activos AS
SELECT
    b.id,
    b.nombre,
    b.tipo_beneficiario,
    b.fecha_nacimiento,
    b.edad                              AS edad_actual,
    b.rango_edad,
    b.genero,
    b.descripcion_necesidad,
    f.nombre                            AS fundacion_residencia,
    f.tipo                              AS tipo_institucion,
    f.region,
    f.ciudad,
    b.contacto_nombre,
    b.contacto_cargo,
    b.contacto_telefono,
    b.contacto_email,
    b.fecha_ingreso,
    b.observaciones
FROM beneficiarios b
INNER JOIN fundaciones_colaboradoras f ON b.fundacion_id = f.id
WHERE b.activo = 1
ORDER BY f.nombre, b.rango_edad, b.nombre;

CREATE OR REPLACE VIEW v_selector_beneficiario_director AS
SELECT
    b.id,
    b.nombre,
    b.tipo_beneficiario,
    COALESCE(b.rango_edad, b.tipo_beneficiario)  AS rango_display,
    b.edad                                        AS edad_actual,
    f.nombre                                      AS fundacion_residencia,
    f.region,
    b.contacto_nombre,
    b.contacto_cargo,
    b.contacto_telefono,
    b.descripcion_necesidad
FROM beneficiarios b
INNER JOIN fundaciones_colaboradoras f ON b.fundacion_id = f.id
WHERE b.activo = 1
ORDER BY
    FIELD(b.rango_edad, '0-2 años','3-6 años','7-12 años','13-17 años'),
    b.nombre;

CREATE OR REPLACE VIEW v_trazabilidad_donacion_beneficiario AS
SELECT
    d.id               AS donacion_id,
    d.don_id,
    d.nombre_donante,
    d.articulo_nombre,
    d.cantidad,
    d.estado           AS estado_donacion,
    d.fecha_entrega,
    b.id               AS beneficiario_id,
    b.nombre           AS beneficiario_nombre,
    b.tipo_beneficiario,
    b.rango_edad,
    f.nombre           AS fundacion_residencia,
    f.region,
    b.contacto_nombre,
    b.contacto_cargo,
    t.id               AS ticket_id,
    t.estado           AS estado_ticket,
    t.director_nombre,
    t.fecha_decision,
    d.entrega_confirmada,
    d.fecha_confirmacion_entrega,
    d.confirmado_por
FROM donaciones d
LEFT JOIN beneficiarios              b ON d.beneficiario_id = b.id
LEFT JOIN fundaciones_colaboradoras  f ON b.fundacion_id     = f.id
LEFT JOIN ticket_aprobacion_director t ON t.donacion_id     = d.id
ORDER BY d.fecha_creacion DESC;

CREATE OR REPLACE VIEW v_resumen_por_fundacion AS
SELECT
    f.nombre                                           AS fundacion_residencia,
    f.tipo,
    f.region,
    f.ciudad,
    COUNT(b.id)                                         AS total_beneficiarios,
    SUM(b.tipo_beneficiario = 'Niño')                  AS ninos_individuales,
    SUM(b.tipo_beneficiario = 'Familia')               AS familias,
    SUM(b.tipo_beneficiario = 'Grupo Hogar')           AS grupos_hogar,
    MIN(b.edad)                                        AS edad_minima,
    MAX(b.edad)                                        AS edad_maxima
FROM fundaciones_colaboradoras f
LEFT JOIN beneficiarios b ON b.fundacion_id = f.id AND b.activo = 1
GROUP BY f.id, f.nombre, f.tipo, f.region, f.ciudad
ORDER BY f.region, f.nombre;

-- ────────────────────────────────────────────────────────────
-- 7. STORED PROCEDURES
-- ────────────────────────────────────────────────────────────
DROP PROCEDURE IF EXISTS sp_asignar_beneficiario;
DELIMITER $$
CREATE PROCEDURE sp_asignar_beneficiario(
    IN p_donacion_id      BIGINT,
    IN p_beneficiario_id  BIGINT,
    IN p_director_id      BIGINT,
    IN p_director_nombre  VARCHAR(200),
    IN p_observaciones    TEXT
)
BEGIN
    DECLARE v_nombre_benef VARCHAR(200);
    SELECT nombre INTO v_nombre_benef FROM beneficiarios WHERE id = p_beneficiario_id;

    UPDATE donaciones
    SET beneficiario_id     = p_beneficiario_id,
        beneficiario_nombre = v_nombre_benef,
        estado              = 'Asignado'
    WHERE id = p_donacion_id;

    INSERT INTO ticket_aprobacion_director
        (donacion_id, beneficiario_id, director_usuario_id, director_nombre,
         observaciones_director, estado)
    VALUES
        (p_donacion_id, p_beneficiario_id, p_director_id, p_director_nombre,
         p_observaciones, 'Pendiente');

    SELECT 'Beneficiario asignado y ticket creado correctamente' AS resultado;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_resolver_ticket;
DELIMITER $$
CREATE PROCEDURE sp_resolver_ticket(
    IN p_ticket_id   BIGINT,
    IN p_estado      ENUM('Aprobado','Rechazado'),
    IN p_motivo      TEXT
)
BEGIN
    UPDATE ticket_aprobacion_director
    SET estado         = p_estado,
        fecha_decision = NOW(),
        motivo_rechazo = IF(p_estado = 'Rechazado', p_motivo, NULL)
    WHERE id = p_ticket_id;

    IF p_estado = 'Aprobado' THEN
        UPDATE donaciones d
        INNER JOIN ticket_aprobacion_director t ON t.donacion_id = d.id
        SET d.estado = 'Aprobado para Entrega'
        WHERE t.id = p_ticket_id;
    ELSE
        UPDATE donaciones d
        INNER JOIN ticket_aprobacion_director t ON t.donacion_id = d.id
        SET d.estado = 'Rechazado', d.beneficiario_id = NULL, d.beneficiario_nombre = NULL
        WHERE t.id = p_ticket_id;
    END IF;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_confirmar_entrega;
DELIMITER $$
CREATE PROCEDURE sp_confirmar_entrega(
    IN p_donacion_id   BIGINT,
    IN p_confirmador   VARCHAR(200)
)
BEGIN
    UPDATE donaciones
    SET entrega_confirmada         = 1,
        fecha_confirmacion_entrega = NOW(),
        confirmado_por             = p_confirmador,
        estado                     = 'Entregado'
    WHERE id = p_donacion_id
      AND beneficiario_id IS NOT NULL;

    SELECT ROW_COUNT() AS filas_afectadas;
END$$
DELIMITER ;

-- ────────────────────────────────────────────────────────────
-- 8. VERIFICACIÓN FINAL
-- ────────────────────────────────────────────────────────────
SELECT CONCAT('Base de datos creada: db_gestion_donaciones') AS resultado;
SELECT CONCAT('Total donaciones de prueba: ', COUNT(*)) AS resumen FROM donaciones;
SELECT CONCAT('Total fundaciones/residencias: ', COUNT(*)) AS resumen FROM fundaciones_colaboradoras;
SELECT CONCAT('Total beneficiarios: ', COUNT(*)) AS resumen FROM beneficiarios;

SELECT
    f.nombre AS fundacion_residencia,
    f.tipo,
    f.region,
    COUNT(b.id) AS total_beneficiarios
FROM fundaciones_colaboradoras f
LEFT JOIN beneficiarios b ON b.fundacion_id = f.id
GROUP BY f.id, f.nombre, f.tipo, f.region
ORDER BY f.region, f.nombre;

-- Ejemplo: buscar a María Suárez y confirmar su fundación + contacto
SELECT * FROM v_beneficiarios_activos WHERE nombre = 'María Suárez';

-- ============================================================
-- FIN DEL SCRIPT v4 — Base de datos completa desde cero
--
-- Resumen de lo creado:
--   • Base de datos: db_gestion_donaciones
--   • Tabla: donaciones (5 registros de prueba)
--   • Tabla: fundaciones_colaboradoras (5 instituciones reales)
--   • Tabla: beneficiarios (20 niños/familias/grupos)
--   • Tabla: ticket_aprobacion_director (vacía, lista para usar)
--   • 4 vistas de consulta
--   • 3 stored procedures
--
-- Próximo paso: configurar tu aplicación Spring Boot
-- (application.properties de service-gestion) para apuntar
-- a esta base: jdbc:mysql://localhost:3306/db_gestion_donaciones
-- ============================================================

-- ============================================================
-- ACTUALIZACIÓN v3.1 — Historial de Entregas con Descuento de Inventario
-- Registra cada entrega confirmada: qué se entregó, a quién y dónde.
-- Se llena automáticamente al confirmar entrega desde el Voluntario/Admin.
-- ============================================================

DROP TABLE IF EXISTS historial_entregas;

CREATE TABLE historial_entregas (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    donacion_id             BIGINT        NOT NULL,
    donacion_codigo         VARCHAR(20)   NULL,
    articulo_nombre         VARCHAR(200)  NULL,
    articulo_id             BIGINT        NULL COMMENT 'FK lógica a db_catalogo.articulos_catalogo.id',
    cantidad                INT           NULL,

    beneficiario_id         BIGINT        NOT NULL,
    beneficiario_nombre     VARCHAR(200)  NULL,
    fundacion_residencia    VARCHAR(150)  NULL COMMENT 'Snapshot del nombre al momento de la entrega',
    region                  VARCHAR(100)  NULL,

    confirmado_por          VARCHAR(200)  NULL,
    observaciones           TEXT          NULL,

    fecha_entrega           DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (beneficiario_id) REFERENCES beneficiarios(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Historial inmutable de entregas confirmadas. Cada fila = una entrega física realizada.';

-- Vista de trazabilidad completa: artículo + cantidad + destino
CREATE OR REPLACE VIEW v_historial_entregas_completo AS
SELECT
    h.id,
    h.donacion_codigo,
    h.articulo_nombre,
    h.cantidad,
    h.beneficiario_nombre,
    h.fundacion_residencia,
    h.region,
    h.confirmado_por,
    h.fecha_entrega,
    h.observaciones
FROM historial_entregas h
ORDER BY h.fecha_entrega DESC;
