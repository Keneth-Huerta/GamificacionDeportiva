package gamificaciondeportiva;

/**
 * Clase de configuración para la aplicación de gamificación deportiva.
 */
public class Configuracion {
    /**
     * URL de la base de datos.
     */
    public static final String DB_URL = "jdbc:mysql://srv1009.hstgr.io/u288355303_GamificacionD";

    /**
     * Usuario de la base de datos.
     */
    public static final String DB_USER = "u288355303_root";

    /**
     * Contraseña de la base de datos.
     */
    public static final String DB_PASSWORD = "1420Gamifi.";

    /**
     * Usuario actual de la aplicación.
     */
    private static Usuario usuarioActual;

    /**
     * Obtiene el usuario actual de la aplicación.
     *
     * @return El usuario actual.
     */
    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Establece el usuario actual de la aplicación.
     *
     * @param usuario El usuario a establecer como actual.
     */
    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }
}