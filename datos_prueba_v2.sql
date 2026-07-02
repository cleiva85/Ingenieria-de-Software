-- ============================================================
--  Fundación Abrázame — Datos de Prueba Completos v2.1
--  Ejecutar DESPUÉS de levantar todos los servicios
--  (los servicios crean las tablas con ddl-auto=update)
--  Cambios v2.1:
--    - Todos los INSERT usan IGNORE (seguro ejecutar múltiples veces)
--    - Enum TipoInstitucion sin tilde (Fundacion en vez de Fundación) para evitar errores de charset
-- ============================================================

-- ────────────────────────────────────────────────────────────
-- 1. db_auth — Usuarios del sistema
-- ────────────────────────────────────────────────────────────
USE db_auth;

-- Contraseña de Admin/Director/SuperAdmin: Admin1234!
-- Contraseña de Voluntarios y Donantes:    User1234!
INSERT IGNORE INTO usuarios
  (nombre, nombre2, apellido_p, apellido_m, rut, fecha_nacimiento, telefono, email, password_hash, rol, activo)
VALUES
-- SuperAdmin
('Sofia',    NULL,      'Torres',   'Castillo', '10.111.111-1', '1980-05-10', '+56912340001', 'super@abrazame.org',    '$2a$10$aDtmztvTkCG0t.Yr4trIl.RXsNewv.tMk0FlOEDUc3ABdA3Ta7EzC', 'SuperAdmin', 1),
-- Admin
('Ana',      NULL,      'Soto',     'Rojas',    '10.222.222-2', '1985-03-22', '+56912340002', 'admin@abrazame.org',    '$2a$10$n8M2xJjwfIxRzF.oTd/z8OKk/IKRHwhh6hq4v0DEpa4vbR1Oa57Ty', 'Admin',      1),
-- Director
('Luis',     NULL,      'Ramirez',  'Vega',     '10.333.333-3', '1978-11-15', '+56912340003', 'director@abrazame.org', '$2a$10$TDQbmvA2ttwOyfyeKrb3jeshIyCdqlHUKEJreH0FlO9YLMTLgNdxa', 'Director',   1),
-- Voluntarios aprobados
('Camila',   'Ignacia', 'Reyes',    'Vargas',   '15.432.876-5', '1998-11-08', '+56955551234', 'voluntario@abrazame.org',       '$2a$10$n8M2xJjwfIxRzF.oTd/z8OKk/IKRHwhh6hq4v0DEpa4vbR1Oa57Ty', 'Voluntario', 1),
('Felipe',   NULL,      'Nunez',    'Araya',    '20.111.333-K', '1995-04-30', '+56966667890', 'felipe.nunez@gmail.com',        '$2a$10$n8M2xJjwfIxRzF.oTd/z8OKk/IKRHwhh6hq4v0DEpa4vbR1Oa57Ty', 'Voluntario', 1),
('Daniela',  'Paz',     'Moreno',   'Silva',    '17.654.321-8', '2000-07-19', '+56933334444', 'daniela.moreno@gmail.com',      '$2a$10$n8M2xJjwfIxRzF.oTd/z8OKk/IKRHwhh6hq4v0DEpa4vbR1Oa57Ty', 'Voluntario', 1),
('Sebastian','Andres',  'Gonzalez', 'Pena',     '19.876.543-2', '1997-02-28', '+56977778888', 'sebastian.gonzalez@hotmail.com','$2a$10$n8M2xJjwfIxRzF.oTd/z8OKk/IKRHwhh6hq4v0DEpa4vbR1Oa57Ty', 'Voluntario', 1),
-- Voluntarios pendientes de aprobacion
('Isidora',  NULL,      'Carrasco', 'Lagos',    '21.222.333-4', '2002-09-05', '+56911112222', 'isidora.carrasco@gmail.com',    NULL, 'Voluntario', 0),
('Matias',   'Jose',    'Fuentes',  'Diaz',     '22.333.444-5', '2001-03-17', '+56922223333', 'matias.fuentes@outlook.com',    NULL, 'Voluntario', 0),
-- Donantes
('Valentina','Paz',     'Morales',  'Fuentes',  '12.345.678-9', '1990-03-15', '+56987654321', 'valentina.morales@gmail.com',   '$2a$10$n8M2xJjwfIxRzF.oTd/z8OKk/IKRHwhh6hq4v0DEpa4vbR1Oa57Ty', 'Donante',    1),
('Rodrigo',  NULL,      'Castro',   'Pizarro',  '98.765.432-1', '1985-07-22', '+56912345678', 'rodrigo.castro@outlook.com',    '$2a$10$n8M2xJjwfIxRzF.oTd/z8OKk/IKRHwhh6hq4v0DEpa4vbR1Oa57Ty', 'Donante',    1),
('Marcela',  NULL,      'Herrera',  'Jimenez',  '14.567.890-3', '1992-06-10', '+56956789012', 'marcela.herrera@gmail.com',     '$2a$10$n8M2xJjwfIxRzF.oTd/z8OKk/IKRHwhh6hq4v0DEpa4vbR1Oa57Ty', 'Donante',    1),
('Andres',   'Felipe',  'Vega',     'Campos',   '16.789.012-6', '1988-12-03', '+56967890123', 'andres.vega@empresa.cl',        '$2a$10$n8M2xJjwfIxRzF.oTd/z8OKk/IKRHwhh6hq4v0DEpa4vbR1Oa57Ty', 'Donante',    1),
('Carolina', NULL,      'Marin',    'Espinoza', '11.234.567-8', '1994-08-25', '+56978901234', 'carolina.marin@gmail.com',      '$2a$10$n8M2xJjwfIxRzF.oTd/z8OKk/IKRHwhh6hq4v0DEpa4vbR1Oa57Ty', 'Donante',    1);

