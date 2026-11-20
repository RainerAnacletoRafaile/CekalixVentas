package modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Venta {

    private int id_venta;
    private int id_cliente;
    private int id_usuario;
    private LocalDate fecha;
    private BigDecimal total;
    private String estado;

    public Venta() {
    }

    public Venta(int id_venta, int id_cliente, int id_usuario,
                 LocalDate fecha, BigDecimal total, String estado) {
        this.id_venta = id_venta;
        this.id_cliente = id_cliente;
        this.id_usuario = id_usuario;
        this.fecha = fecha;
        this.total = total;
        this.estado = estado;
    }

    public int getId_venta() { return id_venta; }
    public void setId_venta(int id_venta) { this.id_venta = id_venta; }
    public int getId_cliente() { return id_cliente; }
    public void setId_cliente(int id_cliente) { this.id_cliente = id_cliente; }
    public int getId_usuario() { return id_usuario; }
    public void setId_usuario(int id_usuario) { this.id_usuario = id_usuario; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
