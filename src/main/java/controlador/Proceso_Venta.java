package controlador;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import datos.Mantenimiento_Venta;
import datos.Mantenimiento_Producto;
import datos.Mantenimiento_Usuario;

import modelo.Producto;
import modelo.Venta;

@WebServlet("/Proceso_Venta")
public class Proceso_Venta extends HttpServlet {

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

        if ("agregar".equals(accion)) {

            int idProducto;
            int cantidad;

            try {
                idProducto = Integer.parseInt(request.getParameter("id_producto"));
                cantidad = Integer.parseInt(request.getParameter("cantidad"));

            } catch (NumberFormatException e) {
                response.sendRedirect("registro_venta.jsp?error=1");
                return;
            }

            if (idProducto <= 0 || cantidad <= 0) {
                response.sendRedirect("registro_venta.jsp?error=1");
                return;
            }

            Mantenimiento_Producto mantenimientoProducto =
                new Mantenimiento_Producto();

            ResultSet resultado = mantenimientoProducto.consulta(
                "SELECT * FROM producto WHERE id_producto = " + idProducto
            );

            try {

                if (resultado != null && resultado.next()) {

                    Producto p = new Producto();

                    p.setId_producto(resultado.getInt("id_producto"));
                    p.setNombre(resultado.getString("nombre"));
                    p.setPrecio(resultado.getBigDecimal("precio"));
                    p.setStock(resultado.getInt("stock"));

                    if (cantidad > p.getStock()) {
                        mantenimientoProducto.cerrar();
                        response.sendRedirect("registro_venta.jsp?stock=1");
                        return;
                    }

                    Mantenimiento_Venta carrito =
                        (Mantenimiento_Venta) sesion.getAttribute("carrito");

                    if (carrito == null) {
                        carrito = new Mantenimiento_Venta();
                    }

                    carrito.agregarProducto(p, cantidad);

                    sesion.setAttribute("carrito", carrito);

                    mantenimientoProducto.cerrar();

                    response.sendRedirect("registro_venta.jsp?agregado=1");
                    return;
                }

            } catch (SQLException e) {
                System.out.println("Error al buscar producto " + e);
            }

            mantenimientoProducto.cerrar();

            response.sendRedirect("registro_venta.jsp?error=1");
            return;
        }

        if ("registrar".equals(accion)) {

            Mantenimiento_Venta mantenimiento =
                (Mantenimiento_Venta) sesion.getAttribute("carrito");

            if (mantenimiento == null ||
                mantenimiento.getProductos().isEmpty()) {

                response.sendRedirect("registro_venta.jsp?error=1");
                return;
            }

            int idCliente;

            try {
                idCliente = Integer.parseInt(request.getParameter("id_cliente"));

            } catch (NumberFormatException e) {
                response.sendRedirect("registro_venta.jsp?error=1");
                return;
            }

            if (idCliente <= 0) {
                response.sendRedirect("registro_venta.jsp?error=1");
                return;
            }

            String usuario = (String) sesion.getAttribute("usuario");

            Mantenimiento_Usuario mantenimientoUsuario =
                new Mantenimiento_Usuario();

            int idUsuario = mantenimientoUsuario.obtenerId(usuario);

            mantenimientoUsuario.cerrar();

            if (idUsuario == 0) {
                response.sendRedirect("registro_venta.jsp?error=1");
                return;
            }

            Venta v = new Venta();

            v.setId_cliente(idCliente);
            v.setId_usuario(idUsuario);
            v.setFecha(LocalDate.now());

            if (mantenimiento.registro(v)) {

                mantenimiento.cerrar();

                sesion.removeAttribute("carrito");

                response.sendRedirect("Proceso_Venta?correcto=1");

            } else {

                mantenimiento.cerrar();

                response.sendRedirect("registro_venta.jsp?error=1");
            }
        }
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

        Mantenimiento_Venta mantenimiento = new Mantenimiento_Venta();

        ResultSet resultado = mantenimiento.consulta(
            "SELECT * FROM venta ORDER BY id_venta DESC"
        );

        ArrayList<Venta> ventas = new ArrayList<>();

        try {

            while (resultado != null && resultado.next()) {

                Venta v = new Venta();

                v.setId_venta(resultado.getInt("id_venta"));
                v.setId_cliente(resultado.getInt("id_cliente"));
                v.setId_usuario(resultado.getInt("id_usuario"));
                v.setFecha(resultado.getDate("fecha").toLocalDate());
                v.setTotal(resultado.getBigDecimal("total"));

                ventas.add(v);
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar ventas " + e);
        }

        mantenimiento.cerrar();

        request.setAttribute("ventas", ventas);

        request.getRequestDispatcher("consulta_venta.jsp")
               .forward(request, response);
    }
}