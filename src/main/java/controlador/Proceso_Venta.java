package controlador;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import datos.Mantenimiento_Producto;
import datos.Mantenimiento_Venta;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.CarritoVenta;
import modelo.Producto;
import modelo.Venta;

@WebServlet("/Proceso_Venta")
public class Proceso_Venta extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);
        if (!autenticado(sesion, response)) return;

        String accion = request.getParameter("accion");

        if ("agregar".equals(accion)) {
            agregarProducto(request, response, sesion);
        } else if ("registrar".equals(accion)) {
            registrarVenta(request, response, sesion);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Acción no válida.");
        }
    }

    private void agregarProducto(HttpServletRequest request, HttpServletResponse response,
                                 HttpSession sesion) throws IOException {
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

        Mantenimiento_Producto daoProducto = new Mantenimiento_Producto();
        Producto producto = daoProducto.buscarPorId(idProducto);

        if (producto == null) {
            response.sendRedirect("registro_venta.jsp?error=1");
            return;
        }

        CarritoVenta carrito = (CarritoVenta) sesion.getAttribute("carrito");
        if (carrito == null) carrito = new CarritoVenta();

        int cantidadEnCarrito = 0;
        List<Producto> productos = carrito.getProductos();
        List<Integer> cantidades = carrito.getCantidades();

        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).getId_producto() == idProducto) {
                cantidadEnCarrito = cantidades.get(i);
                break;
            }
        }

        if (cantidad + cantidadEnCarrito > producto.getStock()) {
            response.sendRedirect("registro_venta.jsp?stock=1");
            return;
        }

        carrito.agregarProducto(producto, cantidad);
        sesion.setAttribute("carrito", carrito);
        response.sendRedirect("registro_venta.jsp?agregado=1");
    }

    private void registrarVenta(HttpServletRequest request, HttpServletResponse response,
                                HttpSession sesion) throws IOException {
        CarritoVenta carrito = (CarritoVenta) sesion.getAttribute("carrito");
        if (carrito == null || carrito.estaVacio()) {
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

        Integer idUsuario = (Integer) sesion.getAttribute("idUsuario");
        if (idCliente <= 0 || idUsuario == null || idUsuario <= 0) {
            response.sendRedirect("registro_venta.jsp?error=1");
            return;
        }

        Venta venta = new Venta();
        venta.setId_cliente(idCliente);
        venta.setId_usuario(idUsuario);
        venta.setFecha(LocalDate.now());

        boolean registrada = new Mantenimiento_Venta().registro(venta, carrito);
        if (registrada) {
            sesion.removeAttribute("carrito");
            response.sendRedirect("Proceso_Venta?correcto=1");
        } else {
            response.sendRedirect("registro_venta.jsp?error=1");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);
        if (!autenticado(sesion, response)) return;

        List<Venta> ventas = new Mantenimiento_Venta().listar();
        request.setAttribute("ventas", ventas);
        request.getRequestDispatcher("consulta_venta.jsp").forward(request, response);
    }

    private boolean autenticado(HttpSession sesion, HttpServletResponse response)
            throws IOException {
        if (sesion == null || sesion.getAttribute("usuario") == null) {
            response.sendRedirect("login.jsp");
            return false;
        }
        return true;
    }
}
