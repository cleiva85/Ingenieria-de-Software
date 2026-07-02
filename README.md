# Proyecto Abrázame — Sistema de Gestión de Donaciones (SGD)

Plataforma web de gestión de donaciones para la Fundación Abrázame, construida como un conjunto de microservicios independientes (Java 21 / Spring Boot 3) detrás de un API Gateway, con frontend estático (HTML/CSS/JS) servido desde el propio Gateway. El sistema contempla cinco roles: **Donante**, **Voluntario**, **Administrador**, **Director** y **SuperAdmin**.

---

## Arquitectura

| Microservicio     | Puerto | Prefijo de ruta (Gateway)          | Base de datos           |
|-------------------|--------|------------------------------------|-------------------------|
| service-auth      | 8081   | `/auth/**`                         | `db_auth`               |
| service-donante   | 8082   | `/donantes/**`, `/voluntarios/**`  | `db_donante`            |
| service-gestion   | 8083   | `/gestion/**`                      | `db_gestion_donaciones` |
| service-catalogo  | 8084   | `/catalogo/**`                     | `db_catalogo`           |
| service-gateway   | 9090   | — (enrutador + sirve los HTML)     | —                       |

Cada microservicio tiene su propia base de datos MySQL (patrón *database per service*). Todo el tráfico del frontend pasa siempre por el Gateway (puerto 9090); **nunca** se debe llamar directamente a los puertos 8081–8084 desde el frontend.

---

## Requisitos previos

- Java 21 (JDK)
- MySQL corriendo en `localhost:3306` (vía XAMPP u otro)
- Maven — `service-donante`, `service-gestion`, `service-catalogo` y `service-gateway` incluyen wrapper (`mvnw`); **`service-auth` no trae `mvnw`**, por lo que necesitas tener `mvn` instalado globalmente (o ejecutarlo desde tu IDE).

---

## 1. Preparar la base de datos

1. Inicia MySQL (XAMPP → Start en Apache y MySQL).
2. Abre phpMyAdmin y ejecuta `schema_completo_abrazame.sql` (en la raíz del proyecto). Crea las 4 bases de datos con sus tablas y los usuarios del sistema.
3. Una vez levantados todos los servicios (ver paso 2), ejecuta `datos_prueba_completo.sql` para cargar datos de ejemplo.

> Las credenciales por defecto en `application.properties` de cada servicio son `root` sin contraseña (configuración estándar de XAMPP). Cada servicio usa `spring.jpa.hibernate.ddl-auto=update`, por lo que las tablas se crean/actualizan automáticamente al arrancar.

---

## 2. Arrancar los microservicios

Levanta primero los cuatro servicios de backend (en cualquier orden) y **el Gateway al final**:

```powershell
# Terminal 1 — Auth (requiere mvn instalado globalmente, no tiene mvnw)
cd service-auth
mvn spring-boot:run

# Terminal 2 — Donante
cd service-donante
.\mvnw spring-boot:run

# Terminal 3 — Gestión
cd service-gestion
.\mvnw spring-boot:run

# Terminal 4 — Catálogo
cd service-catalogo
.\mvnw spring-boot:run

# Terminal 5 — Gateway (siempre al final)
cd service-gateway
.\mvnw spring-boot:run
```

> **Alternativa con VS Code:** el proyecto incluye `.vscode/launch.json` con configuración de debug para cada microservicio. Lánzalos desde el Spring Boot Dashboard respetando el mismo orden.

---

## 3. Abrir el sistema

Una vez que el Gateway esté corriendo, abre estas URLs en el navegador:

