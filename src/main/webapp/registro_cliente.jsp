<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% if (session.getAttribute("usuario") == null) { response.sendRedirect("login.jsp"); return; } %>
<html lang="es"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>CEKALIX - Registrar cliente</title><link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"><link href="css/style.css" rel="stylesheet"></head>
<body><nav class="navbar navbar-dark navbar-cekalix px-3"><a class="navbar-brand fw-bold" href="inicio.jsp">CEKALIX S.A.C.</a><a href="Proceso_Cliente" class="btn btn-outline-light btn-sm">Ver clientes</a></nav>
<div class="container py-4"><div class="row justify-content-center"><div class="col-12 col-md-8 col-lg-7"><div class="card shadow"><div class="card-body p-4"><h4 class="mb-3">Registro de clientes</h4>
<% if (request.getParameter("correcto") != null) { %><div class="alert alert-success">Cliente registrado correctamente.</div><% } else if (request.getParameter("error") != null) { %><div class="alert alert-danger">No se pudo registrar. Revisa el RUC y los datos.</div><% } %>
<form action="Proceso_Cliente" method="post"><input type="hidden" name="accion" value="registrar">
<div class="mb-3"><label class="form-label">Razón social</label><input name="razon_social" class="form-control" maxlength="100" required></div>
<div class="row"><div class="col-md-6 mb-3"><label class="form-label">RUC</label><input name="ruc" class="form-control" maxlength="11" pattern="[0-9]{11}" required></div><div class="col-md-6 mb-3"><label class="form-label">Teléfono</label><input name="telefono" class="form-control" maxlength="15"></div></div>
<div class="mb-3"><label class="form-label">Dirección</label><input name="direccion" class="form-control" maxlength="150"></div>
<button class="btn btn-cekalix" type="submit">Registrar cliente</button><a href="inicio.jsp" class="btn btn-outline-secondary ms-2">Cancelar</a></form>
</div></div></div></div></div></body></html>
