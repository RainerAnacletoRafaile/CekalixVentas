package modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Productos y cantidades de una orden de venta en preparación.
 * Las cantidades representan cajas solicitadas por SKU.
 */
public class CarritoVenta {

    private final List<Producto> productos = new ArrayList<>();
    private final List<Integer> cantidades = new ArrayList<>();

    public void agregarProducto(Producto producto, int cantidad) {
        if (producto == null || cantidad <= 0) {
            throw new IllegalArgumentException("Producto y cantidad deben ser válidos.");
        }

        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).getId_producto() == producto.getId_producto()) {
                cantidades.set(i, cantidades.get(i) + cantidad);
                return;
            }
        }

        productos.add(producto);
        cantidades.add(cantidad);
    }

    public List<Producto> getProductos() {
        return Collections.unmodifiableList(productos);
    }

    public List<Integer> getCantidades() {
        return Collections.unmodifiableList(cantidades);
    }

    public boolean estaVacio() {
        return productos.isEmpty();
    }

    public BigDecimal getPrecioAplicado(int indice) {
        Producto producto = productos.get(indice);
        int cantidad = cantidades.get(indice);
        BigDecimal descuento = obtenerPorcentajeDescuento(cantidad);
        return producto.getPrecio()
            .multiply(BigDecimal.ONE.subtract(descuento))
            .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getSubtotalBruto() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (int i = 0; i < productos.size(); i++) {
            subtotal = subtotal.add(
                productos.get(i).getPrecio().multiply(BigDecimal.valueOf(cantidades.get(i)))
            );
        }
        return subtotal.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getDescuento() {
        return getSubtotalBruto().subtract(getSubtotal()).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getSubtotal() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (int i = 0; i < productos.size(); i++) {
            subtotal = subtotal.add(
                getPrecioAplicado(i).multiply(BigDecimal.valueOf(cantidades.get(i)))
            );
        }
        return subtotal.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotal() {
        return getSubtotal();
    }

    public static BigDecimal obtenerPorcentajeDescuento(int cajas) {
        if (cajas >= 50) return new BigDecimal("0.12");
        if (cajas >= 10) return new BigDecimal("0.05");
        return BigDecimal.ZERO;
    }
}
