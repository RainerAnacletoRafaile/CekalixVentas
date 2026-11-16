package controlador;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import datos.Mantenimiento_Cliente;
import modelo.Cliente;

@WebServlet("/Proceso_Cliente")
public class Proceso_Cliente extends HttpServlet {
    private boolean autenticado(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession sesion = request.getSession(false);
        if (sesion == null || sesion.getAttribute("usuario") == null) {
            response.sendRedirect("login.jsp");
            return false;
        }
        return true;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!autenticado(request, response)) return;
        String accion = request.getParameter("accion");
        Mantenimiento_Cliente dao = new Mantenimiento_Cliente();
        try {
            Cliente c = new Cliente();
            c.setRazon_social(request.getParameter("razon_social"));
            c.setRuc(request.getParameter("ruc"));
            c.setTelefono(request.getParameter("telefono"));
            c.setDireccion(request.getParameter("direccion"));
            boolean ok = false;
            if ("registrar".equals(accion)) ok = dao.registro(c);
            else if ("actualizar".equals(accion)) {
                c.setId_cliente(Integer.parseInt(request.getParameter("id_cliente")));
                ok = dao.actualizar(c);
            }
            if (ok && "registrar".equals(accion)) response.sendRedirect("registro_cliente.jsp?correcto=1");
            else if (ok) response.sendRedirect("Proceso_Cliente?actualizado=1");
            else response.sendRedirect(("registrar".equals(accion) ? "registro_cliente.jsp" : "Proceso_Cliente") + "?error=1");
        } catch (NumberFormatException e) {
            response.sendRedirect("Proceso_Cliente?error=1");
        } finally {
            dao.cerrar();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!autenticado(request, response)) return;
        String ruc = request.getParameter("ruc");
        Mantenimiento_Cliente dao = new Mantenimiento_Cliente();
        ResultSet rs = (ruc == null || ruc.trim().isEmpty())
                ? dao.consulta("SELECT * FROM cliente ORDER BY razon_social")
                : dao.busqueda(ruc.trim());
        ArrayList<Cliente> clientes = new ArrayList<>();
        try {
            while (rs != null && rs.next()) {
                Cliente c = new Cliente();
                c.setId_cliente(rs.getInt("id_cliente"));
                c.setRazon_social(rs.getString("razon_social"));
                c.setRuc(rs.getString("ruc"));
                c.setTelefono(rs.getString("telefono"));
                c.setDireccion(rs.getString("direccion"));
                clientes.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar clientes: " + e.getMessage());
        } finally {
            dao.cerrar();
        }
        request.setAttribute("clientes", clientes);
        request.getRequestDispatcher("consulta_cliente.jsp").forward(request, response);
    }
}