-- ────────────────────────────────────────────────────────────
-- 2. db_donante — Voluntarios y Donantes
-- ────────────────────────────────────────────────────────────
USE db_donante;

-- Voluntarios
INSERT IGNORE INTO voluntarios
  (rut, primer_nombre, segundo_nombre, apellido_paterno, apellido_materno,
   fecha_nacimiento, correo_electronico, telefono, direccion_completa, comuna)
VALUES
('15.432.876-5', 'Camila',   'Ignacia', 'Reyes',    'Vargas',   '1998-11-08', 'voluntario@abrazame.org',        '+56955551234', 'Av. Vicuña Mackenna 890 Depto 12', 'Ñuñoa'),
('20.111.333-K', 'Felipe',   NULL,      'Nunez',    'Araya',    '1995-04-30', 'felipe.nunez@gmail.com',         '+56966667890', 'Pasaje Los Aromos 23',              'Maipu'),
('17.654.321-8', 'Daniela',  'Paz',     'Moreno',   'Silva',    '2000-07-19', 'daniela.moreno@gmail.com',       '+56933334444', 'Calle Los Leones 456 Casa 2',       'Providencia'),
('19.876.543-2', 'Sebastian','Andres',  'Gonzalez', 'Pena',     '1997-02-28', 'sebastian.gonzalez@hotmail.com', '+56977778888', 'Av. Grecia 1234 Depto 5A',          'Nunoa'),
('21.222.333-4', 'Isidora',  NULL,      'Carrasco', 'Lagos',    '2002-09-05', 'isidora.carrasco@gmail.com',     '+56911112222', 'Los Olmos 789',                     'La Florida'),
('22.333.444-5', 'Matias',   'Jose',    'Fuentes',  'Diaz',     '2001-03-17', 'matias.fuentes@outlook.com',     '+56922223333', 'Villa Portales Block 3 Depto 201',  'Estacion Central');

-- Perfiles de voluntario
INSERT IGNORE INTO perfil_voluntario (voluntario_id, dias_disponibles, horario_disponible, tipo_vivienda, region, url_cv, url_certificado_antecedentes)
SELECT id, 'lunes, miercoles, sabado', 'manana, tarde', 'Departamento', 'Metropolitana', NULL, NULL FROM voluntarios WHERE rut='15.432.876-5';
INSERT IGNORE INTO perfil_voluntario (voluntario_id, dias_disponibles, horario_disponible, tipo_vivienda, region, url_cv, url_certificado_antecedentes)
SELECT id, 'martes, jueves, sabado',   'tarde',          'Casa',          'Metropolitana', NULL, NULL FROM voluntarios WHERE rut='20.111.333-K';
INSERT IGNORE INTO perfil_voluntario (voluntario_id, dias_disponibles, horario_disponible, tipo_vivienda, region, url_cv, url_certificado_antecedentes)
SELECT id, 'lunes, viernes',           'manana',         'Departamento', 'Metropolitana', NULL, NULL FROM voluntarios WHERE rut='17.654.321-8';
INSERT IGNORE INTO perfil_voluntario (voluntario_id, dias_disponibles, horario_disponible, tipo_vivienda, region, url_cv, url_certificado_antecedentes)
SELECT id, 'miercoles, sabado, domingo','manana, tarde',  'Casa',         'Metropolitana', NULL, NULL FROM voluntarios WHERE rut='19.876.543-2';

