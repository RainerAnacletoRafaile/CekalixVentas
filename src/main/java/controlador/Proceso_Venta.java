package controlador;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import datos.Mantenimiento_Cliente;
import datos.Mantenimiento_Producto;
import datos.Mantenimiento_Venta;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.CarritoVenta;
import modelo.Cliente;
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
        } else if ("confirmar".equals(accion)) {
            confirmarVenta(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Acción no válida.");
        }
    }

    private void agregarProducto(HttpServletRequest request, HttpServletResponse response,
                                 HttpSession sesion) throws IOException {
        String sku = request.getParameter("sku");
        int cantidad;
        try {
            cantidad = Integer.parseInt(request.getParameter("cantidad"));
        } catch (NumberFormatException e) {
            response.sendRedirect("registro_venta.jsp?error=1");
            return;
        }
        if (sku == null || sku.isBlank() || cantidad <= 0) {
            response.sendRedirect("registro_venta.jsp?error=1");
            return;
        }

        Producto producto = new Mantenimiento_Producto().buscarPorSku(sku.trim());
        if (producto == null) {
            response.sendRedirect("registro_venta.jsp?producto=1");
            return;
        }

        CarritoVenta carrito = (CarritoVenta) sesion.getAttribute("carrito");
        if (carrito == null) carrito = new CarritoVenta();

        int cantidadEnCarrito = 0;
        List<Producto> productos = carrito.getProductos();
        List<Integer> cantidades = carrito.getCantidades();
        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).getId_producto() == producto.getId_producto()) {
                cantidadEnCarrito = cantidades.get(i);
                break;
            }
        }

        if (cantidad + cantidadEnCarrito > producto.getStock_disponible()) {
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

        String ruc = request.getParameter("ruc");
        String razonSocial = request.getParameter("razon_social");
        if (ruc == null || !ruc.matches("\\d{11}") ||
                razonSocial == null || razonSocial.isBlank()) {
            response.sendRedirect("registro_venta.jsp?cliente=1");
            return;
        }

        Cliente cliente = new Mantenimiento_Cliente().buscarPorRuc(ruc);
        if (cliente == null || !cliente.getRazon_social().equalsIgnoreCase(razonSocial.trim())) {
            response.sendRedirect("registro_venta.jsp?cliente=1");
            return;
        }

        Integer idUsuario = (Integer) sesion.getAttribute("idUsuario");
        if (idUsuario == null || idUsuario <= 0) {
            response.sendRedirect("registro_venta.jsp?error=1");
            return;
        }

        Venta venta = new Venta();
        venta.setId_cliente(cliente.getId_cliente());
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

    private void confirmarVenta(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int idVenta;
        try {
            idVenta = Integer.parseInt(request.getParameter("id_venta"));
        } catch (NumberFormatException e) {
            response.sendRedirect("Proceso_Venta?error=1");
            return;
        }
        boolean confirmada = new Mantenimiento_Venta().confirmar(idVenta);
        response.sendRedirect(confirmada ? "Proceso_Venta?confirmada=1" : "Proceso_Venta?stock=1");
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
