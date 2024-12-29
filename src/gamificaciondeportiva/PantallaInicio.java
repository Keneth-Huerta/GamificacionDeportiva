package gamificaciondeportiva;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class PantallaInicio extends JFrame {
	private static final long serialVersionUID = -6409051712070186098L;
	private JTextField txtUsuario;
	private JPasswordField txtContrasena;

	public PantallaInicio() {
		setTitle("Gamificación Deportiva");
		setSize(500, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		try {
			setIconImage(ImageIO.read(this.getClass().getResource("resources/Cubo-EnfocadoL.png")));
		} catch (IOException e) {
			System.out.println("La image no se encuentra");
		}
		mostrarInicioSesion();
	}

	private void cambiarPanel(JPanel nuevoPanel) {
		setContentPane(nuevoPanel);
		pack();
		setLocationRelativeTo(null); // Mantener ventana centrada
	}

	private void mostrarInicioSesion() {
		JPanel panelPrincipal = new JPanel();
		panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
		panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		panelPrincipal.setBackground(new Color(245, 245, 250)); // Fondo claro

		JLabel lblTitulo = new JLabel("Bienvenido a Gamificación Deportiva");
		lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblTitulo.setForeground(new Color(70, 130, 180)); // Azul para el título

		JPanel panelFormulario = new JPanel(new GridLayout(2, 2, 10, 10));
		panelFormulario.setBackground(Color.WHITE);
		panelFormulario
				.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)),
						BorderFactory.createEmptyBorder(15, 15, 15, 15)));

		txtUsuario = new JTextField();
		txtContrasena = new JPasswordField();

		estilizarCampoTexto(txtUsuario);
		estilizarCampoTexto(txtContrasena);

		JLabel lblUsuario = new JLabel("Usuario:");
		JLabel lblContrasena = new JLabel("Contraseña:");

		estilizarEtiqueta(lblUsuario);
		estilizarEtiqueta(lblContrasena);

		panelFormulario.add(lblUsuario);
		panelFormulario.add(txtUsuario);
		panelFormulario.add(lblContrasena);
		panelFormulario.add(txtContrasena);

		JButton btnIniciarSesion = crearBoton("Iniciar Sesión", e -> iniciarSesion());
		JButton btnCrearCuenta = crearBoton("Crear Cuenta", e -> mostrarCrearCuenta());

		JPanel panelBotones = new JPanel(new GridLayout(1, 2, 10, 10));
		panelBotones.setOpaque(false);
		panelBotones.add(btnIniciarSesion);
		panelBotones.add(btnCrearCuenta);

		panelPrincipal.add(lblTitulo);
		panelPrincipal.add(Box.createRigidArea(new Dimension(0, 20)));
		panelPrincipal.add(panelFormulario);
		panelPrincipal.add(Box.createRigidArea(new Dimension(0, 10)));
		panelPrincipal.add(panelBotones);

		cambiarPanel(panelPrincipal);
	}

	private void mostrarCrearCuenta() {
		JPanel panelCrearCuenta = new JPanel();
		panelCrearCuenta.setLayout(new BoxLayout(panelCrearCuenta, BoxLayout.Y_AXIS));
		panelCrearCuenta.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		panelCrearCuenta.setBackground(new Color(245, 245, 250));

		JLabel lblTitulo = new JLabel("Crear Nueva Cuenta");
		lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblTitulo.setForeground(new Color(70, 130, 180));

		JPanel panelFormulario = new JPanel(new GridLayout(3, 2, 10, 10));
		panelFormulario.setBackground(Color.WHITE);
		panelFormulario
				.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)),
						BorderFactory.createEmptyBorder(15, 15, 15, 15)));

		JTextField txtNuevoUsuario = new JTextField();
		JPasswordField txtNuevaContrasena = new JPasswordField();
		JTextField txtNombre = new JTextField();

		panelFormulario.add(new JLabel("Nombre de usuario:"));
		panelFormulario.add(txtNuevoUsuario);
		panelFormulario.add(new JLabel("Contraseña:"));
		panelFormulario.add(txtNuevaContrasena);
		panelFormulario.add(new JLabel("Nombre:"));
		panelFormulario.add(txtNombre);

		JButton btnGuardar = crearBoton("Guardar", e -> crearCuenta(txtNuevoUsuario, txtNuevaContrasena, txtNombre));
		JButton btnCancelar = crearBoton("Cancelar", e -> mostrarInicioSesion());

		JPanel panelBotones = new JPanel(new GridLayout(1, 2, 10, 10));
		panelBotones.setOpaque(false);
		panelBotones.add(btnGuardar);
		panelBotones.add(btnCancelar);

		panelCrearCuenta.add(lblTitulo);
		panelCrearCuenta.add(Box.createRigidArea(new Dimension(0, 20)));
		panelCrearCuenta.add(panelFormulario);
		panelCrearCuenta.add(Box.createRigidArea(new Dimension(0, 10)));
		panelCrearCuenta.add(panelBotones);

		cambiarPanel(panelCrearCuenta);
	}

	private void estilizarEtiqueta(JLabel etiqueta) {
		etiqueta.setFont(new Font("Segoe UI", Font.BOLD, 14));
		etiqueta.setForeground(new Color(50, 50, 50)); // Gris oscuro para contraste
	}

	private JButton crearBoton(String texto, ActionListener accion) {
		JButton boton = new JButton(texto);
		boton.setBackground(new Color(70, 130, 180));
		boton.setForeground(Color.WHITE);
		boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
		boton.setFocusPainted(false);
		boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		boton.addActionListener(accion);
		return boton;
	}

	private void crearCuenta(JTextField txtNuevoUsuario, JPasswordField txtNuevaContrasena, JTextField txtNombre) {
		try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
				Configuracion.DB_PASSWORD)) {

			String id = txtNuevoUsuario.getText();
			String nombre = txtNombre.getText();
			String contrasena = new String(txtNuevaContrasena.getPassword());

			if (id.isEmpty() || nombre.isEmpty() || contrasena.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			String checkQuery = "SELECT COUNT(*) FROM Usuarios WHERE id = ? OR nombre = ?";
			PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
			checkStmt.setString(1, id);
			checkStmt.setString(2, nombre);

			ResultSet rs = checkStmt.executeQuery();
			if (rs.next() && rs.getInt(1) > 0) {
				JOptionPane.showMessageDialog(this, "El usuario ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			String contrasenaCifrada = cifrarContrasena(contrasena);

			String insertQuery = "INSERT INTO Usuarios (id, nombre, nivel, puntosTotales, experiencia, contrasena) VALUES (?, ?, ?, ?, ?, ?)";
			PreparedStatement stmt = conn.prepareStatement(insertQuery);
			stmt.setString(1, id);
			stmt.setString(2, nombre);
			stmt.setInt(3, 1);
			stmt.setInt(4, 0);
			stmt.setInt(5, 0);
			stmt.setString(6, contrasenaCifrada);

			stmt.executeUpdate();
			JOptionPane.showMessageDialog(this, "Cuenta creada con éxito.");
			mostrarInicioSesion();
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error al crear la cuenta: " + ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public static String cifrarContrasena(String contrasena) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] hash = md.digest(contrasena.getBytes());
		StringBuilder hexString = new StringBuilder();
		for (byte b : hash) {
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}

	private void iniciarSesion() {
		JFrame ventanaPadre = this;
		PantallaCarga pantallaCarga = new PantallaCarga(ventanaPadre, "Iniciando sesión...");
		pantallaCarga.mostrar();

		SwingWorker<Void, Void> worker = new SwingWorker<>() {
			@Override
			protected Void doInBackground() {
				try {
					Class.forName("com.mysql.cj.jdbc.Driver");
					try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
							Configuracion.DB_PASSWORD)) {

						String query = "SELECT * FROM Usuarios WHERE id = ?";
						PreparedStatement stmt = conn.prepareStatement(query);
						stmt.setString(1, txtUsuario.getText());
						ResultSet rs = stmt.executeQuery();

						if (rs.next()) {
							// Validar contraseña
							String contrasenaIngresada = new String(txtContrasena.getPassword());
							String contrasenaCifrada = cifrarContrasena(contrasenaIngresada);

							if (!rs.getString("contrasena").equals(contrasenaCifrada)) {
								SwingUtilities.invokeLater(() -> {
									pantallaCarga.ocultar();
									JOptionPane.showMessageDialog(ventanaPadre, "Contraseña incorrecta.", "Error",
											JOptionPane.ERROR_MESSAGE);
								});
								return null;
							}

							Usuario usuario = new Usuario(rs.getString("id"), rs.getString("nombre"));
							usuario.setNivel(rs.getInt("nivel"));
							usuario.setPuntosTotales(rs.getInt("puntosTotales"));
							usuario.setExperiencia(rs.getInt("experiencia"));
							usuario.setEsAdmin(rs.getBoolean("esAdmin")); // Cargar si es administrador

							Configuracion.setUsuarioActual(usuario);

							SistemaGamificacion sistema = new SistemaGamificacion();
							CargaDatos.cargarDatosRelacionados(conn, sistema, usuario);

							SwingUtilities.invokeLater(() -> {
								pantallaCarga.ocultar();
								if (usuario.isEsAdmin()) {
									GraficosAdmin graficosAdmin = new GraficosAdmin(sistema, usuario);
									graficosAdmin.setVisible(true);
								} else {
									Graficos graficos = new Graficos(sistema, usuario);
									graficos.setVisible(true);
								}
								guardarSesion(usuario);
								dispose();
							});
						} else {
							SwingUtilities.invokeLater(() -> {
								pantallaCarga.ocultar();
								JOptionPane.showMessageDialog(ventanaPadre, "Usuario no encontrado.", "Error",
										JOptionPane.ERROR_MESSAGE);
							});
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					SwingUtilities.invokeLater(() -> {
						pantallaCarga.ocultar();
						JOptionPane.showMessageDialog(ventanaPadre, "Error al conectar con la base de datos.", "Error",
								JOptionPane.ERROR_MESSAGE);
					});
				}
				return null;
			}

			@Override
			protected void done() {
				pantallaCarga.ocultar(); // Asegura que la pantalla de carga se cierra
			}
		};

		worker.execute();
	}

	private void estilizarCampoTexto(JTextField campo) {
		campo.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
	}

	// Guardar la sesión en un archivo
	private void guardarSesion(Usuario usuario) {
	    try (FileWriter writer = new FileWriter("sesion.txt")) {
	        writer.write(usuario.getId() + "\n");
	        writer.write((usuario.isEsAdmin() ? "admin" : "normal") + "\n"); // Almacena si es admin o no
	    } catch (IOException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(this, "Error al guardar la sesión.", "Error", JOptionPane.ERROR_MESSAGE);
	    }
	}


}
