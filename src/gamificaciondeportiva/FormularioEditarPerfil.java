package gamificaciondeportiva;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class FormularioEditarPerfil extends JDialog {
	private static final long serialVersionUID = 1L;

	private JTextField txtNombre;
	private JPasswordField txtContrasena;
	private JButton btnGuardar, btnSeleccionarFoto;
	private JLabel lblFotoPerfil;

	private Usuario usuarioActual;
	private SistemaGamificacion sistema;

	public FormularioEditarPerfil(Frame owner, Usuario usuario, SistemaGamificacion sistema) {
		super(owner, "Editar Perfil", true);
		this.usuarioActual = usuario;
		this.sistema = sistema;

		setLayout(new BorderLayout(10, 10));
		setBackground(new Color(245, 245, 250));

		// Panel Principal
		JPanel panelPrincipal = new JPanel();
		panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
		panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		panelPrincipal.setBackground(new Color(245, 245, 250));

		// Foto de Perfil
		JPanel panelFoto = new JPanel();
		panelFoto.setBackground(new Color(245, 245, 250));
		panelFoto.setLayout(new BoxLayout(panelFoto, BoxLayout.Y_AXIS));
		panelFoto.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		lblFotoPerfil = new JLabel();
		lblFotoPerfil.setHorizontalAlignment(SwingConstants.CENTER);
		lblFotoPerfil.setPreferredSize(new Dimension(150, 150)); // Tamaño fijo para la etiqueta
		lblFotoPerfil.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblFotoPerfil.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1)); // Opcional: borde
																								// alrededor de la
																								// imagen

		ImageIcon icono = sistema.obtenerFotoDesdeBaseDeDatos(usuario.getId());
		if (icono != null) {
			lblFotoPerfil.setIcon(hacerImagenCircular(icono));
		} else {
			lblFotoPerfil.setIcon(cargarImagenPredeterminada());
		}

		btnSeleccionarFoto = crearBoton("Cambiar Foto", e -> seleccionarFoto());
		panelFoto.add(lblFotoPerfil);
		panelFoto.add(Box.createRigidArea(new Dimension(0, 10)));
		panelFoto.add(btnSeleccionarFoto);

		// Campos de Edición
		JPanel panelCampos = new JPanel(new GridLayout(2, 2, 10, 10));
		panelCampos.setBackground(Color.WHITE);
		panelCampos
				.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)),
						BorderFactory.createEmptyBorder(15, 15, 15, 15)));

		txtNombre = new JTextField(usuario.getNombre());
		txtContrasena = new JPasswordField();

		estilizarCampoTexto(txtNombre);
		estilizarCampoTexto(txtContrasena);

		panelCampos.add(new JLabel("Nombre:"));
		panelCampos.add(txtNombre);
		panelCampos.add(new JLabel("Contraseña:"));
		panelCampos.add(txtContrasena);

		// Botón Guardar
		btnGuardar = crearBoton("Guardar Cambios", e -> guardarCambios());

		panelPrincipal.add(panelFoto);
		panelPrincipal.add(Box.createRigidArea(new Dimension(0, 20)));
		panelPrincipal.add(panelCampos);
		panelPrincipal.add(Box.createRigidArea(new Dimension(0, 20)));
		panelPrincipal.add(btnGuardar);

		add(panelPrincipal, BorderLayout.CENTER);

		pack();
		setLocationRelativeTo(owner);
	}

	private JButton crearBoton(String texto, ActionListener accion) {
		JButton boton = new JButton(texto);
		boton.setBackground(new Color(70, 130, 180));
		boton.setForeground(Color.WHITE);
		boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
		boton.setFocusPainted(false);
		boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		boton.addActionListener(accion);
		boton.setAlignmentX(Component.CENTER_ALIGNMENT);
		return boton;
	}

	private void estilizarCampoTexto(JTextField campo) {
		campo.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
	}

	private void seleccionarFoto() {
		JFileChooser fileChooser = new JFileChooser();
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			File archivoSeleccionado = fileChooser.getSelectedFile();
			try {
				BufferedImage imagen = ImageIO.read(archivoSeleccionado);
				imagen = escalarImagen(imagen, 150, 150); // Escalar la imagen antes de hacerla circular
				lblFotoPerfil.setIcon(hacerImagenCircular(imagen, 150)); // Aplicar formato circular
				usuarioActual.setFotoPerfil(convertirImagenABytes(imagen)); // Guardar como bytes
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Error al cargar la foto.", "Error", JOptionPane.ERROR_MESSAGE);
			}
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

	private byte[] convertirImagenABytes(BufferedImage imagen) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(imagen, "png", baos);
		return baos.toByteArray();
	}

	private void guardarCambios() {
		String nombre = txtNombre.getText().trim();
		String contrasena = new String(txtContrasena.getPassword()).trim();

		if (nombre.isEmpty()) {
			JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío.", "Advertencia",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
				Configuracion.DB_PASSWORD)) {
			String query;
			PreparedStatement stmt;

			if (!contrasena.isEmpty()) {
				// Si la contraseña no está vacía, actualizarla también
				String contrasenaCifrada = PantallaInicio.cifrarContrasena(contrasena);
				query = "UPDATE Usuarios SET nombre = ?, contrasena = ?, fotoPerfil = ? WHERE id = ?";
				stmt = conn.prepareStatement(query);
				stmt.setString(1, nombre); // Nombre del usuario
				stmt.setString(2, contrasenaCifrada); // Establece la contraseña cifrada
				stmt.setBytes(3, usuarioActual.getFotoPerfil()); // Foto de perfil en bytes
				stmt.setString(4, usuarioActual.getId()); // ID del usuario
			} else {
				// Si no hay contraseña, no la actualices
				query = "UPDATE Usuarios SET nombre = ?, fotoPerfil = ? WHERE id = ?";
				stmt = conn.prepareStatement(query);
				stmt.setString(1, nombre); // Nombre del usuario
				stmt.setBytes(2, usuarioActual.getFotoPerfil()); // Foto de perfil en bytes
				stmt.setString(3, usuarioActual.getId()); // ID del usuario
			}

			stmt.executeUpdate();

			JOptionPane.showMessageDialog(this, "Datos actualizados con éxito.", "Éxito",
					JOptionPane.INFORMATION_MESSAGE);
			dispose();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Error al actualizar los datos. Inténtalo de nuevo.", "Error",
					JOptionPane.ERROR_MESSAGE);
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

	public ImageIcon hacerImagenCircular(BufferedImage originalImage, int diameter) {
		BufferedImage circularImage = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = circularImage.createGraphics();

		g2.setClip(new Ellipse2D.Float(0, 0, diameter, diameter));
		g2.drawImage(originalImage, 0, 0, diameter, diameter, null);
		g2.dispose();

		return new ImageIcon(circularImage);
	}

	private ImageIcon hacerImagenCircular(ImageIcon icono) {
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
}
