<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
if (session.getAttribute("usuario") == null) { response.sendRedirect("login.jsp"); return; }
Integer rol = (Integer) session.getAttribute("idRol");
%>
<html lang="es"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>CEKALIX - Inicio</title><link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"><link href="css/style.css" rel="stylesheet"></head>
<body><nav class="navbar navbar-dark navbar-cekalix px-3"><span class="navbar-brand fw-bold">CEKALIX S.A.C.</span><div class="d-flex align-items-center"><span class="text-white me-3">Usuario: <strong><%= session.getAttribute("usuario") %></strong></span><a href="Cerrar_Sesion" class="btn btn-outline-light btn-sm">Cerrar sesión</a></div></nav>
<div class="container py-4"><h3 class="mb-1">Panel principal</h3><p class="text-muted mb-4">Gestión de clientes, productos y ventas B2B.</p><div class="row g-4">
<div class="col-12 col-md-6 col-lg-4"><a class="enlace-tarjeta" href="registro_cliente.jsp"><div class="card tarjeta-menu h-100 p-4"><h5>Registrar cliente</h5><p class="text-muted mb-0">Agregar un cliente empresarial.</p></div></a></div>
<div class="col-12 col-md-6 col-lg-4"><a class="enlace-tarjeta" href="Proceso_Cliente"><div class="card tarjeta-menu h-100 p-4"><h5>Consultar clientes</h5><p class="text-muted mb-0">Buscar por RUC y editar datos.</p></div></a></div>
<div class="col-12 col-md-6 col-lg-4"><a class="enlace-tarjeta" href="Proceso_Producto"><div class="card tarjeta-menu h-100 p-4"><h5>Productos</h5><p class="text-muted mb-0">Consultar precios y stock.</p></div></a></div>
<div class="col-12 col-md-6 col-lg-4"><a class="enlace-tarjeta" href="registro_venta.jsp"><div class="card tarjeta-menu h-100 p-4"><h5>Registrar venta</h5><p class="text-muted mb-0">Crear una venta con productos.</p></div></a></div>
<div class="col-12 col-md-6 col-lg-4"><a class="enlace-tarjeta" href="Proceso_Venta"><div class="card tarjeta-menu h-100 p-4"><h5>Historial de ventas</h5><p class="text-muted mb-0">Consultar ventas registradas.</p></div></a></div>
<% if (rol != null && rol == 1) { %><div class="col-12 col-md-6 col-lg-4"><a class="enlace-tarjeta" href="registro.jsp"><div class="card tarjeta-menu h-100 p-4"><h5>Administrar vendedores</h5><p class="text-muted mb-0">Crear cuentas para el equipo de ventas.</p></div></a></div><% } %>
</div></div></body></html>
