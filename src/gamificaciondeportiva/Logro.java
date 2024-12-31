package gamificaciondeportiva;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

/**
 * La clase Logro representa un logro en un sistema de gamificación.
 * Cada logro tiene un ID, nombre, descripción, puntos de recompensa, estado de completación,
 * tipo, fecha de completación y ID de usuario asociado.
 * <p>
 * La clase proporciona métodos para crear logros predefinidos, verificar si un logro
 * cumple sus condiciones, completar un logro y registrar la finalización de un logro.
 * <p>
 * Los logros predefinidos incluyen:
 * <ul>
 *   <li>Logros diarios: primera actividad del día, cumplir meta de tiempo, participar en actividad grupal.</li>
 *   <li>Logros semanales: mantener racha de 3 días, completar variedad de deportes, superar marca personal.</li>
 *   <li>Logros mensuales: participar en competencias, alcanzar metas acumuladas, liderar rankings.</li>
 * <p>
 * La clase también proporciona métodos para verificar condiciones específicas para cada tipo de logro,
 * como verificar la primera actividad del día, participación en actividades grupales, variedad de deportes,
 * racha de días y cumplimiento de metas de tiempo o distancia.
 * <p>
 * La clase Logro interactúa con una base de datos para verificar condiciones y registrar la finalización de logros.
 */
public class Logro {
    private String id;
    private String nombre;
    private String descripcion;
    private int puntosRecompensa;
    private boolean completado;
    private TipoLogro tipo;
    private LocalDate fechaCumplimiento;
    private String usuarioId;

    /**
     * Constructor de la clase Logro.
     *
     * @param id               El ID del logro.
     * @param nombre           El nombre del logro.
     * @param descripcion      La descripción del logro.
     * @param puntosRecompensa Los puntos de recompensa del logro.
     * @param tipo             El tipo de logro (DIARIO, SEMANAL, MENSUAL).
     */
    public Logro(String id, String nombre, String descripcion, int puntosRecompensa, TipoLogro tipo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.puntosRecompensa = puntosRecompensa;
        this.completado = false;
        this.tipo = tipo;
    }

    /**
     * Crea un logro diario de "Primera actividad del día".
     *
     * @return Un objeto Logro predefinido.
     */
    public static Logro crearLogroDiario() {
        return new Logro("1", "Primera actividad del día",
                "Realizar cualquier actividad física como la primera actividad del día", 50, TipoLogro.DIARIO);
    }

    /**
     * Crea un logro diario de "Cumplir meta de tiempo".
     *
     * @return Un objeto Logro predefinido.
     */
    public static Logro crearLogroMetaTiempo() {
        return new Logro("2", "Cumplir meta de tiempo", "Completar una actividad en menos de 30 minutos", 100,
                TipoLogro.DIARIO);
    }

    /**
     * Crea un logro diario de "Participar en actividad grupal".
     *
     * @return Un objeto Logro predefinido.
     */
    public static Logro crearLogroParticipacionGrupal() {
        return new Logro("3", "Participar en actividad grupal",
                "Participar en una actividad física con al menos 5 personas", 150, TipoLogro.DIARIO);
    }

    /**
     * Crea un logro semanal de "Mantener racha de 3 días".
     *
     * @return Un objeto Logro predefinido.
     */
    public static Logro crearLogroRachaSemanal() {
        return new Logro("4", "Mantener racha de 3 días", "Realizar actividad física durante 3 días consecutivos", 200,
                TipoLogro.SEMANAL);
    }

    /**
     * Crea un logro semanal de "Completar variedad de deportes".
     *
     * @return Un objeto Logro predefinido.
     */
    public static Logro crearLogroVariedadDeportes() {
        return new Logro("5", "Completar variedad de deportes",
                "Practicar al menos 3 deportes diferentes en una semana", 250, TipoLogro.SEMANAL);
    }

    /**
     * Crea un logro semanal de "Superar marca personal".
     *
     * @return Un objeto Logro predefinido.
     */
    public static Logro crearLogroMarcaPersonal() {
        return new Logro("6", "Superar marca personal",
                "Superar el tiempo o distancia de tu mejor marca en cualquier actividad", 300, TipoLogro.SEMANAL);
    }