-- Donantes persona natural
INSERT IGNORE INTO donantes_persona_natural
  (rut, primer_nombre, segundo_nombre, apellido_paterno, apellido_materno,
   fecha_nacimiento, correo_electronico, telefono, comuna, direccion_completa,
   cantidad, estado_donacion, articulo_nombre, articulo_id, url_imagen, url_video)
VALUES
('12.345.678-9', 'Valentina','Paz',    'Morales', 'Fuentes', '1990-03-15', 'valentina.morales@gmail.com', '+56987654321', 'Providencia',  'Av. Providencia 1234 Depto 5B',  3,  'Pendiente',  'Pijamas',              14, NULL, NULL),
('98.765.432-1', 'Rodrigo',  NULL,     'Castro',  'Pizarro', '1985-07-22', 'rodrigo.castro@outlook.com',  '+56912345678', 'Las Condes',   'Calle El Golf 456 Casa 3',       5,  'Aprobado',   'Juegos de mesa',       36, NULL, NULL),
('14.567.890-3', 'Marcela',  NULL,     'Herrera', 'Jimenez', '1992-06-10', 'marcela.herrera@gmail.com',   '+56956789012', 'Santiago',     'Huerfanos 1090 Of. 12',          8,  'Pendiente',  'Cuadernos',            50, NULL, NULL),
('11.234.567-8', 'Carolina', NULL,     'Marin',   'Espinoza','1994-08-25', 'carolina.marin@gmail.com',    '+56978901234', 'La Florida',   'Av. Vicuña Mackenna 7890',       2,  'Rechazado',  'Cuentos ilustrados',   65, NULL, NULL),
('16.789.012-6', 'Andres',   'Felipe', 'Vega',    'Campos',  '1988-12-03', 'andres.vega@empresa.cl',      '+56967890123', 'Vitacura',     'Av. Alonso de Cordova 4000',     4,  'Aprobado',   'Mochilas',             57, NULL, NULL);

-- Donante empresa
INSERT IGNORE INTO donantes_empresa
  (rut, razon_social, correo_electronico, telefono, comuna, direccion_completa,
   cantidad, estado_donacion, articulo_nombre, articulo_id, url_imagen, url_video)
VALUES
('76.543.210-K', 'Distribuidora Norte S.A.',    'donaciones@distribnorte.cl',  '+56222334455', 'Pudahuel',    'Av. El Salto 2890',              15, 'Aprobado',  'Ropa interior',         17, NULL, NULL),
('77.111.222-3', 'Fundacion Empresas Copec',    'rse@empresascopec.cl',        '+56222445566', 'Las Condes',  'Av. Apoquindo 3600 Piso 8',      20, 'Pendiente', 'Zapatillas deportivas', 21, NULL, NULL),
('78.222.333-4', 'Supermercados Mayorista 10',  'ong@mayorista10.cl',          '+56222556677', 'Renca',       'Av. Pedro Fontova 6800',         12, 'Aprobado',  'Panales',               82, NULL, NULL),
('79.333.444-5', 'BCI Seguros',                 'responsabilidad@bci.cl',      '+56222667788', 'Providencia', 'Av. El Golf 100 Piso 6',         30, 'Pendiente', 'Lapices y plumones',    54, NULL, NULL);

-- ────────────────────────────────────────────────────────────
-- 3. db_catalogo — Artículos (ya cargados en schema, solo ajustamos stock)
-- ────────────────────────────────────────────────────────────
USE db_catalogo;

