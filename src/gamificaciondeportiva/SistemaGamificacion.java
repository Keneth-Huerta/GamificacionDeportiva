package gamificaciondeportiva;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * La clase SistemaGamificacion gestiona los componentes centrales del sistema de gamificación deportiva,
 * incluyendo usuarios, logros, desafíos y competiciones.
 * <p>
 * Esta clase proporciona funcionalidades para:
 * <ul>
 *     <li>Registrar logros completados por los usuarios y actualizarlos en la base de datos.</li>
 *     <li>Obtener el estado de las competiciones en las que un usuario participa.</li>
 *     <li>Gestionar la información de los usuarios, como su foto de perfil y actualizarla en la base de datos.</li>
 *     <li>Registrar actividades deportivas realizadas por los usuarios, así como verificar y actualizar sus logros y desafíos.</li>
 *     <li>Gestionar competencias activas y las puntuaciones asociadas.</li>
 * </ul>
 * <p>
 * Además, la clase soporta la creación de competiciones y desafíos predeterminados, y la actualización de las
 * puntuaciones de los usuarios en los mismos. También incluye mecanismos para mantener el sistema actualizado mediante
 * la notificación de cambios a través de callbacks (Runnable).
 */
public class SistemaGamificacion {
    private List<Usuario> usuarios;
    private List<Logro> logrosDisponibles;
    private List<Desafio> desafiosActivos;
    private List<Competencia> competencias;

    private Runnable onLogrosActualizados;

    /**
     * Constructor de la clase SistemaGamificacion.
     * Inicializa las listas de usuarios, logros, desafíos y competencias.
     */
    public SistemaGamificacion() {
        usuarios = new ArrayList<>();
        logrosDisponibles = new ArrayList<>();
        desafiosActivos = new ArrayList<>();
        competencias = new ArrayList<>();
        inicializarLogros();
        inicializarCompetencias();
        inicializarDesafios();
    }

