package datos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import modelo.Cliente;

public class Mantenimiento_Cliente {

    public boolean registro(Cliente cliente) {
        String sql = "INSERT INTO cliente (razon_social, ruc, telefono, direccion) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection cn = conexion();
             PreparedStatement pst = cn.prepareStatement(sql)) {
            pst.setString(1, cliente.getRazon_social());
            pst.setString(2, cliente.getRuc());
            pst.setString(3, cliente.getTelefono());
            pst.setString(4, cliente.getDireccion());
            return pst.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("No se pudo registrar el cliente: " + e.getMessage());
            return false;
        }
    }

    public List<Cliente> listar() {
        String sql = "SELECT id_cliente, razon_social, ruc, telefono, direccion " +
                     "FROM cliente ORDER BY razon_social";
        List<Cliente> clientes = new ArrayList<>();

        try (Connection cn = conexion();
             PreparedStatement pst = cn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) clientes.add(mapearCliente(rs));
        } catch (SQLException e) {
            System.err.println("No se pudieron consultar los clientes: " + e.getMessage());
        }

        return clientes;
    }

    public Cliente buscarPorRuc(String ruc) {
        String sql = "SELECT id_cliente, razon_social, ruc, telefono, direccion " +
                     "FROM cliente WHERE ruc = ?";

        try (Connection cn = conexion();
             PreparedStatement pst = cn.prepareStatement(sql)) {
            pst.setString(1, ruc);

            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() ? mapearCliente(rs) : null;
            }
        } catch (SQLException e) {
            System.err.println("No se pudo buscar el cliente: " + e.getMessage());
            return null;
        }
    }

    public boolean actualizar(Cliente cliente) {
        String sql = "UPDATE cliente SET razon_social = ?, ruc = ?, telefono = ?, " +
                     "direccion = ? WHERE id_cliente = ?";

        try (Connection cn = conexion();
             PreparedStatement pst = cn.prepareStatement(sql)) {
            pst.setString(1, cliente.getRazon_social());
            pst.setString(2, cliente.getRuc());
            pst.setString(3, cliente.getTelefono());
            pst.setString(4, cliente.getDireccion());
            pst.setInt(5, cliente.getId_cliente());
            return pst.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("No se pudo actualizar el cliente: " + e.getMessage());
            return false;
        }
    }

    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId_cliente(rs.getInt("id_cliente"));
        cliente.setRazon_social(rs.getString("razon_social"));
        cliente.setRuc(rs.getString("ruc"));
        cliente.setTelefono(rs.getString("telefono"));
        cliente.setDireccion(rs.getString("direccion"));
        return cliente;
    }
}
