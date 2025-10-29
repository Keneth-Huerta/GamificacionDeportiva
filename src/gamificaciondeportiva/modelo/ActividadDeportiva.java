package gamificaciondeportiva;

import java.time.LocalDate;
import java.time.LocalTime;


/**
 * La clase ActividadDeportiva representa una actividad física realizada por un usuario.
 * Contiene información sobre el tipo de deporte, duración, distancia, calorías quemadas,
 * fecha y hora de la actividad, y si es una competencia.
 *
 * <p>Proporciona métodos para obtener y establecer los atributos de la actividad,
 * así como para calcular las calorías quemadas en función del tipo de deporte,
 * duración y distancia.</p>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>
 * {@code
 * Usuario usuario = new Usuario("John Doe");
 * ActividadDeportiva actividad = new ActividadDeportiva("001", TipoDeporte.CORRER, 30, 5.0, usuario, false);
 * System.out.println("Calorías quemadas: " + actividad.getCaloriasQuemadas());
 * }
 * </pre>
 *
 * <p>La clase también incluye un enum {@code TipoDeporte} que define los tipos de deportes
 * disponibles: CORRER, NATACION, CICLISMO, FUTBOL, BALONCESTO, VOLLEYBALL, GIMNASIO.</p>
 *
 * @see Usuario
 * @see TipoDeporte
 */
class ActividadDeportiva {
    private String id;
    private TipoDeporte tipo;
    private LocalDate fecha;
    private LocalTime hora;
    private int duracionMinutos;
    private double distanciaKm;
    private int caloriasQuemadas;
    private Usuario usuario;
    private boolean esCompetencia;

    /**
     * Constructor para crear una nueva actividad deportiva.
     *
     * @param id              Identificador de la actividad.
     * @param tipo            Tipo de deporte.
     * @param duracionMinutos Duración de la actividad en minutos.
     * @param distanciaKm     Distancia recorrida en kilómetros.
     * @param usuario         Usuario que realiza la actividad.
     * @param esCompetencia   Indica si la actividad es una competencia.
     */
    public ActividadDeportiva(String id, TipoDeporte tipo, int duracionMinutos, double distanciaKm, Usuario usuario,
                              boolean esCompetencia) {
        this.id = id;
        this.tipo = tipo;
        this.fecha = LocalDate.now();
        this.hora = LocalTime.now();
        this.duracionMinutos = duracionMinutos;
        this.distanciaKm = distanciaKm;
        this.usuario = usuario;
        this.esCompetencia = esCompetencia;
        calcularCalorias();
    }

    /**
     * Calcula las calorías quemadas en función del tipo de deporte, duración y distancia.
     */
    private void calcularCalorias() {
        switch (this.tipo) {
            case CORRER -> this.caloriasQuemadas = (int) ((duracionMinutos * 10) + (distanciaKm * 50));
            case NATACION -> this.caloriasQuemadas = (int) ((duracionMinutos * 12) + (distanciaKm * 60));
            case CICLISMO -> this.caloriasQuemadas = (int) ((duracionMinutos * 8) + (distanciaKm * 40));
            default -> this.caloriasQuemadas = duracionMinutos * 6;
        }
    }

    // Métodos Getters y Setters

    /**
     * Obtiene el identificador de la actividad.
     *
     * @return Identificador de la actividad.
     */
    public String getId() {
        return id;
    }

    /**
     * Establece el identificador de la actividad.
     *
     * @param id Identificador de la actividad.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Indica si la actividad es una competencia.
     *
     * @return true si es una competencia, false en caso contrario.
     */
    public boolean esCompetencia() {
        return esCompetencia;
    }

    /**
     * Obtiene el tipo de deporte de la actividad.
     *
     * @return Tipo de deporte.
     */
    public TipoDeporte getTipo() {
        return tipo;
    }

    /**
     * Establece el tipo de deporte de la actividad.
     *
     * @param tipo Tipo de deporte.
     */
    public void setTipo(TipoDeporte tipo) {
        this.tipo = tipo;
    }

    /**
     * Obtiene la fecha de la actividad.
     *
     * @return Fecha de la actividad.
     */
    public LocalDate getFecha() {
        return fecha;
    }

    /**
     * Establece la fecha de la actividad.
     *
     * @param fecha Fecha de la actividad.
     */
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    /**
     * Obtiene la hora de la actividad.
     *
     * @return Hora de la actividad.
     */
    public LocalTime getHora() {
        return hora;
    }

    /**
     * Establece la hora de la actividad.
     *
     * @param hora Hora de la actividad.
     */
    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    /**
     * Obtiene la duración de la actividad en minutos.
     *
     * @return Duración de la actividad en minutos.
     */
    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    /**
     * Establece la duración de la actividad en minutos.
     *
     * @param duracionMinutos Duración de la actividad en minutos.
     */
    public void setDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    /**
     * Obtiene la distancia recorrida en kilómetros.
     *
     * @return Distancia recorrida en kilómetros.
     */
    public double getDistanciaKm() {
        return distanciaKm;
    }

    /**
     * Establece la distancia recorrida en kilómetros.
     *
     * @param distanciaKm Distancia recorrida en kilómetros.
     */
    public void setDistanciaKm(double distanciaKm) {
        this.distanciaKm = distanciaKm;
    }

    /**
     * Obtiene las calorías quemadas durante la actividad.
     *
     * @return Calorías quemadas.
     */
    public int getCaloriasQuemadas() {
        return caloriasQuemadas;
    }

    /**
     * Establece las calorías quemadas durante la actividad.
     *
     * @param caloriasQuemadas Calorías quemadas.
     */
    public void setCaloriasQuemadas(int caloriasQuemadas) {
        this.caloriasQuemadas = caloriasQuemadas;
    }

    /**
     * Obtiene el usuario que realiza la actividad.
     *
     * @return Usuario que realiza la actividad.
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Establece el usuario que realiza la actividad.
     *
     * @param usuario Usuario que realiza la actividad.
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Enum para los tipos de deporte.
     */
    public enum TipoDeporte {
        CORRER, NATACION, CICLISMO, FUTBOL, BALONCESTO, VOLLEYBALL, GIMNASIO
    }
}