    /**
     * Registra un logro completado por un usuario en la base de datos.
     *
     * @param logro   El logro completado.
     * @param usuario El usuario que completó el logro.
     */
    public void registrarLogroCompletado(Logro logro, Usuario usuario) {
        try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                Configuracion.DB_PASSWORD)) {

            String query = "INSERT INTO LogrosCompletados (usuarioId, logroId, fechaCumplimiento) VALUES (?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE fechaCumplimiento = VALUES(fechaCumplimiento)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, usuario.getId());
            stmt.setString(2, logro.getId());
            stmt.setTimestamp(3, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
            stmt.executeUpdate();

            usuario.ganarPuntos(logro.getPuntosRecompensa());
            notificarLogrosActualizados();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Obtiene el estado de una competencia para un usuario específico.
     *
     * @param competenciaId El ID de la competencia.
     * @param usuarioId     El ID del usuario.
     * @return El estado de la competencia o null si no se encuentra.
     */
    public String obtenerEstadoCompetencia(String competenciaId, String usuarioId) {
        try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                Configuracion.DB_PASSWORD)) {

            String query = "SELECT estado FROM CompetenciasParticipacion WHERE competenciaId = ? AND usuarioId = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, competenciaId);
            stmt.setString(2, usuarioId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("estado");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null; // Retorna null si no encuentra el estado
    }

    /**
     * Obtiene la foto de perfil de un usuario desde la base de datos.
     *
     * @param usuarioId El ID del usuario.
     * @return Un ImageIcon con la foto de perfil o null si no se encuentra.
     */
    public ImageIcon obtenerFotoDesdeBaseDeDatos(String usuarioId) {
        try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                Configuracion.DB_PASSWORD)) {
            String query = "SELECT fotoPerfil FROM Usuarios WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                byte[] fotoBytes = rs.getBytes("fotoPerfil");
                if (fotoBytes != null) {
                    BufferedImage img = ImageIO.read(new ByteArrayInputStream(fotoBytes));
                    return new ImageIcon(img);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null; // Retorna null si no hay imagen o ocurre un error
    }

    /**
     * Carga una imagen predeterminada para el perfil.
     *
     * @return Un ImageIcon con la imagen predeterminada.
     */
    private ImageIcon cargarImagenPredeterminada() {
        try {
            ImageIcon iconoDefault = new ImageIcon(this.getClass().getResource("resources/default-profile.png"));
            Image defaultImage = iconoDefault.getImage();

            return new ImageIcon(hacerImagenCircular(defaultImage, 150));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // Retorna un ícono vacío si ocurre un error.
        return new ImageIcon(new BufferedImage(150, 150, BufferedImage.TYPE_INT_ARGB));
    }

    /**
     * Convierte una imagen en un BufferedImage circular.
     *
     * @param image    La imagen a convertir.
     * @param diameter El diámetro del círculo.
     * @return Un BufferedImage circular.
     */
    public BufferedImage hacerImagenCircular(Image image, int diameter) {
        BufferedImage output = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = output.createGraphics();

        g2.setClip(new Ellipse2D.Float(0, 0, diameter, diameter));
        g2.drawImage(image, 0, 0, diameter, diameter, null);
        g2.dispose();

        return output;
    }

    /**
     * Convierte un ImageIcon en un ImageIcon circular.
     *
     * @param icono El ImageIcon a convertir.
     * @return Un ImageIcon circular.
     */
    public ImageIcon hacerImagenCircular(ImageIcon icono) {
        BufferedImage originalImage = new BufferedImage(icono.getIconWidth(), icono.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = originalImage.createGraphics();
        icono.paintIcon(null, g, 0, 0);
        g.dispose();

        int diameter = Math.min(originalImage.getWidth(), originalImage.getHeight());
        BufferedImage circularImage = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = circularImage.createGraphics();
        g2d.setClip(new Ellipse2D.Float(0, 0, diameter, diameter));
        g2d.drawImage(originalImage, 0, 0, diameter, diameter, null);
        g2d.dispose();

        return new ImageIcon(circularImage);
    }

    /**
     * Actualiza la información de un usuario en la base de datos.
     *
     * @param usuario El usuario a actualizar.
     */
    public void actualizarUsuario(Usuario usuario) {
        try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                Configuracion.DB_PASSWORD)) {
            String query = "UPDATE Usuarios SET nombre = ?, fotoPerfil = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, usuario.getNombre());
            stmt.setBytes(2, usuario.getFotoPerfil()); // Actualizar solo la foto
            stmt.setString(3, usuario.getId());
            stmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Obtiene la competencia relacionada con un usuario.
     *
     * @param usuarioId El ID del usuario.
     * @return El ID de la competencia o null si no se encuentra.
     */
    public String obtenerCompetenciaRelacionada(String usuarioId) {
        try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                Configuracion.DB_PASSWORD)) {
            String query = "SELECT competenciaId FROM CompetenciasParticipacion WHERE usuarioId = ? AND estado = 'EN_PROGRESO'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("competenciaId");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null; // No hay competencias activas para el usuario
    }

    /**
     * Registra una actividad deportiva en la base de datos.
     *
     * @param actividad La actividad deportiva a registrar.
     */
    public void registrarActividad(ActividadDeportiva actividad) {
        try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                Configuracion.DB_PASSWORD)) {
            String query = "INSERT INTO Actividades (id, usuarioId, tipo, duracionMinutos, distanciaKm, fecha, hora, caloriasQuemadas, esCompetencia) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, actividad.getId());
            stmt.setString(2, actividad.getUsuario().getId());
            stmt.setString(3, actividad.getTipo().name());
            stmt.setInt(4, actividad.getDuracionMinutos());
            stmt.setDouble(5, actividad.getDistanciaKm());
            stmt.setDate(6, java.sql.Date.valueOf(actividad.getFecha()));
            stmt.setTime(7, java.sql.Time.valueOf(actividad.getHora()));
            stmt.setInt(8, actividad.getCaloriasQuemadas());
            stmt.setBoolean(9, actividad.esCompetencia());
            stmt.executeUpdate();

            verificarYActualizarLogros(actividad.getUsuario());
            verificarYActualizarDesafios(actividad.getUsuario());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Obtiene la lista de usuarios.
     *
     * @return La lista de usuarios.
     */
    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    /**
     * Obtiene la lista de logros disponibles.
     *
     * @return La lista de logros disponibles.
     */
    public List<Logro> getLogrosDisponibles() {
        return logrosDisponibles;
    }

    /**
     * Obtiene la lista de desafíos activos.
     *
     * @return La lista de desafíos activos.
     */
    public List<Desafio> getDesafiosActivos() {
        return desafiosActivos;
    }

    /**
     * Obtiene la lista de competencias.
     *
     * @return La lista de competencias.
     */
    public List<Competencia> getCompetencias() {
        return competencias;
    }

    /**
     * Establece un Runnable que se ejecutará cuando los logros sean actualizados.
     *
     * @param onLogrosActualizados El Runnable a ejecutar.
     */
    public void setOnLogrosActualizados(Runnable onLogrosActualizados) {
        this.onLogrosActualizados = onLogrosActualizados;
    }

    /**
     * Notifica que los logros han sido actualizados.
     */
    private void notificarLogrosActualizados() {
        if (onLogrosActualizados != null) {
            onLogrosActualizados.run();
        }
    }

    /**
     * Inicializa los logros en la base de datos.
     */
    private void inicializarLogros() {
        try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                Configuracion.DB_PASSWORD)) {

            for (Logro logro : List.of(Logro.crearLogroDiario(), Logro.crearLogroMetaTiempo(),
                    Logro.crearLogroParticipacionGrupal(), Logro.crearLogroRachaSemanal(),
                    Logro.crearLogroVariedadDeportes(), Logro.crearLogroMarcaPersonal(),
                    Logro.crearLogroParticiparCompetencias(), Logro.crearLogroAlcanzarMetasAcumuladas(),
                    Logro.crearLogroLiderarRanking())) {

                String query = "INSERT IGNORE INTO Logros (id, nombre, descripcion, puntosRecompensa, tipo) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, logro.getId());
                stmt.setString(2, logro.getNombre());
                stmt.setString(3, logro.getDescripcion());
                stmt.setInt(4, logro.getPuntosRecompensa());
                stmt.setString(5, logro.getTipo().name());
                stmt.executeUpdate();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Inicializa los desafíos en la base de datos.
     */
    public void inicializarDesafios() {
        try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                Configuracion.DB_PASSWORD)) {

            String query = "SELECT COUNT(*) FROM Desafios";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            if (rs.next() && rs.getInt(1) == 0) {
                // Insertar desafíos por defecto
                String insertQuery = "INSERT INTO Desafios (id, nombre, descripcion, fechaInicio, fechaFin, puntosRecompensa, estado) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);

                // Primer desafío
                insertStmt.setString(1, "D001");
                insertStmt.setString(2, "Reto de distancia");
                insertStmt.setString(3, "Correr 5 km en un día.");
                insertStmt.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
                insertStmt.setDate(5, java.sql.Date.valueOf(LocalDate.now().plusDays(7)));
                insertStmt.setInt(6, 200);
                insertStmt.setString(7, "ACTIVO");
                insertStmt.executeUpdate();

                // Segundo desafío
                insertStmt.setString(1, "D002");
                insertStmt.setString(2, "Entrenamiento constante");
                insertStmt.setString(3, "Realizar actividad física 3 días consecutivos.");
                insertStmt.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
                insertStmt.setDate(5, java.sql.Date.valueOf(LocalDate.now().plusDays(7)));
                insertStmt.setInt(6, 300);
                insertStmt.setString(7, "ACTIVO");
                insertStmt.executeUpdate();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Registra un desafío completado por un usuario en la base de datos.
     *
     * @param desafio El desafío completado.
     * @param usuario El usuario que completó el desafío.
     */
    public void registrarDesafioCompletado(Desafio desafio, Usuario usuario) {
        try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                Configuracion.DB_PASSWORD)) {

            String query = "INSERT INTO DesafiosCompletados (usuarioId, desafioId, fechaCumplimiento) "
                    + "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE fechaCumplimiento = VALUES(fechaCumplimiento)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, usuario.getId());
            stmt.setString(2, desafio.getId());
            stmt.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            stmt.executeUpdate();

            System.out.println("Desafío completado registrado: " + desafio.getNombre());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Calcula los puntos obtenidos por una actividad deportiva.
     *
     * @param actividad La actividad deportiva.
     * @return Los puntos obtenidos.
     */
    public int calcularPuntos(ActividadDeportiva actividad) {
        int puntos = 0;

        // Puntos por distancia recorrida
        puntos += actividad.getDistanciaKm() * 10;

        // Puntos por tiempo
        puntos += actividad.getDuracionMinutos();

        return puntos;
    }

    /**
     * Registra una actividad en una competencia.
     *
     * @param competenciaId El ID de la competencia.
     * @param usuario       El usuario que realizó la actividad.
     * @param actividad     La actividad realizada.
     */
    public void registrarActividadEnCompetencia(String competenciaId, Usuario usuario, ActividadDeportiva actividad) {
        int puntos = calcularPuntos(actividad);

        // Actualizar puntuación en la competencia
        actualizarPuntuacion(competenciaId, usuario, puntos);

        System.out.println(
                "Actividad registrada en la competencia: " + actividad.getTipo() + ". Puntos obtenidos: " + puntos);
    }

    /**
     * Actualiza la puntuación de un usuario en una competencia.
     *
     * @param competenciaId El ID de la competencia.
     * @param usuario       El usuario cuya puntuación se actualizará.
     * @param puntos        Los puntos a añadir.
     */
    public void actualizarPuntuacion(String competenciaId, Usuario usuario, int puntos) {
        try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                Configuracion.DB_PASSWORD)) {

            String query = "UPDATE CompetenciasPuntuaciones SET puntuacion = puntuacion + ? "
                    + "WHERE competenciaId = ? AND usuarioId = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, puntos);
            stmt.setString(2, competenciaId);
            stmt.setString(3, usuario.getId());
            stmt.executeUpdate();

            System.out.println(
                    "Puntuación actualizada: " + puntos + " puntos añadidos para el usuario " + usuario.getNombre());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Marca una competencia como finalizada.
     *
     * @param competenciaId El ID de la competencia.
     */
    public void finalizarCompetencia(String competenciaId) {
        try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                Configuracion.DB_PASSWORD)) {

            String query = "UPDATE CompetenciasParticipacion SET estado = 'FINALIZADA' WHERE competenciaId = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, competenciaId);
            stmt.executeUpdate();

            System.out.println("Competencia " + competenciaId + " marcada como FINALIZADA.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Obtiene la competencia relacionada con un usuario y un tipo de deporte específico.
     *
     * @param usuarioId El ID del usuario.
     * @param tipo      El tipo de deporte.
     * @return El ID de la competencia relacionada o null si no se encuentra.
     */
    public String obtenerCompetenciaRelacionada(String usuarioId, ActividadDeportiva.TipoDeporte tipo) {
        try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                Configuracion.DB_PASSWORD)) {

            String query = "SELECT competenciaId FROM CompetenciasParticipacion cp "
                    + "JOIN Competencias c ON cp.competenciaId = c.id "
                    + "WHERE cp.usuarioId = ? AND cp.estado = 'EN_PROGRESO' AND c.tipoDeporte = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, usuarioId);
            stmt.setString(2, tipo.name());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("competenciaId");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Verifica y actualiza los desafíos completados por un usuario.
     *
     * @param usuario El usuario a verificar y actualizar.
     */
    public void verificarYActualizarDesafios(Usuario usuario) {
        try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                Configuracion.DB_PASSWORD)) {

            String queryDesafios = "SELECT * FROM Desafios WHERE id NOT IN "
                    + "(SELECT desafioId FROM DesafiosCompletados WHERE usuarioId = ?)";
            PreparedStatement stmtDesafios = conn.prepareStatement(queryDesafios);
            stmtDesafios.setString(1, usuario.getId());
            ResultSet rsDesafios = stmtDesafios.executeQuery();

            while (rsDesafios.next()) {
                Desafio desafio = new Desafio(rsDesafios.getString("id"), rsDesafios.getString("nombre"),
                        rsDesafios.getString("descripcion"), rsDesafios.getInt("puntosRecompensa"),
                        rsDesafios.getDate("fechaInicio").toLocalDate(), rsDesafios.getDate("fechaFin").toLocalDate());

                if (desafio.cumpleCondiciones(usuario)) {
                    registrarDesafioCompletado(desafio, usuario);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Verifica y actualiza los logros completados por un usuario.
     *
     * @param usuario El usuario a verificar y actualizar.
     */
    public void verificarYActualizarLogros(Usuario usuario) {
        try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                Configuracion.DB_PASSWORD)) {

            // Consulta los logros que aún no se han completado
            String queryLogros = "SELECT * FROM Logros WHERE id NOT IN "
                    + "(SELECT logroId FROM LogrosCompletados WHERE usuarioId = ?)";
            PreparedStatement stmtLogros = conn.prepareStatement(queryLogros);
            stmtLogros.setString(1, usuario.getId());
            ResultSet rsLogros = stmtLogros.executeQuery();

            // Revisión de cada logro pendiente
            while (rsLogros.next()) {
                Logro logro = new Logro(rsLogros.getString("id"), rsLogros.getString("nombre"),
                        rsLogros.getString("descripcion"), rsLogros.getInt("puntosRecompensa"),
                        Logro.TipoLogro.valueOf(rsLogros.getString("tipo")));

                // Validar las condiciones del logro
                if (logro.cumpleCondiciones(usuario)) {
                    logro.registrarCumplimiento(usuario);

                    // Registrar en la base de datos el logro cumplido
                    String insertQuery = "INSERT INTO LogrosCompletados (usuarioId, logroId, fechaCumplimiento) "
                            + "VALUES (?, ?, ?)";
                    PreparedStatement stmtInsert = conn.prepareStatement(insertQuery);
                    stmtInsert.setString(1, usuario.getId());
                    stmtInsert.setString(2, logro.getId());
                    stmtInsert.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
                    stmtInsert.executeUpdate();

                    System.out.println("Logro completado: " + logro.getNombre());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Registra a un usuario en una competencia.
     *
     * @param competencia La competencia en la que se registrará el usuario.
     * @param usuario     El usuario que se registrará en la competencia.
     */
    public void registrarEnCompetencia(Competencia competencia, Usuario usuario) {
        try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                Configuracion.DB_PASSWORD)) {

            // Verificar el estado de la competencia
            String estadoQuery = "SELECT estado FROM CompetenciasParticipacion WHERE competenciaId = ? AND usuarioId = ?";
            PreparedStatement estadoStmt = conn.prepareStatement(estadoQuery);
            estadoStmt.setString(1, competencia.getId());
            estadoStmt.setString(2, usuario.getId());
            ResultSet rsEstado = estadoStmt.executeQuery();

            if (rsEstado.next()) {
                String estado = rsEstado.getString("estado");
                if ("FINALIZADA".equals(estado)) {
                    JOptionPane.showMessageDialog(null,
                            "La competencia ya ha finalizado. No puedes realizar esta acción.",
                            "Competencia Finalizada", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if ("EN_PROGRESO".equals(estado)) {
                    JOptionPane.showMessageDialog(null, "Ya estás participando en esta competencia.",
                            "Competencia en Progreso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            // Insertar en CompetenciasParticipacion
            String insertParticipacionQuery = "INSERT INTO CompetenciasParticipacion (competenciaId, usuarioId, fechaInicio, fechaFin, estado) "
                    + "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertParticipacionStmt = conn.prepareStatement(insertParticipacionQuery);
            insertParticipacionStmt.setString(1, competencia.getId());
            insertParticipacionStmt.setString(2, usuario.getId());
            insertParticipacionStmt.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            insertParticipacionStmt.setDate(4, java.sql.Date.valueOf(LocalDate.now().plusDays(10))); // Ejemplo
            insertParticipacionStmt.setString(5, "EN_PROGRESO");
            insertParticipacionStmt.executeUpdate();

            // Insertar en CompetenciasPuntuaciones
            String insertPuntuacionQuery = "INSERT INTO CompetenciasPuntuaciones (competenciaId, usuarioId, puntuacion) VALUES (?, ?, ?)";
            PreparedStatement insertPuntuacionStmt = conn.prepareStatement(insertPuntuacionQuery);
            insertPuntuacionStmt.setString(1, competencia.getId());
            insertPuntuacionStmt.setString(2, usuario.getId());
            insertPuntuacionStmt.setInt(3, 0); // Puntuación inicial
            insertPuntuacionStmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Te has registrado en la competencia: " + competencia.getNombre());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al registrarte en la competencia.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Actualiza la puntuación de un usuario en una competencia.
     *
     * @param competenciaId El ID de la competencia.
     * @param usuarioId     El ID del usuario.
     * @param puntos        Los puntos a añadir.
     */
    public void actualizarPuntuacion(String competenciaId, String usuarioId, int puntos) {
        try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                Configuracion.DB_PASSWORD)) {

            String query = "UPDATE CompetenciasPuntuaciones SET puntuacion = puntuacion + ? "
                    + "WHERE competenciaId = ? AND usuarioId = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, puntos);
            stmt.setString(2, competenciaId);
            stmt.setString(3, usuarioId);
            stmt.executeUpdate();

            System.out.println("Puntuación actualizada.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Consulta el ranking de usuarios en una competencia.
     *
     * @param competenciaId El ID de la competencia.
     * @return La lista de usuarios ordenada por puntuación.
     */
    public List<Usuario> consultarRanking(String competenciaId) {
        List<Usuario> ranking = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                Configuracion.DB_PASSWORD)) {

            String query = "SELECT u.id, u.nombre, cp.puntuacion " + "FROM CompetenciasPuntuaciones cp "
                    + "JOIN Usuarios u ON cp.usuarioId = u.id " + "WHERE cp.competenciaId = ? "
                    + "ORDER BY cp.puntuacion DESC";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, competenciaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Usuario usuario = new Usuario(rs.getString("id"), rs.getString("nombre"));
                usuario.setPuntosTotales(rs.getInt("puntuacion"));
                ranking.add(usuario);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return ranking;
    }

    /**
     * Anuncia el ganador de una competencia.
     *
     * @param competenciaId El ID de la competencia.
     * @return El usuario ganador o null si no se encuentra.
     */
    public Usuario anunciarGanador(String competenciaId) {
        Usuario ganador = null;

        try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                Configuracion.DB_PASSWORD)) {

            String query = "SELECT u.id, u.nombre, cp.puntuacion " + "FROM CompetenciasPuntuaciones cp "
                    + "JOIN Usuarios u ON cp.usuarioId = u.id " + "WHERE cp.competenciaId = ? "
                    + "ORDER BY cp.puntuacion DESC LIMIT 1";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, competenciaId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ganador = new Usuario(rs.getString("id"), rs.getString("nombre"));
                ganador.setPuntosTotales(rs.getInt("puntuacion"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (ganador != null) {
            System.out.println("Ganador de la competencia: " + ganador.getNombre() + " con "
                    + ganador.getPuntosTotales() + " puntos.");
        }

        return ganador;
    }

    /**
     * Registra la participación de un usuario en una competencia.
     *
     * @param competencia La competencia en la que se registrará el usuario.
     * @param usuario     El usuario que se registrará en la competencia.
     */
    public void registrarParticipacionCompetencia(Competencia competencia, Usuario usuario) {
        try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                Configuracion.DB_PASSWORD)) {

            String query = "INSERT INTO CompetenciasParticipacion (competenciaId, usuarioId, fechaInicio, fechaFin, estado) "
                    + "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, competencia.getId());
            stmt.setString(2, usuario.getId());
            stmt.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            stmt.setDate(4, java.sql.Date.valueOf(LocalDate.now().plusDays(10))); // Ejemplo
            stmt.setString(5, "REGISTRO"); // Estado inicial
            stmt.executeUpdate();

            System.out.println("Registro en competencia exitoso: " + competencia.getNombre());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Inicializa las competencias en la base de datos.
     */
    public void inicializarCompetencias() {
        try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                Configuracion.DB_PASSWORD)) {

            String query = "SELECT COUNT(*) FROM Competencias";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            if (rs.next() && rs.getInt(1) == 0) {
                // Insertar competencias generales
                String insertQuery = "INSERT INTO Competencias (id, nombre, tipoDeporte) VALUES (?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);

                insertStmt.setString(1, "C001");
                insertStmt.setString(2, "Maratón Escolar");
                insertStmt.setString(3, "CORRER");
                insertStmt.executeUpdate();

                insertStmt.setString(1, "C002");
                insertStmt.setString(2, "Liga de Natación");
                insertStmt.setString(3, "NATACION");
                insertStmt.executeUpdate();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
