package gamificaciondeportiva;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class MainGamificacionDeportiva {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PantallaCarga pantallaCarga = new PantallaCarga(null, "Cargando datos...");
            pantallaCarga.mostrar();

            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    try {
                        Usuario usuario = cargarSesionDesdeArchivo();
                        if (usuario != null) {
                            Configuracion.setUsuarioActual(usuario);

                            SistemaGamificacion sistema = new SistemaGamificacion();
                            try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER, Configuracion.DB_PASSWORD)) {
                                CargaDatos.cargarDatosRelacionados(conn, sistema, usuario);
                            }

                            SwingUtilities.invokeLater(() -> {
                                pantallaCarga.ocultar();
                                if (usuario.isEsAdmin()) {
                                    new GraficosAdmin(sistema, usuario).setVisible(true);
                                } else {
                                    new Graficos(sistema, usuario).setVisible(true);
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void done() {
                    pantallaCarga.ocultar();
                    if (Configuracion.getUsuarioActual() == null) {
                        new PantallaInicio().setVisible(true);
                    }
                }
            };

            worker.execute();
        });
    }

    private static Usuario cargarSesionDesdeArchivo() {
        File sesionFile = new File("sesion.txt");
        if (!sesionFile.exists()) {
            System.out.println("El archivo sesion.txt no existe.");
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(sesionFile))) {
            String id = reader.readLine();
            String tipo = reader.readLine();

            Usuario usuario = cargarUsuarioDesdeBaseDeDatos(id); // Metodo para obtener datos del usuario
            if (usuario != null) {
                usuario.setEsAdmin("admin".equalsIgnoreCase(tipo)); // Define si es administrador
            }
            return usuario;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Usuario cargarUsuarioDesdeBaseDeDatos(String id) {
        try (Connection conn = DriverManager.getConnection(Configuracion.DB_URL, Configuracion.DB_USER, Configuracion.DB_PASSWORD)) {
            String query = "SELECT * FROM Usuarios WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario usuario = new Usuario(rs.getString("id"), rs.getString("nombre"));
                usuario.setNivel(rs.getInt("nivel"));
                usuario.setPuntosTotales(rs.getInt("puntosTotales"));
                usuario.setExperiencia(rs.getInt("experiencia"));
                usuario.setEsAdmin(rs.getBoolean("esAdmin"));
                return usuario;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