| Página                    | URL                                               | Descripción                                                      |
|---------------------------|---------------------------------------------------|------------------------------------------------------------------|
| Inicio                    | http://localhost:9090/index.html                  | Landing page institucional                                       |
| Catálogo                  | http://localhost:9090/catalogo.html               | Catálogo público de necesidades (único punto de entrada a donar) |
| Donar                     | http://localhost:9090/formulario_donante.html     | Formulario público de donación (sin registro)                    |
| Postular como Voluntario  | http://localhost:9090/formulario_voluntario.html  | Formulario de postulación de voluntariado                        |
| Activar cuenta Voluntario | http://localhost:9090/activar_voluntario.html     | Activación de cuenta vía token (enlace enviado por correo)       |
| Login usuarios            | http://localhost:9090/login_usuario.html          | Login general con selector de rol                                |
| Panel Admin               | http://localhost:9090/panel_admin.html            | Gestión de donaciones, voluntarios, inventario y trazabilidad    |
| Vista Director            | http://localhost:9090/vista_director.html         | Aprobación donaciones ≥10 u., asignación de beneficiarios        |
| Vista SuperAdmin          | http://localhost:9090/vista_superadmin.html       | Gestión global de usuarios y roles                               |
| Vista Voluntario          | http://localhost:9090/vista_voluntario.html       | Panel de inspección y prevalidación de donaciones                |
| Mapa de Entregas          | http://localhost:9090/mapa_entregas.html          | Búsqueda de punto de entrega por RUT/código de donación          |

> ⚠️ **No abras los HTML haciendo doble clic.** Usa siempre `http://localhost:9090/...` para que las llamadas al backend pasen correctamente por el Gateway.

---

## 4. Credenciales de prueba

| Email                       | Contraseña | Rol        | Vista que abre            |
|-----------------------------|------------|------------|---------------------------|
| super@abrazame.org          | Admin1234! | SuperAdmin | vista_superadmin.html     |
| admin@abrazame.org          | Admin1234! | Admin      | panel_admin.html          |
| director@abrazame.org       | Admin1234! | Director   | vista_director.html       |
| voluntario@abrazame.org     | User1234!  | Voluntario | vista_voluntario.html     |
| valentina.morales@gmail.com | User1234!  | Donante    | mapa_entregas.html        |

---

## 5. Flujo principal del sistema

```
[Donante]
  catalogo.html → formulario_donante.html → DON-NNN generado → correo de confirmación

[Voluntario]
  vista_voluntario.html → prevalida artículo en terreno → comentario y estado

[Admin] donaciones < 10 unidades
  panel_admin.html → aprueba/rechaza → correo automático al donante

[Admin + Director] donaciones ≥ 10 unidades
  panel_admin.html → deriva a Director → vista_director.html → ticket de aprobación

[Director]
  vista_director.html → pestaña "Asignar Beneficiario" → selecciona beneficiario →
  crea ticket → pestaña "Tickets Aprobación" → aprueba/rechaza →
  donación pasa a "Aprobado para Entrega"

[Voluntario]
  vista_voluntario.html → confirma entrega física al beneficiario →
  stock se descuenta automáticamente en service-catalogo →
  registro inmutable en historial_entregas

[Donante]
  mapa_entregas.html (estado Aprobado) → selecciona punto de entrega + fecha + hora
```

---

## 6. Módulo Beneficiarios (v3.0)

El módulo de beneficiarios gestiona a quién se entrega finalmente cada donación. Está implementado en `BeneficiarioController` dentro de `service-gestion`.

### Entidades principales

- **`beneficiarios`** — niño, familia o grupo hogar que recibe la donación. El contacto es siempre un funcionario de la fundación/residencia, nunca un familiar externo.
- **`fundaciones_colaboradoras`** — fundaciones o residencias donde están alojados los beneficiarios (tipo: Fundacion / Residencia).
- **`ticket_aprobacion_director`** — ticket creado automáticamente cuando el Director asigna un beneficiario a una donación aprobada. Estados: Pendiente → Aprobado / Rechazado.
- **`historial_entregas`** — registro inmutable de entregas confirmadas (sin endpoints PUT/DELETE). Incluye donacion_id, artículo, cantidad, beneficiario, fundación/residencia, confirmado_por y fecha.

### Endpoints disponibles (todos bajo `/gestion/`)

