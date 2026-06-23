# Proyecto Abrázame — Sistema de Gestión de Donaciones (SGD)

Plataforma web de gestión de donaciones para la Fundación Abrázame, construida como un conjunto de microservicios independientes (Java 21 / Spring Boot 3) detrás de un API Gateway, con frontend estático (HTML/CSS/JS) servido desde el propio Gateway. El sistema contempla cinco roles: **Donante**, **Voluntario**, **Administrador**, **Director** y **SuperAdmin**.

## Arquitectura

| Microservicio | Puerto | Prefijo de ruta (Gateway) | Base de datos |
|---|---|---|---|
| service-auth | 8081 | `/auth/**` | `db_auth` |
| service-donante | 8082 | `/donantes/**`, `/voluntarios/**` | `db_donante` |
| service-gestion | 8083 | `/gestion/**` | `db_gestion_donaciones` |
| service-catalogo | 8084 | `/catalogo/**` | `db_catalogo` |
| service-gateway | 9090 | — (enrutador + sirve los HTML) | — |

Cada microservicio tiene su propia base de datos MySQL (patrón *database per service*). Todo el tráfico del frontend hacia los microservicios pasa siempre por el Gateway (puerto 9090); **nunca** se debe llamar directamente a los puertos 8081–8084 desde el frontend.

## Requisitos previos

- Java 21 (JDK)
- MySQL corriendo en `localhost:3306` (vía XAMPP u otro)
- Maven — `service-donante`, `service-gestion`, `service-catalogo` y `service-gateway` incluyen wrapper (`mvnw`); **`service-auth` no trae `mvnw`**, por lo que necesitas tener `mvn` instalado globalmente (o ejecutarlo desde tu IDE) para levantarlo.

## 1. Preparar la base de datos

1. Inicia MySQL (XAMPP → Start en Apache y MySQL).
2. Abre phpMyAdmin y ejecuta `schema_completo_abrazame.sql` (en la raíz del proyecto). Esto crea las 4 bases de datos (`db_auth`, `db_donante`, `db_gestion_donaciones`, `db_catalogo`) con sus tablas.
3. (Opcional) Una vez levantados todos los servicios — ver paso 2 — ejecuta `datos_prueba.sql` para cargar datos de ejemplo (donantes, voluntarios, donaciones, catálogo).

> Las credenciales por defecto en `application.properties` de cada servicio son `root` sin contraseña (configuración estándar de XAMPP). Cada servicio también tiene `spring.jpa.hibernate.ddl-auto=update`, por lo que las tablas se crean/actualizan automáticamente al arrancar si aún no existen.

## 2. Arrancar los microservicios

Levanta primero los cuatro servicios de backend (en cualquier orden) y **el Gateway al final**:

```powershell
# Terminal 1 — Auth (no tiene mvnw, requiere mvn instalado o IDE)
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

# Terminal 5 — Gateway (al final)
cd service-gateway
.\mvnw spring-boot:run
```

> **Alternativa con VS Code:** el proyecto incluye `.vscode/launch.json` con una configuración de debug para cada microservicio (Auth, Donante, Gestión, Catálogo, Gateway). Si usas VS Code con el Spring Boot Dashboard, puedes lanzar cada uno desde ahí en vez de usar la terminal — solo respeta el mismo orden (los 4 servicios de backend primero, Gateway al final). Esa configuración referencia un archivo `.env` en la raíz que el proyecto aún no incluye; si no lo creas, VS Code lo ignora silenciosamente y usa los valores de `application.properties` igualmente.

## 3. Abrir el sistema

Una vez que el Gateway esté corriendo, abre estas URLs en el navegador:

| Página | URL | Descripción |
|---|---|---|
| Inicio | http://localhost:9090/index.html | Landing page institucional |
| Catálogo | http://localhost:9090/catalogo.html | Catálogo público de necesidades |
| Donar | http://localhost:9090/formulario_donante.html | Formulario público de donación (sin registro) |
| Postular como Voluntario | http://localhost:9090/formulario_voluntario.html | Formulario de postulación de voluntariado |
| Activar cuenta Voluntario | http://localhost:9090/activar_voluntario.html | Activación de cuenta vía token (enlace enviado por correo) |
| Acceso Usuarios | http://localhost:9090/login_usuario.html | Login general (selector de rol) |
| Panel Admin | http://localhost:9090/panel_admin.html | Gestión de donaciones, voluntarios, inventario y trazabilidad |
| Vista Director | http://localhost:9090/vista_director.html | Aprobación final de donaciones críticas (≥10 unidades) |
| Vista SuperAdmin | http://localhost:9090/vista_superadmin.html | Gestión global de usuarios y roles |
| Vista Voluntario | http://localhost:9090/vista_voluntario.html | Panel de inspección/prevalidación de donaciones |
| Mapa de Entregas | http://localhost:9090/mapa_entregas.html | Búsqueda de punto de entrega por RUT/código de donación |

> ⚠️ **No abras los HTML haciendo doble clic en el archivo.** Siempre usa las URLs `http://localhost:9090/...` para que las llamadas al backend pasen correctamente por el Gateway.

## Notas

- El correo (Gmail SMTP) está configurado directamente en `application.properties` de `service-auth` y `service-gestion`. Evita subir esas credenciales a un repositorio público; considera moverlas a variables de entorno antes de publicar el repo.
- Para borrar y recrear el esquema desde cero, vuelve a ejecutar `schema_completo_abrazame.sql` (usa `CREATE TABLE IF NOT EXISTS`, por lo que no se pierde data existente).

