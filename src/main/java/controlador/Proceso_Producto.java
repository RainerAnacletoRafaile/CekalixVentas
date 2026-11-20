package controlador;

import java.io.IOException;
import java.util.List;

import datos.Mantenimiento_Producto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Producto;

@WebServlet("/Proceso_Producto")
public class Proceso_Producto extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        if (sesion == null || sesion.getAttribute("usuario") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String nombre = request.getParameter("nombre");
        Mantenimiento_Producto dao = new Mantenimiento_Producto();
        List<Producto> productos = nombre == null || nombre.isBlank()
            ? dao.listar()
            : dao.buscarPorNombre(nombre);

        request.setAttribute("productos", productos);
        request.getRequestDispatcher("consulta_producto.jsp").forward(request, response);
    }
}
