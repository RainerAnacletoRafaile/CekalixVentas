<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    if (session.getAttribute("usuario") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CEKALIX - Inicio</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
   	<link href="css/style.css" rel="stylesheet">
</head>
<body>

<nav class="navbar navbar-dark navbar-cekalix px-3">
    <span class="navbar-brand fw-bold">CEKALIX S.A.C.</span>
    <div class="d-flex align-items-center">
        <span class="text-white me-3">Usuario: <strong><%= session.getAttribute("usuario") %></strong></span>
        <a href="login.jsp" class="btn btn-outline-light btn-sm">Cerrar sesión</a>
    </div>
</nav>

<div class="container py-4">

    <h3 class="mb-1">Bienvenido, <%= session.getAttribute("usuario") %></h3>
    <p class="text-muted mb-4">Sistema de gestión de ventas B2B. Elige una opción:</p>

    <div class="row g-4">

        <div class="col-12 col-md-6 col-lg-4">
            <a class="enlace-tarjeta" href="registro_cliente.jsp">
                <div class="card tarjeta-menu h-100 text-center p-4">
                    <h5 class="mt-2">Registrar cliente</h5>
                    <p class="text-muted mb-0">Agrega un nuevo cliente empresarial.</p>
                </div>
            </a>
        </div>

        <div class="col-12 col-md-6 col-lg-4">
            <a class="enlace-tarjeta" href="Proceso_Cliente">
                <div class="card tarjeta-menu h-100 text-center p-4">
                    <h5 class="mt-2">Consultar clientes</h5>
                    <p class="text-muted mb-0">Lista y busca clientes por RUC.</p>
                </div>
            </a>
        </div>

        <div class="col-12 col-md-6 col-lg-4">
            <a class="enlace-tarjeta" href="consulta_producto.jsp">
                <div class="card tarjeta-menu h-100 text-center p-4">
                    <h5 class="mt-2">Consultar productos</h5>
                    <p class="text-muted mb-0">Revisa precios y stock disponible.</p>
                </div>
            </a>
        </div>

        <div class="col-12 col-md-6 col-lg-6">
            <a class="enlace-tarjeta" href="registro_venta.jsp">
                <div class="card tarjeta-menu h-100 text-center p-4">
                    <h5 class="mt-2">Registrar venta</h5>
                    <p class="text-muted mb-0">Crea una venta con sus productos.</p>
                </div>
            </a>
        </div>

        <div class="col-12 col-md-6 col-lg-6">
            <a class="enlace-tarjeta" href="Proceso_Venta">
                <div class="card tarjeta-menu h-100 text-center p-4">
                    <h5 class="mt-2">Consultar ventas</h5>
                    <p class="text-muted mb-0">Historial de ventas anteriores.</p>
                </div>
            </a>
        </div>

    </div>
</div>

</body>
</html>