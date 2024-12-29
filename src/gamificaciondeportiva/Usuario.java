package gamificaciondeportiva;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

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

	public Usuario(String id, String nombre, JFrame interfaz) {
		this.id = id;
		this.nombre = nombre;
		this.nivel = 1;
		this.puntosTotales = 0;
		this.experiencia = 0;
		this.logrosObtenidos = new ArrayList<>();
		this.desafiosActivos = new ArrayList<>();

	}// Añadir este constructor a Usuario.java

	public Usuario(String id, String nombre) {
		this.id = id;
		this.nombre = nombre;
		this.nivel = 1;
		this.puntosTotales = 0;
		this.experiencia = 0;
		this.logrosObtenidos = new ArrayList<>();
		this.desafiosActivos = new ArrayList<>();

	}

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

	private Runnable onCambioDatos;

	public void setOnCambioDatos(Runnable onCambioDatos) {
		this.onCambioDatos = onCambioDatos;
	}

	private void notificarCambioDatos() {
		if (onCambioDatos != null) {
			onCambioDatos.run();
		}
	}

	public void ganarPuntos(int puntos) {
		this.puntosTotales += puntos;
		ganarExperiencia(puntos);
		actualizarDatosEnBaseDeDatos();
		notificarCambioDatos();
	}

	public void ganarExperiencia(int exp) {
		this.experiencia += exp;
		verificarNivel();
		notificarCambioDatos(); // Notifica cambios
	}

	private void verificarNivel() {
		int nuevoNivel = (this.experiencia / 1000) + 1;
		if (nuevoNivel > this.nivel) {
			this.nivel = nuevoNivel;
			System.out.println("¡Felicidades! Has alcanzado el nivel " + this.nivel);
		}
	}

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

	public void agregarLogro(Logro logro) {
		if (logro != null && !logrosObtenidos.contains(logro)) {
			logrosObtenidos.add(logro);
		}
	}

	public void agregarDesafio(Desafio desafio) {
		if (desafio != null && !desafiosActivos.contains(desafio)) {
			desafiosActivos.add(desafio);
		}
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
