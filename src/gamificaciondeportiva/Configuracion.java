package gamificaciondeportiva;

public class Configuracion {
    public static final String DB_URL = "jdbc:mysql://srv1009.hstgr.io/u288355303_GamificacionD";
    public static final String DB_USER = "u288355303_root";
    public static final String DB_PASSWORD = "1420Gamifi.";

    private static Usuario usuarioActual;

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }
}
