package gamificaciondeportiva;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class PantallaCarga extends JDialog {
	private static final long serialVersionUID = -2140236403018123465L;
	private JProgressBar barraProgreso;
	private JLabel lblIcono;

	public PantallaCarga(JFrame padre, String mensaje) {
		super(padre, true);
		setTitle("Cargando...");
		setSize(400, 300);
		setLocationRelativeTo(padre);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		try {
			setIconImage(ImageIO.read(this.getClass().getResource("resources/Cubo-EnfocadoL.png")));
		} catch (IOException e) {
			System.out.println("La imagen no se encuentra");
		}

		JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
		panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		panelPrincipal.setBackground(new Color(245, 245, 250));

		JLabel lblMensaje = new JLabel(mensaje, SwingConstants.CENTER);
		lblMensaje.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblMensaje.setForeground(new Color(70, 130, 180));

		lblIcono = new JLabel();
		lblIcono.setHorizontalAlignment(SwingConstants.CENTER);

		ImageIcon iconoOriginal = new ImageIcon(this.getClass().getResource("resources/carga.gif"));
		Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(120, 120, Image.SCALE_DEFAULT);
		lblIcono.setIcon(new ImageIcon(imagenEscalada));

		barraProgreso = new JProgressBar();
		barraProgreso.setIndeterminate(true);
		barraProgreso.setBackground(new Color(230, 230, 230));
		barraProgreso.setForeground(new Color(70, 130, 180));
		barraProgreso.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

		panelPrincipal.add(lblMensaje, BorderLayout.NORTH);
		panelPrincipal.add(lblIcono, BorderLayout.CENTER);
		panelPrincipal.add(barraProgreso, BorderLayout.SOUTH);

		add(panelPrincipal);
		pack();
		setLocationRelativeTo(padre);
	}

	public void mostrar() {
		SwingUtilities.invokeLater(() -> setVisible(true));
	}

	public void ocultar() {
		SwingUtilities.invokeLater(() -> setVisible(false));
	}

}
