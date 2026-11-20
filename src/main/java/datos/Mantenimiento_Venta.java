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
                validarEstado(cn, idVenta, "Pendiente");
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
                actualizarEstado(cn, idVenta, "Pendiente", "Confirmada", null);
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

    public boolean despachar(int idVenta) {
        String sqlDetalles = "SELECT id_producto, cantidad FROM detalle_venta WHERE id_venta = ?";
        String sqlDescontar = "UPDATE producto SET stock_fisico = stock_fisico - ?, " +
            "stock_reservado = stock_reservado - ? " +
            "WHERE id_producto = ? AND stock_reservado >= ? AND stock_fisico >= ?";
        try (Connection cn = ConexionBD.obtenerConexion()) {
            cn.setAutoCommit(false);
            try {
                validarEstado(cn, idVenta, "Confirmada");
                try (PreparedStatement detalles = cn.prepareStatement(sqlDetalles);
                     PreparedStatement descontar = cn.prepareStatement(sqlDescontar)) {
                    detalles.setInt(1, idVenta);
                    try (ResultSet rs = detalles.executeQuery()) {
                        boolean hayDetalles = false;
                        while (rs.next()) {
                            hayDetalles = true;
                            int cantidad = rs.getInt("cantidad");
                            int idProducto = rs.getInt("id_producto");
                            descontar.setInt(1, cantidad);
                            descontar.setInt(2, cantidad);
                            descontar.setInt(3, idProducto);
                            descontar.setInt(4, cantidad);
                            descontar.setInt(5, cantidad);
                            if (descontar.executeUpdate() != 1) {
                                throw new SQLException("No se pudo descontar el stock reservado.");
                            }
                        }
                        if (!hayDetalles) throw new SQLException("La orden no contiene productos.");
                    }
                }
                actualizarEstado(cn, idVenta, "Confirmada", "Despachado", null);
                cn.commit();
                return true;
            } catch (SQLException e) {
                cn.rollback();
                System.err.println("No se pudo despachar la orden: " + e.getMessage());
                return false;
            } finally {
                cn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("No se pudo despachar la orden: " + e.getMessage());
            return false;
        }
    }

    public boolean anular(int idVenta, String motivo) {
        if (motivo == null || motivo.isBlank()) return false;
        String sqlEstado = "SELECT estado FROM venta WHERE id_venta = ? FOR UPDATE";
        String sqlDetalles = "SELECT id_producto, cantidad FROM detalle_venta WHERE id_venta = ?";
        String sqlLiberar = "UPDATE producto SET stock_reservado = stock_reservado - ? " +
            "WHERE id_producto = ? AND stock_reservado >= ?";
        try (Connection cn = ConexionBD.obtenerConexion()) {
            cn.setAutoCommit(false);
            try {
                String estado;
                try (PreparedStatement pst = cn.prepareStatement(sqlEstado)) {
                    pst.setInt(1, idVenta);
                    try (ResultSet rs = pst.executeQuery()) {
                        if (!rs.next()) throw new SQLException("La orden no existe.");
                        estado = rs.getString("estado");
                    }
                }
                if (!"Pendiente".equalsIgnoreCase(estado) &&
                    !"Confirmada".equalsIgnoreCase(estado)) {
                    throw new SQLException("Solo se pueden anular órdenes Pendientes o Confirmadas.");
                }

                if ("Confirmada".equalsIgnoreCase(estado)) {
                    try (PreparedStatement detalles = cn.prepareStatement(sqlDetalles);
                         PreparedStatement liberar = cn.prepareStatement(sqlLiberar)) {
                        detalles.setInt(1, idVenta);
                        try (ResultSet rs = detalles.executeQuery()) {
                            while (rs.next()) {
                                int cantidad = rs.getInt("cantidad");
                                liberar.setInt(1, cantidad);
                                liberar.setInt(2, rs.getInt("id_producto"));
                                liberar.setInt(3, cantidad);
                                if (liberar.executeUpdate() != 1) {
                                    throw new SQLException("No se pudo liberar el stock reservado.");
                                }
                            }
                        }
                    }
                }

                try (PreparedStatement pst = cn.prepareStatement(
                        "UPDATE venta SET estado = 'Anulado', motivo_anulacion = ? " +
                        "WHERE id_venta = ? AND estado IN ('Pendiente', 'Confirmada')")) {
                    pst.setString(1, motivo.trim());
                    pst.setInt(2, idVenta);
                    if (pst.executeUpdate() != 1) throw new SQLException("No se pudo anular la orden.");
                }
                cn.commit();
                return true;
            } catch (SQLException e) {
                cn.rollback();
                System.err.println("No se pudo anular la orden: " + e.getMessage());
                return false;
            } finally {
                cn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("No se pudo anular la orden: " + e.getMessage());
            return false;
        }
    }

    private void validarEstado(Connection cn, int idVenta, String estadoEsperado)
            throws SQLException {
        try (PreparedStatement pst = cn.prepareStatement(
                "SELECT estado FROM venta WHERE id_venta = ? FOR UPDATE")) {
            pst.setInt(1, idVenta);
            try (ResultSet rs = pst.executeQuery()) {
                if (!rs.next() || !estadoEsperado.equalsIgnoreCase(rs.getString("estado"))) {
                    throw new SQLException("La orden no existe o no está en estado " + estadoEsperado + ".");
                }
            }
        }
    }

    private void actualizarEstado(Connection cn, int idVenta, String estadoActual,
                                  String estadoNuevo, String motivo) throws SQLException {
        try (PreparedStatement pst = cn.prepareStatement(
                "UPDATE venta SET estado = ? WHERE id_venta = ? AND estado = ?")) {
            pst.setString(1, estadoNuevo);
            pst.setInt(2, idVenta);
            pst.setString(3, estadoActual);
            if (pst.executeUpdate() != 1) throw new SQLException("No se pudo actualizar el estado de la orden.");
        }
    }

    public List<Venta> listar() {
        String sql = "SELECT v.id_venta, v.id_cliente, v.id_usuario, c.razon_social, c.ruc, " +
            "v.fecha, v.total, v.estado, v.motivo_anulacion FROM venta v " +
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
                venta.setMotivo_anulacion(rs.getString("motivo_anulacion"));
                ventas.add(venta);
            }
        } catch (SQLException e) {
            System.err.println("No se pudieron consultar las órdenes de venta: " + e.getMessage());
        }
        return ventas;
    }
}
