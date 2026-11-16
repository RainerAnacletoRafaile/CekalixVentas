package datos;

import java.sql.*;
import modelo.Cliente;

public class Mantenimiento_Cliente extends Libreria {

    PreparedStatement pst;

    // Registrar cliente
    public boolean registro(Cliente c) {
        try {
            String sql = "INSERT INTO cliente "
                       + "(razon_social, ruc, telefono) "
                       + "VALUES (?, ?, ?)";

            pst = conexion().prepareStatement(sql);

            pst.setString(1, c.getRazon_social());
            pst.setString(2, c.getRuc());
            pst.setString(3, c.getTelefono());

            pst.executeUpdate();

            return true;

        } catch (SQLException e) {
            System.out.println("Error al registrar cliente " + e);
            return false;
        }
    }

    // Consultar clientes
    public ResultSet consulta(String sql) {
        try {
            sen = conexion().createStatement();
            data = sen.executeQuery(sql);

        } catch (SQLException e) {
            System.out.println("Error en la consulta " + e);
        }

        return data;
    }

    // Buscar cliente por RUC
    public ResultSet busqueda(String ruc) {
        try {
            String sql = "SELECT * FROM cliente WHERE ruc = ?";

            pst = conexion().prepareStatement(sql);
            pst.setString(1, ruc);

            data = pst.executeQuery();

        } catch (SQLException e) {
            System.out.println("Error al buscar cliente " + e);
        }

        return data;
    }

    // Actualizar cliente
    public boolean actualizar(Cliente c) {
        try {
            String sql = "UPDATE cliente SET "
                       + "razon_social = ?, ruc = ?, telefono = ? "
                       + "WHERE id_cliente = ?";

            pst = conexion().prepareStatement(sql);

            pst.setString(1, c.getRazon_social());
            pst.setString(2, c.getRuc());
            pst.setString(3, c.getTelefono());
            pst.setInt(4, c.getId_cliente());

            pst.executeUpdate();

            return true;

        } catch (SQLException e) {
            System.out.println("Error al actualizar cliente " + e);
            return false;
        }
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
