package gamificaciondeportiva;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.*;

/**
 * Representa a un usuario dentro del sistema de gamificación deportiva.
 * <p>
 * La clase gestiona la información del usuario, como su nombre, nivel, puntos y logros obtenidos,
 * así como los desafíos en los que participa. Además, permite interactuar con la base de datos para:
 * <ul>
 *   <li>Registrar y recuperar actividades realizadas por el usuario.</li>
 *   <li>Actualizar los puntos y la experiencia ganados durante las actividades.</li>
 *   <li>Gestionar el progreso del usuario en desafíos y logros.</li>
 * </ul>
 * <p>
 * Esta clase también proporciona métodos para:
 * <ul>
 *   <li>Obtener los deportes practicados por el usuario en la última semana.</li>
 *   <li>Obtener el historial de actividades del usuario.</li>
 *   <li>Verificar si el usuario ha subido de nivel según su experiencia.</li>
 *   <li>Gestionar el perfil del usuario, incluyendo su foto de perfil y si tiene permisos de administrador.</li>
 * </ul>
 * <p>
 * Los datos del usuario son actualizados automáticamente en la base de datos a medida que cambian.
 */
public class Usuario {
    private String id;
    private String nombre;
    private int nivel;
    private int puntosTotales;
    private int experiencia;
    private List<Logro> logrosObtenidos;
    private List<Desafio> desafiosActivos;
    private byte[] fotoPerfil;
    private boolean esAdmin; // Nuevo atributo
    private Runnable onCambioDatos;

    /**
     * Constructor de la clase Usuario.
     *
     * @param id       El ID del usuario.
     * @param nombre   El nombre del usuario.
     * @param interfaz La interfaz gráfica asociada al usuario.
     */
    public Usuario(String id, String nombre, JFrame interfaz) {
        this.id = id;
        this.nombre = nombre;
        this.nivel = 1;
        this.puntosTotales = 0;
        this.experiencia = 0;
        this.logrosObtenidos = new ArrayList<>();
        this.desafiosActivos = new ArrayList<>();
    }

    /**
     * Constructor de la clase Usuario.
     *
     * @param id     El ID del usuario.
     * @param nombre El nombre del usuario.
     */
    public Usuario(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.nivel = 1;
        this.puntosTotales = 0;
        this.experiencia = 0;
        this.logrosObtenidos = new ArrayList<>();
        this.desafiosActivos = new ArrayList<>();
    }

