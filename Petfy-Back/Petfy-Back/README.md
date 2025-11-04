# Petfy Backend

Backend desarrollado en Spring Boot para la aplicación Petfy - Sistema de paseo de mascotas.

## 📋 Descripción

Este backend proporciona una API REST para gestionar usuarios, paseadores, paseos y calificaciones. Utiliza Basic Authentication para la seguridad y está diseñado para conectarse con el frontend Angular (petfy-app).

## 🏗️ Estructura del Proyecto

```
src/main/java/Petfy/Petfy_Back/
├── config/              # Configuración de seguridad y CORS
│   ├── SecurityConfig.java
│   └── CustomUserDetailsService.java
├── controller/          # Controladores REST (Endpoints)
│   ├── AuthController.java
│   ├── PaseoController.java
│   ├── PaseadorController.java
│   └── CalificacionController.java
├── dto/                 # Data Transfer Objects
│   ├── request/         # DTOs para requests del frontend
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   ├── PaseoRequest.java
│   │   ├── WalkerApplicationRequest.java
│   │   └── CalificacionRequest.java
│   └── response/        # DTOs para responses al frontend
│       ├── UsuarioResponse.java
│       ├── PaseoResponse.java
│       └── ApiResponse.java
├── model/               # Entidades JPA
│   ├── Usuario.java
│   ├── Paseador.java
│   ├── Paseo.java
│   └── Calificacion.java
├── repository/          # Repositorios JPA
│   ├── UsuarioRepository.java
│   ├── PaseadorRepository.java
│   ├── PaseoRepository.java
│   └── CalificacionRepository.java
└── service/             # Interfaces de servicios
    ├── AuthService.java
    ├── PaseoService.java
    ├── PaseadorService.java
    └── CalificacionService.java
```

## 🚀 Tecnologías Utilizadas

- **Spring Boot 3.5.7**
- **Spring Security** (Basic Authentication)
- **Spring Data JPA** (Persistencia)
- **H2 Database** (Base de datos en memoria para desarrollo)
- **Lombok** (Reducción de código boilerplate)
- **Jakarta Validation** (Validación de datos)

## 📦 Dependencias Principales

- `spring-boot-starter-web` - Para APIs REST
- `spring-boot-starter-data-jpa` - Para persistencia
- `spring-boot-starter-security` - Para autenticación
- `spring-boot-starter-validation` - Para validaciones
- `h2` - Base de datos en memoria
- `lombok` - Para reducir código

## 🗄️ Modelo de Datos

### Usuario
- Representa a los usuarios del sistema (clientes y paseadores)
- Campos: id, username, email, password (encriptado), rol, fechaRegistro
- Roles: CUSTOMER, WALKER

### Paseador
- Información adicional de usuarios que son paseadores
- Relación 1:1 con Usuario
- Campos: id, usuario, telefono, descripcion, rutaDocumento, estadoAprobacion, calificacionPromedio

### Paseo
- Representa una solicitud/confirmación de paseo
- Relación con Usuario (cliente) y Paseador
- Campos: id, cliente, paseador, fecha, horaInicio, horaFin, direccion, estado, isCompleted
- Estados: PENDIENTE, CONFIRMADO, EN_PROGRESO, FINALIZADO, CANCELADO

### Calificacion
- Calificaciones de paseos o de la aplicación
- Relación con Usuario y Paseo (opcional)
- Campos: id, usuario, paseo, calificacion (1-5), tipo, comentario

## 🔐 Autenticación

El sistema utiliza **Basic Authentication**:

1. El frontend envía credenciales en el header `Authorization: Basic base64(email:password)`
2. Spring Security valida las credenciales usando `CustomUserDetailsService`
3. Las contraseñas se encriptan con BCrypt antes de guardarse en la base de datos

**Endpoints públicos** (no requieren autenticación):
- `POST /api/auth/register`
- `POST /api/auth/login`

