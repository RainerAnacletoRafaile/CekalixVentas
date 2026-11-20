package datos;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import modelo.CarritoVenta;
import modelo.Producto;
import modelo.Venta;

public class Mantenimiento_Venta {

    private static final String SQL_REGISTRAR_VENTA =
        "INSERT INTO venta (id_cliente, id_usuario, fecha, total) VALUES (?, ?, ?, ?)";

    private static final String SQL_REGISTRAR_DETALLE =
        "INSERT INTO detalle_venta " +
        "(id_venta, id_producto, cantidad, precio_unitario, subtotal) " +
        "VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR_STOCK =
        "UPDATE producto SET stock = stock - ? WHERE id_producto = ? AND stock >= ?";

    public boolean registro(Venta venta, CarritoVenta carrito) {
        if (venta == null || carrito == null || carrito.estaVacio()) return false;

        try (Connection cn = ConexionBD.obtenerConexion()) {
            cn.setAutoCommit(false);

            try {
                int idVenta;

                try (PreparedStatement pst = cn.prepareStatement(
                        SQL_REGISTRAR_VENTA, Statement.RETURN_GENERATED_KEYS)) {
                    pst.setInt(1, venta.getId_cliente());
                    pst.setInt(2, venta.getId_usuario());
                    pst.setDate(3, Date.valueOf(venta.getFecha()));
                    pst.setBigDecimal(4, carrito.getTotal());
                    pst.executeUpdate();

                    try (ResultSet keys = pst.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("No se generó el identificador de la venta.");
                        }
                        idVenta = keys.getInt(1);
                    }
                }

                try (PreparedStatement detalle = cn.prepareStatement(SQL_REGISTRAR_DETALLE);
                     PreparedStatement stock = cn.prepareStatement(SQL_ACTUALIZAR_STOCK)) {

                    List<Producto> productos = carrito.getProductos();
                    List<Integer> cantidades = carrito.getCantidades();

                    for (int i = 0; i < productos.size(); i++) {
                        Producto producto = productos.get(i);
                        int cantidad = cantidades.get(i);

                        if (cantidad <= 0) {
                            throw new SQLException("La cantidad debe ser mayor que cero.");
                        }

                        BigDecimal importe = producto.getPrecio()
                            .multiply(BigDecimal.valueOf(cantidad));

                        detalle.setInt(1, idVenta);
                        detalle.setInt(2, producto.getId_producto());
                        detalle.setInt(3, cantidad);
                        detalle.setBigDecimal(4, producto.getPrecio());
                        detalle.setBigDecimal(5, importe);
                        detalle.addBatch();

                        stock.setInt(1, cantidad);
                        stock.setInt(2, producto.getId_producto());
                        stock.setInt(3, cantidad);

                        if (stock.executeUpdate() != 1) {
                            throw new SQLException(
                                "Stock insuficiente para el producto: " + producto.getNombre()
                            );
                        }
                    }

                    detalle.executeBatch();
                }

                cn.commit();
                return true;
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            } finally {
                cn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("No se pudo registrar la venta: " + e.getMessage());
            return false;
        }
    }

    public List<Venta> listar() {
        String sql = "SELECT id_venta, id_cliente, id_usuario, fecha, total " +
                     "FROM venta ORDER BY id_venta DESC";
        List<Venta> ventas = new ArrayList<>();

        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement pst = cn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Venta venta = new Venta();
                venta.setId_venta(rs.getInt("id_venta"));
                venta.setId_cliente(rs.getInt("id_cliente"));
                venta.setId_usuario(rs.getInt("id_usuario"));
                venta.setFecha(rs.getDate("fecha").toLocalDate());
                venta.setTotal(rs.getBigDecimal("total"));
                ventas.add(venta);
            }
        } catch (SQLException e) {
            System.err.println("No se pudieron consultar las ventas: " + e.getMessage());
        }

        return ventas;
    }
}
