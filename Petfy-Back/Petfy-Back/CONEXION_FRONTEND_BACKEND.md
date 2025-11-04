# Conexión Frontend-Backend - Petfy

Este documento explica cómo se conectaría el frontend Angular (petfy-app) con el backend Spring Boot (petfy-back).

## Arquitectura General

```
Frontend (Angular)          Backend (Spring Boot)
┌─────────────────┐         ┌──────────────────┐
│  Components     │  ─────> │  Controllers     │
│  Services       │  HTTP   │  Services        │
│  localStorage   │  REST   │  Repositories    │
│                 │  API    │  Entities        │
│                 │         │  Database (H2)   │
└─────────────────┘         └──────────────────┘
```

## Autenticación: Basic Auth

### Cómo funciona Basic Auth

El frontend envía las credenciales en cada petición HTTP usando el header `Authorization`:

```
Authorization: Basic base64(email:password)
```

### Flujo de Login

1. **Frontend**: `login.component.ts` → `authService.login()`
   ```typescript
   // Usuario ingresa email y password
   const credentials = { email: "user@example.com", password: "password123" }
   ```

2. **Backend**: `POST /api/auth/login`
   - Recibe `LoginRequest` con email y password
   - Valida credenciales
   - Retorna `UsuarioResponse`

3. **Frontend**: Almacena credenciales para futuras peticiones
   ```typescript
   // En localStorage o sessionStorage
   const authHeader = btoa(`${email}:${password}`)
   localStorage.setItem('authToken', authHeader)
   ```

4. **Frontend**: En cada petición HTTP, incluye el header
   ```typescript
   headers: {
     'Authorization': `Basic ${localStorage.getItem('authToken')}`
   }
   ```

### Flujo de Logout

En Basic Auth stateless, el logout se maneja en el frontend:
- Eliminar credenciales del localStorage
- Limpiar estado del usuario
- No se requiere petición al backend (aunque existe endpoint por si se implementa JWT en el futuro)

## Mapeo de Endpoints

### 1. Autenticación

#### Registro
- **Frontend**: `register.component.ts` → `onSubmit()`
- **Backend**: `POST /api/auth/register`
- **Request**: `RegisterRequest` (username, email, password, confirmPassword)
- **Response**: `ApiResponse<UsuarioResponse>`
- **Conexión**: Frontend envía datos del formulario → Backend crea usuario → Retorna usuario creado

#### Login
- **Frontend**: `login.component.ts` → `onSubmit()`
- **Backend**: `POST /api/auth/login`
- **Request**: `LoginRequest` (email, password)
- **Response**: `ApiResponse<UsuarioResponse>`
- **Conexión**: Frontend envía credenciales → Backend valida → Retorna usuario → Frontend guarda credenciales

#### Usuario Actual
- **Frontend**: `auth.service.ts` → `getCurrentUser()`
- **Backend**: `GET /api/auth/current-user`
- **Header**: `Authorization: Basic ...`
- **Response**: `UsuarioResponse`
- **Conexión**: Frontend envía header con credenciales → Backend identifica usuario → Retorna datos

### 2. Paseadores

#### Solicitud de Paseador
- **Frontend**: `walker-application.component.ts` → `submitApplication()`
- **Backend**: `POST /api/paseadores/solicitar`
- **Request**: `FormData` (phone, description, documentImage)
- **Response**: `ApiResponse<Void>`
- **Conexión**: Frontend envía formulario + imagen → Backend guarda documento → Crea registro Paseador → Actualiza rol

#### Lista de Paseadores
- **Frontend**: `request.component.ts` → `walkers[]`
- **Backend**: `GET /api/paseadores/disponibles`
- **Response**: `List<PaseadorResponse>`
- **Conexión**: Frontend solicita lista → Backend retorna paseadores aprobados → Frontend muestra en dropdown

### 3. Paseos

