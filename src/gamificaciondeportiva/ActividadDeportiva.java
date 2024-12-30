package gamificaciondeportiva;

import java.time.LocalDate;
import java.time.LocalTime;

// Clase para gestionar actividades deportivas
class ActividadDeportiva {
    private String id;
    private TipoDeporte tipo;
    private LocalDate fecha;
    private LocalTime hora;
    private int duracionMinutos;
    private double distanciaKm;
    private int caloriasQuemadas;
    private Usuario usuario;

    private final boolean esCompetencia;

    public enum TipoDeporte {
        CORRER, NATACION, CICLISMO, FUTBOL, BALONCESTO, VOLLEYBALL, GIMNASIO
    }

    public ActividadDeportiva(String id, TipoDeporte tipo, int duracionMinutos, double distanciaKm, Usuario usuario, boolean esCompetencia) {
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

    private void calcularCalorias() {
        switch (this.tipo) {
            case CORRER -> this.caloriasQuemadas = (int) ((duracionMinutos * 10) + (distanciaKm * 50));
            case NATACION -> this.caloriasQuemadas = (int) ((duracionMinutos * 12) + (distanciaKm * 60));
            case CICLISMO -> this.caloriasQuemadas = (int) ((duracionMinutos * 8) + (distanciaKm * 40));
            default -> this.caloriasQuemadas = duracionMinutos * 6;
        }
    }

    // Métodos Getters y Setters
    public String getId() {
        return id;
    }

    public boolean esCompetencia() {
        return esCompetencia;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TipoDeporte getTipo() {
        return tipo;
    }

    public void setTipo(TipoDeporte tipo) {
        this.tipo = tipo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public double getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(double distanciaKm) {
        this.distanciaKm = distanciaKm;
    }

    public int getCaloriasQuemadas() {
        return caloriasQuemadas;
    }

    public void setCaloriasQuemadas(int caloriasQuemadas) {
        this.caloriasQuemadas = caloriasQuemadas;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