-- Actualizar stock para que algunas barras de progreso se vean interesantes
UPDATE articulos_catalogo SET stock_actual = 28 WHERE nombre='Abrigos y chaquetas'   AND rango_edad='0-2 años';
UPDATE articulos_catalogo SET stock_actual = 35 WHERE nombre='Abrigos y chaquetas'   AND rango_edad='3-6 años';
UPDATE articulos_catalogo SET stock_actual = 12 WHERE nombre='Ropa interior'          AND rango_edad='0-2 años';
UPDATE articulos_catalogo SET stock_actual = 60 WHERE nombre='Calcetines'             AND rango_edad='3-6 años';
UPDATE articulos_catalogo SET stock_actual = 80 WHERE nombre='Calcetines'             AND rango_edad='7-12 años';
UPDATE articulos_catalogo SET stock_actual = 20 WHERE nombre='Zapatillas deportivas'  AND rango_edad='7-12 años';
UPDATE articulos_catalogo SET stock_actual = 5  WHERE nombre='Zapatos de colegio'     AND rango_edad='7-12 años';
UPDATE articulos_catalogo SET stock_actual = 95 WHERE nombre='Cuadernos'              AND rango_edad='7-12 años';
UPDATE articulos_catalogo SET stock_actual = 50 WHERE nombre='Lapices y plumones'     AND rango_edad='7-12 años';
UPDATE articulos_catalogo SET stock_actual = 25 WHERE nombre='Mochilas'               AND rango_edad='7-12 años';
UPDATE articulos_catalogo SET stock_actual = 18 WHERE nombre='Peluches y munecas'     AND rango_edad='0-2 años';
UPDATE articulos_catalogo SET stock_actual = 30 WHERE nombre='Juguetes didacticos'    AND rango_edad='3-6 años';
UPDATE articulos_catalogo SET stock_actual = 80 WHERE nombre='Panales'                AND rango_edad='0-2 años';
UPDATE articulos_catalogo SET stock_actual = 40 WHERE nombre='Jabon y shampoo'        AND rango_edad='0-2 años';
UPDATE articulos_catalogo SET stock_actual = 55 WHERE nombre='Cepillos y pasta dental'AND rango_edad='7-12 años';
UPDATE articulos_catalogo SET stock_actual = 15 WHERE nombre='Libros educativos'      AND rango_edad='7-12 años';
UPDATE articulos_catalogo SET stock_actual = 10 WHERE nombre='Novelas y lecturas'     AND rango_edad='13-17 años';

-- ────────────────────────────────────────────────────────────
-- 4. db_gestion_donaciones — Donaciones, fundaciones, beneficiarios, tickets, historial
-- ────────────────────────────────────────────────────────────
USE db_gestion_donaciones;

-- Fundaciones colaboradoras
INSERT IGNORE INTO fundaciones_colaboradoras (nombre, tipo, region, ciudad, direccion, telefono_contacto, email_contacto, activo, fecha_registro)
VALUES
('Hogar de Cristo - Casa Esperanza',         'Residencia', 'Metropolitana', 'Santiago',    'Av. Bulnes 168',                    '+56222391000', 'contacto@hogardecristo.cl',      1, NOW()),
('Fundacion San Jose - Proteccion a la Familia','Fundacion', 'Metropolitana', 'Providencia', 'Av. Matta 1174',                   '+56226985200', 'sanjose@fundacionsanjose.cl',    1, NOW()),
('Casa Taller El Encuentro',                  'Residencia', 'Metropolitana', 'La Florida',  'Av. Vicuña Mackenna 5270',          '+56226181700', 'contacto@casaelencuentro.cl',    1, NOW()),
('Fundacion Nuestros Hijos',                  'Fundacion',  'Metropolitana', 'Las Condes',  'Av. Apoquindo 5555 Of. 202',        '+56222064200', 'donaciones@nuestroshijos.cl',    1, NOW()),
('Residencia Familiar SENAME - Pudahuel',     'Residencia', 'Metropolitana', 'Pudahuel',    'Calle Los Quillayes 890',           '+56222764300', 'residencia.pudahuel@sename.cl',  1, NOW()),
('Aldeas Infantiles SOS Chile - Maipu',       'Fundacion',  'Metropolitana', 'Maipu',       'Av. Pajaritos 3550',                '+56226819000', 'maipu@aldeasinfantiles.cl',      1, NOW());

