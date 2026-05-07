# Proyecto Abrazame v3

## Como arrancar

### 1. Iniciar MySQL
Asegurate de que MySQL este corriendo en localhost:3306.

### 2. Arrancar microservicios (en este orden)

```powershell
# Terminal 1
cd service-donante
.\mvnw spring-boot:run

# Terminal 2
cd service-gestion
.\mvnw spring-boot:run

# Terminal 3
cd service-catalogo
.\mvnw spring-boot:run

# Terminal 4
cd service-gateway
.\mvnw spring-boot:run
```

### 3. Abrir los HTMLs

Una vez que el Gateway este corriendo, abre estas URLs en el browser:

| Pagina | URL |
|--------|-----|
| Catalogo | http://localhost:9090/catalogo.html |
| Registro Donante | http://localhost:9090/formulario_donante.html |
| Registro Voluntario | http://localhost:9090/formulario_voluntario.html |
| Mapa Entregas | http://localhost:9090/mapa_entregas.html |
| Panel Admin | http://localhost:9090/panel_admin.html |

> NO abras los HTML haciendo doble click en el archivo.
> Siempre usa las URLs de http://localhost:9090/

## Puertos
- service-donante: 8082
- service-gestion: 8083
- service-catalogo: 8084
- service-gateway: 9090 (tambien sirve los HTMLs)
