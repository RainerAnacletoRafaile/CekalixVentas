package controlador;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import datos.Mantenimiento_Usuario;
import modelo.Usuario;

@WebServlet("/Proceso_Registro")
public class Proceso_Registro extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String nombre = request.getParameter("nombre");
        String usuario = request.getParameter("usuario");
        String contrasena = request.getParameter("contrasena");

        Usuario u = new Usuario();

        u.setNombre(nombre);
        u.setUsuario(usuario);
        u.setContrasena(contrasena);

        Mantenimiento_Usuario mantenimiento = new Mantenimiento_Usuario();

        boolean registrado = mantenimiento.registro(u);
        mantenimiento.cerrar();

        if (registrado) {
            response.sendRedirect("login.jsp?registro=correcto");
        } else {
            response.sendRedirect("registro.jsp?error=1");
        }
    }
}