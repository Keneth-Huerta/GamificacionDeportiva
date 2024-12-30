package gamificaciondeportiva;

import com.toedter.calendar.JDateChooser;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Objects;

public class GraficosAdmin extends JFrame {
    @Serial
    private static final long serialVersionUID = 4438379178856699935L;
    private DefaultTableModel modeloTablaCompetencias;
    private DefaultTableModel modeloTablaLogros, modeloTablaDesafios;

    public GraficosAdmin(SistemaGamificacion sistema, Usuario usuarioActual) {

        configurarVentana();
        inicializarComponentes();
    }

    private void mostrarDialogoPersonalizado(String mensaje, String titulo, int tipo) {
        JLabel lblMensaje = new JLabel(mensaje);
        lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblMensaje.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(lblMensaje, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel, titulo, tipo);
    }

    private void inicializarSelectorFechas() {
        JDateChooser dateChooserInicio = new JDateChooser();
        dateChooserInicio.setDateFormatString("yyyy-MM-dd");
        dateChooserInicio.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JDateChooser dateChooserFin = new JDateChooser();
        dateChooserFin.setDateFormatString("yyyy-MM-dd");
        dateChooserFin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    private void cerrarSesion() {
        // Elimina el archivo de sesión
        File sesionFile = new File("sesion.txt");
        if (sesionFile.exists()) {
            sesionFile.delete();
        }

        Configuracion.setUsuarioActual(null); // Limpia el usuario actual
        new PantallaInicio().setVisible(true); // Muestra la pantalla de inicio
        dispose(); // Cierra la ventana actual
    }

    private void configurarVentana() {
        setTitle("Administración de Competencias");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        try {
            setIconImage(ImageIO.read(Objects.requireNonNull(this.getClass().getResource("resources/Cubo-EnfocadoL.png"))));
        } catch (IOException e) {
            System.out.println("La imagen no se encuentra");
        }
    }

    private void inicializarComponentes() {
        JTabbedPane tabbedPane = new JTabbedPane();

        // Pestaña de Competencias
        tabbedPane.addTab("Competencias", inicializarPanelCompetencias());

        // Pestaña de Logros
        tabbedPane.addTab("Logros", inicializarPanelLogros());

        // Pestaña de Desafíos
        tabbedPane.addTab("Desafíos", inicializarPanelDesafios());

        // Configuración general del panel
        add(tabbedPane, BorderLayout.CENTER);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private JPanel inicializarPanelDesafios() {
        JPanel panelDesafios = new JPanel(new BorderLayout(10, 10));
        panelDesafios.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelDesafios.setBackground(new Color(245, 245, 250));

        // Tabla de desafíos
        String[] columnas = {"ID", "Nombre", "Descripción", "Fecha Inicio", "Fecha Fin", "Estado"};
        modeloTablaDesafios = new DefaultTableModel(columnas, 0);
        JTable tablaDesafios = new JTable(modeloTablaDesafios);
        tablaDesafios.setRowHeight(25);
        tablaDesafios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaDesafios.getTableHeader().setBackground(new Color(70, 130, 180));
        tablaDesafios.getTableHeader().setForeground(Color.WHITE);

        cargarDesafiosDesdeBaseDeDatos(modeloTablaDesafios);

        JScrollPane scrollTabla = new JScrollPane(tablaDesafios);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnAgregar = crearBoton("Agregar Desafío", e -> agregarDesafio());
        JButton btnEditar = crearBoton("Editar Desafío", e -> editarDesafio(tablaDesafios));
        JButton btnEliminar = crearBoton("Eliminar Desafío", e -> eliminarDesafio(tablaDesafios));
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);

        panelDesafios.add(scrollTabla, BorderLayout.CENTER);
        panelDesafios.add(panelBotones, BorderLayout.SOUTH);

        return panelDesafios;
    }

    private void cargarLogrosDesdeBaseDeDatos(DefaultTableModel modeloTabla) {
        modeloTabla.setRowCount(0); // Limpia la tabla
        try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                Configuracion.DB_PASSWORD)) {

            String query = "SELECT * FROM Logros";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                modeloTabla.addRow(new Object[]{rs.getString("id"), rs.getString("nombre"),
                        rs.getString("descripcion"), rs.getInt("puntosRecompensa"), rs.getString("tipo")});
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void agregarDesafio() {
        JDialog dialog = new JDialog(this, "Agregar Desafío", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelFormulario.setBackground(new Color(245, 245, 250));

        JTextField txtNombre = new JTextField();
        JTextField txtDescripcion = new JTextField();
        JTextField txtPuntos = new JTextField();
        JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"PENDIENTE", "ACTIVO", "COMPLETADO", "EXPIRADO"});
        JDateChooser dateChooserInicio = new JDateChooser();
        dateChooserInicio.setDateFormatString("yyyy-MM-dd");
        dateChooserInicio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JDateChooser dateChooserFin = new JDateChooser();
        dateChooserFin.setDateFormatString("yyyy-MM-dd");
        dateChooserFin.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        estilizarCampoTexto(txtNombre);
        estilizarCampoTexto(txtDescripcion);
        estilizarCampoTexto(txtPuntos);

        panelFormulario.add(new JLabel("Nombre del Desafío:"));
        panelFormulario.add(txtNombre);
        panelFormulario.add(Box.createVerticalStrut(10));

        panelFormulario.add(new JLabel("Descripción:"));
        panelFormulario.add(txtDescripcion);
        panelFormulario.add(Box.createVerticalStrut(10));

        panelFormulario.add(new JLabel("Puntos de Recompensa:"));
        panelFormulario.add(txtPuntos);
        panelFormulario.add(Box.createVerticalStrut(10));

        panelFormulario.add(new JLabel("Estado:"));
        panelFormulario.add(cmbEstado);
        panelFormulario.add(Box.createVerticalStrut(10));

        panelFormulario.add(new JLabel("Fecha de Inicio:"));
        panelFormulario.add(dateChooserInicio);
        panelFormulario.add(Box.createVerticalStrut(10));

        panelFormulario.add(new JLabel("Fecha de Fin:"));
        panelFormulario.add(dateChooserFin);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnGuardar = crearBoton("Guardar", e -> {
            try {
                String nombre = txtNombre.getText();
                String descripcion = txtDescripcion.getText();
                int puntos = Integer.parseInt(txtPuntos.getText());
                String estado = cmbEstado.getSelectedItem().toString();
                java.util.Date fechaInicio = dateChooserInicio.getDate();
                java.util.Date fechaFin = dateChooserFin.getDate();

                if (nombre.isEmpty() || descripcion.isEmpty() || fechaInicio == null || fechaFin == null) {
                    JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Advertencia",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                        Configuracion.DB_PASSWORD)) {
                    String query = "INSERT INTO Desafios (nombre, descripcion, puntosRecompensa, estado, fechaInicio, fechaFin) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, nombre);
                    stmt.setString(2, descripcion);
                    stmt.setInt(3, puntos);
                    stmt.setString(4, estado);
                    stmt.setDate(5, new java.sql.Date(fechaInicio.getTime()));
                    stmt.setDate(6, new java.sql.Date(fechaFin.getTime()));
                    stmt.executeUpdate();
                    cargarDesafiosDesdeBaseDeDatos(modeloTablaDesafios);
                    dialog.dispose();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error al guardar el desafío.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Por favor, ingresa valores válidos.", "Advertencia",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        JButton btnCancelar = crearBoton("Cancelar", e -> dialog.dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        dialog.add(panelFormulario, BorderLayout.CENTER);
        dialog.add(panelBotones, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void editarDesafio(JTable tablaDesafios) {
        int row = tablaDesafios.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un desafío para editar.", "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String desafioId = modeloTablaDesafios.getValueAt(row, 0).toString();
        String nombre = modeloTablaDesafios.getValueAt(row, 1).toString();
        String descripcion = modeloTablaDesafios.getValueAt(row, 2).toString();
        String puntos = modeloTablaDesafios.getValueAt(row, 3).toString();
        String estado = modeloTablaDesafios.getValueAt(row, 4).toString();
        String fechaInicio = modeloTablaDesafios.getValueAt(row, 5).toString();
        String fechaFin = modeloTablaDesafios.getValueAt(row, 6).toString();

        JDialog dialog = new JDialog(this, "Editar Desafío", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelFormulario.setBackground(new Color(245, 245, 250));

        JTextField txtNombre = new JTextField(nombre);
        JTextField txtDescripcion = new JTextField(descripcion);
        JTextField txtPuntos = new JTextField(puntos);
        JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"PENDIENTE", "ACTIVO", "COMPLETADO", "EXPIRADO"});
        cmbEstado.setSelectedItem(estado);
        JDateChooser dateChooserInicio = new JDateChooser();
        dateChooserInicio.setDateFormatString("yyyy-MM-dd");
        dateChooserInicio.setDate(java.sql.Date.valueOf(fechaInicio));
        JDateChooser dateChooserFin = new JDateChooser();
        dateChooserFin.setDateFormatString("yyyy-MM-dd");
        dateChooserFin.setDate(java.sql.Date.valueOf(fechaFin));

        estilizarCampoTexto(txtNombre);
        estilizarCampoTexto(txtDescripcion);
        estilizarCampoTexto(txtPuntos);

        panelFormulario.add(new JLabel("Nombre del Desafío:"));
        panelFormulario.add(txtNombre);
        panelFormulario.add(Box.createVerticalStrut(10));

        panelFormulario.add(new JLabel("Descripción:"));
        panelFormulario.add(txtDescripcion);
        panelFormulario.add(Box.createVerticalStrut(10));

        panelFormulario.add(new JLabel("Puntos de Recompensa:"));
        panelFormulario.add(txtPuntos);
        panelFormulario.add(Box.createVerticalStrut(10));

        panelFormulario.add(new JLabel("Estado:"));
        panelFormulario.add(cmbEstado);
        panelFormulario.add(Box.createVerticalStrut(10));

        panelFormulario.add(new JLabel("Fecha de Inicio:"));
        panelFormulario.add(dateChooserInicio);
        panelFormulario.add(Box.createVerticalStrut(10));

        panelFormulario.add(new JLabel("Fecha de Fin:"));
        panelFormulario.add(dateChooserFin);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnGuardar = crearBoton("Guardar", e -> {
            try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                    Configuracion.DB_PASSWORD)) {
                String query = "UPDATE Desafios SET nombre = ?, descripcion = ?, puntosRecompensa = ?, estado = ?, fechaInicio = ?, fechaFin = ? WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, txtNombre.getText());
                stmt.setString(2, txtDescripcion.getText());
                stmt.setInt(3, Integer.parseInt(txtPuntos.getText()));
                stmt.setString(4, cmbEstado.getSelectedItem().toString());
                stmt.setDate(5, new java.sql.Date(dateChooserInicio.getDate().getTime()));
                stmt.setDate(6, new java.sql.Date(dateChooserFin.getDate().getTime()));
                stmt.setString(7, desafioId);
                stmt.executeUpdate();
                cargarDesafiosDesdeBaseDeDatos(modeloTablaDesafios);
                dialog.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al actualizar el desafío.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        JButton btnCancelar = crearBoton("Cancelar", e -> dialog.dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        dialog.add(panelFormulario, BorderLayout.CENTER);
        dialog.add(panelBotones, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void eliminarDesafio(JTable tablaDesafios) {
        int row = tablaDesafios.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un desafío para eliminar.", "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String desafioId = modeloTablaDesafios.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de eliminar este desafío?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                    Configuracion.DB_PASSWORD)) {
                String query = "DELETE FROM Desafios WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, desafioId);
                stmt.executeUpdate();
                cargarDesafiosDesdeBaseDeDatos(modeloTablaDesafios);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al eliminar el desafío.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel inicializarPanelLogros() {
        JPanel panelLogros = new JPanel(new BorderLayout(10, 10));
        panelLogros.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelLogros.setBackground(new Color(245, 245, 250));

        // Tabla de logros
        String[] columnas = {"ID", "Nombre", "Descripción", "Puntos", "Tipo"};
        modeloTablaLogros = new DefaultTableModel(columnas, 0);
        JTable tablaLogros = new JTable(modeloTablaLogros);
        tablaLogros.setRowHeight(25);
        tablaLogros.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaLogros.getTableHeader().setBackground(new Color(70, 130, 180));
        tablaLogros.getTableHeader().setForeground(Color.WHITE);

        cargarLogrosDesdeBaseDeDatos(modeloTablaLogros);

        JScrollPane scrollTabla = new JScrollPane(tablaLogros);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnAgregar = crearBoton("Agregar Logro", e -> agregarLogro());
        JButton btnEditar = crearBoton("Editar Logro", e -> editarLogro(tablaLogros));
        JButton btnEliminar = crearBoton("Eliminar Logro", e -> eliminarLogro(tablaLogros));
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);

        panelLogros.add(scrollTabla, BorderLayout.CENTER);
        panelLogros.add(panelBotones, BorderLayout.SOUTH);

        return panelLogros;
    }

    private void agregarLogro() {
        JDialog dialog = new JDialog(this, "Agregar Logro", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelFormulario.setBackground(new Color(245, 245, 250));

        JTextField txtNombre = new JTextField();
        JTextField txtDescripcion = new JTextField();
        JTextField txtPuntos = new JTextField();
        JComboBox<String> cmbTipo = new JComboBox<>(new String[]{"DIARIO", "SEMANAL", "MENSUAL"});

        estilizarCampoTexto(txtNombre);
        estilizarCampoTexto(txtDescripcion);
        estilizarCampoTexto(txtPuntos);

        panelFormulario.add(new JLabel("Nombre del Logro:"));
        panelFormulario.add(txtNombre);
        panelFormulario.add(Box.createVerticalStrut(10));

        panelFormulario.add(new JLabel("Descripción:"));
        panelFormulario.add(txtDescripcion);
        panelFormulario.add(Box.createVerticalStrut(10));

        panelFormulario.add(new JLabel("Puntos:"));
        panelFormulario.add(txtPuntos);
        panelFormulario.add(Box.createVerticalStrut(10));

        panelFormulario.add(new JLabel("Tipo:"));
        panelFormulario.add(cmbTipo);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnGuardar = crearBoton("Guardar", e -> {
            String nombre = txtNombre.getText();
            String descripcion = txtDescripcion.getText();
            int puntos = Integer.parseInt(txtPuntos.getText());
            String tipo = cmbTipo.getSelectedItem().toString();

            // Guardar en base de datos
            try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                    Configuracion.DB_PASSWORD)) {
                String query = "INSERT INTO Logros (nombre, descripcion, puntosRecompensa, tipo) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, nombre);
                stmt.setString(2, descripcion);
                stmt.setInt(3, puntos);
                stmt.setString(4, tipo);
                stmt.executeUpdate();
                cargarLogrosDesdeBaseDeDatos(modeloTablaLogros);
                dialog.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al guardar el logro.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        JButton btnCancelar = crearBoton("Cancelar", e -> dialog.dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        dialog.add(panelFormulario, BorderLayout.CENTER);
        dialog.add(panelBotones, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void editarLogro(JTable tablaLogros) {
        int row = tablaLogros.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un logro para editar.", "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String logroId = modeloTablaLogros.getValueAt(row, 0).toString();
        String nombre = modeloTablaLogros.getValueAt(row, 1).toString();
        String descripcion = modeloTablaLogros.getValueAt(row, 2).toString();
        String puntos = modeloTablaLogros.getValueAt(row, 3).toString();
        String tipo = modeloTablaLogros.getValueAt(row, 4).toString();

        JDialog dialog = new JDialog(this, "Editar Logro", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelFormulario.setBackground(new Color(245, 245, 250));

        JTextField txtNombre = new JTextField(nombre);
        JTextField txtDescripcion = new JTextField(descripcion);
        JTextField txtPuntos = new JTextField(puntos);
        JComboBox<String> cmbTipo = new JComboBox<>(new String[]{"DIARIO", "SEMANAL", "MENSUAL"});
        cmbTipo.setSelectedItem(tipo);

        estilizarCampoTexto(txtNombre);
        estilizarCampoTexto(txtDescripcion);
        estilizarCampoTexto(txtPuntos);

        panelFormulario.add(new JLabel("Nombre del Logro:"));
        panelFormulario.add(txtNombre);
        panelFormulario.add(Box.createVerticalStrut(10));

        panelFormulario.add(new JLabel("Descripción:"));
        panelFormulario.add(txtDescripcion);
        panelFormulario.add(Box.createVerticalStrut(10));

        panelFormulario.add(new JLabel("Puntos:"));
        panelFormulario.add(txtPuntos);
        panelFormulario.add(Box.createVerticalStrut(10));

        panelFormulario.add(new JLabel("Tipo:"));
        panelFormulario.add(cmbTipo);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnGuardar = crearBoton("Guardar", e -> {
            try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                    Configuracion.DB_PASSWORD)) {
                String query = "UPDATE Logros SET nombre = ?, descripcion = ?, puntosRecompensa = ?, tipo = ? WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, txtNombre.getText());
                stmt.setString(2, txtDescripcion.getText());
                stmt.setInt(3, Integer.parseInt(txtPuntos.getText()));
                stmt.setString(4, cmbTipo.getSelectedItem().toString());
                stmt.setString(5, logroId);
                stmt.executeUpdate();
                cargarLogrosDesdeBaseDeDatos(modeloTablaLogros);
                dialog.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al actualizar el logro.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        JButton btnCancelar = crearBoton("Cancelar", e -> dialog.dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        dialog.add(panelFormulario, BorderLayout.CENTER);
        dialog.add(panelBotones, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void eliminarLogro(JTable tablaLogros) {
        int row = tablaLogros.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un logro para eliminar.", "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String logroId = modeloTablaLogros.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de eliminar este logro?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                    Configuracion.DB_PASSWORD)) {
                String query = "DELETE FROM Logros WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, logroId);
                stmt.executeUpdate();
                cargarLogrosDesdeBaseDeDatos(modeloTablaLogros);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al eliminar el logro.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel inicializarPanelCompetencias() {
        JPanel panelCompetencias = new JPanel(new BorderLayout(10, 10));

        modeloTablaCompetencias = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Tipo", "Fecha Inicio", "Fecha Fin", "Estado"}, 0) {

            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hace las celdas no editables
            }
        };

        JTable tablaCompetencias = new JTable(modeloTablaCompetencias);
        tablaCompetencias.setRowHeight(25);
        tablaCompetencias.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaCompetencias.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaCompetencias.getTableHeader().setBackground(new Color(70, 130, 180));
        tablaCompetencias.getTableHeader().setForeground(Color.WHITE);
        tablaCompetencias.setSelectionBackground(new Color(135, 206, 250));
        tablaCompetencias.setSelectionForeground(Color.BLACK);

        JScrollPane scrollTabla = new JScrollPane(tablaCompetencias);
        panelCompetencias.add(scrollTabla, BorderLayout.CENTER);

        // Panel de Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(new Color(245, 245, 250));

        JButton btnAgregar = crearBoton("Agregar Competencia", e -> agregarCompetencia());
        JButton btnEditar = crearBoton("Editar Competencia", e -> editarCompetencia(tablaCompetencias));
        JButton btnEliminar = crearBoton("Eliminar Competencia", e -> eliminarCompetencia(tablaCompetencias));
        JButton btnCerrarSesion = crearBoton("Cerrar Sesión", e -> cerrarSesion());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnCerrarSesion);

        panelCompetencias.add(panelBotones, BorderLayout.SOUTH);

        // Cargar datos iniciales
        cargarCompetenciasDesdeBaseDeDatos();

        return panelCompetencias;
    }

    private void cargarDesafiosDesdeBaseDeDatos(DefaultTableModel modeloTabla) {
        modeloTabla.setRowCount(0); // Limpia la tabla
        try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                Configuracion.DB_PASSWORD)) {

            String query = "SELECT * FROM Desafios";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                modeloTabla
                        .addRow(new Object[]{rs.getString("id"), rs.getString("nombre"), rs.getString("descripcion"),
                                rs.getDate("fechaInicio"), rs.getDate("fechaFin"), rs.getString("estado")});
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void cargarCompetenciasDesdeBaseDeDatos() {
        modeloTablaCompetencias.setRowCount(0); // Limpiar la tabla antes de llenarla
        try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                Configuracion.DB_PASSWORD)) {

            String query = "SELECT * FROM Competencias";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                modeloTablaCompetencias
                        .addRow(new Object[]{rs.getString("id"), rs.getString("nombre"), rs.getString("tipoDeporte"),
                                rs.getDate("fechaInicio"), rs.getDate("fechaFin"), rs.getString("estado")});
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

    private void estilizarCampoTexto(JTextField campo) {
        campo.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    private void agregarCompetencia() {
        JDialog dialog = new JDialog(this, "Agregar Competencia", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelFormulario.setBackground(new Color(245, 245, 250));

        // Campos del formulario
        JTextField txtNombre = new JTextField();
        estilizarCampoTexto(txtNombre);

        JComboBox<String> cmbTipoDeporte = new JComboBox<>(
                new String[]{"CORRER", "NATACION", "CICLISMO", "FUTBOL", "BALONCESTO", "VOLLEYBALL", "GIMNASIO"});
        cmbTipoDeporte.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JDateChooser dateChooserInicio = new JDateChooser();
        dateChooserInicio.setDateFormatString("yyyy-MM-dd");
        dateChooserInicio.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JDateChooser dateChooserFin = new JDateChooser();
        dateChooserFin.setDateFormatString("yyyy-MM-dd");
        dateChooserFin.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Añadir componentes al formulario
        panelFormulario.add(new JLabel("Nombre de la Competencia:"));
        panelFormulario.add(txtNombre);
        panelFormulario.add(Box.createVerticalStrut(10));

        panelFormulario.add(new JLabel("Tipo de Deporte:"));
        panelFormulario.add(cmbTipoDeporte);
        panelFormulario.add(Box.createVerticalStrut(10));

        panelFormulario.add(new JLabel("Fecha de Inicio:"));
        panelFormulario.add(dateChooserInicio);
        panelFormulario.add(Box.createVerticalStrut(10));

        panelFormulario.add(new JLabel("Fecha de Fin:"));
        panelFormulario.add(dateChooserFin);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.setBackground(new Color(245, 245, 250));

        JButton btnGuardar = crearBoton("Guardar", e -> {
            String nombre = txtNombre.getText().trim();
            String tipoDeporte = (String) cmbTipoDeporte.getSelectedItem();
            Date fechaInicio = dateChooserInicio.getDate();
            Date fechaFin = dateChooserFin.getDate();

            if (nombre.isEmpty() || fechaInicio == null || fechaFin == null) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Advertencia",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (fechaInicio.after(fechaFin)) {
                JOptionPane.showMessageDialog(this, "La fecha de inicio no puede ser después de la fecha de fin.",
                        "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Determinar el estado de la competencia según las fechas
            String estado;
            Date hoy = new Date();
            if (hoy.before(fechaInicio)) {
                estado = "PENDIENTE";
            } else if (hoy.after(fechaFin)) {
                estado = "FINALIZADA";
            } else {
                estado = "ACTIVO";
            }

            try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                    Configuracion.DB_PASSWORD)) {

                String query = "INSERT INTO Competencias (id, nombre, tipoDeporte, fechaInicio, fechaFin, estado) "
                        + "VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);

                // Generar un ID único para la competencia
                String id = "COMP" + System.currentTimeMillis();

                stmt.setString(1, id); // ID generado
                stmt.setString(2, nombre); // Nombre
                stmt.setString(3, tipoDeporte); // Tipo de deporte
                stmt.setDate(4, new java.sql.Date(fechaInicio.getTime())); // Fecha inicio
                stmt.setDate(5, new java.sql.Date(fechaFin.getTime())); // Fecha fin
                stmt.setString(6, estado); // Estado calculado

                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Competencia agregada con éxito.", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                cargarCompetenciasDesdeBaseDeDatos(); // Refrescar la tabla
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al agregar la competencia: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        JButton btnCancelar = crearBoton("Cancelar", e -> dialog.dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        dialog.add(panelFormulario, BorderLayout.CENTER);
        dialog.add(panelBotones, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void editarCompetencia(JTable tablaCompetencias) {
        int filaSeleccionada = tablaCompetencias.getSelectedRow();
        if (filaSeleccionada == -1) {
            mostrarMensaje("Por favor, selecciona una competencia para editar.", "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener datos de la fila seleccionada
        String id = modeloTablaCompetencias.getValueAt(filaSeleccionada, 0).toString();
        String nombreActual = modeloTablaCompetencias.getValueAt(filaSeleccionada, 1).toString();
        String tipoDeporteActual = modeloTablaCompetencias.getValueAt(filaSeleccionada, 2).toString();
        Date fechaInicioActual = java.sql.Date
                .valueOf(modeloTablaCompetencias.getValueAt(filaSeleccionada, 3).toString());
        Date fechaFinActual = java.sql.Date.valueOf(modeloTablaCompetencias.getValueAt(filaSeleccionada, 4).toString());
        String estadoActual = modeloTablaCompetencias.getValueAt(filaSeleccionada, 5).toString();

        JDialog dialog = new JDialog(this, "Editar Competencia", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelFormulario.setBackground(new Color(245, 245, 250));

        // Campos del formulario
        JTextField txtNombre = new JTextField(nombreActual);
        estilizarCampoTexto(txtNombre);

        JComboBox<String> cmbTipoDeporte = new JComboBox<>(
                new String[]{"CORRER", "NATACION", "CICLISMO", "FUTBOL", "BALONCESTO", "VOLLEYBALL", "GIMNASIO"});
        cmbTipoDeporte.setSelectedItem(tipoDeporteActual);
        cmbTipoDeporte.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JDateChooser dateChooserInicio = new JDateChooser(fechaInicioActual);
        dateChooserInicio.setDateFormatString("yyyy-MM-dd");
        dateChooserInicio.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JDateChooser dateChooserFin = new JDateChooser(fechaFinActual);
        dateChooserFin.setDateFormatString("yyyy-MM-dd");
        dateChooserFin.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panelFormulario.add(new JLabel("Nombre de la Competencia:"));
        panelFormulario.add(txtNombre);
        panelFormulario.add(Box.createVerticalStrut(10));

        panelFormulario.add(new JLabel("Tipo de Deporte:"));
        panelFormulario.add(cmbTipoDeporte);
        panelFormulario.add(Box.createVerticalStrut(10));

        panelFormulario.add(new JLabel("Fecha de Inicio:"));
        panelFormulario.add(dateChooserInicio);
        panelFormulario.add(Box.createVerticalStrut(10));

        panelFormulario.add(new JLabel("Fecha de Fin:"));
        panelFormulario.add(dateChooserFin);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.setBackground(new Color(245, 245, 250));

        JButton btnGuardar = crearBoton("Guardar Cambios", e -> {
            String nuevoNombre = txtNombre.getText().trim();
            String nuevoTipoDeporte = (String) cmbTipoDeporte.getSelectedItem();
            Date nuevaFechaInicio = dateChooserInicio.getDate();
            Date nuevaFechaFin = dateChooserFin.getDate();

            if (nuevoNombre.isEmpty() || nuevaFechaInicio == null || nuevaFechaFin == null) {
                mostrarMensaje("Todos los campos son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (nuevaFechaInicio.after(nuevaFechaFin)) {
                mostrarMensaje("La fecha de inicio no puede ser después de la fecha de fin.", "Error de Validación",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                    Configuracion.DB_PASSWORD)) {

                String query = "UPDATE Competencias SET nombre = ?, tipoDeporte = ?, fechaInicio = ?, fechaFin = ? WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);

                stmt.setString(1, nuevoNombre);
                stmt.setString(2, nuevoTipoDeporte);
                stmt.setDate(3, new java.sql.Date(nuevaFechaInicio.getTime()));
                stmt.setDate(4, new java.sql.Date(nuevaFechaFin.getTime()));
                stmt.setString(5, id);

                stmt.executeUpdate();

                mostrarMensaje("Competencia actualizada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarCompetenciasDesdeBaseDeDatos(); // Refrescar la tabla
                dialog.dispose();
            } catch (Exception ex) {
                mostrarMensaje("Error al actualizar la competencia: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        JButton btnCancelar = crearBoton("Cancelar", e -> dialog.dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        dialog.add(panelFormulario, BorderLayout.CENTER);
        dialog.add(panelBotones, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void mostrarMensaje(String mensaje, String titulo, int tipoMensaje) {
        UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.BOLD, 14));
        UIManager.put("OptionPane.background", new Color(245, 245, 250));
        UIManager.put("Panel.background", new Color(245, 245, 250));
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipoMensaje);
    }

    private void eliminarCompetencia(JTable tabla) {
        int row = tabla.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una competencia para eliminar.", "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) tabla.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que deseas eliminar esta competencia?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER,
                    Configuracion.DB_PASSWORD)) {
                String query = "DELETE FROM Competencias WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, id);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Competencia eliminada con éxito.");
                cargarCompetenciasDesdeBaseDeDatos();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al eliminar la competencia.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
