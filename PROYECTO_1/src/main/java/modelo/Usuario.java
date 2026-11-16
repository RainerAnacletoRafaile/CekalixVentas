package modelo;

public class Usuario {

    // Atributos de la tabla usuario
    private int id_usuario;
    private String nombre;
    private String usuario;
    private String contrasena;
    private boolean estado;

    // Constructor vacío
    public Usuario() {
    }

    // Constructor con todos los atributos
    public Usuario(int id_usuario, String nombre, String usuario,
                   String contrasena, boolean estado) {

        this.id_usuario = id_usuario;
        this.nombre = nombre;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.estado = estado;
    }

    // Getters y Setters
    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
