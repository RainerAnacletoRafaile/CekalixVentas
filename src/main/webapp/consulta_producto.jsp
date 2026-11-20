<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="modelo.Producto" %>
<%
    if (session.getAttribute("usuario") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    if (request.getAttribute("productos") == null) {
        response.sendRedirect("Proceso_Producto");
        return;
    }
    List<Producto> productos = (List<Producto>) request.getAttribute("productos");
    String nombre = request.getParameter("nombre");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CEKALIX - Productos para ventas</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="css/style.css" rel="stylesheet">
</head>
<body>
<nav class="navbar navbar-dark navbar-cekalix px-3">
    <a class="navbar-brand fw-bold text-white" href="inicio.jsp">CEKALIX S.A.C.</a>
    <div>
        <a href="registro_venta.jsp" class="btn btn-outline-light btn-sm me-2">Registrar venta</a>
        <a href="inicio.jsp" class="btn btn-outline-light btn-sm">Menú principal</a>
    </div>
</nav>

<div class="container py-4">
    <h4 class="mb-2">Consulta de productos</h4>
    <p class="text-muted">Consulta de SKU, características y disponibilidad para atender ventas B2B.</p>

    <form action="Proceso_Producto" method="get" class="mb-4">
        <div class="input-group">
            <input type="text" name="nombre" class="form-control"
                   placeholder="Buscar por nombre o SKU"
                   value="<%= nombre == null ? "" : nombre.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;") %>">
            <button type="submit" class="btn btn-cekalix">Buscar</button>
            <a href="Proceso_Producto" class="btn btn-outline-secondary">Ver todos</a>
        </div>
    </form>

    <div class="card shadow">
        <div class="table-responsive">
            <table class="table table-striped table-hover align-middle mb-0 tabla-cekalix">
                <thead>
                    <tr>
                        <th>SKU</th>
                        <th>Producto</th>
                        <th>Medida</th>
                        <th>Grosor</th>
                        <th>Color</th>
                        <th class="text-end">Precio/caja</th>
                        <th class="text-center">Cajas físicas</th>
                        <th class="text-center">Reservadas</th>
                        <th class="text-center">Disponibles</th>
                        <th class="text-center">Unidades disponibles</th>
                    </tr>
                </thead>
                <tbody>
                <% if (productos != null && !productos.isEmpty()) {
                    for (Producto producto : productos) {
                        int disponibles = producto.getStock_disponible();
                        String color = disponibles < 10 ? "danger" : "success";
                %>
                    <tr>
                        <td class="fw-semibold"><%= producto.getSku() %></td>
                        <td><%= producto.getNombre() %></td>
                        <td><%= producto.getMedida() == null ? "-" : producto.getMedida() %></td>
                        <td><%= producto.getGrosor() == null ? "-" : producto.getGrosor() %></td>
                        <td><%= producto.getColor() == null ? "-" : producto.getColor() %></td>
                        <td class="text-end">S/ <%= producto.getPrecio() %></td>
                        <td class="text-center"><%= producto.getStock_fisico() %></td>
                        <td class="text-center"><%= producto.getStock_reservado() %></td>
                        <td class="text-center">
                            <span class="badge bg-<%= color %>"><%= disponibles %></span>
                            <% if (disponibles < 10) { %>
                                <small class="d-block text-danger">Stock crítico</small>
                            <% } %>
                        </td>
                        <td class="text-center"><%= producto.getUnidades_disponibles() %></td>
                    </tr>
                <%  }
                   } else { %>
                    <tr>
                        <td colspan="10" class="text-center text-muted py-4">No se encontraron productos.</td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
    <p class="small text-muted mt-2">La disponibilidad se calcula como cajas físicas menos cajas reservadas.</p>
</div>
</body>
</html>