#### Crear Paseo
- **Frontend**: `request.component.ts` → `confirmRequest()`
- **Backend**: `POST /api/paseos`
- **Request**: `PaseoRequest` (date, startTime, endTime, address, walker)
- **Response**: `ApiResponse<PaseoResponse>`
- **Conexión**: Frontend envía datos del paseo → Backend crea paseo → Si es "Aleatorio", asigna paseador → Retorna paseo creado

#### Actualizar Paseo
- **Frontend**: `request.component.ts` → `confirmRequest()` cuando `isEditing = true`
- **Backend**: `PUT /api/paseos/{id}`
- **Request**: `PaseoRequest` (datos actualizados)
- **Response**: `ApiResponse<PaseoResponse>`
- **Conexión**: Frontend envía datos actualizados → Backend actualiza paseo → Cambia estado a PENDIENTE si estaba confirmado

#### Listar Paseos del Cliente
- **Frontend**: `requests.component.ts` → `loadRequests()`
- **Backend**: `GET /api/paseos/cliente`
- **Response**: `List<PaseoResponse>`
- **Conexión**: Frontend solicita paseos → Backend identifica cliente desde token → Retorna lista de paseos

#### Listar Paseos Pendientes
- **Frontend**: `requests.component.ts` → `pendingRequests`
- **Backend**: `GET /api/paseos/cliente/pendientes`
- **Response**: `List<PaseoResponse>`
- **Conexión**: Frontend filtra paseos pendientes → Backend retorna solo pendientes

#### Listar Paseos Confirmados
- **Frontend**: `requests.component.ts` → `confirmedRequests`
- **Backend**: `GET /api/paseos/cliente/confirmados`
- **Response**: `List<PaseoResponse>`
- **Conexión**: Frontend filtra paseos confirmados → Backend retorna solo confirmados

#### Aceptar Paseo (Paseador)
- **Frontend**: `walker-requests.component.ts` → `acceptRequest()`
- **Backend**: `POST /api/paseos/{id}/aceptar`
- **Response**: `ApiResponse<PaseoResponse>`
- **Conexión**: Frontend envía ID del paseo → Backend identifica paseador → Asigna paseador al paseo → Cambia estado a CONFIRMADO

#### Iniciar Paseo
- **Frontend**: `walker-requests.component.ts` → `startWalk()`
- **Backend**: `POST /api/paseos/{id}/iniciar`
- **Response**: `ApiResponse<PaseoResponse>`
- **Conexión**: Frontend envía ID del paseo → Backend cambia estado a EN_PROGRESO → Guarda fecha de inicio

#### Finalizar Paseo
- **Frontend**: `walker-requests.component.ts` → `finishWalk()`
- **Backend**: `POST /api/paseos/{id}/finalizar`
- **Response**: `ApiResponse<PaseoResponse>`
- **Conexión**: Frontend envía ID del paseo → Backend cambia estado a FINALIZADO → Marca isCompleted = true

#### Cancelar Paseo
- **Frontend**: `requests.component.ts` → `cancelRequest()`
- **Backend**: `DELETE /api/paseos/{id}`
- **Response**: `ApiResponse<Void>`
- **Conexión**: Frontend envía ID del paseo → Backend verifica que pertenezca al cliente → Cambia estado a CANCELADO o elimina

#### Historial de Paseos
- **Frontend**: `history.component.ts` → `loadFinalizedWalks()`
- **Backend**: `GET /api/paseos/cliente/finalizados`
- **Response**: `List<PaseoResponse>`
- **Conexión**: Frontend solicita paseos finalizados → Backend retorna solo paseos con isCompleted = true

#### Filtrar por Fechas
- **Frontend**: `history.component.ts` → `filterWalks()`
- **Backend**: `GET /api/paseos/cliente/finalizados/filtrados?startDate=2025-01-01&endDate=2025-12-31`
- **Response**: `List<PaseoResponse>`
- **Conexión**: Frontend envía rango de fechas → Backend filtra paseos en ese rango → Retorna resultados

