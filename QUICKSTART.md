# Guía de Inicio Rápido

¿Quieres empezar rápidamente? Sigue estos pasos para tener el sistema funcionando en minutos.

## Instalación Rápida (10 minutos)

### Prerequisitos
Asegúrate de tener instalado:
- ☑️ Java JDK 17+
- ☑️ MySQL 8.0+
- ☑️ Git

### Paso 1: Clonar el Repositorio
```bash
git clone https://github.com/Keneth-Huerta/GamificacionDeportiva.git
cd GamificacionDeportiva
```

### Paso 2: Configurar la Base de Datos
```bash
# Accede a MySQL
mysql -u root -p

# En MySQL, ejecuta:
CREATE DATABASE gamificacion_deportiva CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'gamificacion_user'@'localhost' IDENTIFIED BY 'tu_contraseña_segura';
GRANT SELECT, INSERT, UPDATE, DELETE ON gamificacion_deportiva.* TO 'gamificacion_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;

# Importa el esquema
mysql -u gamificacion_user -p gamificacion_deportiva < database/schema.sql
```

### Paso 3: Configurar las Credenciales (IMPORTANTE)

⚠️ **El proyecto usa variables de entorno para proteger tus credenciales.**

**Linux / macOS:**
```bash
# Configura las variables de entorno
export GAMIFICACION_DB_URL="jdbc:mysql://localhost:3306/gamificacion_deportiva"
export GAMIFICACION_DB_USER="gamificacion_user"
export GAMIFICACION_DB_PASSWORD="tu_contraseña_segura"

# Para hacerlo permanente, agrega a ~/.bashrc o ~/.zshrc:
echo 'export GAMIFICACION_DB_URL="jdbc:mysql://localhost:3306/gamificacion_deportiva"' >> ~/.bashrc
echo 'export GAMIFICACION_DB_USER="gamificacion_user"' >> ~/.bashrc
echo 'export GAMIFICACION_DB_PASSWORD="tu_contraseña_segura"' >> ~/.bashrc
source ~/.bashrc
```

**Windows (CMD):**
```cmd
# Configurar permanentemente
setx GAMIFICACION_DB_URL "jdbc:mysql://localhost:3306/gamificacion_deportiva"
setx GAMIFICACION_DB_USER "gamificacion_user"
setx GAMIFICACION_DB_PASSWORD "tu_contraseña_segura"

# IMPORTANTE: Cierra y abre una nueva ventana de CMD para que los cambios surtan efecto
```

**Windows (PowerShell):**
```powershell
# Configurar permanentemente (requiere permisos de administrador)
[System.Environment]::SetEnvironmentVariable('GAMIFICACION_DB_URL', 'jdbc:mysql://localhost:3306/gamificacion_deportiva', 'User')
[System.Environment]::SetEnvironmentVariable('GAMIFICACION_DB_USER', 'gamificacion_user', 'User')
[System.Environment]::SetEnvironmentVariable('GAMIFICACION_DB_PASSWORD', 'tu_contraseña_segura', 'User')

# IMPORTANTE: Cierra y abre una nueva ventana de PowerShell
```