-- Beneficiarios
INSERT IGNORE INTO beneficiarios
  (nombre, tipo_beneficiario, fecha_nacimiento, rango_edad, genero, descripcion_necesidad,
   fundacion_id, contacto_nombre, contacto_cargo, contacto_telefono, contacto_email,
   activo, fecha_ingreso, fecha_registro, observaciones)
VALUES
('Matias A.',    'Niño',        '2019-03-12', '3-6 años',   'Masculino',       'Necesita ropa de invierno y útiles escolares para el jardín.',     1, 'Patricia Soto',     'Coordinadora de Casos',  '+56222391001', 'psoto@hogardecristo.cl',      1, '2023-08-01', NOW(), NULL),
('Sofia B.',     'Niño',        '2017-07-25', '7-12 años',  'Femenino',        'Requiere material escolar y calzado talla 32.',                    1, 'Patricia Soto',     'Coordinadora de Casos',  '+56222391001', 'psoto@hogardecristo.cl',      1, '2022-05-10', NOW(), NULL),
('Lucas C.',     'Niño',        '2021-01-08', '0-2 años',   'Masculino',       'Bebé recién ingresado, requiere pañales y ropa talla S.',          2, 'Roberto Muñoz',     'Director Hogar',         '+56226985201', 'rmunoz@fundacionsanjose.cl',  1, '2024-01-15', NOW(), NULL),
('Valentina D.', 'Niño',        '2015-11-30', '7-12 años',  'Femenino',        'Necesita mochila, cuadernos y útiles para enseñanza básica.',      2, 'Roberto Muñoz',     'Director Hogar',         '+56226985201', 'rmunoz@fundacionsanjose.cl',  1, '2021-09-20', NOW(), NULL),
('Hermanos E.',  'Familia',     NULL,         '3-6 años',   'No_especificado', 'Familia con 3 hijos, necesitan ropa de invierno y juguetes.',      3, 'Carmen Lopez',      'Trabajadora Social',     '+56226181701', 'clopez@casaelencuentro.cl',   1, '2023-03-05', NOW(), 'Familia reingresada tras crisis habitacional'),
('Nicolas F.',   'Niño',        '2010-06-18', '13-17 años', 'Masculino',       'Adolescente en etapa escolar, requiere útiles y calzado.',         3, 'Carmen Lopez',      'Trabajadora Social',     '+56226181701', 'clopez@casaelencuentro.cl',   1, '2022-11-12', NOW(), NULL),
('Isabella G.',  'Niño',        '2022-04-03', '0-2 años',   'Femenino',        'Lactante que necesita pañales, ropa y artículos de higiene.',      4, 'Juan Perez',        'Coordinador',            '+56222064201', 'jperez@nuestroshijos.cl',     1, '2024-02-20', NOW(), NULL),
('Grupo Hogar H.','Grupo_Hogar',NULL,         '7-12 años',  'No_especificado', 'Hogar con 8 niños en edad escolar, prioridad en útiles y libros.', 5, 'Andrea Vasquez',    'Directora Residencia',   '+56222764301', 'avasquez@sename.cl',          1, '2020-06-01', NOW(), 'Residencia con alta rotación'),
('Diego I.',     'Niño',        '2013-09-14', '7-12 años',  'Masculino',       'Niño con necesidades educativas, requiere libros y material.',     5, 'Andrea Vasquez',    'Directora Residencia',   '+56222764301', 'avasquez@sename.cl',          1, '2023-07-30', NOW(), NULL),
('Camila J.',    'Niño',        '2008-12-01', '13-17 años', 'Femenino',        'Adolescente en enseñanza media, necesita útiles y ropa.',          6, 'Monica Ruiz',       'Coordinadora Aldeas',    '+56226819001', 'mruiz@aldeasinfantiles.cl',   1, '2022-04-18', NOW(), NULL);

-- Donaciones
INSERT IGNORE INTO donaciones
  (don_id, donante_id, nombre_donante, tipo_donante, articulo_nombre, articulo_id,
   cantidad, estado, fecha_entrega, punto_entrega, hora_estimada, fecha_creacion,
   url_imagen, url_video, comentario_voluntario, email_donante,
   recibida, beneficiario_id, beneficiario_nombre, entrega_confirmada, fecha_confirmacion_entrega, confirmado_por)
