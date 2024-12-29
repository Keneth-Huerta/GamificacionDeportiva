package gamificaciondeportiva;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class Graficos extends JFrame {
	private static final long serialVersionUID = 2208912880386379928L;
	private SistemaGamificacion sistema;
	private Usuario usuarioActual;
	private DefaultTableModel modeloTablaLogros, modeloTablaActividades, modeloTablaDesafios, modeloTablaCompetencias;
	private JTabbedPane tabbedPane;
	private JPanel panelPerfil;
	private JPanel panelActividades;
	private JPanel panelLogros;
	private JPanel panelDesafios;
	private JPanel panelCompetencias;
	private JPanel panelRanking;
	private DefaultTableModel modeloTablaRanking;
	private JTable tablaCompetencias; // Declarar como variable de instancia
	private JLabel lblFotoPerfil;

	public Graficos(SistemaGamificacion sistema, Usuario usuarioActual) {
		this.sistema = sistema;
		this.usuarioActual = usuarioActual;

		configurarVentana();

		inicializarComponentes();

		try {
			setIconImage(ImageIO.read(this.getClass().getResource("resources/Cubo-EnfocadoL.png")));
		} catch (IOException e) {
			System.out.println("La imagen no se encuentra");
		}
	}

	private void actualizarDesafiosTabla() {
		modeloTablaDesafios.setRowCount(0); // Limpiar la tabla actual

		try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
				Configuracion.DB_PASSWORD)) {

			String query = "SELECT d.id, d.nombre, d.descripcion, d.estado, dc.fechaCumplimiento " + "FROM Desafios d "
					+ "LEFT JOIN DesafiosCompletados dc ON d.id = dc.desafioId AND dc.usuarioId = ?";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, usuarioActual.getId());
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				String estado = rs.getString("estado");
				if (rs.getDate("fechaCumplimiento") != null) {
					estado = "COMPLETADO";
				}
				modeloTablaDesafios.addRow(new Object[] { rs.getString("id"), rs.getString("nombre"),
						rs.getString("descripcion"), estado });
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void cargarRanking(String competenciaId) {
		modeloTablaRanking.setRowCount(0); // Limpiar la tabla

		List<Usuario> ranking = sistema.consultarRanking(competenciaId);
		int posicion = 1;

		for (Usuario usuario : ranking) {
			modeloTablaRanking.addRow(new Object[] { posicion++, // Posición
					usuario.getNombre(), // Nombre del usuario
					usuario.getPuntosTotales() // Puntos acumulados
			});
		}
	}

	private String obtenerEstadoLogro(String logroId) {
		try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
				Configuracion.DB_PASSWORD)) {

			String query = "SELECT COUNT(*) FROM LogrosCompletados WHERE usuarioId = ? AND logroId = ?";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, usuarioActual.getId());
			stmt.setString(2, logroId);
			ResultSet rs = stmt.executeQuery();

			if (rs.next() && rs.getInt(1) > 0) {
				return "Completado";
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "Pendiente"; // Estado predeterminado si no está completado
	}

	private int calcularProgresoLogro(String logroId, int objetivo) {
		if (objetivo == 0) {
			return 100; // Si el logro no tiene un objetivo definido, se considera completado
						// automáticamente
		}

		try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
				Configuracion.DB_PASSWORD)) {

			// Verificar si el logro ya está completado
			String queryCompletado = "SELECT COUNT(*) AS completado FROM LogrosCompletados WHERE usuarioId = ? AND logroId = ?";
			PreparedStatement stmtCompletado = conn.prepareStatement(queryCompletado);
			stmtCompletado.setString(1, usuarioActual.getId());
			stmtCompletado.setString(2, logroId);
			ResultSet rsCompletado = stmtCompletado.executeQuery();

			if (rsCompletado.next() && rsCompletado.getInt("completado") > 0) {
				return 100; // Si el logro está completado, devuelve 100%
			}

			// Calcular el progreso actual según actividades relacionadas con el logro
			String queryProgreso = "SELECT SUM(duracionMinutos) AS progresoActual FROM Actividades WHERE usuarioId = ?";
			PreparedStatement stmtProgreso = conn.prepareStatement(queryProgreso);
			stmtProgreso.setString(1, usuarioActual.getId());
			ResultSet rsProgreso = stmtProgreso.executeQuery();

			if (rsProgreso.next()) {
				int progresoActual = rsProgreso.getInt("progresoActual");
				return Math.min(100, (progresoActual * 100) / objetivo); // Calcular el porcentaje basado en el objetivo
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return 0; // En caso de error o sin datos, retorna 0% de progreso
	}

	private void configurarVentana() {
		setTitle("Gamificación Deportiva");
		setSize(1000, 700); // Ventana más grande para mejor visualización
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout(10, 10));
		getContentPane().setBackground(new Color(245, 245, 250)); // Fondo suave
	}

	private void inicializarComponentes() {
		tabbedPane = new JTabbedPane();
		estilizarTabbedPane(tabbedPane);

		inicializarPanelPerfil();
		inicializarPanelActividades();
		inicializarPanelLogros();
		inicializarPanelDesafios();
		inicializarPanelCompetencias();
		inicializarPanelRanking();

		tabbedPane.addTab("Perfil", null, panelPerfil, "Perfil de usuario");
		tabbedPane.addTab("Actividades", null, panelActividades, "Registro de actividades");
		tabbedPane.addTab("Logros", null, panelLogros, "Logros disponibles");
		tabbedPane.addTab("Desafíos", null, panelDesafios, "Desafíos activos");
		tabbedPane.addTab("Competencias", null, panelCompetencias, "Competencias disponibles");
		tabbedPane.addTab("Ranking", null, panelRanking, "Ranking de competencias");

		add(tabbedPane, BorderLayout.CENTER);
	}

	private void estilizarTabbedPane(JTabbedPane tabbedPane) {
		tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
		tabbedPane.setForeground(new Color(50, 50, 50)); // Color del texto de las pestañas
		tabbedPane.setBackground(new Color(245, 245, 250)); // Fondo de las pestañas inactivas

		// Cambiar el color de las pestañas seleccionadas y no seleccionadas
		UIManager.put("TabbedPane.selected", new Color(70, 130, 180)); // Fondo de pestaña activa
		UIManager.put("TabbedPane.unselectedBackground", new Color(230, 230, 230)); // Fondo inactivo

		// Espaciado interno para las pestañas
		tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}

	private void cerrarSesion() {
		File sesionFile = new File("sesion.txt");
		if (sesionFile.exists()) {
			sesionFile.delete();
		}
		Configuracion.setUsuarioActual(null);
		new PantallaInicio().setVisible(true);
		this.dispose();
	}

	private void inicializarPanelDesafios() {
		panelDesafios = new JPanel(new BorderLayout(10, 10));
		panelDesafios.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panelDesafios.setBackground(new Color(245, 245, 250));

		String[] columnas = { "Nombre", "Descripción", "Estado" };
		modeloTablaDesafios = new DefaultTableModel(columnas, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // Hace las celdas no editables
			}
		};

		JTable tablaDesafios = new JTable(modeloTablaDesafios);
		habilitarToolTipsTabla(tablaDesafios);
		tablaDesafios.setRowHeight(25);
		tablaDesafios.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		tablaDesafios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
		tablaDesafios.getTableHeader().setBackground(new Color(70, 130, 180));
		tablaDesafios.getTableHeader().setForeground(Color.WHITE);
		tablaDesafios.setSelectionBackground(new Color(135, 206, 250));
		tablaDesafios.setSelectionForeground(Color.BLACK);

		JScrollPane scrollTabla = new JScrollPane(tablaDesafios);
		scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

		cargarDesafiosDesdeBaseDeDatos();
		panelDesafios.add(scrollTabla, BorderLayout.CENTER);
	}

	private void cargarDesafiosDesdeBaseDeDatos() {
		try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
				Configuracion.DB_PASSWORD)) {

			String query = "SELECT d.*, "
					+ "(SELECT COUNT(*) FROM DesafiosCompletados WHERE usuarioId = ? AND desafioId = d.id) AS completado "
					+ "FROM Desafios d";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, usuarioActual.getId());
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				modeloTablaDesafios.addRow(new Object[] { rs.getString("nombre"), rs.getString("descripcion"),
						rs.getInt("completado") > 0 ? "Completado" : "Pendiente" });
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private ImageIcon cargarImagenPredeterminada() {
		try {
			BufferedImage defaultImage = ImageIO.read(getClass().getResource("resources/default-profile.png"));
			defaultImage = escalarImagen(defaultImage, 150, 150); // Escalar antes de convertir
			return hacerImagenCircular(defaultImage, 150); // Convertir en circular
		} catch (Exception ex) {
			System.err.println("Error al cargar la imagen predeterminada: " + ex.getMessage());
			ex.printStackTrace();
			// Retorna un ícono vacío si ocurre un error.
			BufferedImage emptyImage = new BufferedImage(150, 150, BufferedImage.TYPE_INT_ARGB);
			return new ImageIcon(emptyImage);
		}
	}

	private BufferedImage escalarImagen(BufferedImage originalImage, int width, int height) {
		Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage scaledBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = scaledBufferedImage.createGraphics();
		g2d.drawImage(scaledImage, 0, 0, null);
		g2d.dispose();

		return scaledBufferedImage;
	}

	public ImageIcon hacerImagenCircular(BufferedImage originalImage, int diameter) {
		BufferedImage circularImage = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = circularImage.createGraphics();

		g2.setClip(new Ellipse2D.Float(0, 0, diameter, diameter));
		g2.drawImage(originalImage, 0, 0, diameter, diameter, null);
		g2.dispose();

		return new ImageIcon(circularImage);
	}

	private void inicializarPanelPerfil() {
		panelPerfil = new JPanel();
		panelPerfil.setLayout(new BoxLayout(panelPerfil, BoxLayout.Y_AXIS));
		panelPerfil.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		panelPerfil.setBackground(new Color(245, 245, 250)); // Fondo claro

		// Inicializar lblFotoPerfil
		lblFotoPerfil = new JLabel("", SwingConstants.CENTER);
		lblFotoPerfil.setHorizontalAlignment(SwingConstants.CENTER);

		// Sección de Información General
		JPanel panelInfoGeneral = new JPanel(new BorderLayout(10, 10));
		panelInfoGeneral.setBackground(Color.WHITE);
		panelInfoGeneral
				.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Información General"),
						BorderFactory.createEmptyBorder(10, 10, 10, 10)));

		JLabel lblNombre = new JLabel("Nombre: " + usuarioActual.getNombre());
		JLabel lblNivel = new JLabel("Nivel: " + usuarioActual.getNivel());

		try {
			if (usuarioActual.getFotoPerfil() != null) {
				ImageIcon iconoCircular = hacerImagenCircular(usuarioActual.getFotoPerfil(), 150);
				lblFotoPerfil.setIcon(iconoCircular);
			} else {
				lblFotoPerfil.setIcon(cargarImagenPredeterminada());
			}
		} catch (Exception ex) {
			lblFotoPerfil.setText("Error al cargar foto");
			ex.printStackTrace();
		}

		estilizarEtiquetaPerfil(lblNombre);
		estilizarEtiquetaPerfil(lblNivel);

		panelInfoGeneral.add(lblFotoPerfil, BorderLayout.NORTH);
		panelInfoGeneral.add(lblNombre, BorderLayout.CENTER);
		panelInfoGeneral.add(lblNivel, BorderLayout.SOUTH);

		// Sección de Progreso
		JPanel panelProgreso = new JPanel();
		panelProgreso.setLayout(new BoxLayout(panelProgreso, BoxLayout.Y_AXIS));
		panelProgreso.setBackground(Color.WHITE);
		panelProgreso.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Progreso"),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));

		JLabel lblPuntos = new JLabel("Puntos Totales: " + usuarioActual.getPuntosTotales());
		estilizarEtiquetaPerfil(lblPuntos);

		JLabel lblExperiencia = new JLabel("Experiencia: " + usuarioActual.getExperiencia());
		estilizarEtiquetaPerfil(lblExperiencia);

		JProgressBar barraExperiencia = new JProgressBar();
		barraExperiencia.setMaximum(1000); // Máximo fijo
		barraExperiencia.setValue(usuarioActual.getExperiencia() % 1000);
		barraExperiencia.setStringPainted(true);
		barraExperiencia.setBackground(new Color(230, 230, 230));
		barraExperiencia.setForeground(new Color(70, 130, 180));

		panelProgreso.add(lblPuntos);
		panelProgreso.add(lblExperiencia);
		panelProgreso.add(Box.createRigidArea(new Dimension(0, 10)));
		panelProgreso.add(barraExperiencia);

		// Sección de Botones de Acción
		JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		panelBotones.setOpaque(false);

		JButton btnEditarPerfil = crearBoton("Editar Perfil", e -> editarPerfil());
		JButton btnCerrarSesion = crearBoton("Cerrar Sesión", e -> cerrarSesion());

		panelBotones.add(btnEditarPerfil);
		panelBotones.add(btnCerrarSesion);

		// Añadir las secciones al panel principal
		panelPerfil.add(panelInfoGeneral);
		panelPerfil.add(Box.createRigidArea(new Dimension(0, 20)));
		panelPerfil.add(panelProgreso);
		panelPerfil.add(Box.createRigidArea(new Dimension(0, 20)));
		panelPerfil.add(panelBotones);
	}

	private void estilizarEtiquetaPerfil(JLabel etiqueta) {
		etiqueta.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		etiqueta.setForeground(new Color(50, 50, 50)); // Gris oscuro
	}

	private JButton crearBoton(String texto, ActionListener accion) {
		JButton boton = new JButton(texto);
		boton.setBackground(new Color(70, 130, 180));
		boton.setForeground(Color.WHITE);
		boton.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // Texto más pequeño
		boton.setFocusPainted(false);
		boton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Bordes más pequeños
		boton.setPreferredSize(new Dimension(120, 30)); // Tamaño fijo para botones pequeños
		boton.addActionListener(accion);
		return boton;
	}

	private ImageIcon hacerImagenCircular(byte[] imagenBytes, int diameter) {
		try {
			BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imagenBytes));
			BufferedImage circularImage = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);

			Graphics2D g2 = circularImage.createGraphics();
			g2.setClip(new Ellipse2D.Float(0, 0, diameter, diameter));
			g2.drawImage(originalImage, 0, 0, diameter, diameter, null);
			g2.dispose();

			return new ImageIcon(circularImage);
		} catch (Exception e) {
			e.printStackTrace();
			return null; // Retorna null si ocurre un error
		}
	}

	private void editarPerfil() {
		FormularioEditarPerfil formulario = new FormularioEditarPerfil(this, usuarioActual, sistema);
		formulario.setVisible(true); // Abre el formulario de edición

		// Refrescar datos desde la base de datos
		try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
				Configuracion.DB_PASSWORD)) {
			String query = "SELECT nombre, fotoPerfil FROM Usuarios WHERE id = ?";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, usuarioActual.getId());
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				usuarioActual.setNombre(rs.getString("nombre"));
				usuarioActual.setFotoPerfil(rs.getBytes("fotoPerfil"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		actualizarFotoPerfil(); // Actualizar visualmente la foto de perfil
	}

	private void actualizarBarraProgresoLogros(JProgressBar barraProgreso) {
		int totalLogros = sistema.getLogrosDisponibles().size();
		long logrosCompletados = sistema.getLogrosDisponibles().stream().filter(Logro::isCompletado).count();

		if (totalLogros > 0) {
			int progreso = (int) ((logrosCompletados * 100) / totalLogros);
			barraProgreso.setValue(progreso);
			barraProgreso.setString(progreso + "% Completado");
		} else {
			barraProgreso.setValue(0);
			barraProgreso.setString("0% Completado");
		}
	}

	private void actualizarFotoPerfil() {
		try {
			// Obtener la imagen desde la base de datos
			ImageIcon nuevaFoto = sistema.obtenerFotoDesdeBaseDeDatos(usuarioActual.getId());
			if (nuevaFoto != null) {
				lblFotoPerfil.setIcon(hacerImagenCircular(nuevaFoto, 150));
				lblFotoPerfil.setText(""); // Eliminar el texto "Sin Foto"
			} else {
				lblFotoPerfil.setIcon(null); // Limpia cualquier imagen previa
				lblFotoPerfil.setText("Sin Foto"); // Establece texto si no hay foto
			}
		} catch (Exception ex) {
			lblFotoPerfil.setIcon(null); // Limpia cualquier imagen previa
			lblFotoPerfil.setText("Error al cargar foto"); // Manejo de errores
			ex.printStackTrace();
		}
	}

	private ImageIcon hacerImagenCircular(ImageIcon icono, int diameter) {
		BufferedImage originalImage = new BufferedImage(icono.getIconWidth(), icono.getIconHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = originalImage.createGraphics();
		icono.paintIcon(null, g, 0, 0);
		g.dispose();

		BufferedImage circularImage = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = circularImage.createGraphics();
		g2.setClip(new Ellipse2D.Float(0, 0, diameter, diameter));
		g2.drawImage(originalImage, 0, 0, diameter, diameter, null);
		g2.dispose();

		return new ImageIcon(circularImage);
	}

	public ImageIcon obtenerFotoDesdeBaseDeDatos(String usuarioId) {
		try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
				Configuracion.DB_PASSWORD)) {

			String query = "SELECT fotoPerfil FROM Usuarios WHERE id = ?";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, usuarioId);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				byte[] fotoBytes = rs.getBytes("fotoPerfil");
				if (fotoBytes != null && fotoBytes.length > 0) {
					BufferedImage img = ImageIO.read(new ByteArrayInputStream(fotoBytes));
					return new ImageIcon(img);
				} else {
					System.out.println("El usuario no tiene foto de perfil almacenada.");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null; // Si no hay imagen o ocurre un error
	}

	private void habilitarToolTipsTabla(JTable tabla) {
		tabla.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			@Override
			public void mouseMoved(java.awt.event.MouseEvent e) {
				java.awt.Point p = e.getPoint();
				int row = tabla.rowAtPoint(p);
				int column = tabla.columnAtPoint(p);
				if (row != -1 && column != -1) {
					Object value = tabla.getValueAt(row, column);
					if (value != null) {
						tabla.setToolTipText(value.toString());
					} else {
						tabla.setToolTipText(null);
					}
				}
			}
		});
	}

	private void inicializarPanelRanking() {
		panelRanking = new JPanel(new BorderLayout(10, 10));
		panelRanking.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panelRanking.setBackground(new Color(245, 245, 250));

		String[] columnasRanking = { "Posición", "Nombre", "Puntuación" };
		modeloTablaRanking = new DefaultTableModel(columnasRanking, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // Hace las celdas no editables
			}
		};

		JTable tablaRanking = new JTable(modeloTablaRanking);
		tablaRanking.setRowHeight(25);
		tablaRanking.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		tablaRanking.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
		tablaRanking.getTableHeader().setBackground(new Color(70, 130, 180));
		tablaRanking.getTableHeader().setForeground(Color.WHITE);
		tablaRanking.setSelectionBackground(new Color(135, 206, 250));
		tablaRanking.setSelectionForeground(Color.BLACK);

		JScrollPane scrollRanking = new JScrollPane(tablaRanking);
		scrollRanking.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

		JButton btnActualizarRanking = new JButton("Actualizar Ranking");
		btnActualizarRanking.setBackground(new Color(70, 130, 180));
		btnActualizarRanking.setForeground(Color.WHITE);
		btnActualizarRanking.setFocusPainted(false);
		btnActualizarRanking.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		btnActualizarRanking.addActionListener(e -> {
			int row = tablaCompetencias.getSelectedRow();
			if (row != -1) {
				String competenciaId = (String) modeloTablaCompetencias.getValueAt(row, 0);
				cargarRanking(competenciaId);
			} else {
				JOptionPane.showMessageDialog(this, "Selecciona una competencia para ver su ranking.", "Advertencia",
						JOptionPane.WARNING_MESSAGE);
			}
		});

		panelRanking.add(scrollRanking, BorderLayout.CENTER);
		panelRanking.add(btnActualizarRanking, BorderLayout.SOUTH);
	}

	private void inicializarPanelCompetencias() {
		panelCompetencias = new JPanel(new BorderLayout(10, 10));
		panelCompetencias.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panelCompetencias.setBackground(new Color(245, 245, 250));

		String[] columnas = { "ID (Oculto)", "Nombre", "Deporte", "Estado" };
		modeloTablaCompetencias = new DefaultTableModel(columnas, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // Hace las celdas no editables
			}
		};
		tablaCompetencias = new JTable(modeloTablaCompetencias);
		habilitarToolTipsTabla(tablaCompetencias);
		tablaCompetencias.setRowHeight(25);
		tablaCompetencias.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		tablaCompetencias.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
		tablaCompetencias.getTableHeader().setBackground(new Color(70, 130, 180));
		tablaCompetencias.getTableHeader().setForeground(Color.WHITE);
		tablaCompetencias.setSelectionBackground(new Color(135, 206, 250));
		tablaCompetencias.setSelectionForeground(Color.BLACK);
		tablaCompetencias.getColumnModel().getColumn(0).setMinWidth(0); // Ocultar columna ID
		tablaCompetencias.getColumnModel().getColumn(0).setMaxWidth(0);

		JScrollPane scrollTabla = new JScrollPane(tablaCompetencias);
		scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

		// Llama a cargar las competencias desde la base de datos
		cargarCompetenciasDesdeBaseDeDatos();

		panelCompetencias.add(scrollTabla, BorderLayout.CENTER);

		// Crear un panel para los botones
		JPanel panelBotones = new JPanel(new GridLayout(1, 2, 10, 10)); // Distribución horizontal para los botones

		// Botón para registrarse en competencias
		JButton btnRegistrarse = new JButton("Registrarse en Competencia");
		btnRegistrarse.setBackground(new Color(70, 130, 180));
		btnRegistrarse.setForeground(Color.WHITE);
		btnRegistrarse.setFocusPainted(false);
		btnRegistrarse.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		btnRegistrarse.addActionListener(e -> {
			int row = tablaCompetencias.getSelectedRow();
			if (row != -1) {
				String competenciaId = (String) modeloTablaCompetencias.getValueAt(row, 0);

				Competencia competencia = sistema.getCompetencias().stream()
						.filter(c -> c.getId().equals(competenciaId)).findFirst().orElse(null);

				if (competencia != null) {
					String estadoActual = sistema.obtenerEstadoCompetencia(competenciaId, usuarioActual.getId());
					if ("FINALIZADA".equalsIgnoreCase(estadoActual)) {
						JOptionPane.showMessageDialog(this, "La competencia ya ha finalizado.", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					sistema.registrarEnCompetencia(competencia, usuarioActual);

					// Refrescar la tabla solo si se actualiza correctamente
					cargarCompetenciasDesdeBaseDeDatos();
				} else {
					JOptionPane.showMessageDialog(this, "No se encontró la competencia seleccionada.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(this, "Selecciona una competencia para registrarte.", "Advertencia",
						JOptionPane.WARNING_MESSAGE);
			}
		});

		// Botón para anunciar ganador
		JButton btnAnunciarGanador = new JButton("Anunciar Ganador");
		btnAnunciarGanador.setBackground(new Color(70, 130, 180));
		btnAnunciarGanador.setForeground(Color.WHITE);
		btnAnunciarGanador.setFocusPainted(false);
		btnAnunciarGanador.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		btnAnunciarGanador.addActionListener(e -> {
			int row = tablaCompetencias.getSelectedRow();
			if (row != -1) {
				String competenciaId = (String) modeloTablaCompetencias.getValueAt(row, 0);
				Usuario ganador = sistema.anunciarGanador(competenciaId);

				if (ganador != null) {
					JOptionPane.showMessageDialog(this,
							"El ganador de la competencia es: " + ganador.getNombre() + " con "
									+ ganador.getPuntosTotales() + " puntos.",
							"Ganador", JOptionPane.INFORMATION_MESSAGE);

					// Marcar la competencia como finalizada
					sistema.finalizarCompetencia(competenciaId);

					// Actualizar el estado en la tabla
					modeloTablaCompetencias.setValueAt("Finalizada", row, 3);
				} else {
					JOptionPane.showMessageDialog(this, "No se encontró un ganador para esta competencia.",
							"Sin ganador", JOptionPane.WARNING_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(this, "Selecciona una competencia para anunciar al ganador.",
						"Advertencia", JOptionPane.WARNING_MESSAGE);
			}
		});

		// Añadir botones al panel
		panelBotones.add(btnRegistrarse);
		panelBotones.add(btnAnunciarGanador);

		// Añadir el panel de botones al sur
		panelCompetencias.add(panelBotones, BorderLayout.SOUTH);
	}

	private void cargarCompetenciasDesdeBaseDeDatos() {
		try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
				Configuracion.DB_PASSWORD)) {

			String query = "SELECT c.id, c.nombre, c.tipoDeporte, cp.estado " + "FROM Competencias c "
					+ "LEFT JOIN CompetenciasParticipacion cp ON c.id = cp.competenciaId AND cp.usuarioId = ?";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, usuarioActual.getId());
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				String tipoDeporte = rs.getString("tipoDeporte");
				String estado = rs.getString("estado");

				// Transformar valores a formato amigable
				tipoDeporte = transformarTipoDeporte(tipoDeporte);
				estado = transformarEstado(estado);

				modeloTablaCompetencias.addRow(new Object[] { rs.getString("id"), // ID de la competencia
						rs.getString("nombre"), // Nombre de la competencia
						tipoDeporte, // Tipo de deporte transformado
						estado != null ? estado : "No registrado" // Estado transformado
				});
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// Método para transformar el tipo de deporte
	private String transformarTipoDeporte(String tipoDeporte) {
		switch (tipoDeporte) {
			case "CORRER":
				return "Correr";
			case "NATACION":
				return "Natación";
			case "CICLISMO":
				return "Ciclismo";
			case "FUTBOL":
				return "Fútbol";
			case "BALONCESTO":
				return "Baloncesto";
			case "VOLLEYBALL":
				return "Voleibol";
			case "GIMNASIO":
				return "Gimnasio";
			default:
				return tipoDeporte;
		}
	}

	// Método para transformar el estado
	private String transformarEstado(String estado) {
		if (estado == null) {
			return "No registrado"; // Valor por defecto para estados nulos
		}

		switch (estado) {
			case "EN_PROGRESO":
				return "En progreso";
			case "REGISTRO":
				return "Registro";
			case "FINALIZADA":
				return "Finalizada";
			default:
				return estado; // Retorna el estado original si no coincide con los valores esperados
		}
	}

	private void inicializarPanelLogros() {
		panelLogros = new JPanel(new BorderLayout(10, 10));
		panelLogros.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panelLogros.setBackground(new Color(245, 245, 250));

		String[] columnas = { "Nombre", "Descripción", "Puntos", "Estado", "Progreso" };
		modeloTablaLogros = new DefaultTableModel(columnas, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // Hace las celdas no editables
			}
		};

		JTable tablaLogros = new JTable(modeloTablaLogros) {
			private static final long serialVersionUID = 1L;

			@Override
			public String getToolTipText(java.awt.event.MouseEvent e) {
				java.awt.Point p = e.getPoint();
				int row = rowAtPoint(p);
				int column = columnAtPoint(p);
				if (row != -1 && column != -1) {
					Object value = getValueAt(row, column);
					return value != null ? value.toString() : null;
				}
				return super.getToolTipText(e);
			}
		};
		habilitarToolTipsTabla(tablaLogros);
		tablaLogros.setRowHeight(30);

		// Configuración de renderizador para la columna de progreso
		tablaLogros.getColumnModel().getColumn(4).setCellRenderer(new TableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				if (value instanceof JProgressBar) {
					JProgressBar progressBar = (JProgressBar) value;
					progressBar.setStringPainted(true); // Mostrar el texto del progreso
					progressBar.setBackground(new Color(230, 230, 230));
					progressBar.setForeground(new Color(70, 130, 180));
					return progressBar;
				}
				return null;
			}
		});

		tablaLogros.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
		tablaLogros.getTableHeader().setBackground(new Color(70, 130, 180));
		tablaLogros.getTableHeader().setForeground(Color.WHITE);
		tablaLogros.setSelectionBackground(new Color(135, 206, 250));
		tablaLogros.setSelectionForeground(Color.BLACK);

		JScrollPane scrollTabla = new JScrollPane(tablaLogros);
		scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

		actualizarLogrosTabla();

		JButton btnVerificarLogros = new JButton("Verificar Logros");
		btnVerificarLogros.setBackground(new Color(70, 130, 180));
		btnVerificarLogros.setForeground(Color.WHITE);
		btnVerificarLogros.setFocusPainted(false);
		btnVerificarLogros.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		btnVerificarLogros.addActionListener(e -> {
			PantallaCarga pantallaCarga = new PantallaCarga(this, "Verificando logros, por favor espere...");
			SwingWorker<Void, Void> worker = new SwingWorker<>() {
				@Override
				protected Void doInBackground() throws Exception {
					sistema.verificarYActualizarLogros(usuarioActual);
					return null;
				}

				@Override
				protected void done() {
					pantallaCarga.ocultar();
					actualizarLogrosTabla();
					JOptionPane.showMessageDialog(panelLogros, "Logros actualizados correctamente.");
				}
			};

			worker.execute();
			pantallaCarga.mostrar();
		});

		panelLogros.add(scrollTabla, BorderLayout.CENTER);
		panelLogros.add(btnVerificarLogros, BorderLayout.SOUTH);
	}

	private void actualizarLogrosTabla() {
		modeloTablaLogros.setRowCount(0); // Limpiar la tabla antes de llenarla

		try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
				Configuracion.DB_PASSWORD)) {

			String query = "SELECT * FROM Logros";
			PreparedStatement stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				String nombre = rs.getString("nombre");
				String descripcion = rs.getString("descripcion");
				int puntosRecompensa = rs.getInt("puntosRecompensa");
				String logroId = rs.getString("id");
				int objetivo = rs.getInt("objetivo");
				String estado = obtenerEstadoLogro(logroId);
				int progreso = calcularProgresoLogro(logroId, objetivo);

				// Crear barra de progreso para la columna
				JProgressBar barraProgreso = new JProgressBar(0, 100);
				barraProgreso.setValue(progreso);
				barraProgreso.setStringPainted(true);
				barraProgreso.setString(progreso + "%");

				modeloTablaLogros.addRow(new Object[] { nombre, descripcion, puntosRecompensa, estado, barraProgreso });
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void inicializarPanelActividades() {
		panelActividades = new JPanel(new BorderLayout(15, 15));
		panelActividades.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		panelActividades.setBackground(Color.WHITE);

		// Panel de formulario mejorado
		JPanel formulario = new JPanel(new GridBagLayout());
		formulario.setBackground(Color.WHITE);
		formulario
				.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)),
						BorderFactory.createEmptyBorder(15, 15, 15, 15)));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);

		// Componentes del formulario
		JComboBox<ActividadDeportiva.TipoDeporte> comboDeporte = new JComboBox<>(
				ActividadDeportiva.TipoDeporte.values());
		JTextField txtDuracion = new JTextField(15);
		JTextField txtDistancia = new JTextField(15);
		JCheckBox chkEsCompetencia = new JCheckBox("¿Es una competencia?");

		// Estilo para los componentes
		comboDeporte.setBackground(Color.WHITE);
		txtDuracion.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(200, 200, 200)), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		txtDistancia.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(200, 200, 200)), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		// Añadir componentes con GridBagLayout
		gbc.gridx = 0;
		gbc.gridy = 0;
		formulario.add(new JLabel("Tipo de Deporte:"), gbc);
		gbc.gridx = 1;
		formulario.add(comboDeporte, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		formulario.add(new JLabel("Duración (minutos):"), gbc);
		gbc.gridx = 1;
		formulario.add(txtDuracion, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		formulario.add(new JLabel("Distancia (km):"), gbc);
		gbc.gridx = 1;
		formulario.add(txtDistancia, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		formulario.add(chkEsCompetencia, gbc);

		// Tabla mejorada
		String[] columnas = { "Fecha", "Deporte", "Duración", "Distancia", "Calorías" };
		modeloTablaActividades = new DefaultTableModel(columnas, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		JTable tablaActividades = new JTable(modeloTablaActividades);
		habilitarToolTipsTabla(tablaActividades);
		tablaActividades.setRowHeight(25);
		tablaActividades.getTableHeader().setBackground(new Color(70, 130, 180));
		tablaActividades.getTableHeader().setForeground(Color.WHITE);
		tablaActividades.setSelectionBackground(new Color(135, 206, 250));

		JScrollPane scrollTabla = new JScrollPane(tablaActividades);
		scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

		// Botón de registro mejorado
		JButton btnRegistrar = new JButton("Registrar Actividad");
		btnRegistrar.setBackground(new Color(70, 130, 180));
		btnRegistrar.setForeground(Color.WHITE);
		btnRegistrar.setFocusPainted(false);
		btnRegistrar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

		cargarActividadesDesdeBaseDeDatos(modeloTablaActividades);

		btnRegistrar.addActionListener(e -> {
			PantallaCarga pantallaCarga = new PantallaCarga(this, "Registrando actividad, por favor espere...");

			SwingWorker<Void, Void> worker = new SwingWorker<>() {
				@Override
				protected Void doInBackground() throws Exception {
					try {
						ActividadDeportiva.TipoDeporte tipo = (ActividadDeportiva.TipoDeporte) comboDeporte
								.getSelectedItem();
						int duracion = Integer.parseInt(txtDuracion.getText());
						double distancia = Double.parseDouble(txtDistancia.getText());
						boolean esCompetencia = chkEsCompetencia.isSelected();

						ActividadDeportiva actividad = new ActividadDeportiva("A" + System.currentTimeMillis(), tipo,
								duracion, distancia, usuarioActual, esCompetencia);

						if (esCompetencia) {
							String competenciaId = sistema.obtenerCompetenciaRelacionada(usuarioActual.getId(), tipo);
							if (competenciaId != null) {
								sistema.registrarActividadEnCompetencia(competenciaId, usuarioActual, actividad);
								cargarRanking(competenciaId);
							} else {
								JOptionPane.showMessageDialog(null,
										"No estás registrado en una competencia activa para este deporte.",
										"Advertencia", JOptionPane.WARNING_MESSAGE);
								return null;
							}
						} else {
							sistema.registrarActividad(actividad);
							sistema.verificarYActualizarLogros(usuarioActual);
							sistema.verificarYActualizarDesafios(usuarioActual);
						}

						modeloTablaActividades.addRow(
								new Object[] { actividad.getFecha().toString() + " " + actividad.getHora().toString(),
										tipo, duracion + " min", distancia + " km", actividad.getCaloriasQuemadas() });

						txtDuracion.setText("");
						txtDistancia.setText("");
						chkEsCompetencia.setSelected(false);

						actualizarLogrosTabla();
						actualizarDesafiosTabla();

						JOptionPane.showMessageDialog(null, "Actividad registrada con éxito!");
					} catch (NumberFormatException ex) {
						JOptionPane.showMessageDialog(null, "Por favor, ingrese valores numéricos válidos.", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
					return null;
				}

				@Override
				protected void done() {
					pantallaCarga.dispose();
				}
			};

			worker.execute();
			pantallaCarga.setVisible(true);
		});

		// Organizar componentes
		panelActividades.add(formulario, BorderLayout.NORTH);
		panelActividades.add(scrollTabla, BorderLayout.CENTER);
		panelActividades.add(btnRegistrar, BorderLayout.SOUTH);
	}

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

	private void cargarActividadesDesdeBaseDeDatos(DefaultTableModel modeloTabla) {
		try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
				Configuracion.DB_PASSWORD)) {

			String query = "SELECT * FROM Actividades WHERE usuarioId = ?";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, usuarioActual.getId());
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				modeloTabla.addRow(new Object[] { rs.getDate("fecha").toString() + " " + rs.getTime("hora").toString(),
						rs.getString("tipo"), rs.getInt("duracionMinutos") + " min",
						rs.getDouble("distanciaKm") + " km", rs.getInt("caloriasQuemadas") });
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
