<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
if (session.getAttribute("usuario") == null || !Integer.valueOf(1).equals(session.getAttribute("idRol"))) { response.sendRedirect("inicio.jsp"); return; }
%>
<html lang="es"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>CEKALIX - Registrar vendedor</title><link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"><link href="css/style.css" rel="stylesheet"></head>
<body><nav class="navbar navbar-dark navbar-cekalix px-3"><a class="navbar-brand fw-bold" href="inicio.jsp">CEKALIX S.A.C.</a><a href="inicio.jsp" class="btn btn-outline-light btn-sm">Volver</a></nav>
<div class="container py-5"><div class="row justify-content-center"><div class="col-12 col-md-7 col-lg-5"><div class="card shadow"><div class="card-body p-4"><h4 class="mb-3">Registrar vendedor</h4>
<% if (request.getParameter("correcto") != null) { %><div class="alert alert-success">Vendedor registrado correctamente.</div><% } else if (request.getParameter("error") != null) { %><div class="alert alert-danger">No se pudo registrar. Verifica los datos o si el usuario ya existe.</div><% } %>
<form action="Proceso_Registro" method="post"><div class="mb-3"><label class="form-label">Nombre completo</label><input type="text" name="nombre" class="form-control" maxlength="100" required></div><div class="mb-3"><label class="form-label">Usuario</label><input type="text" name="usuario" class="form-control" maxlength="40" required></div><div class="mb-3"><label class="form-label">Contraseña</label><input type="password" name="contrasena" class="form-control" minlength="6" required></div><button class="btn btn-cekalix w-100" type="submit">Crear vendedor</button></form>
</div></div></div></div></div></body></html>