VALUES
('DON-001', 1, 'Valentina Morales Fuentes', 'Persona Natural', 'Pijamas',               14,  3,  'Pendiente',          '2025-07-10', 'Sede Providencia',            '10:00', NOW() - INTERVAL 2 DAY,  NULL, NULL, NULL,                             'valentina.morales@gmail.com', 0, NULL,  NULL,         0, NULL, NULL),
('DON-002', 2, 'Rodrigo Castro Pizarro',    'Persona Natural', 'Juegos de mesa',         36,  5,  'Aprobado',           '2025-07-08', 'Casillero Mall Plaza Tobalaba','09:30', NOW() - INTERVAL 5 DAY,  NULL, NULL, 'Artículos en buen estado.',     'rodrigo.castro@outlook.com',  1,  2,    'Sofia B.',   0, NULL, NULL),
('DON-003', 3, 'Marcela Herrera Jimenez',   'Persona Natural', 'Cuadernos',              50,  8,  'Pendiente',          '2025-07-12', 'Sede Providencia',            '15:00', NOW() - INTERVAL 1 DAY,  NULL, NULL, NULL,                             'marcela.herrera@gmail.com',   0, NULL,  NULL,         0, NULL, NULL),
('DON-004', 4, 'Andres Vega Campos',        'Persona Natural', 'Mochilas',               57,  4,  'Aprobado',           '2025-07-06', 'Punto Convenio Las Condes',   '11:00', NOW() - INTERVAL 7 DAY,  NULL, NULL, 'Mochilas casi sin uso.',        'andres.vega@empresa.cl',      1,  4,    'Valentina D.',0, NULL, NULL),
('DON-005', 5, 'Carolina Marin Espinoza',   'Persona Natural', 'Cuentos ilustrados',     65,  2,  'Rechazado',          NULL,         NULL,                           NULL,    NOW() - INTERVAL 3 DAY,  NULL, NULL, 'Libros con daño en portada.',   'carolina.marin@gmail.com',    0, NULL,  NULL,         0, NULL, NULL),
('DON-006', NULL,'Distribuidora Norte S.A.','Empresa',         'Ropa interior',          17,  15, 'Aprobado para Entrega','2025-07-15','Sede Providencia',            '14:00', NOW() - INTERVAL 10 DAY, NULL, NULL, 'Lote verificado completo.',     'donaciones@distribnorte.cl',  1,  1,    'Matias A.',  0, NULL, NULL),
('DON-007', NULL,'Supermercados Mayorista 10','Empresa',       'Panales',                82,  12, 'Entregado',          '2025-07-01', 'Sede Providencia',            '09:00', NOW() - INTERVAL 15 DAY, NULL, NULL, 'Entrega exitosa.',              'ong@mayorista10.cl',          1,  3,    'Lucas C.',   1, NOW() - INTERVAL 14 DAY, 'Camila Reyes'),
('DON-008', 2, 'Rodrigo Castro Pizarro',    'Persona Natural', 'Zapatillas deportivas',  21,  3,  'Pendiente',          '2025-07-18', 'Casillero Mall Plaza Tobalaba','10:30', NOW() - INTERVAL 1 DAY,  NULL, NULL, NULL,                             'rodrigo.castro@outlook.com',  0, NULL,  NULL,         0, NULL, NULL),
('DON-009', NULL,'BCI Seguros',             'Empresa',         'Lapices y plumones',     54,  30, 'Pendiente',          '2025-07-20', 'Punto Convenio Las Condes',   '16:00', NOW(),                   NULL, NULL, NULL,                             'responsabilidad@bci.cl',      0, NULL,  NULL,         0, NULL, NULL),
('DON-010', 1, 'Valentina Morales Fuentes', 'Persona Natural', 'Jabon y shampoo',        71,  6,  'Aprobado',           '2025-07-09', 'Sede Providencia',            '12:00', NOW() - INTERVAL 6 DAY,  NULL, NULL, 'Productos sellados sin abrir.', 'valentina.morales@gmail.com', 1,  7,    'Isabella G.',0, NULL, NULL),
('DON-011', 3, 'Marcela Herrera Jimenez',   'Persona Natural', 'Libros educativos',      67,  5,  'Pendiente',          '2025-07-22', 'Sede Providencia',            '10:00', NOW(),                   NULL, NULL, NULL,                             'marcela.herrera@gmail.com',   0, NULL,  NULL,         0, NULL, NULL),
('DON-012', NULL,'Fundacion Empresas Copec','Empresa',         'Zapatillas deportivas',  22,  20, 'Aprobado para Entrega','2025-07-16','Punto Convenio Las Condes',  '13:00', NOW() - INTERVAL 8 DAY,  NULL, NULL, 'Lote en excelente estado.',     'rse@empresascopec.cl',        1,  8,    'Grupo Hogar H.',0, NULL, NULL),
('DON-013', 4, 'Andres Vega Campos',        'Persona Natural', 'Calcetines',             32,  10, 'Entregado',          '2025-06-28', 'Casillero Mall Plaza Tobalaba','11:30', NOW() - INTERVAL 20 DAY, NULL, NULL, 'Calcetines nuevos, empacados.', 'andres.vega@empresa.cl',      1,  9,    'Diego I.',   1, NOW() - INTERVAL 19 DAY, 'Felipe Nunez'),
('DON-014', 5, 'Carolina Marin Espinoza',   'Persona Natural', 'Desodorantes',           83,  8,  'Pendiente',          '2025-07-25', 'Sede Providencia',            '15:30', NOW(),                   NULL, NULL, NULL,                             'carolina.marin@gmail.com',    0, NULL,  NULL,         0, NULL, NULL),
('DON-015', 1, 'Valentina Morales Fuentes', 'Persona Natural', 'Rompecabezas',           41,  4,  'Aprobado',           '2025-07-11', 'Sede Providencia',            '09:00', NOW() - INTERVAL 4 DAY,  NULL, NULL, 'Completos, revisados.',         'valentina.morales@gmail.com', 1, 10,    'Camila J.',  0, NULL, NULL);

