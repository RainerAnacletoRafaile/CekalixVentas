<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,java.util.Map" %>
<%
    if (session.getAttribute("usuario") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    List<Map<String, Object>> reporte =
        (List<Map<String, Object>>) request.getAttribute("reporte");
    Object desde = request.getAttribute("desde");
    Object hasta = request.getAttribute("hasta");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CEKALIX - Reporte de ventas</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="css/style.css" rel="stylesheet">
</head>
<body>
<nav class="navbar navbar-dark navbar-cekalix px-3">
    <a class="navbar-brand fw-bold text-white" href="inicio.jsp">CEKALIX S.A.C.</a>
    <a href="Proceso_Venta" class="btn btn-outline-light btn-sm">Volver a órdenes</a>
</nav>
<div class="container py-4">
    <h4 class="mb-2">Reporte de ventas por característica de producto</h4>
    <p class="text-muted">Solo considera órdenes en estado Despachado. Los resultados se ordenan por cajas vendidas.</p>
    <% if (request.getParameter("reporteError") != null) { %>
        <div class="alert alert-warning">El rango de fechas no es válido. Revisa las fechas seleccionadas.</div>
    <% } %>

    <form action="Proceso_Venta" method="get" class="row g-3 align-items-end mb-4">
        <input type="hidden" name="accion" value="reporte">
        <div class="col-12 col-md-4">
            <label class="form-label">Desde</label>
            <input type="date" name="desde" class="form-control" value="<%= desde == null ? "" : desde %>">
        </div>
        <div class="col-12 col-md-4">
            <label class="form-label">Hasta</label>
            <input type="date" name="hasta" class="form-control" value="<%= hasta == null ? "" : hasta %>">
        </div>
        <div class="col-12 col-md-4">
            <button type="submit" class="btn btn-cekalix">Generar reporte</button>
            <a href="Proceso_Venta?accion=reporte" class="btn btn-outline-secondary">Limpiar fechas</a>
        </div>
    </form>

    <div class="card shadow">
        <div class="table-responsive">
            <table class="table table-striped table-hover align-middle mb-0 tabla-cekalix">
                <thead>
                    <tr>
                        <th>SKU</th>
                        <th>Medida</th>
                        <th>Grosor</th>
                        <th>Color</th>
                        <th class="text-end">Cajas vendidas</th>
                        <th class="text-end">Monto facturado</th>
                    </tr>
                </thead>
                <tbody>
                <% if (reporte != null && !reporte.isEmpty()) {
                    for (Map<String, Object> fila : reporte) { %>
                    <tr>
                        <td><%= fila.get("sku") %></td>
                        <td><%= fila.get("medida") == null ? "-" : fila.get("medida") %></td>
                        <td><%= fila.get("grosor") == null ? "-" : fila.get("grosor") %></td>
                        <td><%= fila.get("color") == null ? "-" : fila.get("color") %></td>
                        <td class="text-end"><%= fila.get("cajas_vendidas") %></td>
                        <td class="text-end">S/ <%= fila.get("monto_total") %></td>
                    </tr>
                <%  }
                   } else { %>
                    <tr>
                        <td colspan="6" class="text-center text-muted py-4">No hay ventas despachadas para el periodo seleccionado.</td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>
