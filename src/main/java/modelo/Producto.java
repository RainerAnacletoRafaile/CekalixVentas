package modelo;

import java.math.BigDecimal;

public class Producto {

    private int id_producto;
    private String sku;
    private String nombre;
    private String medida;
    private String grosor;
    private String color;
    private BigDecimal precio;
    private int unidades_por_caja;
    private int stock_fisico;
    private int stock_reservado;

    public Producto() {
    }

    public Producto(int id_producto, String sku, String nombre, String medida,
                    String grosor, String color, BigDecimal precio,
                    int unidades_por_caja, int stock_fisico, int stock_reservado) {
        this.id_producto = id_producto;
        this.sku = sku;
        this.nombre = nombre;
        this.medida = medida;
        this.grosor = grosor;
        this.color = color;
        this.precio = precio;
        this.unidades_por_caja = unidades_por_caja;
        this.stock_fisico = stock_fisico;
        this.stock_reservado = stock_reservado;
    }

    public int getId_producto() { return id_producto; }
    public void setId_producto(int id_producto) { this.id_producto = id_producto; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getMedida() { return medida; }
    public void setMedida(String medida) { this.medida = medida; }
    public String getGrosor() { return grosor; }
    public void setGrosor(String grosor) { this.grosor = grosor; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public int getUnidades_por_caja() { return unidades_por_caja; }
    public void setUnidades_por_caja(int unidades_por_caja) { this.unidades_por_caja = unidades_por_caja; }
    public int getStock_fisico() { return stock_fisico; }
    public void setStock_fisico(int stock_fisico) { this.stock_fisico = stock_fisico; }
    public int getStock_reservado() { return stock_reservado; }
    public void setStock_reservado(int stock_reservado) { this.stock_reservado = stock_reservado; }

    public int getStock_disponible() {
        return stock_fisico - stock_reservado;
    }

    public int getUnidades_disponibles() {
        return getStock_disponible() * unidades_por_caja;
    }
}
