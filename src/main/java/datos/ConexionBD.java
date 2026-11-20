package datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Centraliza la configuración y apertura de conexiones JDBC.
 * Las operaciones SQL y las transacciones pertenecen a cada clase DAO.
 */
public class ConexionBD {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/cekalix";
    private static final String USUARIO = "root";
    private static final String CLAVE = "";

    protected Connection conn;
    protected Statement sen;
    protected ResultSet data;

    protected Connection conexion() throws SQLException {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver JDBC de MySQL.", e);
        }

        conn = DriverManager.getConnection(URL, USUARIO, CLAVE);
        sen = conn.createStatement();
        return conn;
    }

    public void cerrar() {
        try {
            if (data != null) data.close();
        } catch (SQLException e) {
            System.err.println("Error al cerrar ResultSet: " + e.getMessage());
        }

        try {
            if (sen != null) sen.close();
        } catch (SQLException e) {
            System.err.println("Error al cerrar Statement: " + e.getMessage());
        }

        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println("Error al cerrar conexión: " + e.getMessage());
        } finally {
            data = null;
            sen = null;
            conn = null;
        }
    }
}
