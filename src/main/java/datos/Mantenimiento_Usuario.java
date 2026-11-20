package datos;

import java.sql.*;
import modelo.Usuario;

public class Mantenimiento_Usuario extends ConexionBD {
    private PreparedStatement pst;

    // Los vendedores se crean con rol 2; el administrador inicial viene del SQL.
    public boolean registro(Usuario u) {
        String sql = "INSERT INTO usuario (id_rol, nombre, usuario, contrasena) VALUES (2, ?, ?, ?)";
        try {
            pst = conexion().prepareStatement(sql);
            pst.setString(1, u.getNombre());
            pst.setString(2, u.getUsuario());
            pst.setString(3, u.getContrasena());
            return pst.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean validar(String usuario, String contrasena) {
        String sql = "SELECT id_usuario FROM usuario WHERE usuario = ? AND contrasena = ? AND estado = TRUE";
        try {
            pst = conexion().prepareStatement(sql);
            pst.setString(1, usuario);
            pst.setString(2, contrasena);
            data = pst.executeQuery();
            return data.next();
        } catch (SQLException e) {
            System.out.println("Error al validar usuario: " + e.getMessage());
            return false;
        }
    }

    public int obtenerId(String usuario) {
        String sql = "SELECT id_usuario FROM usuario WHERE usuario = ? AND estado = TRUE";
        try {
            pst = conexion().prepareStatement(sql);
            pst.setString(1, usuario);
            data = pst.executeQuery();
            if (data.next()) return data.getInt("id_usuario");
        } catch (SQLException e) {
            System.out.println("Error al obtener usuario: " + e.getMessage());
        }
        return 0;
    }

    public int obtenerRol(String usuario) {
        String sql = "SELECT id_rol FROM usuario WHERE usuario = ? AND estado = TRUE";
        try {
            pst = conexion().prepareStatement(sql);
            pst.setString(1, usuario);
            data = pst.executeQuery();
            if (data.next()) return data.getInt("id_rol");
        } catch (SQLException e) {
            System.out.println("Error al obtener rol: " + e.getMessage());
        }
        return 0;
    }

    public ResultSet consultaUsuarios() {
        try {
            pst = conexion().prepareStatement(
                "SELECT id_usuario, nombre, usuario, id_rol, estado FROM usuario ORDER BY id_usuario"
            );
            data = pst.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error en la consulta de usuarios: " + e.getMessage());
        }
        return data;
    }

    public void cerrar() {
        try {
            if (data != null) data.close();
            if (pst != null) pst.close();
            if (sen != null) sen.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.out.println("Error al cerrar conexión: " + e.getMessage());
        }
    }
}