**Endpoints protegidos** (requieren autenticación):
- Todos los demás endpoints bajo `/api/*`

## 📡 Endpoints Principales

### Autenticación (`/api/auth`)
- `POST /api/auth/register` - Registro de nuevo usuario
- `POST /api/auth/login` - Login de usuario
- `GET /api/auth/current-user` - Obtener usuario actual
- `POST /api/auth/logout` - Logout (manejo principal en frontend)

### Paseos (`/api/paseos`)
- `POST /api/paseos` - Crear nuevo paseo
- `PUT /api/paseos/{id}` - Actualizar paseo
- `GET /api/paseos/cliente` - Obtener paseos del cliente
- `GET /api/paseos/cliente/pendientes` - Paseos pendientes del cliente
- `GET /api/paseos/cliente/confirmados` - Paseos confirmados del cliente
- `GET /api/paseos/cliente/finalizados` - Paseos finalizados del cliente
- `GET /api/paseos/pendientes` - Todos los paseos pendientes (para paseadores)
- `GET /api/paseos/paseador/confirmados` - Paseos confirmados del paseador
- `POST /api/paseos/{id}/aceptar` - Aceptar paseo (paseador)
- `POST /api/paseos/{id}/iniciar` - Iniciar paseo
- `POST /api/paseos/{id}/finalizar` - Finalizar paseo
- `DELETE /api/paseos/{id}` - Cancelar paseo

### Paseadores (`/api/paseadores`)
- `POST /api/paseadores/solicitar` - Solicitar ser paseador
- `GET /api/paseadores/disponibles` - Lista de paseadores disponibles

### Calificaciones (`/api/calificaciones`)
- `POST /api/calificaciones` - Crear calificación

## 🔧 Configuración

### application.properties

```properties
# Puerto del servidor
server.port=8080

# Base de datos H2
spring.datasource.url=jdbc:h2:mem:petfydb
spring.datasource.username=sa

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Consola H2 (http://localhost:8080/h2-console)
spring.h2.console.enabled=true
```

### CORS

El backend está configurado para aceptar peticiones desde:
- Origin: `http://localhost:4200` (Frontend Angular)

## 📝 Estado del Proyecto

⚠️ **IMPORTANTE**: Este proyecto contiene la **estructura completa** pero **NO está implementada la lógica de negocio**. 

Los controladores, servicios y repositorios están definidos con:
- ✅ Interfaces y clases base creadas
- ✅ Endpoints definidos con documentación
- ✅ Validaciones de datos configuradas
- ✅ Relaciones entre entidades establecidas
- ❌ Lógica de negocio NO implementada (marcada con `// TODO:`)

### Próximos Pasos para Implementar

1. Implementar clases de servicio (AuthServiceImpl, PaseoServiceImpl, etc.)
2. Implementar la lógica de negocio en los controladores
3. Agregar manejo de excepciones global
4. Implementar tests unitarios e integración
5. Configurar base de datos de producción (PostgreSQL/MySQL)

## 🔗 Conexión con Frontend

Para ver cómo se conectaría el frontend con el backend, consulta el archivo:
**`CONEXION_FRONTEND_BACKEND.md`**

Este documento explica:
- Flujo de autenticación
- Mapeo de endpoints frontend-backend
- Estructura de datos
- Ejemplos de implementación

## 🧪 Ejecutar el Proyecto

```bash
# Compilar el proyecto
mvn clean install

# Ejecutar la aplicación
mvn spring-boot:run

# La aplicación estará disponible en:
# http://localhost:8080
```

## 📚 Documentación Adicional

- **CONEXION_FRONTEND_BACKEND.md** - Detalles sobre cómo conectar frontend y backend
- **Endpoints** - Documentados con comentarios en cada controlador
- **Entidades** - Documentadas con comentarios Javadoc

## 👥 Contribución

Este es un proyecto académico que muestra la arquitectura y estructura de un backend REST para una aplicación de paseo de mascotas.


