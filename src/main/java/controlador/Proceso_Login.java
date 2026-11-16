package controlador;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import datos.Mantenimiento_Usuario;

@WebServlet("/Proceso_Login")
public class Proceso_Login extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String usuario = request.getParameter("usuario");
        String contrasena = request.getParameter("contrasena");

        Mantenimiento_Usuario dao = new Mantenimiento_Usuario();
        boolean valido = dao.validar(usuario, contrasena);
        int idUsuario = valido ? dao.obtenerId(usuario) : 0;
        int idRol = valido ? dao.obtenerRol(usuario) : 0;
        dao.cerrar();

        if (valido && idUsuario > 0) {
            HttpSession anterior = request.getSession(false);
            if (anterior != null) anterior.invalidate();
            HttpSession sesion = request.getSession(true);
            sesion.setAttribute("usuario", usuario);
            sesion.setAttribute("idUsuario", idUsuario);
            sesion.setAttribute("idRol", idRol);
            response.sendRedirect("inicio.jsp");
        } else {
            response.sendRedirect("login.jsp?error=1");
        }
    }
}
