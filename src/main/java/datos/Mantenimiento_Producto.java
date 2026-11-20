package datos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import modelo.Producto;

public class Mantenimiento_Producto {

    private static final String COLUMNAS =
        "SELECT id_producto, sku, nombre, medida, grosor, color, precio, " +
        "unidades_por_caja, stock_fisico, stock_reservado FROM producto ";

    public List<Producto> listar() {
        return ejecutarConsulta(COLUMNAS + "ORDER BY nombre", null);
    }

    public List<Producto> buscarPorNombre(String busqueda) {
        String sql = COLUMNAS + "WHERE nombre LIKE ? OR sku LIKE ? ORDER BY nombre";
        List<Producto> productos = new ArrayList<>();
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement pst = cn.prepareStatement(sql)) {
            String parametro = "%" + busqueda.trim() + "%";
            pst.setString(1, parametro);
            pst.setString(2, parametro);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) productos.add(mapearProducto(rs));
            }
        } catch (SQLException e) {
            System.err.println("No se pudieron consultar los productos: " + e.getMessage());
        }
        return productos;
    }

    public Producto buscarPorSku(String sku) {
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement pst = cn.prepareStatement(COLUMNAS + "WHERE sku = ?")) {
            pst.setString(1, sku.trim());
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() ? mapearProducto(rs) : null;
            }
        } catch (SQLException e) {
            System.err.println("No se pudo buscar el producto por SKU: " + e.getMessage());
            return null;
        }
    }

    public Producto buscarPorId(int idProducto) {
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement pst = cn.prepareStatement(COLUMNAS + "WHERE id_producto = ?")) {
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
            rs.getInt("id_producto"), rs.getString("sku"), rs.getString("nombre"),
            rs.getString("medida"), rs.getString("grosor"), rs.getString("color"),
            rs.getBigDecimal("precio"), rs.getInt("unidades_por_caja"),
            rs.getInt("stock_fisico"), rs.getInt("stock_reservado")
        );
    }
}