    /**
     * Crea un logro mensual de "Participar en competencias".
     *
     * @return Un objeto Logro predefinido.
     */
    public static Logro crearLogroParticiparCompetencias() {
        return new Logro("7", "Participar en competencias",
                "Participar en al menos una competencia deportiva en el mes", 400, TipoLogro.MENSUAL);
    }

    /**
     * Crea un logro mensual de "Alcanzar metas acumuladas".
     *
     * @return Un objeto Logro predefinido.
     */
    public static Logro crearLogroAlcanzarMetasAcumuladas() {
        return new Logro("8", "Alcanzar metas acumuladas",
                "Alcanzar un total de 10,000 metros en actividades físicas durante el mes", 500, TipoLogro.MENSUAL);
    }

    /**
     * Crea un logro mensual de "Liderar rankings".
     *
     * @return Un objeto Logro predefinido.
     */
    public static Logro crearLogroLiderarRanking() {
        return new Logro("9", "Liderar rankings", "Estar en el primer lugar del ranking de puntos en una competencia",
                600, TipoLogro.MENSUAL);
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    /**
     * Completa el logro para un usuario específico.
     *
     * @param usuario El usuario que completa el logro.
     */
    public void completarLogro(Usuario usuario) {
        if (!completado && usuario != null) {
            this.completado = true;
            this.fechaCumplimiento = LocalDate.now();
            this.usuarioId = usuario.getId();
            usuario.ganarPuntos(puntosRecompensa);
            System.out.println("¡Logro desbloqueado: " + this.nombre + "!");
        }
    }

    /**
     * Verifica si el logro cumple sus condiciones para un usuario específico.
     *
     * @param usuario El usuario a verificar.
     * @return true si el logro cumple sus condiciones, false en caso contrario.
     */
    public boolean cumpleCondiciones(Usuario usuario) {
        try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                Configuracion.DB_PASSWORD)) {
            switch (this.id) {
                case "1": // Primera actividad del día
                    return verificarPrimeraActividad(usuario, conn);
                case "2": // Cumplir meta de tiempo o distancia
                    return verificarMeta(usuario, conn);
                case "3": // Participar en actividad grupal
                    return verificarActividadGrupal(usuario, conn);
                case "4": // Mantener racha de días
                    return verificarRacha(usuario, conn);
                case "5": // Variedad de deportes
                    return verificarVariedadDeportes(usuario, conn);
                default:
                    return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Verifica si el usuario ha realizado la primera actividad del día.
     *
     * @param usuario El usuario a verificar.
     * @param conn    La conexión a la base de datos.
     * @return true si el usuario ha realizado la primera actividad del día, false en caso contrario.
     * @throws Exception Si ocurre un error durante la verificación.
     */
    private boolean verificarPrimeraActividad(Usuario usuario, Connection conn) throws Exception {
        String query = "SELECT hora FROM Actividades WHERE usuarioId = ? AND fecha = CURDATE() ORDER BY hora ASC LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, usuario.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getTime("hora").toLocalTime().isBefore(java.time.LocalTime.of(12, 0));
            }
        }
        return false;
    }

