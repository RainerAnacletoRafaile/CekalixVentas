package datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Fábrica centralizada de conexiones JDBC para la capa de datos.
 * Cada DAO administra sus recursos con try-with-resources.
 */
public final class ConexionBD {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/cekalix";
    private static final String USUARIO = "root";
    private static final String CLAVE = "";

    private ConexionBD() {
        // Clase utilitaria: no se instancia.
    }

    public static Connection obtenerConexion() throws SQLException {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver JDBC de MySQL.", e);
        }

        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }
}
