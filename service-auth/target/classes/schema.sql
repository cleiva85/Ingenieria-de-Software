-- Crear tabla solo si no existe (evita error al reiniciar)
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

-- Insertar admins solo si no existen
INSERT IGNORE INTO usuarios (nombre, nombre2, apellido_p, apellido_m, email, password_hash, rol, activo)
VALUES
('Ana',   NULL, 'Soto',    'Rojas',    'admin@abrazame.org',    '$2a$10$n8M2xJjwfIxRzF.oTd/z8OKk/IKRHwhh6hq4v0DEpa4vbR1Oa57Ty', 'Admin',      1),
('Luis',  NULL, 'Ramírez', 'Vega',     'director@abrazame.org', '$2a$10$TDQbmvA2ttwOyfyeKrb3jeshIyCdqlHUKEJreH0FlO9YLMTLgNdxa', 'Director',   1),
('Sofía', NULL, 'Torres',  'Castillo', 'super@abrazame.org',    '$2a$10$aDtmztvTkCG0t.Yr4trIl.RXsNewv.tMk0FlOEDUc3ABdA3Ta7EzC', 'SuperAdmin', 1),
('Valentina', 'Paz', 'Morales', 'Fuentes', 'valentina.morales@gmail.com', '$2a$10$SQbdVlKTTsTorRzg8m3Uz.Z2Pg6zigYG3bEB7fw2e9xnfUT9vK.ZO', 'Donante', 1),
('Rodrigo', NULL, 'Castro', 'Pizarro', 'rodrigo.castro@outlook.com', '$2a$10$ehrNxDLHi1V.q0O1lcAbkOgcg/pgztzfWwlAd5xy6xKjEsB5dKLLq', 'Donante', 1);