    /**
     * Verifica si el usuario ha participado en una actividad grupal.
     *
     * @param usuario El usuario a verificar.
     * @param conn    La conexión a la base de datos.
     * @return true si el usuario ha participado en una actividad grupal, false en caso contrario.
     * @throws Exception Si ocurre un error durante la verificación.
     */
    private boolean verificarActividadGrupal(Usuario usuario, Connection conn) throws Exception {
        String query = "SELECT COUNT(*) AS total " + "FROM Actividades WHERE usuarioId = ? AND esCompetencia = TRUE";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, usuario.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total") > 0;
            }
        }
        return false;
    }

    /**
     * Verifica si el usuario ha practicado una variedad de deportes en la última semana.
     *
     * @param usuario El usuario a verificar.
     * @param conn    La conexión a la base de datos.
     * @return true si el usuario ha practicado al menos 3 deportes diferentes en la última semana, false en caso contrario.
     * @throws Exception Si ocurre un error durante la verificación.
     */
    private boolean verificarVariedadDeportes(Usuario usuario, Connection conn) throws Exception {
        String query = "SELECT DISTINCT tipo FROM Actividades "
                + "WHERE usuarioId = ? AND fecha >= CURDATE() - INTERVAL 7 DAY";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, usuario.getId());
            ResultSet rs = stmt.executeQuery();

            int tiposDeportes = 0;
            while (rs.next()) {
                tiposDeportes++;
            }
            return tiposDeportes >= this.getObjetivo();
        }
    }

    /**
     * Obtiene el objetivo del logro.
     *
     * @return El objetivo del logro.
     */
    public int getObjetivo() {
        return 3; // Valor predeterminado si no está configurado en la base de datos
    }

    /**
     * Registra el cumplimiento del logro para un usuario específico.
     *
     * @param usuario El usuario que ha cumplido el logro.
     */
    public void registrarCumplimiento(Usuario usuario) {
        try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                Configuracion.DB_PASSWORD)) {
            String query = "INSERT INTO LogrosCompletados (usuarioId, logroId, fechaCumplimiento) "
                    + "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE fechaCumplimiento = VALUES(fechaCumplimiento)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, usuario.getId());
                stmt.setString(2, this.id);
                stmt.setTimestamp(3, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
                stmt.executeUpdate();

                System.out.println("Logro registrado: " + this.nombre);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Verifica si el usuario ha mantenido una racha de días consecutivos realizando actividad física.
     *
     * @param usuario El usuario a verificar.
     * @param conn    La conexión a la base de datos.
     * @return true si el usuario ha mantenido la racha, false en caso contrario.
     * @throws Exception Si ocurre un error durante la verificación.
     */
    private boolean verificarRacha(Usuario usuario, Connection conn) throws Exception {
        String query = "SELECT DISTINCT fecha FROM Actividades "
                + "WHERE usuarioId = ? AND fecha BETWEEN CURDATE() - INTERVAL ? DAY AND CURDATE()";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, usuario.getId());
            stmt.setInt(2, this.getObjetivo());
            ResultSet rs = stmt.executeQuery();

            int diasConActividad = 0;
            while (rs.next()) {
                diasConActividad++;
            }
            return diasConActividad >= this.getObjetivo();
        }
    }

    /**
     * Verifica si el usuario ha cumplido una meta de tiempo o distancia en el día actual.
     *
     * @param usuario El usuario a verificar.
     * @param conn    La conexión a la base de datos.
     * @return true si el usuario ha cumplido la meta, false en caso contrario.
     * @throws Exception Si ocurre un error durante la verificación.
     */
    private boolean verificarMeta(Usuario usuario, Connection conn) throws Exception {
        String query = "SELECT SUM(duracionMinutos) AS totalTiempo, SUM(distanciaKm) AS totalDistancia "
                + "FROM Actividades WHERE usuarioId = ? AND fecha = CURDATE()";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, usuario.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int tiempo = rs.getInt("totalTiempo");
                double distancia = rs.getDouble("totalDistancia");
                return tiempo >= this.getObjetivo() || distancia >= this.getObjetivo();
            }
        }
        return false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getPuntosRecompensa() {
        return puntosRecompensa;
    }

    public void setPuntosRecompensa(int puntosRecompensa) {
        this.puntosRecompensa = puntosRecompensa;
    }

    public boolean isCompletado() {
        return completado;
    }

    public void setCompletado(boolean completado) {
        this.completado = completado;
    }

    public TipoLogro getTipo() {
        return tipo;
    }

    public void setTipo(TipoLogro tipo) {
        this.tipo = tipo;
    }

    public LocalDate getFechaCumplimiento() {
        return fechaCumplimiento;
    }

    public void setFechaCumplimiento(LocalDate fechaCumplimiento) {
        this.fechaCumplimiento = fechaCumplimiento;
    }

    /**
     * Enumeración que define los tipos de logros.
     */
    public enum TipoLogro {
        DIARIO, SEMANAL, MENSUAL
    }
}