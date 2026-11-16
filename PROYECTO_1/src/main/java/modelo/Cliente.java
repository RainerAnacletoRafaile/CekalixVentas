package modelo;

public class Cliente {

    // Atributos de la tabla cliente
    private int id_cliente;
    private String razon_social;
    private String ruc;
    private String telefono;

    // Constructor vacío
    public Cliente() {
    }

    // Constructor con todos los atributos
    public Cliente(int id_cliente, String razon_social,
                   String ruc, String telefono) {

        this.id_cliente = id_cliente;
        this.razon_social = razon_social;
        this.ruc = ruc;
        this.telefono = telefono;
    }

    // Getters y Setters
    public int getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getRazon_social() {
        return razon_social;
    }

    public void setRazon_social(String razon_social) {
        this.razon_social = razon_social;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
