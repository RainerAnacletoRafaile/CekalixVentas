package datos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import modelo.Producto;

public class Mantenimiento_Producto extends ConexionBD {

    public Producto buscarPorId(int idProducto) {
        String sql = "SELECT id_producto, nombre, precio, stock " +
                     "FROM producto WHERE id_producto = ?";

        try (Connection cn = conexion();
             PreparedStatement pst = cn.prepareStatement(sql)) {
            pst.setInt(1, idProducto);

            try (ResultSet rs = pst.executeQuery()) {
                if (!rs.next()) return null;

                return new Producto(
                    rs.getInt("id_producto"),
                    rs.getString("nombre"),
                    rs.getBigDecimal("precio"),
                    rs.getInt("stock")
                );
            }
        } catch (SQLException e) {
            System.err.println("No se pudo buscar el producto: " + e.getMessage());
            return null;
        }
    }

    // Se conserva para las vistas que consultan el catálogo mediante ResultSet.
    public ResultSet consulta(String sql) {
        try {
            sen = conexion().createStatement();
            data = sen.executeQuery(sql);
        } catch (SQLException e) {
            System.err.println("No se pudo consultar productos: " + e.getMessage());
        }
        return data;
    }

    public ResultSet busqueda(String nombre) {
        try {
            String sql = "SELECT * FROM producto WHERE nombre LIKE ?";
            conn = conexion();
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, "%" + nombre + "%");
            data = pst.executeQuery();
        } catch (SQLException e) {
            System.err.println("No se pudo buscar el producto: " + e.getMessage());
        }
        return data;
    }
}
