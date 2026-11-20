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
        "INSERT INTO venta (id_cliente, id_usuario, fecha, total, estado) " +
        "VALUES (?, ?, ?, ?, 'Pendiente')";

    private static final String SQL_REGISTRAR_DETALLE =
        "INSERT INTO detalle_venta (id_venta, id_producto, cantidad, precio_unitario, subtotal) " +
        "VALUES (?, ?, ?, ?, ?)";

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
                        if (!keys.next()) throw new SQLException("No se generó el ID de la venta.");
                        idVenta = keys.getInt(1);
                    }
                }

                try (PreparedStatement detalle = cn.prepareStatement(SQL_REGISTRAR_DETALLE)) {
                    List<Producto> productos = carrito.getProductos();
                    List<Integer> cantidades = carrito.getCantidades();
                    for (int i = 0; i < productos.size(); i++) {
                        int cantidad = cantidades.get(i);
                        if (cantidad <= 0) throw new SQLException("La cantidad de cajas debe ser mayor que cero.");
                        BigDecimal precioAplicado = carrito.getPrecioAplicado(i);
                        BigDecimal subtotal = precioAplicado.multiply(BigDecimal.valueOf(cantidad));
                        detalle.setInt(1, idVenta);
                        detalle.setInt(2, productos.get(i).getId_producto());
                        detalle.setInt(3, cantidad);
                        detalle.setBigDecimal(4, precioAplicado);
                        detalle.setBigDecimal(5, subtotal);
                        detalle.addBatch();
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
            System.err.println("No se pudo registrar la orden de venta: " + e.getMessage());
            return false;
        }
    }

    public boolean confirmar(int idVenta) {
        String sqlEstado = "SELECT estado FROM venta WHERE id_venta = ? FOR UPDATE";
        String sqlDetalles = "SELECT id_producto, cantidad FROM detalle_venta WHERE id_venta = ?";
        String sqlReservar = "UPDATE producto SET stock_reservado = stock_reservado + ? " +
            "WHERE id_producto = ? AND (stock_fisico - stock_reservado) >= ?";
        String sqlConfirmar = "UPDATE venta SET estado = 'Confirmada' " +
            "WHERE id_venta = ? AND estado = 'Pendiente'";

        try (Connection cn = ConexionBD.obtenerConexion()) {
            cn.setAutoCommit(false);
            try {
                try (PreparedStatement estado = cn.prepareStatement(sqlEstado)) {
                    estado.setInt(1, idVenta);
                    try (ResultSet rs = estado.executeQuery()) {
                        if (!rs.next() || !"Pendiente".equalsIgnoreCase(rs.getString("estado"))) {
                            throw new SQLException("La orden no existe o ya fue procesada.");
                        }
                    }
                }

                try (PreparedStatement detalles = cn.prepareStatement(sqlDetalles);
                     PreparedStatement reservar = cn.prepareStatement(sqlReservar)) {
                    detalles.setInt(1, idVenta);
                    try (ResultSet rs = detalles.executeQuery()) {
                        boolean hayDetalles = false;
                        while (rs.next()) {
                            hayDetalles = true;
                            int cantidad = rs.getInt("cantidad");
                            reservar.setInt(1, cantidad);
                            reservar.setInt(2, rs.getInt("id_producto"));
                            reservar.setInt(3, cantidad);
                            if (reservar.executeUpdate() != 1) {
                                throw new SQLException("Stock disponible insuficiente para uno de los productos.");
                            }
                        }
                        if (!hayDetalles) throw new SQLException("La orden no contiene productos.");
                    }
                }

                try (PreparedStatement confirmar = cn.prepareStatement(sqlConfirmar)) {
                    confirmar.setInt(1, idVenta);
                    if (confirmar.executeUpdate() != 1) throw new SQLException("No se pudo confirmar la orden.");
                }
                cn.commit();
                return true;
            } catch (SQLException e) {
                cn.rollback();
                System.err.println("No se pudo confirmar la orden: " + e.getMessage());
                return false;
            } finally {
                cn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("No se pudo confirmar la orden: " + e.getMessage());
            return false;
        }
    }

    public List<Venta> listar() {
        String sql = "SELECT v.id_venta, v.id_cliente, v.id_usuario, c.razon_social, c.ruc, " +
            "v.fecha, v.total, v.estado FROM venta v " +
            "INNER JOIN cliente c ON c.id_cliente = v.id_cliente ORDER BY v.id_venta DESC";
        List<Venta> ventas = new ArrayList<>();
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement pst = cn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Venta venta = new Venta();
                venta.setId_venta(rs.getInt("id_venta"));
                venta.setId_cliente(rs.getInt("id_cliente"));
                venta.setId_usuario(rs.getInt("id_usuario"));
                venta.setRazon_social(rs.getString("razon_social"));
                venta.setRuc(rs.getString("ruc"));
                venta.setFecha(rs.getDate("fecha").toLocalDate());
                venta.setTotal(rs.getBigDecimal("total"));
                venta.setEstado(rs.getString("estado"));
                ventas.add(venta);
            }
        } catch (SQLException e) {
            System.err.println("No se pudieron consultar las órdenes de venta: " + e.getMessage());
        }
        return ventas;
    }
}
