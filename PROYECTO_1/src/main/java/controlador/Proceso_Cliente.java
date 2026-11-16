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

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);

        if (sesion == null || sesion.getAttribute("usuario") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String accion = request.getParameter("accion");

        Mantenimiento_Cliente mantenimiento = new Mantenimiento_Cliente();

        if ("registrar".equals(accion)) {

            Cliente c = new Cliente();

            c.setRazon_social(request.getParameter("razon_social"));
            c.setRuc(request.getParameter("ruc"));
            c.setTelefono(request.getParameter("telefono"));

            if (mantenimiento.registro(c)) {
                response.sendRedirect("registro_cliente.jsp?correcto=1");
            } else {
                response.sendRedirect("registro_cliente.jsp?error=1");
            }

        } else if ("actualizar".equals(accion)) {

            Cliente c = new Cliente();

            c.setId_cliente(Integer.parseInt(request.getParameter("id_cliente")));
            c.setRazon_social(request.getParameter("razon_social"));
            c.setRuc(request.getParameter("ruc"));
            c.setTelefono(request.getParameter("telefono"));

            if (mantenimiento.actualizar(c)) {
                response.sendRedirect("Proceso_Cliente?actualizado=1");
            } else {
                response.sendRedirect("Proceso_Cliente?error=1");
            }
        }

        mantenimiento.cerrar();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);

        if (sesion == null || sesion.getAttribute("usuario") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String ruc = request.getParameter("ruc");

        Mantenimiento_Cliente mantenimiento = new Mantenimiento_Cliente();

        ResultSet resultado;

        if (ruc == null || ruc.isEmpty()) {
            resultado = mantenimiento.consulta("SELECT * FROM cliente");
        } else {
            resultado = mantenimiento.busqueda(ruc);
        }

        ArrayList<Cliente> clientes = new ArrayList<>();

        try {
            while (resultado != null && resultado.next()) {

                Cliente c = new Cliente();

                c.setId_cliente(resultado.getInt("id_cliente"));
                c.setRazon_social(resultado.getString("razon_social"));
                c.setRuc(resultado.getString("ruc"));
                c.setTelefono(resultado.getString("telefono"));

                clientes.add(c);
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar clientes " + e);
        }

        mantenimiento.cerrar();

        request.setAttribute("clientes", clientes);

        request.getRequestDispatcher("consulta_cliente.jsp")
               .forward(request, response);
    }
}
