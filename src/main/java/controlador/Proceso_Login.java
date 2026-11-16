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
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String usuario = request.getParameter("usuario");
        String contrasena = request.getParameter("contrasena");

        Mantenimiento_Usuario mantenimiento = new Mantenimiento_Usuario();

        boolean valido = mantenimiento.validar(usuario, contrasena);
        mantenimiento.cerrar();

        if (valido) {
            HttpSession sesion = request.getSession();
            sesion.setAttribute("usuario", usuario);

            response.sendRedirect("inicio.jsp");
        } else {
            response.sendRedirect("login.jsp?error=1");
        }
    }
}