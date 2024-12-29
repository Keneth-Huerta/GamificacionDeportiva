package gamificaciondeportiva;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CargaDatos {

	public static void cargarDatosRelacionados(Connection conn, SistemaGamificacion sistema, Usuario usuario)
			throws Exception {
		cargarLogros(conn, sistema, usuario);
		cargarDesafios(conn, sistema, usuario);
		cargarCompetencias(conn, sistema, usuario); // Pasa el usuario como parámetro
		cargarActividades(conn, sistema, usuario);
		// Al cargar los datos del usuario desde la base de datos
		try (Connection conn1 = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
		        Configuracion.DB_PASSWORD)) {

		    String query = "SELECT fotoPerfil FROM Usuarios WHERE id = ?";
		    PreparedStatement stmt = conn1.prepareStatement(query);
		    stmt.setString(1, usuario.getId());
		    ResultSet rs = stmt.executeQuery();

		    if (rs.next()) {
		        usuario.setFotoPerfil(rs.getBytes("fotoPerfil"));
		    }
		} catch (Exception ex) {
		    ex.printStackTrace();
		}

	}

	public static void cargarLogros(Connection conn, SistemaGamificacion sistema, Usuario usuario) throws Exception {
		String logrosQuery = "SELECT l.*, "
				+ "CASE WHEN lc.usuarioId IS NOT NULL THEN TRUE ELSE FALSE END AS completado, "
				+ "lc.fechaCumplimiento " + "FROM Logros l "
				+ "LEFT JOIN LogrosCompletados lc ON l.id = lc.logroId AND lc.usuarioId = ?";
		PreparedStatement stmt = conn.prepareStatement(logrosQuery);
		stmt.setString(1, usuario.getId());
		ResultSet rs = stmt.executeQuery();

		while (rs.next()) {
			Logro logro = new Logro(rs.getString("id"), rs.getString("nombre"), rs.getString("descripcion"),
					rs.getInt("puntosRecompensa"), Logro.TipoLogro.valueOf(rs.getString("tipo")));
			if (rs.getBoolean("completado")) {
				logro.setCompletado(true);
				logro.setFechaCumplimiento(rs.getDate("fechaCumplimiento").toLocalDate());
			}
			sistema.getLogrosDisponibles().add(logro);
		}
	}

	public static void cargarDesafios(Connection conn, SistemaGamificacion sistema, Usuario usuario) throws Exception {
		String query = "SELECT d.*, " + "CASE WHEN dc.usuarioId IS NOT NULL THEN TRUE ELSE FALSE END AS completado "
				+ "FROM Desafios d " + "LEFT JOIN DesafiosCompletados dc ON d.id = dc.desafioId AND dc.usuarioId = ?";
		PreparedStatement stmt = conn.prepareStatement(query);
		stmt.setString(1, usuario.getId());
		ResultSet rs = stmt.executeQuery();

		while (rs.next()) {
			Desafio desafio = new Desafio(rs.getString("id"), // ID del desafío
					rs.getString("nombre"), // Nombre
					rs.getString("descripcion"), // Descripción
					rs.getInt("puntosRecompensa"), // Puntos de recompensa
					rs.getDate("fechaInicio").toLocalDate(), // Fecha de inicio
					rs.getDate("fechaFin").toLocalDate() // Fecha de fin
			);
			desafio.setEstado(Desafio.EstadoDesafio.valueOf(rs.getString("estado"))); // Estado actual

			// Marcar el desafío como completado si aplica
			if (rs.getBoolean("completado")) {
				desafio.setEstado(Desafio.EstadoDesafio.COMPLETADO);
			}

			sistema.getDesafiosActivos().add(desafio); // Agregar el desafío al sistema
		}
	}

	public static void cargarActividades(Connection conn, SistemaGamificacion sistema, Usuario usuario)
			throws Exception {
		String actividadesQuery = "SELECT * FROM Actividades WHERE usuarioId = ?";
		PreparedStatement stmt = conn.prepareStatement(actividadesQuery);
		stmt.setString(1, usuario.getId());
		ResultSet rs = stmt.executeQuery();

		while (rs.next()) {
			ActividadDeportiva actividad = new ActividadDeportiva(rs.getString("id"),
					ActividadDeportiva.TipoDeporte.valueOf(rs.getString("tipo")), rs.getInt("duracionMinutos"),
					rs.getDouble("distanciaKm"), usuario, false);
			actividad.setFecha(rs.getDate("fecha").toLocalDate());
			actividad.setHora(rs.getTime("hora").toLocalTime());
			sistema.getUsuarios().stream().filter(u -> u.getId().equals(usuario.getId())).findFirst()
					.ifPresent(u -> u.agregarDesafio(null)); // Agrega lógica adicional si es necesario
		}
	}

	public static void cargarCompetencias(Connection conn, SistemaGamificacion sistema, Usuario usuario)
			throws Exception {
		String query = "SELECT c.id, c.nombre, c.tipoDeporte, " + "cp.fechaInicio, cp.fechaFin, cp.estado "
				+ "FROM Competencias c "
				+ "LEFT JOIN CompetenciasParticipacion cp ON c.id = cp.competenciaId AND cp.usuarioId = ?";
		PreparedStatement stmt = conn.prepareStatement(query);
		stmt.setString(1, usuario.getId());
		ResultSet rs = stmt.executeQuery();

		while (rs.next()) {
			Competencia competencia = new Competencia(rs.getString("id"), // ID de la competencia
					rs.getString("nombre"), // Nombre de la competencia
					ActividadDeportiva.TipoDeporte.valueOf(rs.getString("tipoDeporte")), // Tipo de deporte
					rs.getDate("fechaInicio") != null ? rs.getDate("fechaInicio").toLocalDate() : null, // Fecha de
																										// inicio
					rs.getDate("fechaFin") != null ? rs.getDate("fechaFin").toLocalDate() : null // Fecha de fin
			);
			competencia.setEstado(
					rs.getString("estado") != null ? Competencia.EstadoCompetencia.valueOf(rs.getString("estado"))
							: Competencia.EstadoCompetencia.REGISTRO); // Estado por defecto
			sistema.getCompetencias().add(competencia);
		}
	}

}
