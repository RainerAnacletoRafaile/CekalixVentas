package datos;

import java.sql.*;

public class Libreria {

    public String driver = "com.mysql.cj.jdbc.Driver";
    public String cadena = "jdbc:mysql://localhost:3306/cekalix";
    public String usuario = "root";
    public String clave = "";

    public Connection conn;
    public Statement sen;
    public ResultSet data;

    public Connection conexion() {
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(cadena, usuario, clave);
            sen = conn.createStatement();

        } catch (ClassNotFoundException e1) {
            System.out.println("Error en el driver " + e1);

        } catch (SQLException e2) {
            System.out.println("Error en la conexion " + e2);
        }

        return conn;
    }
}
