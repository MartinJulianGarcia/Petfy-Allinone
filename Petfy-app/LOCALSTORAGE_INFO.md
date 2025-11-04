# Información sobre localStorage en Petfy

## 📦 Datos guardados en localStorage

Todos los datos temporales de la aplicación se guardan en el `localStorage` del navegador:

### 1. **Usuario actual**
- **Clave**: `currentUser`
- **Contiene**: Información del usuario logueado (username, email, password, role)
- **Se limpia**: Al hacer logout

### 2. **Peticiones de paseo**
- **Clave**: `walkRequests`
- **Contiene**: Array con todas las solicitudes de paseo (pendientes, confirmadas, finalizadas)
- **Se limpia**: Al hacer logout

### 3. **Calificaciones de paseos**
- **Clave**: `walkRatings`
- **Contiene**: Objeto con las calificaciones de cada paseo (clave: id del paseo, valor: calificación 1-5)
- **Se limpia**: Al hacer logout

### 4. **Calificación de la app**
- **Clave**: `appRating`
- **Contiene**: Calificación general de la aplicación (1-5)
- **Se limpia**: Al hacer logout

### 5. **Chats**
- **Claves**: `chat_{requestId}_{walkerName}` (ej: `chat_123456_Martin`)
- **Contiene**: Array con los mensajes de cada chat
- **Se limpia**: Al hacer logout (todas las claves que empiezan con `chat_`)

### 6. **Usuarios registrados (legacy)**
- **Clave**: `users`
- **Contiene**: Array con usuarios (ya no se usa, quedó del código anterior)
- **Nota**: Se mantiene por compatibilidad, pero ya no se usa porque ahora los usuarios están en el backend

## 🧹 Cómo limpiar los datos

### Opción 1: Automático (recomendado)
Al hacer **logout** desde cualquier parte de la aplicación, se limpian automáticamente todos los datos excepto `users` (que es legacy).

### Opción 2: Manual desde el navegador

1. **Abrir las herramientas de desarrollador**:
   - Chrome/Edge: `F12` o `Ctrl + Shift + I`
   - Firefox: `F12` o `Ctrl + Shift + I`

2. **Ir a la pestaña "Application"** (Chrome) o "Almacenamiento" (Firefox)

3. **En el menú lateral, expandir "Local Storage"**

4. **Seleccionar** `http://localhost:4200`

5. **Eliminar las claves**:
   - Click derecho en cada clave → Delete
   - O usar el botón "Clear All" para limpiar todo

### Opción 3: Desde la consola del navegador

Abre la consola (`F12` → pestaña "Console") y ejecuta:

```javascript
// Limpiar todo
localStorage.clear();

// O limpiar específicamente:
localStorage.removeItem('currentUser');
localStorage.removeItem('walkRequests');
localStorage.removeItem('walkRatings');
localStorage.removeItem('appRating');

// Limpiar todos los chats
Object.keys(localStorage).forEach(key => {
  if (key.startsWith('chat_')) {
    localStorage.removeItem(key);
  }
});
```

## ⚠️ Nota importante

**Cuando cambias de usuario**:
- Al hacer logout, todos los datos se limpian automáticamente
- Al iniciar sesión con otro usuario, empezará con datos limpios
- Los datos NO se mezclan entre usuarios porque se limpian al cerrar sesión

## 🔄 Flujo de datos

1. **Login**: Se guarda `currentUser` en localStorage
2. **Uso de la app**: Se guardan paseos, chats, calificaciones en localStorage
3. **Logout**: Se limpian TODOS los datos relacionados con la sesión
4. **Nuevo login**: Empieza con datos limpios

## 📝 Nota sobre el backend

Aunque ahora el registro y login están conectados al backend, los paseos y chats todavía se guardan en localStorage del frontend. Esto es temporal hasta que se conecten todos los endpoints al backend.

