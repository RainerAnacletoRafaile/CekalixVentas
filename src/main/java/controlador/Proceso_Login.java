package controlador;

import java.io.IOException;

import datos.Mantenimiento_Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Usuario;

@WebServlet("/Proceso_Login")
public class Proceso_Login extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nombreUsuario = request.getParameter("usuario");
        String contrasena = request.getParameter("contrasena");

        if (nombreUsuario == null || contrasena == null ||
                nombreUsuario.isBlank() || contrasena.isBlank()) {
            response.sendRedirect("login.jsp?error=1");
            return;
        }

        Mantenimiento_Usuario dao = new Mantenimiento_Usuario();
        Usuario usuario = dao.autenticar(nombreUsuario.trim(), contrasena);

        if (usuario == null) {
            response.sendRedirect("login.jsp?error=1");
            return;
        }

        HttpSession anterior = request.getSession(false);
        if (anterior != null) anterior.invalidate();

        HttpSession sesion = request.getSession(true);
        sesion.setAttribute("usuario", usuario.getUsuario());
        sesion.setAttribute("idUsuario", usuario.getId_usuario());
        sesion.setAttribute("idRol", usuario.getId_rol());

        response.sendRedirect("inicio.jsp");
    }
}
