package datos;

import java.util.ArrayList;
import java.sql.*;

import modelo.Venta;
import modelo.Producto;

public class Mantenimiento_Venta extends ConexionBD {

    ArrayList<Producto> productos = new ArrayList<>();
    ArrayList<Integer> cantidades = new ArrayList<>();

    double subtotal;
    double igv;
    double total;

    // Agregar producto a la venta
    public void agregarProducto(Producto p, int cantidad) {

        productos.add(p);
        cantidades.add(cantidad);

        calcular();

        System.out.println("Producto agregado a la venta");
    }

    // Listar productos de la venta
    public void listado() {

        if (productos.isEmpty()) {

            System.out.println("No existen productos en la venta");

        } else {

            for (int i = 0; i < productos.size(); i++) {

                System.out.println("Producto: " + productos.get(i).getNombre());

                System.out.println("Cantidad: " + cantidades.get(i));
            }
        }
    }

    // Calcular subtotal, IGV y total
    public void calcular() {

        subtotal = 0;

        for (int i = 0; i < productos.size(); i++) {

            subtotal = subtotal +
                productos.get(i).getPrecio().doubleValue() * cantidades.get(i);
        }

        subtotal = Math.round(subtotal * 100.0) / 100.0;

        igv = Math.round((subtotal * 0.18) * 100.0) / 100.0;

        total = Math.round((subtotal + igv) * 100.0) / 100.0;
    }

    // Obtener productos
    public ArrayList<Producto> getProductos() {
        return productos;
    }

    // Obtener cantidades
    public ArrayList<Integer> getCantidades() {
        return cantidades;
    }

    // Obtener subtotal
    public double getSubtotal() {
        return subtotal;
    }

    // Obtener IGV
    public double getIgv() {
        return igv;
    }

    // Obtener total
    public double getTotal() {
        return total;
    }

    // Registrar venta
    public boolean registro(Venta v) {

        if (productos.isEmpty()) {
            System.out.println("No existen productos para registrar");
            return false;
        }

        for (int i = 0; i < cantidades.size(); i++) {

            if (cantidades.get(i) <= 0) {
                System.out.println("Cantidad incorrecta");
                return false;
            }
        }

        try {

            conexion();

            conn.setAutoCommit(false);

            calcular();

            String sql = "INSERT INTO venta "
                       + "(id_cliente, id_usuario, fecha, total) "
                       + "VALUES (?, ?, ?, ?)";

            PreparedStatement pst = conn.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS
            );

            pst.setInt(1, v.getId_cliente());
            pst.setInt(2, v.getId_usuario());
            pst.setDate(3, Date.valueOf(v.getFecha()));
            pst.setBigDecimal(4,
                java.math.BigDecimal.valueOf(total));

            pst.executeUpdate();

            ResultSet resultado = pst.getGeneratedKeys();

            if (!resultado.next()) {
                throw new SQLException("No se obtuvo el ID de la venta");
            }

            int idVenta = resultado.getInt(1);

            resultado.close();
            pst.close();

            for (int i = 0; i < productos.size(); i++) {

                Producto p = productos.get(i);

                int cantidad = cantidades.get(i);

                String sqlDetalle = "INSERT INTO detalle_venta "
                                  + "(id_venta, id_producto, cantidad, "
                                  + "precio_unitario, subtotal) "
                                  + "VALUES (?, ?, ?, ?, ?)";

                PreparedStatement pstDetalle =
                    conn.prepareStatement(sqlDetalle);

                double precio = p.getPrecio().doubleValue();

                double importe = Math.round(
                    precio * cantidad * 100.0
                ) / 100.0;

                pstDetalle.setInt(1, idVenta);
                pstDetalle.setInt(2, p.getId_producto());
                pstDetalle.setInt(3, cantidad);

                pstDetalle.setBigDecimal(4, p.getPrecio());

                pstDetalle.setBigDecimal(5,
                    java.math.BigDecimal.valueOf(importe));

                pstDetalle.executeUpdate();

                pstDetalle.close();

                String sqlStock = "UPDATE producto "
                                + "SET stock = stock - ? "
                                + "WHERE id_producto = ? "
                                + "AND stock >= ?";

                PreparedStatement pstStock =
                    conn.prepareStatement(sqlStock);

                pstStock.setInt(1, cantidad);
                pstStock.setInt(2, p.getId_producto());
                pstStock.setInt(3, cantidad);

                int filas = pstStock.executeUpdate();

                pstStock.close();

                if (filas == 0) {

                    throw new SQLException("Stock insuficiente");
                }
            }

            conn.commit();

            conn.setAutoCommit(true);

            System.out.println("Venta registrada correctamente");

            return true;

        } catch (SQLException e) {

            if (conn != null) {

                try {

                    conn.rollback();

                    conn.setAutoCommit(true);

                } catch (SQLException error) {

                    System.out.println("Error al cancelar la venta " + error);
                }
            }

            System.out.println("Error al registrar venta " + e);

            return false;
        }
    }

    // Consultar ventas
    public ResultSet consulta(String sql) {

        try {

            conexion();

            data = sen.executeQuery(sql);

        } catch (SQLException e) {

            System.out.println("Error en la consulta " + e);
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
