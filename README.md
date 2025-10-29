# Sistema de Gamificación Deportiva

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-GPL--3.0-blue.svg)](LICENSE)

Sistema de gamificación diseñado para motivar y rastrear actividades deportivas mediante logros, desafíos y competencias. Permite a los usuarios registrar sus actividades físicas, ganar puntos, subir de nivel y competir con otros usuarios.

## Tabla de Contenidos

- [Características](#características)
- [Requisitos Previos](#requisitos-previos)
- [Instalación y Configuración](#instalación-y-configuración)
- [Ejecución del Proyecto](#ejecución-del-proyecto)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Documentación](#documentación)
- [Base de Datos](#base-de-datos)
- [Contribuir](#contribuir)
- [Licencia](#licencia)

## Características

### Sistema de Gamificación
- **Niveles y Experiencia**: Sistema de progresión basado en puntos de experiencia
- **Logros**: Desbloquea logros al completar objetivos específicos
- **Desafíos**: Participa en desafíos temporales con recompensas
- **Competencias**: Compite con otros usuarios en eventos deportivos

### Gestión de Actividades
- **Registro de Actividades**: Registra actividades deportivas con detalles completos
- **Tipos de Deporte Soportados**:
  - Correr
  - Natación
  - Ciclismo
  - Fútbol
  - Baloncesto
  - Volleyball
  - Gimnasio

### Análisis y Estadísticas
- **Gráficos Personalizados**: Visualiza tu progreso con gráficos interactivos
- **Historial de Actividades**: Consulta tu historial completo de ejercicios
- **Seguimiento de Calorías**: Calcula automáticamente las calorías quemadas
- **Estadísticas Semanales**: Analiza tus deportes más practicados

### Funcionalidades Sociales
- **Perfiles de Usuario**: Personaliza tu perfil con foto y datos personales
- **Competencias Grupales**: Participa en competencias con otros usuarios
- **Tabla de Clasificación**: Ve cómo te comparas con otros deportistas
- **Panel de Administración**: Herramientas especiales para administradores

## Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

- **Java Development Kit (JDK) 17 o superior**
  ```bash
  java -version
  ```

- **MySQL 8.0 o superior**
  ```bash
  mysql --version
  ```

- **Maven** (opcional, para gestión de dependencias)
  ```bash
  mvn -version
  ```

## Instalación y Configuración

### Paso 1: Clonar el Repositorio

```bash
git clone https://github.com/Keneth-Huerta/GamificacionDeportiva.git
cd GamificacionDeportiva
```

### Paso 2: Configurar la Base de Datos

1. Accede a MySQL con tu usuario root:

```bash
mysql -u root -p
```

2. Ejecuta los siguientes comandos SQL:

```sql
-- Crear la base de datos
CREATE DATABASE gamificacion_deportiva CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Crear un usuario específico para la aplicación
CREATE USER 'gamificacion_user'@'localhost' IDENTIFIED BY 'tu_contraseña_segura';

-- Otorgar permisos
GRANT SELECT, INSERT, UPDATE, DELETE ON gamificacion_deportiva.* TO 'gamificacion_user'@'localhost';
FLUSH PRIVILEGES;

-- Salir
EXIT;
```

3. Importa el esquema de la base de datos:

```bash
mysql -u gamificacion_user -p gamificacion_deportiva < database/schema.sql
```

### Paso 3: Configurar las Credenciales de la Aplicación

El proyecto utiliza **variables de entorno** para gestionar las credenciales de la base de datos de forma segura.

Configura las siguientes variables de entorno antes de ejecutar la aplicación:

**Linux/macOS**:
```bash
export GAMIFICACION_DB_URL="jdbc:mysql://localhost:3306/gamificacion_deportiva"
export GAMIFICACION_DB_USER="gamificacion_user"
export GAMIFICACION_DB_PASSWORD="tu_contraseña_segura"
```

**Windows (CMD)**:
```cmd
set GAMIFICACION_DB_URL=jdbc:mysql://localhost:3306/gamificacion_deportiva
set GAMIFICACION_DB_USER=gamificacion_user
set GAMIFICACION_DB_PASSWORD=tu_contraseña_segura
```

**Windows (PowerShell)**:
```powershell
$env:GAMIFICACION_DB_URL="jdbc:mysql://localhost:3306/gamificacion_deportiva"
$env:GAMIFICACION_DB_USER="gamificacion_user"
$env:GAMIFICACION_DB_PASSWORD="tu_contraseña_segura"
```

Si las variables no están configuradas, el sistema usará valores por defecto para desarrollo local.

## Ejecución del Proyecto

### Opción 1: Usando un IDE (Recomendado)

#### IntelliJ IDEA
1. Abre IntelliJ IDEA
2. Selecciona `File > Open` y navega a la carpeta del proyecto
3. Espera a que el IDE indexe el proyecto
4. Configura el JDK: `File > Project Structure > Project SDK` (selecciona JDK 17 o superior)
5. Busca la clase `MainGamificacionDeportiva.java` en `src/gamificaciondeportiva/`
6. Haz clic derecho sobre el archivo y selecciona `Run 'MainGamificacionDeportiva.main()'`

#### Eclipse
1. Abre Eclipse
2. Selecciona `File > Import > General > Existing Projects into Workspace`
3. Navega a la carpeta del proyecto y haz clic en `Finish`
4. Configura el JDK: Clic derecho en el proyecto > `Properties > Java Build Path > Libraries`
5. Busca `MainGamificacionDeportiva.java` en el explorador de proyectos
6. Haz clic derecho y selecciona `Run As > Java Application`

#### NetBeans
1. Abre NetBeans
2. Selecciona `File > Open Project` y navega a la carpeta del proyecto
3. Configura el JDK en las propiedades del proyecto
4. Haz clic derecho en el proyecto y selecciona `Run`

### Opción 2: Desde la Línea de Comandos

1. **Compilar el proyecto**:

```bash
# Crear el directorio de salida si no existe
mkdir -p out

# Compilar todos los archivos Java
javac -d out -encoding UTF-8 src/gamificaciondeportiva/*.java
```

2. **Ejecutar la aplicación**:

```bash
java -cp out gamificaciondeportiva.MainGamificacionDeportiva
```

3. **Compilar y ejecutar en un solo paso** (Linux/macOS):

```bash
javac -d out -encoding UTF-8 src/gamificaciondeportiva/*.java && java -cp out gamificaciondeportiva.MainGamificacionDeportiva
```

**Windows (CMD)**:
```cmd
javac -d out -encoding UTF-8 src/gamificaciondeportiva/*.java & java -cp out gamificaciondeportiva.MainGamificacionDeportiva
```

### Opción 3: Crear un Script de Ejecución

#### Linux/macOS (run.sh)

Crea un archivo `run.sh`:

```bash
#!/bin/bash
echo "Compilando el proyecto..."
javac -d out -encoding UTF-8 src/gamificaciondeportiva/*.java

if [ $? -eq 0 ]; then
    echo "Compilación exitosa. Ejecutando aplicación..."
    java -cp out gamificaciondeportiva.MainGamificacionDeportiva
else
    echo "Error en la compilación."
    exit 1
fi
```

Dale permisos de ejecución y ejecútalo:

```bash
chmod +x run.sh
./run.sh
```

#### Windows (run.bat)

Crea un archivo `run.bat`:

```batch
@echo off
echo Compilando el proyecto...
javac -d out -encoding UTF-8 src\gamificaciondeportiva\*.java

if %errorlevel% equ 0 (
    echo Compilacion exitosa. Ejecutando aplicacion...
    java -cp out gamificaciondeportiva.MainGamificacionDeportiva
) else (
    echo Error en la compilacion.
    exit /b 1
)
```

Ejecútalo:

```cmd
run.bat
```

### Verificación de la Ejecución

Si todo está configurado correctamente, deberías ver:

1. Una pantalla de carga inicial
2. La pantalla de inicio de sesión (si no hay sesión guardada)
3. O directamente la interfaz principal (si hay una sesión activa)

### Solución de Problemas Comunes

**Error: "Could not find or load main class"**
- Verifica que compilaste desde la raíz del proyecto
- Asegúrate de que el classpath (`-cp out`) esté especificado correctamente

**Error: "Access denied for user"**
- Verifica las credenciales en `Configuracion.java`
- Asegúrate de que el usuario MySQL tiene los permisos necesarios

**Error: "Communications link failure"**
- Verifica que MySQL esté ejecutándose
- Confirma que el puerto 3306 esté disponible

**Error de codificación de caracteres**
- Asegúrate de compilar con `-encoding UTF-8`

Para más detalles, consulta la [Guía de Configuración](SETUP.md).

## Configuración Adicional

## Uso

### Iniciar Sesión

Al iniciar la aplicación por primera vez, deberás crear un usuario o iniciar sesión con credenciales existentes.

### Registrar una Actividad

1. Desde el menú principal, selecciona "Registrar Actividad"
2. Completa los siguientes datos:
   - Tipo de deporte
   - Duración (minutos)
   - Distancia (kilómetros, si aplica)
   - Fecha y hora
3. Haz clic en "Guardar"

### Ver Logros y Desafíos

- Navega a la sección de **Logros** para ver los logros disponibles y cuáles has completado
- En **Desafíos**, encontrarás desafíos activos en los que puedes participar
- Los logros y desafíos completados otorgan puntos de experiencia

### Participar en Competencias

1. Ve a la sección de **Competencias**
2. Únete a una competencia activa
3. Registra tus actividades durante el período de la competencia
4. Compite por la mejor puntuación

### Administración (Solo Administradores)

Los usuarios con privilegios de administrador tienen acceso a:
- Panel de administración con estadísticas globales
- Gestión de usuarios
- Creación de competencias y desafíos
- Visualización de métricas del sistema

## Estructura del Proyecto

```
GamificacionDeportiva/
├── src/
│   └── gamificaciondeportiva/
│       ├── ActividadDeportiva.java      # Modelo de actividad deportiva
│       ├── CargaDatos.java              # Carga inicial de datos
│       ├── Competencia.java             # Gestión de competencias
│       ├── Configuracion.java           # Configuración de la aplicación
│       ├── Desafio.java                 # Gestión de desafíos
│       ├── FormularioEditarPerfil.java  # Edición de perfil de usuario
│       ├── Graficos.java                # Interfaz gráfica de usuario
│       ├── GraficosAdmin.java           # Panel de administración
│       ├── Logro.java                   # Sistema de logros
│       ├── MainGamificacionDeportiva.java # Punto de entrada principal
│       ├── PantallaCarga.java           # Pantalla de carga
│       ├── PantallaInicio.java          # Pantalla de inicio de sesión
│       ├── SistemaGamificacion.java     # Lógica central del sistema
│       ├── Usuario.java                 # Modelo de usuario
│       └── resources/                   # Recursos (imágenes, iconos)
│           ├── Cubo-EnfocadoL.png
│           ├── carga.gif
│           └── default-profile.png
├── Documentación privada/               # Javadoc privado (desarrollo)
├── Documentación pública/               # Javadoc público (usuarios)
├── database/                            # Scripts de base de datos
│   └── schema.sql                       # Esquema completo de la BD
├── sesion.txt                           # Archivo de sesión temporal (no commitear)
├── .gitignore
├── README.md
├── QUICKSTART.md
├── SETUP.md
├── CONTRIBUTING.md
├── SECURITY.md
├── CHANGELOG.md
└── LICENSE
```

### Archivos que Debes Modificar para Configurar el Proyecto

Para que el proyecto funcione correctamente en tu entorno, debes modificar ÚNICAMENTE este archivo:

**Archivo de Configuración Principal:**
```
src/gamificaciondeportiva/Configuracion.java
```

Este archivo contiene:
- URL de conexión a la base de datos
- Usuario de la base de datos
- Contraseña de la base de datos

**Ejemplo de modificación:**

```java
public class Configuracion {
    // MODIFICA ESTAS LÍNEAS con tus credenciales
    public static final String DB_URL = "jdbc:mysql://localhost:3306/gamificacion_deportiva";
    public static final String DB_USER = "tu_usuario_mysql";
    public static final String DB_PASSWORD = "tu_contraseña_mysql";
    
    // NO MODIFIQUES el resto del archivo
    private static Usuario usuarioActual;
    
    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }
    
    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }
}
```

**IMPORTANTE**: 
- NO subas este archivo con tus credenciales reales a Git
- Considera usar variables de entorno en su lugar (ver SETUP.md)
- El archivo ya está configurado para conectarse a una base de datos remota por defecto

**Archivos que NO Debes Modificar:**
- Todos los demás archivos .java (a menos que estés desarrollando nuevas características)
- Archivos de documentación
- Scripts de base de datos

## Documentación

### JavaDoc

El proyecto incluye documentación completa generada con JavaDoc:

- **Documentación Pública**: Navega a `Documentación pública/index.html` para la documentación orientada al usuario
- **Documentación Privada**: Navega a `Documentación privada/index.html` para la documentación completa del desarrollo

Para generar la documentación:

```bash
# Documentación pública
javadoc -d "Documentación pública" -sourcepath src -subpackages gamificaciondeportiva -public

# Documentación privada
javadoc -d "Documentación privada" -sourcepath src -subpackages gamificaciondeportiva -private
```

### Clases Principales

#### `SistemaGamificacion`
Gestiona los componentes centrales del sistema, incluyendo:
- Registro de logros completados
- Estado de competencias
- Gestión de actividades deportivas
- Actualización de puntuaciones

#### `Usuario`
Representa a un usuario del sistema con:
- Información de perfil
- Nivel y puntos
- Logros y desafíos activos
- Historial de actividades

#### `ActividadDeportiva`
Modela una actividad física realizada:
- Tipo de deporte
- Duración y distancia
- Cálculo de calorías
- Fecha y hora

## Base de Datos

### Esquema Principal

El sistema utiliza las siguientes tablas principales:

#### Usuarios
```sql
CREATE TABLE Usuarios (
    id VARCHAR(50) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    nivel INT DEFAULT 1,
    puntosTotales INT DEFAULT 0,
    experiencia INT DEFAULT 0,
    fotoPerfil BLOB,
    esAdmin BOOLEAN DEFAULT FALSE
);
```

#### Actividades
```sql
CREATE TABLE Actividades (
    id VARCHAR(50) PRIMARY KEY,
    usuarioId VARCHAR(50),
    tipo VARCHAR(50),
    fecha DATE,
    hora TIME,
    duracionMinutos INT,
    distanciaKm DOUBLE,
    caloriasQuemadas INT,
    esCompetencia BOOLEAN,
    FOREIGN KEY (usuarioId) REFERENCES Usuarios(id)
);
```

#### Logros
```sql
CREATE TABLE Logros (
    id VARCHAR(50) PRIMARY KEY,
    nombre VARCHAR(100),
    descripcion TEXT,
    tipo VARCHAR(50),
    puntosRecompensa INT,
    metaRequerida INT
);
```

#### LogrosCompletados
```sql
CREATE TABLE LogrosCompletados (
    usuarioId VARCHAR(50),
    logroId VARCHAR(50),
    fechaCumplimiento TIMESTAMP,
    PRIMARY KEY (usuarioId, logroId),
    FOREIGN KEY (usuarioId) REFERENCES Usuarios(id),
    FOREIGN KEY (logroId) REFERENCES Logros(id)
);
```

#### Competencias
```sql
CREATE TABLE Competencias (
    id VARCHAR(50) PRIMARY KEY,
    nombre VARCHAR(100),
    descripcion TEXT,
    fechaInicio DATE,
    fechaFin DATE,
    tipoDeporte VARCHAR(50),
    estado VARCHAR(20)
);
```

#### Desafios
```sql
CREATE TABLE Desafios (
    id VARCHAR(50) PRIMARY KEY,
    nombre VARCHAR(100),
    descripcion TEXT,
    tipoDeporte VARCHAR(50),
    metaRequerida INT,
    puntosRecompensa INT,
    fechaInicio DATE,
    fechaFin DATE,
    estado VARCHAR(20)
);
```

### Script de Inicialización

Se recomienda crear un script SQL completo para inicializar la base de datos. Puedes encontrar el script completo en `database/schema.sql`.

## Contribuir

## Contribuir

¡Las contribuciones son bienvenidas! Si deseas contribuir al proyecto:

1. **Fork** el repositorio
2. Crea una **rama** para tu característica (`git checkout -b feature/NuevaCaracteristica`)
3. **Commit** tus cambios (`git commit -m 'Agrega nueva característica'`)
4. **Push** a la rama (`git push origin feature/NuevaCaracteristica`)
5. Abre un **Pull Request**

### Guías de Contribución

- Mantén el código limpio y bien documentado
- Sigue las convenciones de nomenclatura existentes
- Agrega pruebas para nuevas funcionalidades
- Actualiza la documentación según sea necesario

## Licencia

Este proyecto está bajo la Licencia GPL-3.0 - ver el archivo [LICENSE](LICENSE) para más detalles.

## Autor

**Keneth Huerta**
- GitHub: [@Keneth-Huerta](https://github.com/Keneth-Huerta)

## Agradecimientos

- A todos los contribuidores del proyecto
- A la comunidad de Java y MySQL
- A los usuarios que proporcionan retroalimentación

## Soporte

Si tienes alguna pregunta o problema:

1. Revisa la [documentación](#documentación)
2. Busca en [Issues](https://github.com/Keneth-Huerta/GamificacionDeportiva/issues) existentes
3. Crea un nuevo [Issue](https://github.com/Keneth-Huerta/GamificacionDeportiva/issues/new) si es necesario

---

Si este proyecto te fue útil, considera darle una estrella en GitHub.
