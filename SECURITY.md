# Guía de Seguridad

Esta guía cubre las mejores prácticas de seguridad para el Sistema de Gamificación Deportiva.

## Estado de Seguridad del Proyecto

**SEGURO PARA USO PÚBLICO**
- Sin credenciales hardcodeadas en el código fuente
- Variables de entorno implementadas correctamente
- `.gitignore` configurado para proteger archivos sensibles
- Documentación completa de seguridad
- Ejemplos de configuración seguros

## Advertencias Importantes

### NUNCA hagas esto:

1. **NO subas credenciales a Git**
   - NUNCA incluyas credenciales reales en el código fuente
   - NO compartas archivos `.env` con credenciales reales
   - VERIFICA que `.gitignore` esté configurado antes de hacer commit
   - USA `git status` para revisar qué archivos se van a subir

2. **NO uses contraseñas débiles**
   - Evita contraseñas comunes (123456, password, admin, etc.)
   - No uses la misma contraseña para múltiples servicios
   - Usa al menos 12 caracteres con mezcla de:
     - Mayúsculas (A-Z)
     - Minúsculas (a-z)
     - Números (0-9)
     - Símbolos (!@#$%^&*)

3. **NO uses el usuario root de MySQL**
   - Crea un usuario específico para la aplicación
   - Limita los privilegios solo a lo necesario (SELECT, INSERT, UPDATE, DELETE)
   - NO otorgues permisos GRANT OPTION, DROP, CREATE USER innecesariamente

4. **NO expongas tu base de datos públicamente**
   - NO abras el puerto MySQL (3306) a Internet sin protección
   - USA firewall para restricciones de red
   - CONFIGURA bind-address a 127.0.0.1 si solo accedes localmente
   - CONSIDERA usar VPN o SSH tunneling para acceso remoto

## Configuración Segura

### 1. Gestión de Credenciales con Variables de Entorno

El proyecto está configurado para usar variables de entorno. **Esta es la forma recomendada**.

**¿Por qué es seguro?**
- Las credenciales NO están en archivos de código
- NO se comparten accidentalmente en Git
- Fácil de cambiar por entorno (desarrollo, producción)
- Cada desarrollador/servidor tiene sus propias credenciales

**Configuración:**

```bash
# Linux/macOS - Agrega a ~/.bashrc o ~/.zshrc
export GAMIFICACION_DB_URL="jdbc:mysql://localhost:3306/gamificacion_deportiva"
export GAMIFICACION_DB_USER="gamificacion_user"
export GAMIFICACION_DB_PASSWORD="tu_contraseña_segura_aquí"

# Windows - Usar variables de sistema permanentes
setx GAMIFICACION_DB_URL "jdbc:mysql://localhost:3306/gamificacion_deportiva"
setx GAMIFICACION_DB_USER "gamificacion_user"
setx GAMIFICACION_DB_PASSWORD "tu_contraseña_segura_aquí"
```

#### Opción Alternativa: Archivo de Propiedades Externo

**Ventajas:**
- Fácil de editar
- Separación clara de configuración

**Importante:**
- El archivo DEBE estar en `.gitignore`
- Establecer permisos restrictivos (solo lectura para el usuario)

```bash
# Linux/macOS - Permisos restrictivos
chmod 600 config.properties

# Solo el propietario puede leer
ls -l config.properties
# -rw------- 1 usuario grupo ... config.properties
```

### 2. Seguridad de Base de Datos

#### Crear Usuario con Privilegios Limitados

```sql
-- Crear usuario específico
CREATE USER 'gamificacion_user'@'localhost' 
IDENTIFIED BY 'contraseña_super_segura_12345!@#';

-- Otorgar SOLO los privilegios necesarios
GRANT SELECT, INSERT, UPDATE, DELETE 
ON gamificacion_deportiva.* 
TO 'gamificacion_user'@'localhost';

-- NO otorgar privilegios innecesarios como:
-- DROP, CREATE, ALTER, GRANT OPTION, FILE, SUPER, etc.

FLUSH PRIVILEGES;
```

#### Verificar Privilegios

```sql
-- Mostrar privilegios del usuario
SHOW GRANTS FOR 'gamificacion_user'@'localhost';

-- Resultado esperado:
-- GRANT USAGE ON *.* TO 'gamificacion_user'@'localhost'
-- GRANT SELECT, INSERT, UPDATE, DELETE ON `gamificacion_deportiva`.* TO 'gamificacion_user'@'localhost'
```

#### Configurar MySQL de Forma Segura

```ini
# /etc/mysql/my.cnf o /etc/my.cnf

[mysqld]
# Escuchar solo en localhost (no accesible desde red)
bind-address = 127.0.0.1

# Deshabilitar LOAD DATA LOCAL INFILE
local-infile = 0

# Usar protocolos seguros
require_secure_transport = ON

# Límites de conexiones
max_connections = 100
max_user_connections = 50

# Logs de errores y consultas lentas
log_error = /var/log/mysql/error.log
slow_query_log = 1
slow_query_log_file = /var/log/mysql/slow.log
```

### 3. Protección de Contraseñas

#### Generar Contraseñas Seguras

```bash
# Linux/macOS - Generar contraseña aleatoria
openssl rand -base64 32

# O usa un gestor de contraseñas como:
# - 1Password
# - LastPass
# - Bitwarden
# - KeePassXC
```

#### Características de Contraseña Segura

- Al menos 16 caracteres
- Mezcla de mayúsculas y minúsculas
- Números
- Símbolos especiales
- No contiene palabras del diccionario
- Única (no reutilizada)

**Ejemplos:**
```
Débil: password123
Débil: GamificacionDeportiva2024
Fuerte: x7$mK9#pL2@nQ5*wR8^vT3&yU6
Fuerte: Tr1_9@kL#8mP$5nQ_2wX!7vZ
```

### 4. Seguridad del Código

#### Prevención de Inyección SQL

**Siempre usa PreparedStatement:**

```java
// CORRECTO - Usar PreparedStatement
String query = "SELECT * FROM Usuarios WHERE id = ?";
PreparedStatement stmt = conn.prepareStatement(query);
stmt.setString(1, userId);
ResultSet rs = stmt.executeQuery();

// INCORRECTO - Concatenación de strings
String query = "SELECT * FROM Usuarios WHERE id = '" + userId + "'";
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery(query);
```

#### Validación de Entrada

```java
// Validar entradas del usuario
public void registrarActividad(String usuarioId, int duracion) {
    // Validar que el ID no esté vacío
    if (usuarioId == null || usuarioId.trim().isEmpty()) {
        throw new IllegalArgumentException("ID de usuario inválido");
    }
    
    // Validar rangos razonables
    if (duracion < 1 || duracion > 1440) { // 1 minuto a 24 horas
        throw new IllegalArgumentException("Duración inválida");
    }
    
    // ... proceder con la operación
}
```

#### Cierre de Recursos

```java
// CORRECTO - Usar try-with-resources
try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
     PreparedStatement stmt = conn.prepareStatement(query)) {
    // Usar recursos
} catch (SQLException e) {
    // Manejar error sin exponer detalles sensibles
    logger.error("Error de base de datos", e);
    throw new RuntimeException("Error al procesar la solicitud");
}

// INCORRECTO - No cerrar recursos
Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
PreparedStatement stmt = conn.prepareStatement(query);
// ... usar recursos pero posiblemente no cerrarlos
```

### 5. Manejo de Errores

#### NO expongas información sensible en errores

```java
// INCORRECTO - Expone detalles de la base de datos
catch (SQLException e) {
    JOptionPane.showMessageDialog(null, 
        "Error: " + e.getMessage() + "\nQuery: " + query);
}

// CORRECTO - Mensaje genérico al usuario, detalles en logs
catch (SQLException e) {
    logger.error("Error al acceder a la base de datos", e);
    JOptionPane.showMessageDialog(null, 
        "Error al procesar la solicitud. Por favor, intenta nuevamente.");
}
```

### 6. Permisos de Archivos

#### Linux/macOS

```bash
# Archivos de configuración - Solo lectura para el propietario
chmod 600 config.properties
chmod 600 sesion.txt

# Directorios - Lectura y ejecución para el propietario
chmod 700 database/

# Archivos de código fuente - Lectura y escritura
chmod 644 src/gamificaciondeportiva/*.java

# Verificar permisos
ls -la config.properties
# -rw------- 1 usuario grupo ... config.properties
```

#### Windows

```powershell
# Establecer permisos solo para el usuario actual
icacls config.properties /inheritance:r
icacls config.properties /grant:r "%USERNAME%:(R)"

# Verificar permisos
icacls config.properties
```

### 7. Conexiones Seguras

#### Usar SSL/TLS para MySQL

```java
// Configurar conexión con SSL
String DB_URL = "jdbc:mysql://localhost:3306/gamificacion_deportiva" +
                "?useSSL=true" +
                "&requireSSL=true" +
                "&verifyServerCertificate=true";
```

#### Configuración MySQL para SSL

```sql
-- Verificar si SSL está habilitado
SHOW VARIABLES LIKE '%ssl%';

-- Requerir SSL para el usuario
ALTER USER 'gamificacion_user'@'localhost' REQUIRE SSL;
```

### 8. Actualizaciones y Parches

#### Mantén el Software Actualizado

```bash
# Verificar versión de Java
java -version

# Verificar versión de MySQL
mysql --version

# Actualizar regularmente:
# - Java JDK
# - MySQL Server
# - Drivers JDBC
# - Sistema operativo
```

### 9. Logs y Auditoría

#### Implementar Logging Seguro

```java
import java.util.logging.*;

public class SecureLogger {
    private static final Logger logger = Logger.getLogger("GamificacionDeportiva");
    
    static {
        try {
            // Configurar archivo de log con permisos restrictivos
            FileHandler fh = new FileHandler("logs/app.log", true);
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
            logger.setLevel(Level.INFO);
        } catch (IOException e) {
            System.err.println("Error al configurar logger: " + e.getMessage());
        }
    }
    
    public static void logAcceso(String usuario, String accion) {
        logger.info(String.format("Usuario: %s | Acción: %s", usuario, accion));
    }
    
    public static void logError(String mensaje, Exception e) {
        logger.log(Level.SEVERE, mensaje, e);
    }
}
```

### 10. Backup y Recuperación

#### Realizar Backups Regulares

```bash
# Backup de la base de datos
mysqldump -u gamificacion_user -p gamificacion_deportiva > backup_$(date +%Y%m%d).sql

# Comprimir backup
gzip backup_$(date +%Y%m%d).sql

# Automatizar con cron (Linux/macOS)
# Editar crontab
crontab -e

# Agregar línea para backup diario a las 2 AM
0 2 * * * mysqldump -u user -ppassword dbname | gzip > /backups/backup_$(date +\%Y\%m\%d).sql.gz
```

#### Encriptar Backups

```bash
# Encriptar backup con GPG
gpg --symmetric --cipher-algo AES256 backup_20241028.sql.gz

# Desencriptar cuando sea necesario
gpg --decrypt backup_20241028.sql.gz.gpg > backup_20241028.sql.gz
```

## Checklist de Seguridad

Antes de desplegar en producción:

- [ ] Credenciales no están en el código fuente
- [ ] Variables de entorno están configuradas
- [ ] Usuario de BD tiene privilegios mínimos necesarios
- [ ] Contraseñas son fuertes y únicas
- [ ] MySQL está configurado para escuchar solo en localhost
- [ ] SSL/TLS está habilitado para conexiones de BD
- [ ] Validación de entrada está implementada
- [ ] PreparedStatement se usa para todas las consultas
- [ ] Manejo de errores no expone información sensible
- [ ] Permisos de archivos están configurados correctamente
- [ ] Logs están implementados y protegidos
- [ ] Backups automáticos están configurados
- [ ] Software está actualizado a las últimas versiones
- [ ] `.gitignore` incluye archivos sensibles
- [ ] Código ha sido revisado para vulnerabilidades

## En Caso de Compromiso de Seguridad

Si sospechas que la seguridad ha sido comprometida:

1. **Cambia inmediatamente todas las contraseñas**
2. **Revoca y crea nuevos usuarios de base de datos**
3. **Revisa los logs para actividad sospechosa**
4. **Restaura desde un backup conocido como seguro**
5. **Documenta el incidente**
6. **Implementa medidas adicionales para prevenir recurrencia**

## Recursos Adicionales

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [MySQL Security Guide](https://dev.mysql.com/doc/refman/8.0/en/security.html)
- [Java Security Coding Guidelines](https://www.oracle.com/java/technologies/javase/seccodeguide.html)

---

**Recuerda**: La seguridad es un proceso continuo, no un destino. Revisa y actualiza regularmente tus prácticas de seguridad.