-- Tickets de aprobación (donaciones >= 10 unidades o enviadas a Director)
INSERT IGNORE INTO ticket_aprobacion_director
  (donacion_id, beneficiario_id, director_usuario_id, director_nombre, estado, fecha_decision, motivo_rechazo, observaciones_director, fecha_creacion)
VALUES
(6,  1, 3, 'Luis Ramirez Vega', 'Aprobado',  NOW() - INTERVAL 9 DAY,  NULL,                                  'Donación grande aprobada para Hogar de Cristo.', NOW() - INTERVAL 10 DAY),
(7,  3, 3, 'Luis Ramirez Vega', 'Aprobado',  NOW() - INTERVAL 14 DAY, NULL,                                  'Pañales urgentes para lactante.',                NOW() - INTERVAL 15 DAY),
(12, 8, 3, 'Luis Ramirez Vega', 'Aprobado',  NOW() - INTERVAL 7 DAY,  NULL,                                  'Zapatillas para grupo hogar SENAME.',            NOW() - INTERVAL 8 DAY),
(9,  NULL, 3,'Luis Ramirez Vega','Pendiente', NULL,                    NULL,                                  NULL,                                             NOW());

-- Historial de entregas (donaciones ya entregadas)
INSERT IGNORE INTO historial_entregas
  (donacion_id, articulo_nombre, cantidad, beneficiario_id, beneficiario_nombre,
   region, observaciones, fecha_entrega)
VALUES
(7,  'Panales',       12, 3, 'Lucas C.',   'Metropolitana', 'Entrega recibida conforme por coordinadora. Pañales en perfecto estado.',                    NOW() - INTERVAL 14 DAY),
(13, 'Calcetines',    10, 9, 'Diego I.',   'Metropolitana', 'Niño presente en entrega. Calcetines nuevos talla 34-36.',                                    NOW() - INTERVAL 19 DAY);

-- ============================================================
--  ✅ Datos cargados correctamente:
--     • 14 usuarios  (1 SuperAdmin, 1 Admin, 1 Director, 4+2 Voluntarios, 5 Donantes)
--     • 6 voluntarios en db_donante + perfiles
--     • 5 donantes persona natural + 4 empresas
--     • 6 fundaciones colaboradoras
--     • 10 beneficiarios
--     • 15 donaciones en distintos estados
--     • 4 tickets de aprobación
--     • 2 entregas en historial
--
--  Credenciales:
--     super@abrazame.org    / Admin1234!
--     admin@abrazame.org    / Admin1234!
--     director@abrazame.org / Admin1234!
--     voluntario@abrazame.org / User1234!
-- ============================================================
