package datos;

import java.sql.*;

public class Mantenimiento_Producto extends Libreria {

    // Consultar productos
    public ResultSet consulta(String sql) {
        try {
            sen = conexion().createStatement();
            data = sen.executeQuery(sql);

        } catch (SQLException e) {
            System.out.println("Error en la consulta " + e);
        }

        return data;
    }

    // Buscar producto por nombre
    public ResultSet busqueda(String nombre) {
        try {
            String sql = "SELECT * FROM producto WHERE nombre LIKE ?";

            PreparedStatement pst = conexion().prepareStatement(sql);

            pst.setString(1, "%" + nombre + "%");

            data = pst.executeQuery();

        } catch (SQLException e) {
            System.out.println("Error al buscar producto " + e);
        }

        return data;
    }

    // Cerrar conexion
    public void cerrar() {
        try {
            if (data != null) data.close();
            if (sen != null) sen.close();
            if (conn != null) conn.close();

        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexion " + e);
        }
    }
}
