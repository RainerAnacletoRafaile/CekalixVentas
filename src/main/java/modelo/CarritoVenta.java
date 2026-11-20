package modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Carrito temporal de una venta. No contiene conexiones ni lógica JDBC.
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

    public BigDecimal getSubtotal() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (int i = 0; i < productos.size(); i++) {
            subtotal = subtotal.add(
                productos.get(i).getPrecio().multiply(BigDecimal.valueOf(cantidades.get(i)))
            );
        }
        return subtotal.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getIgv() {
        return getSubtotal().multiply(new BigDecimal("0.18"))
            .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotal() {
        return getSubtotal().add(getIgv()).setScale(2, RoundingMode.HALF_UP);
    }
}
