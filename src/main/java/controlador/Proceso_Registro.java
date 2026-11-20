package controlador;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import datos.Mantenimiento_Usuario;
import modelo.Usuario;

@WebServlet("/Proceso_Registro")
public class Proceso_Registro extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        if (sesion == null || !Integer.valueOf(1).equals(sesion.getAttribute("idRol"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Solo el administrador puede registrar vendedores.");
            return;
        }

        String nombre = request.getParameter("nombre");
        String usuario = request.getParameter("usuario");
        String contrasena = request.getParameter("contrasena");
        if (nombre == null || nombre.trim().isEmpty() ||
            usuario == null || usuario.trim().isEmpty() ||
            contrasena == null || contrasena.length() < 6) {
            response.sendRedirect("registro.jsp?error=1");
            return;
        }

        Usuario u = new Usuario();
        u.setNombre(nombre.trim());
        u.setUsuario(usuario.trim());
        u.setContrasena(contrasena);

        Mantenimiento_Usuario dao = new Mantenimiento_Usuario();
        boolean registrado = dao.registro(u);

        response.sendRedirect(registrado ? "registro.jsp?correcto=1" : "registro.jsp?error=1");
    }
}
