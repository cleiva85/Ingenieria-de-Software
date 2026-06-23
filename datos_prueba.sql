-- ============================================================
--  Fundación Abrázame — Datos de Prueba
--  Ejecutar en phpMyAdmin después de levantar todos los servicios
--  (los servicios crean las tablas automáticamente con ddl-auto=update)
-- ============================================================

-- ── 2 DONANTES PERSONA NATURAL ──────────────────────────────
USE db_donante;

INSERT INTO donantes_persona_natural
  (rut, primer_nombre, segundo_nombre, apellido_paterno, apellido_materno,
   fecha_nacimiento, correo_electronico, telefono, comuna, direccion_completa,
   cantidad, estado_donacion, articulo_nombre, articulo_id, url_imagen, url_video)
VALUES
(
  '12.345.678-9',
  'Valentina', 'Paz',
  'Morales', 'Fuentes',
  '1990-03-15',
  'valentina.morales@gmail.com',
  '+56 9 8765 4321',
  'Providencia',
  'Av. Providencia 1234, Depto 5B',
  3,
  'Pendiente',
  'Ropa de invierno niño',
  1,
  NULL, NULL
),
(
  '98.765.432-1',
  'Rodrigo', NULL,
  'Castro', 'Pizarro',
  '1985-07-22',
  'rodrigo.castro@outlook.com',
  '+56 9 1234 5678',
  'Las Condes',
  'Calle El Golf 456, Casa 3',
  5,
  'En revisión',
  'Juguetes educativos',
  2,
  NULL, NULL
);

-- ── 2 VOLUNTARIOS ───────────────────────────────────────────
INSERT INTO voluntarios
  (rut, primer_nombre, segundo_nombre, apellido_paterno, apellido_materno,
   fecha_nacimiento, correo_electronico, telefono, direccion_completa, comuna)
VALUES
(
  '15.432.876-5',
  'Camila', 'Ignacia',
  'Reyes', 'Vargas',
  '1998-11-08',
  'voluntario@abrazame.org',
  '+56 9 5555 1234',
  'Av. Vicuña Mackenna 890, Depto 12',
  'Ñuñoa'
),
(
  '20.111.333-K',
  'Felipe', NULL,
  'Núñez', 'Araya',
  '1995-04-30',
  'felipe.nunez@gmail.com',
  '+56 9 6666 7890',
  'Pasaje Los Aromos 23',
  'Maipú'
);

-- ── 5 ARTÍCULOS CATÁLOGO ────────────────────────────────────
USE db_catalogo;

INSERT INTO articulos_catalogo
  (nombre, categoria, rango_edad, prioridad, meta_stock, stock_actual, activo, descripcion)
VALUES
(
  'Ropa de invierno niño',
  'Vestuario',
  '2-8 años',
  'Alta',
  50, 12,
  TRUE,
  'Abrigos, chaquetas y sweaters en buen estado para niños en edad escolar.'
),
(
  'Juguetes educativos',
  'Juguetes',
  '3-10 años',
  'Media',
  30, 7,
  TRUE,
  'Juegos de mesa, rompecabezas y juguetes que estimulen el aprendizaje.'
),
(
  'Útiles escolares',
  'Educación',
  '6-14 años',
  'Alta',
  100, 34,
  TRUE,
  'Cuadernos, lápices, estuches y materiales para el año escolar.'
),
(
  'Calzado infantil',
  'Vestuario',
  '1-12 años',
  'Media',
  40, 5,
  TRUE,
  'Zapatos, zapatillas y botines en buen estado para niños y niñas.'
),
(
  'Libros infantiles',
  'Educación',
  '4-12 años',
  'Baja',
  60, 21,
  TRUE,
  'Cuentos, novelas y libros educativos para fomentar la lectura en los niños.'
);

-- ============================================================
--  ✅ Listo! Recuerda tener todos los servicios levantados
--     antes de ejecutar este script para que las tablas existan.
-- ============================================================
