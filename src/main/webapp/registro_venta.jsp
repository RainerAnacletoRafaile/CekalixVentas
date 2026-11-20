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
    <title>CEKALIX - Registrar venta</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
   	<link href="css/style.css" rel="stylesheet">
</head>
<body>

<nav class="navbar navbar-dark navbar-cekalix px-3">
    <a class="navbar-brand fw-bold text-white" href="inicio.jsp">CEKALIX S.A.C.</a>
    <div>
        <a href="Proceso_Producto" class="btn btn-outline-light btn-sm me-2">Ver productos</a>
        <a href="Proceso_Venta" class="btn btn-outline-light btn-sm me-2">Ver ventas</a>
        <a href="inicio.jsp" class="btn btn-outline-light btn-sm">Menú principal</a>
    </div>
</nav>

<div class="container py-4">

    <h4 class="mb-3">Registro de venta</h4>

    <% if (request.getParameter("agregado") != null) { %>
        <div class="alert alert-success py-2">Producto agregado a la venta.</div>
    <% } else if (request.getParameter("stock") != null) { %>
        <div class="alert alert-warning py-2">La cantidad supera el stock disponible.</div>
    <% } else if (request.getParameter("error") != null) { %>
        <div class="alert alert-danger py-2">No se pudo completar la operación. Verifica el ID del producto, el ID del cliente y las cantidades.</div>
    <% } %>

    <%
        CarritoVenta carrito =
            (CarritoVenta) session.getAttribute("carrito");

        boolean hayProductos = carrito != null && !carrito.estaVacio();
    %>

    <div class="row g-4">

        <!-- Columna izquierda: agregar producto -->
        <div class="col-12 col-lg-4">
            <div class="card shadow">
                <div class="card-body p-4">
                    <h5 class="mb-3">Agregar productos</h5>

                    <form action="Proceso_Venta" method="post">

                        <input type="hidden" name="accion" value="agregar">

                        <div class="mb-3">
                            <label class="form-label fw-semibold">ID del producto</label>
                            <input type="number" name="id_producto" class="form-control" min="1" required>
                            <div class="form-text">
                                Consulta el ID en <a href="Proceso_Producto">Productos</a>.
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-semibold">Cantidad</label>
                            <input type="number" name="cantidad" class="form-control" min="1" required>
                        </div>

                        <button type="submit" class="btn btn-cekalix w-100">Agregar producto</button>

                    </form>
                </div>
            </div>
        </div>

        <!-- Columna derecha: carrito y registro -->
        <div class="col-12 col-lg-8">
            <div class="card shadow">
                <div class="card-body p-4">
                    <h5 class="mb-3">Productos de la venta</h5>

                    <% if (hayProductos) {

                        List<Producto> productos = carrito.getProductos();
                        List<Integer> cantidades = carrito.getCantidades();
                    %>

                    <div class="table-responsive">
                        <table class="table table-striped align-middle tabla-cekalix">
                            <thead>
                                <tr>
                                    <th>Producto</th>
                                    <th class="text-end">Precio unitario</th>
                                    <th class="text-center">Cantidad</th>
                                    <th class="text-end">Subtotal</th>
                                </tr>
                            </thead>
                            <tbody>
                            <%
                                for (int i = 0; i < productos.size(); i++) {

                                    Producto p = productos.get(i);
                                    int cantidad = cantidades.get(i);

                                    double importe =
                                        p.getPrecio().doubleValue() * cantidad;
                            %>
                                <tr>
                                    <td><%= p.getNombre() %></td>
                                    <td class="text-end">S/ <%= p.getPrecio() %></td>
                                    <td class="text-center"><%= cantidad %></td>
                                    <td class="text-end">S/ <%= String.format("%.2f", importe) %></td>
                                </tr>
                            <%
                                }
                            %>
                            </tbody>
                        </table>
                    </div>

                    <!-- Resumen -->
                    <div class="ms-auto" style="max-width: 300px;">
                        <div class="d-flex justify-content-between">
                            <span>Subtotal</span>
                            <span>S/ <%= carrito.getSubtotal().toPlainString() %></span>
                        </div>
                        <div class="d-flex justify-content-between">
                            <span>IGV (18 %)</span>
                            <span>S/ <%= carrito.getIgv().toPlainString() %></span>
                        </div>
                        <hr class="my-2">
                        <div class="d-flex justify-content-between fw-bold fs-5">
                            <span>Total</span>
                            <span>S/ <%= carrito.getTotal().toPlainString() %></span>
                        </div>
                    </div>

                    <hr class="my-4">

                    <!-- Registrar venta -->
                    <h5 class="mb-3">Registrar venta</h5>

                    <form action="Proceso_Venta" method="post" class="row g-2 align-items-end">

                        <input type="hidden" name="accion" value="registrar">

                        <div class="col-12 col-md-6">
                            <label class="form-label fw-semibold">ID del cliente</label>
                            <input type="number" name="id_cliente" class="form-control" min="1" required>
                        </div>

                        <div class="col-12 col-md-6">
                            <button type="submit" class="btn btn-cekalix w-100">Registrar venta</button>
                        </div>

                    </form>

                    <% } else { %>

                    <div class="alert alert-info mb-0">Aún no se han agregado productos a la venta.</div>

                    <% } %>

                </div>
            </div>
        </div>

    </div>
</div>

</body>
</html>