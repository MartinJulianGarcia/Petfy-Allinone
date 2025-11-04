# Configuración de Lombok

## ⚠️ Error: "Lombok requires enabled annotation processing"

Este error ocurre cuando el IDE no tiene habilitado el procesamiento de anotaciones. Sigue las instrucciones según tu IDE:

## 🔧 IntelliJ IDEA

### 1. Instalar el plugin de Lombok

1. Ve a **File** → **Settings** (o `Ctrl + Alt + S`)
2. Ve a **Plugins**
3. Busca "Lombok"
4. Instala el plugin "Lombok" (por JetBrains)
5. Reinicia IntelliJ IDEA

### 2. Habilitar Annotation Processing

1. Ve a **File** → **Settings** (o `Ctrl + Alt + S`)
2. Ve a **Build, Execution, Deployment** → **Compiler** → **Annotation Processors**
3. Marca la casilla **"Enable annotation processing"**
4. Haz clic en **Apply** y luego **OK**

### 3. Configurar el proyecto

1. Ve a **File** → **Project Structure** (o `Ctrl + Alt + Shift + S`)
2. Ve a **Modules**
3. Selecciona tu módulo `Petfy-Back`
4. Ve a la pestaña **Dependencies**
5. Asegúrate de que Lombok esté en el classpath

### 4. Reiniciar el proyecto

- **File** → **Invalidate Caches / Restart...** → **Invalidate and Restart**

## 🔧 Eclipse

### 1. Instalar Lombok

1. Descarga Lombok desde: https://projectlombok.org/download
2. Ejecuta el archivo `.jar` descargado
3. Selecciona tu instalación de Eclipse
4. Haz clic en "Install/Update"
5. Reinicia Eclipse

### 2. Habilitar Annotation Processing

1. Click derecho en el proyecto → **Properties**
2. Ve a **Java Compiler** → **Annotation Processing**
3. Marca **"Enable annotation processing"**
4. Haz clic en **Apply and Close**

## 🔧 Visual Studio Code

### 1. Instalar extensiones

1. Instala la extensión "Language Support for Java(TM) by Red Hat"
2. Instala la extensión "Lombok Annotations Support for VS Code"

### 2. Configurar settings.json

Agrega estas configuraciones en `.vscode/settings.json`:

```json
{
  "java.jdt.ls.lombokSupport.enabled": true,
  "java.configuration.updateBuildConfiguration": "automatic"
}
```

## ✅ Verificar que funciona

Después de configurar, deberías poder usar las anotaciones de Lombok sin errores:

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    // ...
}
```

## 🚀 Si el problema persiste

1. **Limpiar y reconstruir el proyecto**:
   ```bash
   mvn clean install
   ```

2. **Verificar que Lombok esté en el pom.xml**:
   ```xml
   <dependency>
       <groupId>org.projectlombok</groupId>
       <artifactId>lombok</artifactId>
       <optional>true</optional>
   </dependency>
   ```

3. **Actualizar dependencias**:
   - IntelliJ: Click derecho en `pom.xml` → **Maven** → **Reload project**
   - Eclipse: Click derecho en proyecto → **Maven** → **Update Project**

## 📝 Nota

Lombok funciona a nivel de compilación, generando código automáticamente. El IDE necesita el plugin para reconocer estas anotaciones y evitar errores de compilación.


