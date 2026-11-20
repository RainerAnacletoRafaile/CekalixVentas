package controlador;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

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
            procesarEstado(request, response, "confirmar");
        } else if ("despachar".equals(accion)) {
            procesarEstado(request, response, "despachar");
        } else if ("anular".equals(accion)) {
            if (!esAdministrador(sesion, response)) return;
            anularVenta(request, response);
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

    private void procesarEstado(HttpServletRequest request, HttpServletResponse response,
                                String accion) throws IOException {
        int idVenta;
        try {
            idVenta = Integer.parseInt(request.getParameter("id_venta"));
        } catch (NumberFormatException e) {
            response.sendRedirect("Proceso_Venta?error=1");
            return;
        }

        Mantenimiento_Venta mantenimiento = new Mantenimiento_Venta();
        boolean resultado = "confirmar".equals(accion)
            ? mantenimiento.confirmar(idVenta)
            : mantenimiento.despachar(idVenta);

        String parametro = "confirmar".equals(accion) ? "confirmada" : "despachada";
        response.sendRedirect(resultado
            ? "Proceso_Venta?" + parametro + "=1"
            : "Proceso_Venta?stock=1");
    }

    private void anularVenta(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int idVenta;
        try {
            idVenta = Integer.parseInt(request.getParameter("id_venta"));
        } catch (NumberFormatException e) {
            response.sendRedirect("Proceso_Venta?error=1");
            return;
        }

        String motivo = request.getParameter("motivo");
        boolean anulada = new Mantenimiento_Venta().anular(idVenta, motivo);
        response.sendRedirect(anulada
            ? "Proceso_Venta?anulada=1"
            : "Proceso_Venta?anulacion=error");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        if (!autenticado(sesion, response)) return;

        if ("reporte".equals(request.getParameter("accion"))) {
            LocalDate desde = null;
            LocalDate hasta = null;
            try {
                String valorDesde = request.getParameter("desde");
                String valorHasta = request.getParameter("hasta");
                if (valorDesde != null && !valorDesde.isBlank()) desde = LocalDate.parse(valorDesde);
                if (valorHasta != null && !valorHasta.isBlank()) hasta = LocalDate.parse(valorHasta);
                if (desde != null && hasta != null && desde.isAfter(hasta)) {
                    response.sendRedirect("Proceso_Venta?accion=reporte&reporteError=1");
                    return;
                }
            } catch (DateTimeParseException e) {
                response.sendRedirect("Proceso_Venta?reporteError=1");
                return;
            }

            List<Map<String, Object>> reporte =
                new Mantenimiento_Venta().reportePorCaracteristicas(desde, hasta);
            request.setAttribute("reporte", reporte);
            request.setAttribute("desde", desde);
            request.setAttribute("hasta", hasta);
            request.getRequestDispatcher("reporte_ventas.jsp").forward(request, response);
            return;
        }

        List<Venta> ventas = new Mantenimiento_Venta().listar();
        request.setAttribute("ventas", ventas);
        request.getRequestDispatcher("consulta_venta.jsp").forward(request, response);
    }

    private boolean esAdministrador(HttpSession sesion, HttpServletResponse response)
            throws IOException {
        if (!Integer.valueOf(1).equals(sesion.getAttribute("idRol"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                "Solo el administrador puede anular órdenes.");
            return false;
        }
        return true;
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
