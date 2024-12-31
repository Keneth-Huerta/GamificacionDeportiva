package gamificaciondeportiva;

import gamificaciondeportiva.ActividadDeportiva.TipoDeporte;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * La clase Competencia representa una competencia deportiva con participantes y puntuaciones.
 * Permite registrar participantes, actualizar sus puntuaciones, iniciar y finalizar la competencia,
 * y anunciar los ganadores.
 *
 * <p>Esta clase incluye métodos para:</p>
 * <ul>
 *   <li>Registrar participantes en la competencia</li>
 *   <li>Actualizar la puntuación de los participantes</li>
 *   <li>Iniciar y finalizar la competencia</li>
 *   <li>Anunciar los ganadores de la competencia</li>
 * </ul>
 *
 * <p>Constructor de la clase:</p>
 *
 * @param id          Identificador de la competencia.
 * @param nombre      Nombre de la competencia.
 * @param tipoDeporte Tipo de deporte de la competencia.
 * @param fechaInicio Fecha de inicio de la competencia.
 * @param fechaFin    Fecha de fin de la competencia.
 */
public class Competencia {
    private String id;
    private String nombre;
    private TipoDeporte tipoDeporte;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Map<Usuario, Integer> participantesYPuntos;
    private EstadoCompetencia estado;

    /**
     * Constructor para crear una nueva competencia.
     *
     * @param id          Identificador de la competencia.
     * @param nombre      Nombre de la competencia.
     * @param tipoDeporte Tipo de deporte de la competencia.
     * @param fechaInicio Fecha de inicio de la competencia.
     * @param fechaFin    Fecha de fin de la competencia.
     */
    public Competencia(String id, String nombre, TipoDeporte tipoDeporte, LocalDate fechaInicio, LocalDate fechaFin) {
        this.id = id;
        this.nombre = nombre;
        this.tipoDeporte = tipoDeporte;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.participantesYPuntos = new HashMap<>();
        this.estado = EstadoCompetencia.REGISTRO;
    }

    /**
     * Registra un participante en la competencia.
     *
     * @param usuario Usuario a registrar.
     */
    public void registrarParticipante(Usuario usuario) {
        if (estado == EstadoCompetencia.REGISTRO) {
            participantesYPuntos.put(usuario, 0);
            System.out.println(usuario.getNombre() + " registrado en la competencia: " + this.nombre);
        }
    }

    /**
     * Actualiza la puntuación de un participante.
     *
     * @param usuario Usuario cuya puntuación se va a actualizar.
     * @param puntos  Puntos a añadir.
     */
    public void actualizarPuntuacion(Usuario usuario, int puntos) {
        if (estado == EstadoCompetencia.EN_PROGRESO) {
            participantesYPuntos.put(usuario, participantesYPuntos.getOrDefault(usuario, 0) + puntos);
        }
    }

    /**
     * Inicia la competencia.
     */
    public void iniciarCompetencia() {
        if (estado == EstadoCompetencia.REGISTRO && LocalDate.now().isEqual(fechaInicio)) {
            estado = EstadoCompetencia.EN_PROGRESO;
            System.out.println("La competencia " + nombre + " ha comenzado.");
        }
    }

    /**
     * Finaliza la competencia.
     */
    public void finalizarCompetencia() {
        if (estado == EstadoCompetencia.EN_PROGRESO) {
            estado = EstadoCompetencia.FINALIZADA;
            anunciarGanadores();
        }
    }

    /**
     * Anuncia los ganadores de la competencia.
     */
    private void anunciarGanadores() {
        List<Map.Entry<Usuario, Integer>> ranking = new ArrayList<>(participantesYPuntos.entrySet());
        ranking.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        System.out.println("\u00a1Resultados de la competencia " + this.nombre + "!");
        for (int i = 0; i < Math.min(3, ranking.size()); i++) {
            Map.Entry<Usuario, Integer> entrada = ranking.get(i);
            System.out.println((i + 1) + "\u00b0 lugar: " + entrada.getKey().getNombre() + " con " + entrada.getValue() + " puntos");
        }
    }

    // Métodos Getters y Setters

    /**
     * Obtiene el identificador de la competencia.
     *
     * @return Identificador de la competencia.
     */
    public String getId() {
        return id;
    }

    /**
     * Establece el identificador de la competencia.
     *
     * @param id Identificador de la competencia.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre de la competencia.
     *
     * @return Nombre de la competencia.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de la competencia.
     *
     * @param nombre Nombre de la competencia.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el tipo de deporte de la competencia.
     *
     * @return Tipo de deporte de la competencia.
     */
    public TipoDeporte getTipoDeporte() {
        return tipoDeporte;
    }

    /**
     * Establece el tipo de deporte de la competencia.
     *
     * @param tipoDeporte Tipo de deporte de la competencia.
     */
    public void setTipoDeporte(TipoDeporte tipoDeporte) {
        this.tipoDeporte = tipoDeporte;
    }

    /**
     * Obtiene la fecha de inicio de la competencia.
     *
     * @return Fecha de inicio de la competencia.
     */
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    /**
     * Establece la fecha de inicio de la competencia.
     *
     * @param fechaInicio Fecha de inicio de la competencia.
     */
    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /**
     * Obtiene la fecha de fin de la competencia.
     *
     * @return Fecha de fin de la competencia.
     */
    public LocalDate getFechaFin() {
        return fechaFin;
    }

    /**
     * Establece la fecha de fin de la competencia.
     *
     * @param fechaFin Fecha de fin de la competencia.
     */
    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    /**
     * Obtiene el mapa de participantes y sus puntos.
     *
     * @return Mapa de participantes y sus puntos.
     */
    public Map<Usuario, Integer> getParticipantesYPuntos() {
        return participantesYPuntos;
    }

    /**
     * Establece el mapa de participantes y sus puntos.
     *
     * @param participantesYPuntos Mapa de participantes y sus puntos.
     */
    public void setParticipantesYPuntos(Map<Usuario, Integer> participantesYPuntos) {
        this.participantesYPuntos = participantesYPuntos;
    }

    /**
     * Obtiene el estado de la competencia.
     *
     * @return Estado de la competencia.
     */
    public EstadoCompetencia getEstado() {
        return estado;
    }

    /**
     * Establece el estado de la competencia.
     *
     * @param estado Estado de la competencia.
     */
    public void setEstado(EstadoCompetencia estado) {
        this.estado = estado;
    }

    /**
     * Enum para los estados de la competencia.
     */
    public enum EstadoCompetencia {
        REGISTRO, EN_PROGRESO, FINALIZADA
    }
}