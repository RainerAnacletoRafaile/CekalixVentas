package datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Centraliza la creación de conexiones JDBC para la capa de datos.
 * Las consultas y transacciones pertenecen a cada clase DAO.
 */
public class ConexionBD {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/cekalix";
    private static final String USUARIO = "root";
    private static final String CLAVE = "";

    protected Connection conn;

    protected Connection conexion() throws SQLException {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver JDBC de MySQL.", e);
        }

        conn = DriverManager.getConnection(URL, USUARIO, CLAVE);
        return conn;
    }
}
