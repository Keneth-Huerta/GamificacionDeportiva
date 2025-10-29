package gamificaciondeportiva;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 * La clase Desafio representa un desafío dentro de la aplicación de gamificación deportiva.
 * Cada desafío tiene un identificador, nombre, descripción, fechas de inicio y fin, puntos de recompensa,
 * una lista de participantes y un estado.
 *
 * <p>Esta clase permite:</p>
 * <ul>
 *   <li>Registrar y unirse a desafíos</li>
 *   <li>Actualizar el estado del desafío según la fecha actual</li>
 *   <li>Verificar si un usuario cumple las condiciones del desafío</li>
 *   <li>Marcar el desafío como completado y otorgar puntos de recompensa</li>
 *   <li>Obtener los detalles del desafío, como el nombre, descripción y fechas</li>
 * </ul>
 *
 * <p>Constructor de la clase:</p>
 *
 * @param id               Identificador del desafío.
 * @param nombre           Nombre del desafío.
 * @param descripcion      Descripción del desafío.
 * @param puntosRecompensa Puntos de recompensa del desafío.
 * @param fechaInicio      Fecha de inicio del desafío.
 * @param fechaFin         Fecha de fin del desafío.
 */
class Desafio {
    private String id;
    private String nombre;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private int puntosRecompensa;
    private List<Usuario> participantes;
    private EstadoDesafio estado;

