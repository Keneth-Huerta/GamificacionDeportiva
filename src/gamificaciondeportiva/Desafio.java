package gamificaciondeportiva;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//Clase para gestionar desafíos
class Desafio {
	private String id;
	private String nombre;
	private String descripcion;
	private LocalDate fechaInicio;
	private LocalDate fechaFin;
	private int puntosRecompensa;
	private List<Usuario> participantes;
	private EstadoDesafio estado;

	public enum EstadoDesafio {
		PENDIENTE, ACTIVO, COMPLETADO, EXPIRADO
	}

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

	public LocalDate getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(LocalDate fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public LocalDate getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(LocalDate fechaFin) {
		this.fechaFin = fechaFin;
	}

	public int getPuntosRecompensa() {
		return puntosRecompensa;
	}

	public void setPuntosRecompensa(int puntosRecompensa) {
		this.puntosRecompensa = puntosRecompensa;
	}

	public List<Usuario> getParticipantes() {
		return participantes;
	}

	public void setParticipantes(List<Usuario> participantes) {
		this.participantes = participantes;
	}

	public EstadoDesafio getEstado() {
		return estado;
	}

	public void setEstado(EstadoDesafio estado) {
		this.estado = estado;
	}

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

	// Métodos adicionales
	public void unirseAlDesafio(Usuario usuario) {
		if (!participantes.contains(usuario)) {
			participantes.add(usuario);
			System.out.println(usuario.getNombre() + " se ha unido al desafío: " + this.nombre);
		}
	}

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

	public void completarDesafio(Usuario usuario) {
		if (estado == EstadoDesafio.ACTIVO && participantes.contains(usuario)) {
			estado = EstadoDesafio.COMPLETADO;
			usuario.ganarPuntos(puntosRecompensa);
			System.out.println("Desafío completado: " + nombre + ". ¡" + usuario.getNombre() + " ha ganado "
					+ puntosRecompensa + " puntos!");
		}
	}

	public boolean esActivo() {
		return estado == EstadoDesafio.ACTIVO;
	}
}