# Windows (CMD)
set GAMIFICACION_DB_URL=jdbc:mysql://localhost:3306/gamificacion_deportiva
set GAMIFICACION_DB_USER=gamificacion_user
set GAMIFICACION_DB_PASSWORD=tu_contraseña
```

**Opción C - Edición directa (Solo para pruebas)**
```bash
# Edita src/gamificaciondeportiva/Configuracion.java
# Actualiza DB_URL, DB_USER y DB_PASSWORD
```

### Paso 4: Compilar y Ejecutar
```bash
# Compilar
javac -d out src/gamificaciondeportiva/*.java

# Ejecutar
java -cp out gamificaciondeportiva.MainGamificacionDeportiva
```

## Primeros Pasos en la Aplicación

### 1. Iniciar Sesión
Al iniciar, usa las credenciales del administrador por defecto:
- **Usuario ID**: `admin001`
- **Nombre**: `Administrador`

### 2. Crear tu Perfil
1. Ve a **Editar Perfil**
2. Actualiza tu nombre
3. Sube una foto de perfil (opcional)
4. Guarda los cambios

### 3. Registrar tu Primera Actividad
1. Haz clic en **"Registrar Actividad"**
2. Selecciona un deporte (ej: Correr)
3. Ingresa:
   - Duración: 30 minutos
   - Distancia: 5 km
   - Fecha: hoy
4. Guarda la actividad
5. ¡Verás que ganas puntos y experiencia!

### 4. Ver tus Logros
1. Navega a la sección **Logros**
2. Verás los logros disponibles
3. Los logros completados aparecerán marcados
4. Cada logro otorga puntos de recompensa

### 5. Unirte a una Competencia (Opcional)
1. Ve a **Competencias**
2. Selecciona una competencia activa
3. Únete a la competencia
4. Tus actividades del tipo correspondiente sumarán puntos

## 📊 Funcionalidades Principales

### Sistema de Puntos
- **Actividades**: Ganas puntos por cada actividad registrada
- **Logros**: Completa objetivos para ganar puntos extra
- **Desafíos**: Participa en desafíos temporales
- **Competencias**: Compite con otros usuarios

### Niveles
- Subes de nivel ganando experiencia (XP)
- Cada 1000 XP = 1 nivel
- Las actividades otorgan XP automáticamente

### Tipos de Deporte Soportados
- 🏃 **Correr**: Basado en distancia y tiempo
- 🏊 **Natación**: Alta quema de calorías
- 🚴 **Ciclismo**: Largas distancias
- ⚽ **Fútbol**: Deporte de equipo
- 🏀 **Baloncesto**: Alta intensidad
- 🏐 **Volleyball**: Deporte recreativo
- 💪 **Gimnasio**: Entrenamiento de fuerza

## 🎯 Categorías de Logros

### 📈 Actividad Total
- Primer Paso (5 actividades) - 50 puntos
- En Movimiento (10 actividades) - 100 puntos
- Deportista Activo (25 actividades) - 250 puntos

### 🔥 Calorías Quemadas
- Quemador Novato (1000 calorías) - 100 puntos
- Quemador Experto (5000 calorías) - 300 puntos
- Incinerador (10000 calorías) - 600 puntos

### 🏃 Distancia Recorrida
- Primeros 10K - 100 puntos
- Media Centena (50 km) - 250 puntos
- Centenario (100 km) - 500 puntos

### ⚡ Racha de Días
- Racha Inicial (3 días) - 150 puntos
- Semana Perfecta (7 días) - 300 puntos
- Dos Semanas (14 días) - 700 puntos

## 💡 Consejos

### Para Maximizar Puntos
1. **Registra actividades diariamente** para mantener rachas
2. **Varía los deportes** para desbloquear logros específicos
3. **Únete a competencias** para ganar puntos extra
4. **Completa desafíos** antes de que expiren

### Para Administradores
- Accede al panel de administración para ver estadísticas globales
- Puedes gestionar usuarios y crear nuevas competencias
- Monitorea el progreso de todos los usuarios

## 🐛 Problemas Comunes

### "No se puede conectar a la base de datos"
```bash
# Verifica que MySQL esté ejecutándose
sudo systemctl status mysql  # Linux
brew services list           # macOS
sc query MySQL80            # Windows

# Verifica las credenciales en Configuracion.java
```

### "Clase principal no encontrada"
```bash
# Asegúrate de compilar primero
javac -d out src/gamificaciondeportiva/*.java

# Ejecuta con el classpath correcto
java -cp out gamificaciondeportiva.MainGamificacionDeportiva
```

### "La interfaz no aparece"
```bash
# Asegúrate de tener un entorno gráfico
# No ejecutes en modo headless
java -Djava.awt.headless=false -cp out gamificaciondeportiva.MainGamificacionDeportiva
```

## 📚 Siguiente Paso

¿Quieres más detalles? Consulta:
- [README completo](README.md) - Documentación completa del proyecto
- [Guía de Configuración](SETUP.md) - Configuración detallada
- [Guía de Contribución](CONTRIBUTING.md) - Cómo contribuir al proyecto
- [Documentación JavaDoc](Documentación%20pública/index.html) - API completa

## 🆘 ¿Necesitas Ayuda?

- Revisa los [Issues](https://github.com/Keneth-Huerta/GamificacionDeportiva/issues)
- Crea un nuevo Issue si encuentras un problema
- Consulta la documentación completa

---

¡Disfruta usando el Sistema de Gamificación Deportiva! 🏆
