<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    if (session.getAttribute("usuario") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<%@ page import="java.util.List" %>
<%@ page import="modelo.Venta" %>
<%
    if (request.getAttribute("ventas") == null) {
        response.sendRedirect("Proceso_Venta");
        return;
    }
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CEKALIX - Consultar ventas</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="css/style.css" rel="stylesheet">
</head>
<body>

<nav class="navbar navbar-dark navbar-cekalix px-3">
    <a class="navbar-brand fw-bold text-white" href="inicio.jsp">CEKALIX S.A.C.</a>
    <div>
        <a href="registro_venta.jsp" class="btn btn-outline-light btn-sm me-2">Nueva venta</a>
        <a href="inicio.jsp" class="btn btn-outline-light btn-sm">Menú principal</a>
    </div>
</nav>

<div class="container py-4">

    <div class="d-flex justify-content-between align-items-center mb-3">
        <h4 class="mb-0">Consulta de ventas</h4>
        <a href="registro_venta.jsp" class="btn btn-cekalix">+ Registrar nueva venta</a>
    </div>

    <% if (request.getParameter("correcto") != null) { %>
        <div class="alert alert-success py-2">Venta registrada correctamente.</div>
    <% } %>

    <div class="card shadow">
        <div class="table-responsive">
            <table class="table table-striped table-hover align-middle mb-0 tabla-cekalix">
                <thead>
                    <tr>
                        <th>ID de venta</th>
                        <th>ID de cliente</th>
                        <th>ID de usuario</th>
                        <th>Fecha</th>
                        <th class="text-end">Total</th>
                    </tr>
                </thead>
                <tbody>

                <%
                    ArrayList<Venta> ventas =
                        (List<Venta>) request.getAttribute("ventas");

                    if (ventas != null && !ventas.isEmpty()) {

                        for (Venta v : ventas) {
                %>
                    <tr>
                        <td><%= v.getId_venta() %></td>
                        <td><%= v.getId_cliente() %></td>
                        <td><%= v.getId_usuario() %></td>
                        <td><%= v.getFecha() %></td>
                        <td class="text-end fw-semibold">S/ <%= v.getTotal() %></td>
                    </tr>
                <%
                        }

                    } else {
                %>
                    <tr>
                        <td colspan="5" class="text-center text-muted py-4">No existen ventas registradas.</td>
                    </tr>
                <%
                    }
                %>

                </tbody>
            </table>
        </div>
    </div>

</div>

</body>
</html>