package gamificaciondeportiva.config;

import gamificaciondeportiva.modelo.Usuario;

/**
 * Clase de configuración centralizada para la aplicación de gamificación deportiva.
 * 
 * <p>Esta clase gestiona la configuración de la base de datos mediante variables de entorno
 * y mantiene el estado de la sesión del usuario actual.</p>
 * 
 * <h2>Configuración de Base de Datos</h2>
 * <p>Las credenciales de la base de datos se obtienen de variables de entorno para mantener
 * la seguridad. Las siguientes variables deben estar configuradas:</p>
 * <ul>
 *   <li><code>GAMIFICACION_DB_URL</code> - URL de conexión JDBC (ej: jdbc:mysql://localhost:3306/gamificacion_deportiva)</li>
 *   <li><code>GAMIFICACION_DB_USER</code> - Usuario de la base de datos</li>
 *   <li><code>GAMIFICACION_DB_PASSWORD</code> - Contraseña de la base de datos</li>
 * </ul>
 * 
 * <h3>Ejemplo de configuración en Linux/macOS:</h3>
 * <pre>
 * export GAMIFICACION_DB_URL="jdbc:mysql://localhost:3306/gamificacion_deportiva"
 * export GAMIFICACION_DB_USER="gamificacion_user"
 * export GAMIFICACION_DB_PASSWORD="tu_contraseña_segura"
 * </pre>
 * 
 * <h3>Ejemplo de configuración en Windows (CMD):</h3>
 * <pre>
 * set GAMIFICACION_DB_URL=jdbc:mysql://localhost:3306/gamificacion_deportiva
 * set GAMIFICACION_DB_USER=gamificacion_user
 * set GAMIFICACION_DB_PASSWORD=tu_contraseña_segura
 * </pre>
 * 
 * <h3>Ejemplo de configuración en Windows (PowerShell):</h3>
 * <pre>
 * $env:GAMIFICACION_DB_URL="jdbc:mysql://localhost:3306/gamificacion_deportiva"
 * $env:GAMIFICACION_DB_USER="gamificacion_user"
 * $env:GAMIFICACION_DB_PASSWORD="tu_contraseña_segura"
 * </pre>
 * 
 * <p><strong>Importante:</strong> Si las variables de entorno no están configuradas, 
 * se utilizarán valores por defecto que apuntan a una configuración local. 
 * Esto es <em>solo para desarrollo</em> y <strong>DEBE</strong> cambiarse en producción.</p>
 * 
 * <h2>Gestión de Sesión</h2>
 * <p>La clase mantiene una referencia al usuario actualmente autenticado mediante
 * los métodos {@link #getUsuarioActual()} y {@link #setUsuarioActual(Usuario)}.</p>
 * 
 * @see Usuario
 * @version 2.0
 * @since 1.0
 */
public class Configuracion {
    
    /**
     * URL de conexión a la base de datos.
     * Se obtiene de la variable de entorno GAMIFICACION_DB_URL.
     * Por defecto: jdbc:mysql://localhost:3306/gamificacion_deportiva
     */
    public static final String DB_URL = getEnvOrDefault(
        "GAMIFICACION_DB_URL", 
        "jdbc:mysql://localhost:3306/gamificacion_deportiva"
    );

    /**
     * Usuario de la base de datos.
     * Se obtiene de la variable de entorno GAMIFICACION_DB_USER.
     * Por defecto: gamificacion_user
     */
    public static final String DB_USER = getEnvOrDefault(
        "GAMIFICACION_DB_USER", 
        "gamificacion_user"
    );

    /**
     * Contraseña de la base de datos.
     * Se obtiene de la variable de entorno GAMIFICACION_DB_PASSWORD.
     * Por defecto: password (CAMBIAR EN PRODUCCIÓN)
     */
    public static final String DB_PASSWORD = getEnvOrDefault(
        "GAMIFICACION_DB_PASSWORD", 
        "password"
    );

    /**
     * Usuario actualmente autenticado en la aplicación.
     */
    private static Usuario usuarioActual;

    /**
     * Constructor privado para prevenir instanciación.
     * Esta clase solo debe usarse mediante sus métodos estáticos.
     */
    private Configuracion() {
        throw new IllegalStateException("Clase de utilidad - no debe ser instanciada");
    }

    /**
     * Obtiene el valor de una variable de entorno o retorna un valor por defecto.
     * 
     * <p>Este método verifica primero si existe la variable de entorno especificada.
     * Si no existe o está vacía, retorna el valor por defecto proporcionado.</p>
     * 
     * <p><strong>Advertencia de Seguridad:</strong> Cuando se usa el valor por defecto,
     * se imprime una advertencia en la consola para alertar al usuario que debería
     * configurar la variable de entorno correspondiente.</p>
     * 
     * @param envVar Nombre de la variable de entorno a buscar
     * @param defaultValue Valor por defecto a retornar si la variable no existe
     * @return El valor de la variable de entorno o el valor por defecto
     */
    private static String getEnvOrDefault(String envVar, String defaultValue) {
        String value = System.getenv(envVar);
        if (value == null || value.trim().isEmpty()) {
            System.err.println(" ADVERTENCIA: Variable de entorno '" + envVar + 
                             "' no configurada. Usando valor por defecto.");
            System.err.println("   Para producción, configure: export " + envVar + "=<valor>");
            return defaultValue;
        }
        return value;
    }

    /**
     * Obtiene el usuario actualmente autenticado.
     * 
     * @return El usuario actual, o null si no hay ningún usuario autenticado
     */
    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Establece el usuario actualmente autenticado.
     * 
     * @param usuario El usuario a establecer como actual. Puede ser null para cerrar sesión.
     */
    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }

    /**
     * Verifica si hay un usuario autenticado actualmente.
     * 
     * @return true si hay un usuario autenticado, false en caso contrario
     */
    public static boolean haySesionActiva() {
        return usuarioActual != null;
    }

    /**
     * Cierra la sesión del usuario actual.
     * 
     * <p>Este método establece el usuario actual como null, efectivamente
     * cerrando cualquier sesión activa.</p>
     */
    public static void cerrarSesion() {
        usuarioActual = null;
    }

    /**
     * Obtiene información sobre la configuración actual de la base de datos.
     * 
     * <p><strong>Nota de Seguridad:</strong> La contraseña se muestra parcialmente
     * enmascarada (solo los primeros 2 caracteres son visibles).</p>
     * 
     * @return String con la información de configuración (sin exponer la contraseña completa)
     */
    public static String getInfoConfiguracion() {
        StringBuilder info = new StringBuilder();
        info.append("Configuración de Base de Datos:\n");
        info.append("  URL: ").append(DB_URL).append("\n");
        info.append("  Usuario: ").append(DB_USER).append("\n");
        info.append("  Contraseña: ");
        
        // Enmascarar la contraseña mostrando solo los primeros 2 caracteres
        if (DB_PASSWORD != null && DB_PASSWORD.length() > 2) {
            info.append(DB_PASSWORD.substring(0, 2)).append("********");
        } else {
            info.append("********");
        }
        
        return info.toString();
    }
}