### 4. Calificaciones

#### Calificar
- **Frontend**: `history.component.ts` → `submitRating()`
- **Backend**: `POST /api/calificaciones`
- **Request**: `CalificacionRequest` (calificacion, tipo, paseoId, comentario)
- **Response**: `ApiResponse<Void>`
- **Conexión**: Frontend envía calificación → Backend crea registro → Si es de paseo, actualiza promedio del paseador

## Estructura de Datos

### Usuario (Frontend → Backend)

**Frontend** (`auth.service.ts`):
```typescript
interface User {
  username: string;
  email: string;
  password: string;
  role?: 'customer' | 'walker';
}
```

**Backend** (`Usuario.java`):
```java
@Entity
public class Usuario {
    private Long id;
    private String username;
    private String email;
    private String password; // Encriptado con BCrypt
    private RolUsuario rol; // CUSTOMER o WALKER
}
```

### Paseo (Frontend → Backend)

**Frontend** (`request.component.ts`):
```typescript
interface RequestData {
  id?: number;
  date: string;
  startTime: string;
  endTime: string;
  address: string;
  walker: string;
  status: 'pending' | 'confirmed';
}
```

**Backend** (`Paseo.java`):
```java
@Entity
public class Paseo {
    private Long id;
    private Usuario cliente;
    private Paseador paseador;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String direccion;
    private EstadoPaseo estado; // PENDIENTE, CONFIRMADO, EN_PROGRESO, FINALIZADO, CANCELADO
    private Boolean isCompleted;
}
```

## Configuración de CORS

El backend está configurado para aceptar peticiones desde:
- **Origin**: `http://localhost:4200` (Angular dev server)
- **Métodos**: GET, POST, PUT, DELETE, PATCH, OPTIONS
- **Headers**: Authorization, Content-Type, X-Requested-With

## Ejemplo de Implementación en Frontend

### Servicio HTTP (Angular)

```typescript
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private apiUrl = 'http://localhost:8080/api';
  
  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const authToken = localStorage.getItem('authToken');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Basic ${authToken}`
    });
  }

  // Ejemplo: Login
  login(credentials: LoginRequest): Observable<ApiResponse<UsuarioResponse>> {
    return this.http.post<ApiResponse<UsuarioResponse>>(
      `${this.apiUrl}/auth/login`,
      credentials
    );
  }

  // Ejemplo: Crear Paseo
  crearPaseo(paseo: PaseoRequest): Observable<ApiResponse<PaseoResponse>> {
    return this.http.post<ApiResponse<PaseoResponse>>(
      `${this.apiUrl}/paseos`,
      paseo,
      { headers: this.getHeaders() }
    );
  }

  // Ejemplo: Obtener Paseos
  obtenerPaseos(): Observable<PaseoResponse[]> {
    return this.http.get<PaseoResponse[]>(
      `${this.apiUrl}/paseos/cliente`,
      { headers: this.getHeaders() }
    );
  }
}
```

## Notas Importantes

1. **Seguridad**: Las contraseñas se encriptan con BCrypt en el backend
2. **Estados**: Los estados de paseos se manejan en el backend (PENDIENTE, CONFIRMADO, etc.)
3. **Validaciones**: El backend valida todos los datos antes de procesarlos
4. **Base de Datos**: Por ahora usa H2 en memoria, pero se puede cambiar a PostgreSQL/MySQL
5. **Archivos**: Los documentos de paseadores se guardan en `./uploads` (configurable)

## Próximos Pasos para Implementar

1. Implementar las clases de servicio (AuthServiceImpl, PaseoServiceImpl, etc.)
2. Implementar la lógica de negocio en los controladores
3. Configurar el interceptor HTTP en Angular para incluir automáticamente el header Authorization
4. Manejar errores y respuestas del backend en el frontend
5. Implementar auto-confirmación de paseos (timer de 10 segundos) en el backend
6. Implementar sistema de notificaciones (opcional)


