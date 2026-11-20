<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    if (session.getAttribute("usuario") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<%@ page import="java.util.List" %>
<%@ page import="modelo.Producto" %>
<%@ page import="modelo.CarritoVenta" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CEKALIX - Registrar orden de venta</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="css/style.css" rel="stylesheet">
</head>
<body>
<nav class="navbar navbar-dark navbar-cekalix px-3">
    <a class="navbar-brand fw-bold text-white" href="inicio.jsp">CEKALIX S.A.C.</a>
    <div>
        <a href="Proceso_Producto" class="btn btn-outline-light btn-sm me-2">Consultar productos</a>
        <a href="Proceso_Venta" class="btn btn-outline-light btn-sm me-2">Ver órdenes</a>
        <a href="inicio.jsp" class="btn btn-outline-light btn-sm">Menú principal</a>
    </div>
</nav>

<div class="container py-4">
    <h4 class="mb-2">Registro de orden de venta B2B</h4>
    <p class="text-muted">Las cantidades se ingresan en cajas. La orden se registra como Pendiente.</p>

    <% if (request.getParameter("agregado") != null) { %>
        <div class="alert alert-success py-2">SKU agregado a la orden.</div>
    <% } else if (request.getParameter("stock") != null) { %>
        <div class="alert alert-warning py-2">No hay suficientes cajas disponibles para esa cantidad.</div>
    <% } else if (request.getParameter("producto") != null) { %>
        <div class="alert alert-danger py-2">No se encontró un producto con ese SKU.</div>
    <% } else if (request.getParameter("error") != null) { %>
        <div class="alert alert-danger py-2">No se pudo completar la operación. Verifica los datos ingresados.</div>
    <% } %>

    <%
        CarritoVenta carrito = (CarritoVenta) session.getAttribute("carrito");
        boolean hayProductos = carrito != null && !carrito.estaVacio();
    %>

    <div class="row g-4">
        <div class="col-12 col-lg-4">
            <div class="card shadow">
                <div class="card-body p-4">
                    <h5 class="mb-3">Agregar SKU</h5>
                    <form action="Proceso_Venta" method="post">
                        <input type="hidden" name="accion" value="agregar">
                        <div class="mb-3">
                            <label class="form-label fw-semibold">SKU del producto</label>
                            <input type="text" name="sku" class="form-control" maxlength="40" required>
                            <div class="form-text">Consulta el SKU en <a href="Proceso_Producto">Productos</a>.</div>
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-semibold">Cantidad de cajas</label>
                            <input type="number" name="cantidad" class="form-control" min="1" required>
                        </div>
                        <button type="submit" class="btn btn-cekalix w-100">Agregar a la orden</button>
                    </form>
                </div>
            </div>
        </div>

        <div class="col-12 col-lg-8">
            <div class="card shadow">
                <div class="card-body p-4">
                    <h5 class="mb-3">Productos solicitados</h5>
                    <% if (hayProductos) {
                        List<Producto> productos = carrito.getProductos();
                        List<Integer> cantidades = carrito.getCantidades();
                    %>
                    <div class="table-responsive">
                        <table class="table table-striped align-middle tabla-cekalix">
                            <thead>
                                <tr>
                                    <th>SKU</th>
                                    <th>Producto</th>
                                    <th class="text-end">Precio base</th>
                                    <th class="text-center">Cajas</th>
                                    <th class="text-center">Descuento</th>
                                    <th class="text-end">Precio aplicado</th>
                                    <th class="text-end">Subtotal</th>
                                </tr>
                            </thead>
                            <tbody>
                            <% for (int i = 0; i < productos.size(); i++) {
                                Producto p = productos.get(i);
                                int cantidad = cantidades.get(i);
                                java.math.BigDecimal descuento = modelo.CarritoVenta.obtenerPorcentajeDescuento(cantidad)
                                    .multiply(new java.math.BigDecimal("100"));
                                java.math.BigDecimal precioAplicado = carrito.getPrecioAplicado(i);
                                java.math.BigDecimal importe = precioAplicado.multiply(java.math.BigDecimal.valueOf(cantidad));
                            %>
                                <tr>
                                    <td><%= p.getSku() %></td>
                                    <td><%= p.getNombre() %></td>
                                    <td class="text-end">S/ <%= p.getPrecio() %></td>
                                    <td class="text-center"><%= cantidad %></td>
                                    <td class="text-center"><%= descuento.stripTrailingZeros().toPlainString() %>%</td>
                                    <td class="text-end">S/ <%= precioAplicado.toPlainString() %></td>
                                    <td class="text-end">S/ <%= importe.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString() %></td>
                                </tr>
                            <% } %>
                            </tbody>
                        </table>
                    </div>

                    <div class="ms-auto" style="max-width: 320px;">
                        <div class="d-flex justify-content-between">
                            <span>Subtotal sin descuento</span>
                            <span>S/ <%= carrito.getSubtotalBruto().toPlainString() %></span>
                        </div>
                        <div class="d-flex justify-content-between">
                            <span>Descuento B2B</span>
                            <span>- S/ <%= carrito.getDescuento().toPlainString() %></span>
                        </div>
                        <hr class="my-2">
                        <div class="d-flex justify-content-between fw-bold fs-5">
                            <span>Total de la orden</span>
                            <span>S/ <%= carrito.getTotal().toPlainString() %></span>
                        </div>
                    </div>

                    <hr class="my-4">
                    <h5 class="mb-3">Datos del cliente empresarial</h5>
                    <form action="Proceso_Venta" method="post" class="row g-2 align-items-end">
                        <input type="hidden" name="accion" value="registrar">
                        <div class="col-12 col-md-6">
                            <label class="form-label fw-semibold">ID del cliente</label>
                            <input type="number" name="id_cliente" class="form-control" min="1" required>
                        </div>
                        <div class="col-12 col-md-6">
                            <button type="submit" class="btn btn-cekalix w-100">Registrar orden pendiente</button>
                        </div>
                    </form>
                    <% } else { %>
                    <div class="alert alert-info mb-0">Aún no se han agregado productos a la orden.</div>
                    <% } %>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
