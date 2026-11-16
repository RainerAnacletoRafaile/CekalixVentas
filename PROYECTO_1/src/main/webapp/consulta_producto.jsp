<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    if (session.getAttribute("usuario") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="datos.Mantenimiento_Producto" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CEKALIX - Consultar productos</title>
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

    <h4 class="mb-3">Consulta de productos</h4>

    <!-- Buscador por nombre -->
    <form action="consulta_producto.jsp" method="get" class="mb-4">
        <div class="input-group">
            <input type="text" name="nombre" class="form-control"
                   placeholder="Buscar producto por nombre">
            <button type="submit" class="btn btn-cekalix">Buscar</button>
            <a href="consulta_producto.jsp" class="btn btn-outline-secondary">Ver todos</a>
        </div>
    </form>

    <!-- Tabla -->
    <div class="card shadow">
        <div class="table-responsive">
            <table class="table table-striped table-hover align-middle mb-0 tabla-cekalix">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th class="text-end">Precio</th>
                        <th class="text-center">Stock</th>
                    </tr>
                </thead>
                <tbody>

                <%
                    Mantenimiento_Producto mantenimiento =
                        new Mantenimiento_Producto();

                    String nombre = request.getParameter("nombre");

                    ResultSet resultado;

                    if (nombre == null || nombre.isEmpty()) {
                        resultado = mantenimiento.consulta(
                            "SELECT * FROM producto"
                        );
                    } else {
                        resultado = mantenimiento.busqueda(nombre);
                    }

                    try {
                        boolean hayProductos = false;

                        while (resultado != null && resultado.next()) {
                            hayProductos = true;
                            int stock = resultado.getInt("stock");

                            String color = "success";
                            if (stock <= 20) {
                                color = "danger";
                            } else if (stock <= 50) {
                                color = "warning text-dark";
                            }
                %>

                    <tr>
                        <td><%= resultado.getInt("id_producto") %></td>
                        <td><%= resultado.getString("nombre") %></td>
                        <td class="text-end">S/ <%= resultado.getBigDecimal("precio") %></td>
                        <td class="text-center">
                            <span class="badge bg-<%= color %>"><%= stock %></span>
                        </td>
                    </tr>

                <%
                        }

                        if (!hayProductos) {
                %>