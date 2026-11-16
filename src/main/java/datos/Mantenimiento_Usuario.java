package datos;

import java.sql.*;
import modelo.Usuario;

public class Mantenimiento_Usuario extends Libreria {

    PreparedStatement pst;

    // Registrar usuario
    public boolean registro(Usuario u) {
        try {
            String sql = "INSERT INTO usuario "
                       + "(nombre, usuario, contrasena) "
                       + "VALUES (?, ?, SHA2(?, 256))";

            pst = conexion().prepareStatement(sql);

            pst.setString(1, u.getNombre());
            pst.setString(2, u.getUsuario());
            pst.setString(3, u.getContrasena());

            pst.executeUpdate();

            return true;

        } catch (SQLException e) {
            System.out.println("Error al registrar usuario " + e);
            return false;
        }
    }

    // Consultar usuarios
    public ResultSet consulta(String sql) {
        try {
            sen = conexion().createStatement();
            data = sen.executeQuery(sql);

        } catch (SQLException e) {
            System.out.println("Error en la consulta " + e);
        }

        return data;
    }

    // Validar inicio de sesion
    public boolean validar(String usuario, String contrasena) {
        try {
            String sql = "SELECT * FROM usuario "
                       + "WHERE usuario = ? "
                       + "AND contrasena = SHA2(?, 256) "
                       + "AND estado = TRUE";

            pst = conexion().prepareStatement(sql);

            pst.setString(1, usuario);
            pst.setString(2, contrasena);

            data = pst.executeQuery();

            if (data.next()) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Error al validar usuario " + e);
            return false;
        }
    }


    // Obtener id del usuario por su nombre de usuario
    public int obtenerId(String usuario) {
        try {
            String sql = "SELECT id_usuario FROM usuario WHERE usuario = ?";

            pst = conexion().prepareStatement(sql);
            pst.setString(1, usuario);

            data = pst.executeQuery();

            if (data.next()) {
                return data.getInt("id_usuario");
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener usuario " + e);
        }

        return 0;
    }


    // Cerrar conexion
    public void cerrar() {
        try {
            if (data != null) data.close();
            if (sen != null) sen.close();
            if (pst != null) pst.close();
            if (conn != null) conn.close();

        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexion " + e);
        }
    }
}
