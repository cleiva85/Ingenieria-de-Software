-- ============================================================
--  Fundación Abrázame — Script BD de Autenticación
--  Ejecutar en phpMyAdmin (XAMPP) o en la consola MySQL
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_auth
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE db_auth;

-- Tabla usuarios (Hibernate la crea sola con ddl-auto=update,
-- pero este script sirve como referencia / backup)
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
    password_hash     VARCHAR(255)  NOT NULL,
    rol               ENUM('Donante','Voluntario','Admin','Director','SuperAdmin') NOT NULL,
    activo            TINYINT(1)    NOT NULL DEFAULT 1,
    intentos_fallidos INT           NOT NULL DEFAULT 0,
    bloqueado_hasta   DATETIME,
    fecha_registro    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
--  Usuarios de PRUEBA
--  Contraseña de todos: Test1234!
--  Hash BCrypt generado para "Test1234!"
-- ============================================================
INSERT INTO usuarios
    (nombre, apellido_p, apellido_m, email, password_hash, rol, activo)
VALUES
    ('María',  'González', 'Muñoz',    'donante@abrazame.org',    '$2a$10$7QzV1ZqY0VkB9mXkL3R4.OkH1S2dNpW6fGjT8uCeIbYlMnXoPqRsA', 'Donante',    1),
    ('Carlos', 'Pérez',    'Lagos',    'voluntario@abrazame.org', '$2a$10$7QzV1ZqY0VkB9mXkL3R4.OkH1S2dNpW6fGjT8uCeIbYlMnXoPqRsA', 'Voluntario', 1),
    ('Ana',    'Soto',     'Rojas',    'admin@abrazame.org',      '$2a$10$7QzV1ZqY0VkB9mXkL3R4.OkH1S2dNpW6fGjT8uCeIbYlMnXoPqRsA', 'Admin',      1),
    ('Luis',   'Ramírez',  'Vega',     'director@abrazame.org',   '$2a$10$7QzV1ZqY0VkB9mXkL3R4.OkH1S2dNpW6fGjT8uCeIbYlMnXoPqRsA', 'Director',   1),
    ('Sofía',  'Torres',   'Castillo', 'super@abrazame.org',      '$2a$10$7QzV1ZqY0VkB9mXkL3R4.OkH1S2dNpW6fGjT8uCeIbYlMnXoPqRsA', 'SuperAdmin', 1);

-- ⚠️  IMPORTANTE: el hash de arriba es un ejemplo visual.
--     Spring Boot genera el hash real al registrarse desde el formulario.
--     Para los usuarios de prueba, regístralos desde login_usuario.html
--     o login_admin.html, que llaman a POST /auth/register.
--     O usa el endpoint directamente con Postman / curl:
--
--  curl -X POST http://localhost:8081/auth/register \
--    -H "Content-Type: application/json" \
--    -d '{"nombre":"María","apellidoP":"González","apellidoM":"Muñoz",
--         "email":"donante@abrazame.org","password":"Test1234!","rol":"Donante"}'
-- ============================================================
