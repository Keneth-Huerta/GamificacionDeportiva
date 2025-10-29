# Guía de Configuración

Esta guía te ayudará a configurar correctamente el Sistema de Gamificación Deportiva.

## Tabla de Contenidos

- [Requisitos del Sistema](#requisitos-del-sistema)
- [Instalación de Java](#instalación-de-java)
- [Instalación de MySQL](#instalación-de-mysql)
- [Configuración de la Base de Datos](#configuración-de-la-base-de-datos)
- [Configuración de la Aplicación](#configuración-de-la-aplicación)
- [Variables de Entorno](#variables-de-entorno)
- [Solución de Problemas](#solución-de-problemas)

## Requisitos del Sistema

### Hardware Mínimo
- **Procesador**: 1.5 GHz o superior
- **RAM**: 2 GB mínimo (4 GB recomendado)
- **Espacio en disco**: 500 MB para la aplicación + espacio para la base de datos

### Software Requerido
- **Java JDK**: 17 o superior
- **MySQL**: 8.0 o superior
- **Sistema Operativo**: Windows 10+, macOS 10.14+, o Linux (Ubuntu 18.04+)

## Instalación de Java

### Windows

1. Descarga el JDK desde [Oracle](https://www.oracle.com/java/technologies/downloads/) o [OpenJDK](https://adoptium.net/)
2. Ejecuta el instalador
3. Configura la variable de entorno `JAVA_HOME`:
   ```cmd
   setx JAVA_HOME "C:\Program Files\Java\jdk-17"
   setx PATH "%PATH%;%JAVA_HOME%\bin"
   ```
4. Verifica la instalación:
   ```cmd
   java -version
   javac -version
   ```

### macOS

Usando Homebrew:
```bash
brew install openjdk@17
sudo ln -sfn /usr/local/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk
```

Verifica la instalación:
```bash
java -version
javac -version
```

### Linux (Ubuntu/Debian)

```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

Verifica la instalación:
```bash
java -version
javac -version
```

## Instalación de MySQL

### Windows

1. Descarga MySQL desde [mysql.com](https://dev.mysql.com/downloads/installer/)
2. Ejecuta el instalador y sigue el asistente
3. Durante la instalación:
   - Selecciona "Developer Default"
   - Configura una contraseña root segura
   - Acepta el puerto por defecto (3306)

### macOS

Usando Homebrew:
```bash
brew install mysql
brew services start mysql
mysql_secure_installation
```

### Linux (Ubuntu/Debian)

```bash
sudo apt update
sudo apt install mysql-server
sudo systemctl start mysql
sudo mysql_secure_installation
```

## Configuración de la Base de Datos

### 1. Acceder a MySQL

```bash
mysql -u root -p
```

### 2. Crear Base de Datos y Usuario

```sql
-- Crear la base de datos
CREATE DATABASE gamificacion_deportiva CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Crear un usuario específico (recomendado)
CREATE USER 'gamificacion_user'@'localhost' IDENTIFIED BY 'tu_contraseña_segura';

-- Otorgar permisos
GRANT ALL PRIVILEGES ON gamificacion_deportiva.* TO 'gamificacion_user'@'localhost';
FLUSH PRIVILEGES;

-- Salir
EXIT;
```

### 3. Importar el Esquema

```bash
mysql -u gamificacion_user -p gamificacion_deportiva < database/schema.sql
```

### 4. Verificar la Creación

```bash
mysql -u gamificacion_user -p gamificacion_deportiva

# Dentro de MySQL
SHOW TABLES;
DESCRIBE Usuarios;
EXIT;
```

## Configuración de la Aplicación

### Opción 1: Configuración Directa (No Recomendado para Producción)

Edita el archivo `src/gamificaciondeportiva/Configuracion.java`:

```java
public class Configuracion {
    public static final String DB_URL = "jdbc:mysql://localhost:3306/gamificacion_deportiva";
    public static final String DB_USER = "gamificacion_user";
    public static final String DB_PASSWORD = "tu_contraseña_segura";
    
    // ... resto del código
}
```

⚠️ **Advertencia**: No subas este archivo con credenciales reales a un repositorio público.

### Opción 2: Variables de Entorno (Recomendado)

1. Modifica `Configuracion.java` para usar variables de entorno:

```java
public class Configuracion {
    public static final String DB_URL = System.getenv("GAMIFICACION_DB_URL") != null 
        ? System.getenv("GAMIFICACION_DB_URL")
        : "jdbc:mysql://localhost:3306/gamificacion_deportiva";
    
    public static final String DB_USER = System.getenv("GAMIFICACION_DB_USER") != null
        ? System.getenv("GAMIFICACION_DB_USER")
        : "gamificacion_user";
    
    public static final String DB_PASSWORD = System.getenv("GAMIFICACION_DB_PASSWORD");
    
    // ... resto del código
}
```

2. Configura las variables de entorno:

#### Windows (CMD)
```cmd
setx GAMIFICACION_DB_URL "jdbc:mysql://localhost:3306/gamificacion_deportiva"
setx GAMIFICACION_DB_USER "gamificacion_user"
setx GAMIFICACION_DB_PASSWORD "tu_contraseña_segura"
```

#### Windows (PowerShell)
```powershell
[Environment]::SetEnvironmentVariable("GAMIFICACION_DB_URL", "jdbc:mysql://localhost:3306/gamificacion_deportiva", "User")
[Environment]::SetEnvironmentVariable("GAMIFICACION_DB_USER", "gamificacion_user", "User")
[Environment]::SetEnvironmentVariable("GAMIFICACION_DB_PASSWORD", "tu_contraseña_segura", "User")
```

#### macOS/Linux (bash/zsh)
Agrega al final de `~/.bashrc` o `~/.zshrc`:
```bash
export GAMIFICACION_DB_URL="jdbc:mysql://localhost:3306/gamificacion_deportiva"
export GAMIFICACION_DB_USER="gamificacion_user"
export GAMIFICACION_DB_PASSWORD="tu_contraseña_segura"
```

Luego recarga la configuración:
```bash
source ~/.bashrc  # o ~/.zshrc
```

### Opción 3: Archivo de Propiedades (Alternativa)

1. Crea un archivo `config.properties` (agregado a .gitignore):
```properties
db.url=jdbc:mysql://localhost:3306/gamificacion_deportiva
db.user=gamificacion_user
db.password=tu_contraseña_segura
```

2. Modifica `Configuracion.java` para leer este archivo:
```java
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuracion {
    private static Properties props = new Properties();
    
    static {
        try {
            props.load(new FileInputStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static final String DB_URL = props.getProperty("db.url");
    public static final String DB_USER = props.getProperty("db.user");
    public static final String DB_PASSWORD = props.getProperty("db.password");
    
    // ... resto del código
}
```

## Variables de Entorno

### Variables Disponibles

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `GAMIFICACION_DB_URL` | URL de conexión a MySQL | `jdbc:mysql://localhost:3306/gamificacion_deportiva` |
| `GAMIFICACION_DB_USER` | Usuario de la base de datos | `gamificacion_user` |
| `GAMIFICACION_DB_PASSWORD` | Contraseña del usuario | `tu_contraseña_segura` |

### Verificar Variables

#### Windows
```cmd
echo %GAMIFICACION_DB_URL%
echo %GAMIFICACION_DB_USER%
```

#### macOS/Linux
```bash
echo $GAMIFICACION_DB_URL
echo $GAMIFICACION_DB_USER
```

## Solución de Problemas

### Error: "Could not find or load main class"

**Causa**: CLASSPATH no configurado correctamente.

**Solución**:
```bash
# Asegúrate de compilar primero
javac -d out src/gamificaciondeportiva/*.java

# Ejecuta con el classpath correcto
java -cp out gamificaciondeportiva.MainGamificacionDeportiva
```

### Error: "Access denied for user"

**Causa**: Credenciales incorrectas o permisos insuficientes.

**Solución**:
1. Verifica las credenciales en `Configuracion.java`
2. Confirma que el usuario tiene permisos:
   ```sql
   SHOW GRANTS FOR 'gamificacion_user'@'localhost';
   ```
3. Si es necesario, vuelve a otorgar permisos:
   ```sql
   GRANT ALL PRIVILEGES ON gamificacion_deportiva.* TO 'gamificacion_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

### Error: "Communications link failure"

**Causa**: MySQL no está ejecutándose o el puerto es incorrecto.

**Solución**:
1. Verifica que MySQL esté ejecutándose:
   ```bash
   # Windows
   sc query MySQL80
   
   # macOS
   brew services list
   
   # Linux
   sudo systemctl status mysql
   ```
2. Inicia MySQL si está detenido:
   ```bash
   # Windows
   net start MySQL80
   
   # macOS
   brew services start mysql
   
   # Linux
   sudo systemctl start mysql
   ```

### Error: "Table doesn't exist"

**Causa**: El esquema no se ha importado correctamente.

**Solución**:
```bash
mysql -u gamificacion_user -p gamificacion_deportiva < database/schema.sql
```

### Problema: La aplicación no muestra la interfaz gráfica

**Causa**: Entorno gráfico no disponible o Java headless.

**Solución**:
1. Asegúrate de tener un entorno gráfico (no ejecutes en servidor sin GUI)
2. Verifica que Java no esté en modo headless:
   ```bash
   java -Djava.awt.headless=false -cp out gamificaciondeportiva.MainGamificacionDeportiva
   ```

### Problema: Caracteres especiales no se muestran correctamente

**Causa**: Codificación incorrecta.

**Solución**:
1. Verifica que la base de datos use UTF-8:
   ```sql
   SHOW CREATE DATABASE gamificacion_deportiva;
   ```
2. Al compilar, especifica la codificación:
   ```bash
   javac -encoding UTF-8 -d out src/gamificaciondeportiva/*.java
   ```

## Soporte Adicional

Si después de seguir esta guía sigues teniendo problemas:

1. Revisa los [Issues](https://github.com/Keneth-Huerta/GamificacionDeportiva/issues) existentes
2. Crea un nuevo Issue con:
   - Descripción detallada del problema
   - Pasos que seguiste
   - Mensajes de error completos
   - Versiones de Java y MySQL
   - Sistema operativo

---

¿Listo para comenzar? Sigue la [Guía de Instalación](README.md#-instalación) en el README principal.
