package datos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import modelo.Producto;

public class Mantenimiento_Producto {

    public List<Producto> listar() {
        String sql = "SELECT id_producto, nombre, precio, stock " +
                     "FROM producto ORDER BY nombre";
        return ejecutarConsulta(sql, null);
    }

    public List<Producto> buscarPorNombre(String nombre) {
        String sql = "SELECT id_producto, nombre, precio, stock " +
                     "FROM producto WHERE nombre LIKE ? ORDER BY nombre";
        return ejecutarConsulta(sql, "%" + nombre.trim() + "%");
    }

    public Producto buscarPorId(int idProducto) {
        String sql = "SELECT id_producto, nombre, precio, stock " +
                     "FROM producto WHERE id_producto = ?";

        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement pst = cn.prepareStatement(sql)) {
            pst.setInt(1, idProducto);

            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() ? mapearProducto(rs) : null;
            }
        } catch (SQLException e) {
            System.err.println("No se pudo buscar el producto: " + e.getMessage());
            return null;
        }
    }

    private List<Producto> ejecutarConsulta(String sql, String parametro) {
        List<Producto> productos = new ArrayList<>();

        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement pst = cn.prepareStatement(sql)) {
            if (parametro != null) pst.setString(1, parametro);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) productos.add(mapearProducto(rs));
            }
        } catch (SQLException e) {
            System.err.println("No se pudieron consultar los productos: " + e.getMessage());
        }

        return productos;
    }

    private Producto mapearProducto(ResultSet rs) throws SQLException {
        return new Producto(
            rs.getInt("id_producto"),
            rs.getString("nombre"),
            rs.getBigDecimal("precio"),
            rs.getInt("stock")
        );
    }
}