    /**
     * Obtiene los deportes practicados por el usuario en la última semana.
     *
     * @return Un conjunto de nombres de deportes practicados.
     */
    public Set<String> getDeportesPracticadosUltimaSemana() {
        Set<String> deportes = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                Configuracion.DB_PASSWORD)) {

            String query = "SELECT DISTINCT tipo FROM Actividades WHERE usuarioId = ? AND fecha >= ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, this.getId());
            stmt.setDate(2, java.sql.Date.valueOf(LocalDate.now().minusDays(7)));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                deportes.add(rs.getString("tipo"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return deportes;
    }

    /**
     * Obtiene el registro de actividades del usuario.
     *
     * @return Un mapa con las fechas y un valor booleano indicando si hubo actividad.
     */
    public Map<LocalDate, Boolean> getRegistroActividades() {
        Map<LocalDate, Boolean> registro = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                Configuracion.DB_PASSWORD)) {

            String query = "SELECT DISTINCT fecha FROM Actividades WHERE usuarioId = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, this.getId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LocalDate fecha = rs.getDate("fecha").toLocalDate();
                registro.put(fecha, true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return registro;
    }

    /**
     * Actualiza los datos del usuario en la base de datos.
     */
    private void actualizarDatosEnBaseDeDatos() {
        try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                Configuracion.DB_PASSWORD)) {

            String query = "UPDATE Usuarios SET puntosTotales = ?, experiencia = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, this.puntosTotales);
            stmt.setInt(2, this.experiencia);
            stmt.setString(3, this.id);
            stmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Establece un callback para notificar cambios en los datos del usuario.
     *
     * @param onCambioDatos El callback a ejecutar cuando cambien los datos.
     */
    public void setOnCambioDatos(Runnable onCambioDatos) {
        this.onCambioDatos = onCambioDatos;
    }

    /**
     * Notifica que los datos del usuario han cambiado.
     */
    private void notificarCambioDatos() {
        if (onCambioDatos != null) {
            onCambioDatos.run();
        }
    }

    /**
     * Añade puntos al usuario y actualiza la base de datos.
     *
     * @param puntos Los puntos a añadir.
     */
    public void ganarPuntos(int puntos) {
        this.puntosTotales += puntos;
        ganarExperiencia(puntos);
        actualizarDatosEnBaseDeDatos();
        notificarCambioDatos();
    }

    /**
     * Añade experiencia al usuario y verifica si ha subido de nivel.
     *
     * @param exp La experiencia a añadir.
     */
    public void ganarExperiencia(int exp) {
        this.experiencia += exp;
        verificarNivel();
        notificarCambioDatos(); // Notifica cambios
    }

    /**
     * Verifica si el usuario ha subido de nivel basado en su experiencia.
     */
    private void verificarNivel() {
        int nuevoNivel = (this.experiencia / 1000) + 1;
        if (nuevoNivel > this.nivel) {
            this.nivel = nuevoNivel;
            System.out.println("¡Felicidades! Has alcanzado el nivel " + this.nivel);
        }
    }

    /**
     * Obtiene la última actividad realizada por el usuario.
     *
     * @return La última actividad deportiva del usuario, o null si no hay actividades.
     */
    public ActividadDeportiva getUltimaActividad() {
        try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                Configuracion.DB_PASSWORD)) {

            String query = "SELECT * FROM Actividades WHERE usuarioId = ? ORDER BY fecha DESC, hora DESC LIMIT 1";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, this.getId());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new ActividadDeportiva(rs.getString("id"),
                        ActividadDeportiva.TipoDeporte.valueOf(rs.getString("tipo")), rs.getInt("duracionMinutos"),
                        rs.getDouble("distanciaKm"), this, // Usuario actual
                        rs.getBoolean("esCompetencia"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null; // Si no hay actividades
    }

    /**
     * Agrega un logro a la lista de logros obtenidos por el usuario.
     *
     * @param logro El logro a agregar.
     */
    public void agregarLogro(Logro logro) {
        if (logro != null && !logrosObtenidos.contains(logro)) {
            logrosObtenidos.add(logro);
        }
    }

    /**
     * Agrega un desafío a la lista de desafíos activos del usuario.
     *
     * @param desafio El desafío a agregar.
     */
    public void agregarDesafio(Desafio desafio) {
        if (desafio != null && !desafiosActivos.contains(desafio)) {
            desafiosActivos.add(desafio);
        }
    }

    // Métodos getter y setter para los atributos de la clase

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

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public int getPuntosTotales() {
        return puntosTotales;
    }

    public void setPuntosTotales(int puntosTotales) {
        this.puntosTotales = puntosTotales;
    }

    public int getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(int experiencia) {
        this.experiencia = experiencia;
    }

    public List<Logro> getLogrosObtenidos() {
        return logrosObtenidos;
    }

    public void setLogrosObtenidos(List<Logro> logrosObtenidos) {
        this.logrosObtenidos = logrosObtenidos;
    }

    public List<Desafio> getDesafiosActivos() {
        return desafiosActivos;
    }

    public void setDesafiosActivos(List<Desafio> desafiosActivos) {
        this.desafiosActivos = desafiosActivos;
    }

    public byte[] getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(byte[] fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public boolean isEsAdmin() {
        return esAdmin;
    }

    public void setEsAdmin(boolean esAdmin) {
        this.esAdmin = esAdmin;
    }
}