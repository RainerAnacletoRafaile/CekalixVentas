package modelo;

import java.math.BigDecimal;

public class Producto {

    // Atributos de la tabla producto
    private int id_producto;
    private String nombre;
    private BigDecimal precio;
    private int stock;

    // Constructor vacío
    public Producto() {
    }

    // Constructor con todos los atributos
    public Producto(int id_producto, String nombre,
                    BigDecimal precio, int stock) {

        this.id_producto = id_producto;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

    // Getters y Setters
    public int getId_producto() {
        return id_producto;
    }

    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
