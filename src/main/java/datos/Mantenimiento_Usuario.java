package datos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import modelo.Usuario;

public class Mantenimiento_Usuario {

    private static final String SQL_REGISTRO =
        "INSERT INTO usuario (id_rol, nombre, usuario, contrasena) VALUES (2, ?, ?, ?)";

    private static final String SQL_AUTENTICAR =
        "SELECT id_usuario, id_rol, nombre, usuario " +
        "FROM usuario WHERE usuario = ? AND contrasena = ? AND estado = TRUE";

    public boolean registro(Usuario usuario) {
        try (Connection cn = conexion();
             PreparedStatement pst = cn.prepareStatement(SQL_REGISTRO)) {

            pst.setString(1, usuario.getNombre());
            pst.setString(2, usuario.getUsuario());
            pst.setString(3, usuario.getContrasena());

            return pst.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("No se pudo registrar el usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Valida las credenciales y recupera los datos necesarios para la sesión
     * en una sola consulta y una sola conexión.
     */
    public Usuario autenticar(String nombreUsuario, String contrasena) {
        try (Connection cn = conexion();
             PreparedStatement pst = cn.prepareStatement(SQL_AUTENTICAR)) {

            pst.setString(1, nombreUsuario);
            pst.setString(2, contrasena);

            try (ResultSet rs = pst.executeQuery()) {
                if (!rs.next()) return null;

                Usuario usuario = new Usuario();
                usuario.setId_usuario(rs.getInt("id_usuario"));
                usuario.setId_rol(rs.getInt("id_rol"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setUsuario(rs.getString("usuario"));
                usuario.setEstado(true);
                return usuario;
            }
        } catch (SQLException e) {
            System.err.println("No se pudo autenticar al usuario: " + e.getMessage());
            return null;
        }
    }
}
