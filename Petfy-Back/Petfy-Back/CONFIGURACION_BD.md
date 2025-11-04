# Configuración de Base de Datos MySQL

## ✅ Configuración Actual

El backend está configurado para conectarse a MySQL con la siguiente configuración:

- **Base de datos**: `petfy_bd`
- **Host**: `localhost`
- **Puerto**: `3306`
- **Usuario**: `root` (debes cambiarlo según tu configuración)
- **Contraseña**: (vacía por defecto, debes configurarla)

## 🔧 Personalizar Credenciales

Edita el archivo `src/main/resources/application.properties` y actualiza las siguientes líneas según tu configuración de MySQL:

```properties
spring.datasource.username=TU_USUARIO_MYSQL
spring.datasource.password=TU_CONTRASEÑA_MYSQL
```

## 📝 Ejemplo de Configuración

Si tu usuario de MySQL es `petfy_user` y tu contraseña es `miPassword123`, la configuración sería:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/petfy_bd?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=petfy_user
spring.datasource.password=miPassword123
```

## 🗄️ Crear la Base de Datos

Asegúrate de que la base de datos `petfy_bd` exista en tu servidor MySQL. Si no existe, créala con:

```sql
CREATE DATABASE petfy_bd CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## 🔄 Comportamiento de Hibernate

Con `spring.jpa.hibernate.ddl-auto=update`, Hibernate:
- Creará automáticamente las tablas si no existen
- Actualizará el esquema si hay cambios en las entidades
- **NO eliminará** datos existentes

Si prefieres un control más estricto, puedes cambiar a:
- `validate`: Solo valida el esquema sin hacer cambios
- `create`: Crea las tablas cada vez (elimina datos)
- `create-drop`: Crea al inicio y elimina al finalizar

## 🚀 Probar la Conexión

1. Asegúrate de que MySQL esté corriendo
2. Verifica que la base de datos `petfy_bd` exista
3. Actualiza las credenciales en `application.properties`
4. Ejecuta la aplicación Spring Boot
5. Revisa los logs para confirmar que la conexión fue exitosa

## ⚠️ Notas Importantes

- El puerto 3306 es el puerto por defecto de MySQL
- Si tu MySQL está en otro puerto, cambia `3306` en la URL
- `useSSL=false` está configurado para desarrollo local
- En producción, considera usar SSL: `useSSL=true`

## 📦 Dependencia Agregada

Se agregó la dependencia `mysql-connector-j` en el `pom.xml`. Asegúrate de ejecutar:

```bash
mvn clean install
```

Para descargar las dependencias.


