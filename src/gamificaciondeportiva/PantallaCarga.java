package gamificaciondeportiva;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * PantallaCarga es un JDialog personalizado que muestra una pantalla de carga con una barra de progreso y un mensaje.
 * Se utiliza para indicar que un proceso está en curso y el usuario debe esperar.
 *
 * <p>Esta clase extiende JDialog e incluye un JProgressBar y un JLabel para mostrar un ícono y un mensaje.</p>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>
 * {@code
 * PantallaCarga pantallaCarga = new PantallaCarga(parentFrame, "Cargando, por favor espere...");
 * pantallaCarga.mostrar();
 * // Realizar alguna tarea que dure mucho tiempo
 * pantallaCarga.ocultar();
 * }
 * </pre>
 *
 * <p>Detalles del constructor:</p>
 *
 * @param padre   El JFrame principal de este diálogo.
 * @param mensaje El mensaje que se mostrará en la pantalla de carga.
 *
 *                <p>Métodos:</p>
 *                <ul>
 *                  <li>{@link #mostrar()} - Muestra la pantalla de carga.</li>
 *                  <li>{@link #ocultar()} - Oculta la pantalla de carga.</li>
 *                </ul>
 *
 *                <p>Nota:</p>
 *                <ul>
 *                  <li>Se espera que el ícono de carga se encuentre en "resources/carga.gif".</li>
 *                  <li>Se espera que el ícono de la ventana se encuentre en "resources/Cubo-EnfocadoL.png".</li>
 *                </ul>
 *
 *                <p>Ejemplo de cómo configurar la imagen del ícono:</p>
 *                <pre>
 *                {@code
 *                try {
 *                    setIconImage(ImageIO.read(this.getClass().getResource("resources/Cubo-EnfocadoL.png")));
 *                } catch (IOException e) {
 *                    System.out.println("La imagen no se encuentra");
 *                }
 *                }
 *                </pre>
 * @see javax.swing.JDialog
 * @see javax.swing.JProgressBar
 * @see javax.swing.JLabel
 */

public class PantallaCarga extends JDialog {
    private static final long serialVersionUID = -2140236403018123465L;
    private JProgressBar barraProgreso;
    private JLabel lblIcono;

    /**
     * Constructor de PantallaCarga.
     * Configura la ventana de diálogo y sus componentes.
     *
     * @param padre   El JFrame principal de este diálogo.
     * @param mensaje El mensaje que se mostrará en la pantalla de carga.
     */
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

    /**
     * Muestra la pantalla de carga.
     */
    public void mostrar() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }

    /**
     * Oculta la pantalla de carga.
     */
    public void ocultar() {
        SwingUtilities.invokeLater(() -> setVisible(false));
    }

}