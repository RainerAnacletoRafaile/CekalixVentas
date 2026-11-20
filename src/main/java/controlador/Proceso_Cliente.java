package controlador;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import datos.Mantenimiento_Cliente;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Cliente;

@WebServlet("/Proceso_Cliente")
public class Proceso_Cliente extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!autenticado(request, response)) return;

        String accion = request.getParameter("accion");
        Mantenimiento_Cliente dao = new Mantenimiento_Cliente();

        Cliente cliente = new Cliente();
        cliente.setRazon_social(valor(request, "razon_social"));
        cliente.setRuc(valor(request, "ruc"));
        cliente.setTelefono(valor(request, "telefono"));
        cliente.setDireccion(valor(request, "direccion"));

        boolean resultado;
        if ("registrar".equals(accion)) {
            resultado = dao.registro(cliente);
            response.sendRedirect(resultado
                ? "registro_cliente.jsp?correcto=1"
                : "registro_cliente.jsp?error=1");
            return;
        }

        if ("actualizar".equals(accion)) {
            try {
                cliente.setId_cliente(Integer.parseInt(request.getParameter("id_cliente")));
            } catch (NumberFormatException e) {
                response.sendRedirect("Proceso_Cliente?error=1");
                return;
            }

            resultado = dao.actualizar(cliente);
            response.sendRedirect(resultado
                ? "Proceso_Cliente?actualizado=1"
                : "Proceso_Cliente?error=1");
            return;
        }

        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Acción no válida.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!autenticado(request, response)) return;

        String ruc = request.getParameter("ruc");
        Mantenimiento_Cliente dao = new Mantenimiento_Cliente();
        List<Cliente> clientes;

        if (ruc == null || ruc.isBlank()) {
            clientes = dao.listar();
        } else {
            clientes = new ArrayList<>();
            Cliente cliente = dao.buscarPorRuc(ruc.trim());
            if (cliente != null) clientes.add(cliente);
        }

        request.setAttribute("clientes", clientes);
        request.getRequestDispatcher("consulta_cliente.jsp").forward(request, response);
    }

    private boolean autenticado(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession sesion = request.getSession(false);
        if (sesion == null || sesion.getAttribute("usuario") == null) {
            response.sendRedirect("login.jsp");
            return false;
        }
        return true;
    }

    private String valor(HttpServletRequest request, String parametro) {
        String valor = request.getParameter(parametro);
        return valor == null ? "" : valor.trim();
    }
}