    /**
     * Constructor para crear un nuevo desafío.
     *
     * @param id               Identificador del desafío.
     * @param nombre           Nombre del desafío.
     * @param descripcion      Descripción del desafío.
     * @param puntosRecompensa Puntos de recompensa del desafío.
     * @param fechaInicio      Fecha de inicio del desafío.
     * @param fechaFin         Fecha de fin del desafío.
     */
    public Desafio(String id, String nombre, String descripcion, int puntosRecompensa, LocalDate fechaInicio,
                   LocalDate fechaFin) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.puntosRecompensa = puntosRecompensa;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.participantes = new ArrayList<>();
        this.estado = EstadoDesafio.PENDIENTE;
    }

    // Getters y Setters

    /**
     * Obtiene el identificador del desafío.
     *
     * @return Identificador del desafío.
     */
    public String getId() {
        return id;
    }

    /**
     * Establece el identificador del desafío.
     *
     * @param id Identificador del desafío.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del desafío.
     *
     * @return Nombre del desafío.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del desafío.
     *
     * @param nombre Nombre del desafío.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la descripción del desafío.
     *
     * @return Descripción del desafío.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción del desafío.
     *
     * @param descripcion Descripción del desafío.
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene la fecha de inicio del desafío.
     *
     * @return Fecha de inicio del desafío.
     */
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    /**
     * Establece la fecha de inicio del desafío.
     *
     * @param fechaInicio Fecha de inicio del desafío.
     */
    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /**
     * Obtiene la fecha de fin del desafío.
     *
     * @return Fecha de fin del desafío.
     */
    public LocalDate getFechaFin() {
        return fechaFin;
    }

    /**
     * Establece la fecha de fin del desafío.
     *
     * @param fechaFin Fecha de fin del desafío.
     */
    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    /**
     * Obtiene los puntos de recompensa del desafío.
     *
     * @return Puntos de recompensa del desafío.
     */
    public int getPuntosRecompensa() {
        return puntosRecompensa;
    }

    /**
     * Establece los puntos de recompensa del desafío.
     *
     * @param puntosRecompensa Puntos de recompensa del desafío.
     */
    public void setPuntosRecompensa(int puntosRecompensa) {
        this.puntosRecompensa = puntosRecompensa;
    }

    /**
     * Obtiene la lista de participantes del desafío.
     *
     * @return Lista de participantes del desafío.
     */
    public List<Usuario> getParticipantes() {
        return participantes;
    }

    /**
     * Establece la lista de participantes del desafío.
     *
     * @param participantes Lista de participantes del desafío.
     */
    public void setParticipantes(List<Usuario> participantes) {
        this.participantes = participantes;
    }

    /**
     * Obtiene el estado del desafío.
     *
     * @return Estado del desafío.
     */
    public EstadoDesafio getEstado() {
        return estado;
    }

    /**
     * Establece el estado del desafío.
     *
     * @param estado Estado del desafío.
     */
    public void setEstado(EstadoDesafio estado) {
        this.estado = estado;
    }

    /**
     * Verifica si un usuario cumple las condiciones del desafío.
     *
     * @param usuario Usuario a verificar.
     * @return true si cumple las condiciones, false en caso contrario.
     */
    public boolean cumpleCondiciones(Usuario usuario) {
        // Ejemplo de lógica para diferentes tipos de desafíos
        switch (this.getId()) {
            case "D001": // Reto de distancia
                return verificarDistanciaTotal(usuario, 5); // 5 km como meta
            case "D002": // Entrenamiento constante
                return verificarRacha(usuario, 3); // 3 días consecutivos
            default:
                return false;
        }
    }

    /**
     * Verifica si un usuario ha recorrido una distancia total mínima.
     *
     * @param usuario         Usuario a verificar.
     * @param metaDistanciaKm Distancia mínima en kilómetros.
     * @return true si ha recorrido la distancia mínima, false en caso contrario.
     */
    private boolean verificarDistanciaTotal(Usuario usuario, double metaDistanciaKm) {
        try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                Configuracion.DB_PASSWORD)) {

            String query = "SELECT SUM(distanciaKm) AS totalDistancia FROM Actividades WHERE usuarioId = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, usuario.getId());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double totalDistancia = rs.getDouble("totalDistancia");
                return totalDistancia >= metaDistanciaKm;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Verifica si un usuario ha mantenido una racha de actividad durante un número de días consecutivos.
     *
     * @param usuario Usuario a verificar.
     * @param dias    Número de días consecutivos.
     * @return true si ha mantenido la racha, false en caso contrario.
     */
    private boolean verificarRacha(Usuario usuario, int dias) {
        try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                Configuracion.DB_PASSWORD)) {

            String query = "SELECT DISTINCT fecha FROM Actividades WHERE usuarioId = ? AND fecha >= ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, usuario.getId());
            stmt.setDate(2, java.sql.Date.valueOf(LocalDate.now().minusDays(dias - 1)));
            ResultSet rs = stmt.executeQuery();

            int diasActivos = 0;
            while (rs.next()) {
                diasActivos++;
            }

            return diasActivos >= dias;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Permite a un usuario unirse al desafío.
     *
     * @param usuario Usuario que se une al desafío.
     */
    public void unirseAlDesafio(Usuario usuario) {
        if (!participantes.contains(usuario)) {
            participantes.add(usuario);
            System.out.println(usuario.getNombre() + " se ha unido al desafío: " + this.nombre);
        }
    }

    /**
     * Actualiza el estado del desafío en función de la fecha actual.
     */
    public void actualizarEstado() {
        LocalDate ahora = LocalDate.now();
        if (ahora.isBefore(fechaInicio)) {
            estado = EstadoDesafio.PENDIENTE;
        } else if (ahora.isAfter(fechaFin)) {
            estado = EstadoDesafio.EXPIRADO;
        } else if (estado != EstadoDesafio.COMPLETADO) {
            estado = EstadoDesafio.ACTIVO;
        }
    }

    /**
     * Marca el desafío como completado para un usuario y le otorga los puntos de recompensa.
     *
     * @param usuario Usuario que completa el desafío.
     */
    public void completarDesafio(Usuario usuario) {
        if (estado == EstadoDesafio.ACTIVO && participantes.contains(usuario)) {
            estado = EstadoDesafio.COMPLETADO;
            usuario.ganarPuntos(puntosRecompensa);
            System.out.println("Desafío completado: " + nombre + ". ¡" + usuario.getNombre() + " ha ganado "
                    + puntosRecompensa + " puntos!");
        }
    }

    /**
     * Verifica si el desafío está activo.
     *
     * @return true si el desafío está activo, false en caso contrario.
     */
    public boolean esActivo() {
        return estado == EstadoDesafio.ACTIVO;
    }

    /**
     * Enum para los estados del desafío.
     */
    public enum EstadoDesafio {
        PENDIENTE, ACTIVO, COMPLETADO, EXPIRADO
    }
}