| Método | Endpoint                                  | Descripción                                           |
|--------|-------------------------------------------|-------------------------------------------------------|
| GET    | `/gestion/fundaciones`                    | Listar fundaciones/residencias colaboradoras activas  |
| GET    | `/gestion/beneficiarios`                  | Listar beneficiarios activos (con fundación enriquecida) |
| GET    | `/gestion/beneficiarios/{id}`             | Detalle de un beneficiario                            |
| POST   | `/gestion/beneficiarios`                  | Crear beneficiario (Admin/Director)                   |
| PUT    | `/gestion/beneficiarios/{id}`             | Actualizar datos del beneficiario                     |
| PUT    | `/gestion/{donId}/asignar-beneficiario`   | Director asigna beneficiario + crea ticket            |
| GET    | `/gestion/tickets`                        | Listar todos los tickets de aprobación                |
| PUT    | `/gestion/tickets/{ticketId}/resolver`    | Director aprueba o rechaza un ticket                  |
| PUT    | `/gestion/{donId}/confirmar-entrega`      | Voluntario/Admin confirma entrega física (descuenta stock) |
| GET    | `/gestion/historial-entregas`             | Historial completo de entregas (trazabilidad)         |
| GET    | `/gestion/historial-entregas/beneficiario/{id}` | Historial de un beneficiario específico         |

### Flujo de asignación

1. Director entra a `vista_director.html` → pestaña **Asignar Beneficiario**
2. Busca beneficiario por nombre, filtra por rango de edad o fundación
3. Hace clic en **+ Nueva Asignación** → selecciona la donación aprobada
4. El sistema crea el ticket en estado `Pendiente` y cambia el estado de la donación a `Asignado`
5. Director va a pestaña **Tickets Aprobación** → aprueba o rechaza
   - Si aprueba → donación pasa a `Aprobado para Entrega`
   - Si rechaza → `beneficiarioId` y `beneficiarioNombre` se limpian automáticamente para reasignación
6. Voluntario o Admin confirma la entrega física → stock se descuenta en `service-catalogo` vía WebClient → registro en `historial_entregas`

### Vista Director — 7 pestañas

| Pestaña              | Descripción                                                    |
|----------------------|----------------------------------------------------------------|
| Donaciones >10 u.    | Tabla con donaciones críticas pendientes de aprobación Director|
| Asignar Beneficiario | Buscador + filtros por edad y fundación, botón Nueva Asignación|
| Tickets Aprobación   | Bandeja de tickets con botones Aprobar / Rechazar              |
| Inventario           | Grilla artículos catálogo con habilitar/deshabilitar           |
| Entregas Realizadas  | Historial inmutable de entregas confirmadas                    |
| Reportes             | Generación de reportes PDF y Excel                             |
| Trazabilidad         | Línea de tiempo de todas las acciones del sistema              |

---

## 7. Roles y permisos

| Rol        | Acceso principal                                                                    |
|------------|-------------------------------------------------------------------------------------|
| Donante    | Catálogo, formulario de donación, mapa de entregas (solo su donación)              |
| Voluntario | Vista de donaciones asignadas, prevalidación y confirmación de entrega             |
| Admin      | Panel completo: donaciones, voluntarios, inventario, trazabilidad                  |
| Director   | Aprobación donaciones ≥10 u., asignación beneficiarios, resolución tickets         |
| SuperAdmin | Gestión global de usuarios, roles, eliminación individual y masiva                 |

---

## 8. Aprobación de voluntarios

1. El voluntario se registra en `formulario_voluntario.html` → queda en estado **Pendiente**
2. Login en `login_usuario.html` con `admin@abrazame.org` / `Admin1234!`
3. Panel Admin → pestaña **Voluntarios** → clic en `[✅ Aprobar]`
4. El sistema envía correo automático con link de activación (token válido 24h)
5. El voluntario accede a `activar_voluntario.html`, crea su contraseña e inicia sesión

También por API:
```
PUT http://localhost:9090/auth/voluntarios/{id}/aprobar
```

---

## 9. Notas

- El correo Gmail SMTP está configurado en `application.properties` de `service-auth` y `service-gestion` con la cuenta `proyectoabrazame1@gmail.com`. **No subir estas credenciales a un repositorio público** — moverlas a variables de entorno antes del despliegue en producción.
- La tabla `historial_entregas` es **inmutable** — solo se puede insertar, nunca modificar ni eliminar.
- Para recrear el esquema desde cero, ejecuta nuevamente `schema_completo_abrazame.sql`.
- El campo `estado` en `Donacion` es String libre — pendiente migrar a enum como `EstadoTicket` en una próxima versión.
- Pendiente implementar control de rol en los endpoints del Gateway para restringir acceso según rol efectivo.
