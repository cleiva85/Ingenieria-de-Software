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
2. Abre phpMyAdmin y ejecuta `schema_completo_abrazame.sql` (en la raíz del proyecto). Esto crea las 4 bases de datos con sus tablas y los usuarios del sistema.
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

> **Alternativa con VS Code:** el proyecto incluye `.vscode/launch.json` con una configuración de debug para cada microservicio. Si usas el Spring Boot Dashboard, lánzalos desde ahí respetando el mismo orden.

---

## 3. Abrir el sistema

Una vez que el Gateway esté corriendo, abre estas URLs en el navegador:

| Página                    | URL                                               | Descripción                                              |
|---------------------------|---------------------------------------------------|----------------------------------------------------------|
| Inicio                    | http://localhost:9090/index.html                  | Landing page institucional                               |
| Catálogo                  | http://localhost:9090/catalogo.html               | Catálogo público de necesidades                          |
| Donar                     | http://localhost:9090/formulario_donante.html     | Formulario público de donación (sin registro)            |
| Postular como Voluntario  | http://localhost:9090/formulario_voluntario.html  | Formulario de postulación de voluntariado                |
| Activar cuenta Voluntario | http://localhost:9090/activar_voluntario.html     | Activación de cuenta vía token (enlace enviado por correo)|
| Acceso Usuarios           | http://localhost:9090/login_usuario.html          | Login general (selector de rol)                          |
| Panel Admin               | http://localhost:9090/panel_admin.html            | Gestión de donaciones, voluntarios, inventario y trazabilidad |
| Vista Director            | http://localhost:9090/vista_director.html         | Aprobación de donaciones ≥10 u., asignación de beneficiarios |
| Vista SuperAdmin          | http://localhost:9090/vista_superadmin.html       | Gestión global de usuarios y roles                       |
| Vista Voluntario          | http://localhost:9090/vista_voluntario.html       | Panel de inspección/prevalidación de donaciones          |
| Mapa de Entregas          | http://localhost:9090/mapa_entregas.html          | Búsqueda de punto de entrega por RUT/código de donación  |

> ⚠️ **No abras los HTML haciendo doble clic.** Usa siempre `http://localhost:9090/...` para que las llamadas al backend pasen correctamente por el Gateway.

---

## 4. Credenciales de prueba

| Email                        | Contraseña  | Rol        | Vista que abre           |
|------------------------------|-------------|------------|--------------------------|
| super@abrazame.org           | Admin1234!  | SuperAdmin | vista_superadmin.html    |
| admin@abrazame.org           | Admin1234!  | Admin      | panel_admin.html         |
| director@abrazame.org        | Admin1234!  | Director   | vista_director.html      |
| voluntario@abrazame.org      | User1234!   | Voluntario | vista_voluntario.html    |
| valentina.morales@gmail.com  | User1234!   | Donante    | mapa_entregas.html       |

---

## 5. Flujo principal del sistema

```
[Donante]
  catalogo.html → formulario_donante.html → DON-NNN generado → correo de confirmación

[Admin]
  panel_admin.html → aprueba/rechaza donaciones < 10 unidades → correo al donante
  panel_admin.html → donaciones ≥ 10 u. → se envían a Director

[Director]
  vista_director.html → aprueba ticket → asigna beneficiario → donación queda "Aprobado para Entrega"

[Voluntario]
  vista_voluntario.html → prevalida artículo en terreno → confirma entrega al beneficiario

[Donante]
  mapa_entregas.html (estado Aprobado) → selecciona punto de entrega + fecha + hora
```

---

## 6. Roles y permisos

| Rol        | Acceso principal                                                          |
|------------|---------------------------------------------------------------------------|
| Donante    | Catálogo, formulario de donación, mapa de entregas (solo su donación)    |
| Voluntario | Vista de donaciones asignadas, prevalidación y confirmación de entrega   |
| Admin      | Panel completo: donaciones, voluntarios, inventario, trazabilidad        |
| Director   | Aprobación de donaciones críticas (≥10 u.), asignación de beneficiarios  |
| SuperAdmin | Gestión global de usuarios, roles, eliminación individual y masiva       |

---

## 7. Aprobación de voluntarios

Los voluntarios se registran en `formulario_voluntario.html` y quedan en estado **Pendiente**. Para aprobar:

1. Inicia sesión en `login_usuario.html` con `admin@abrazame.org` / `Admin1234!`
2. Ve a la pestaña **Voluntarios** en `panel_admin.html`
3. Haz clic en `[✅ Aprobar]` — el sistema envía automáticamente un correo al voluntario con el link de activación
4. El voluntario entra a `activar_voluntario.html`, crea su contraseña y ya puede iniciar sesión

También puedes aprobar directamente por API:
```
PUT http://localhost:9090/auth/voluntarios/{id}/aprobar
```

---

## Notas

- El correo (Gmail SMTP) está configurado en `application.properties` de `service-auth` y `service-gestion`. Evita subir esas credenciales a un repositorio público.
- Para recrear el esquema desde cero, ejecuta nuevamente `schema_completo_abrazame.sql` (usa `CREATE TABLE IF NOT EXISTS`).
- La tabla `historial_entregas` es **inmutable** — solo se puede insertar, nunca modificar ni eliminar, para garantizar trazabilidad.
