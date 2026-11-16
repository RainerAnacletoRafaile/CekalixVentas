package datos;

import java.sql.*;
import modelo.Cliente;

public class Mantenimiento_Cliente extends Libreria {
    private PreparedStatement pst;

    public boolean registro(Cliente c) {
        String sql = "INSERT INTO cliente (razon_social, ruc, telefono, direccion) VALUES (?, ?, ?, ?)";
        try {
            pst = conexion().prepareStatement(sql);
            pst.setString(1, c.getRazon_social());
            pst.setString(2, c.getRuc());
            pst.setString(3, c.getTelefono());
            pst.setString(4, c.getDireccion());
            return pst.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Error al registrar cliente: " + e.getMessage());
            return false;
        }
    }

    public ResultSet consulta(String sql) {
        try {
            sen = conexion().createStatement();
            data = sen.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("Error en la consulta: " + e.getMessage());
        }
        return data;
    }

    public ResultSet busqueda(String ruc) {
        try {
            pst = conexion().prepareStatement("SELECT * FROM cliente WHERE ruc = ?");
            pst.setString(1, ruc);
            data = pst.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error al buscar cliente: " + e.getMessage());
        }
        return data;
    }

    public boolean actualizar(Cliente c) {
        String sql = "UPDATE cliente SET razon_social=?, ruc=?, telefono=?, direccion=? WHERE id_cliente=?";
        try {
            pst = conexion().prepareStatement(sql);
            pst.setString(1, c.getRazon_social());
            pst.setString(2, c.getRuc());
            pst.setString(3, c.getTelefono());
            pst.setString(4, c.getDireccion());
            pst.setInt(5, c.getId_cliente());
            return pst.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Error al actualizar cliente: " + e.getMessage());
            return false;
        }
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
