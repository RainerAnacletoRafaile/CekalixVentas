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
    <title>CEKALIX - Órdenes de venta</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="css/style.css" rel="stylesheet">
</head>
<body>
<nav class="navbar navbar-dark navbar-cekalix px-3">
    <a class="navbar-brand fw-bold text-white" href="inicio.jsp">CEKALIX S.A.C.</a>
    <div>
        <a href="registro_venta.jsp" class="btn btn-outline-light btn-sm me-2">Nueva orden</a>
        <a href="inicio.jsp" class="btn btn-outline-light btn-sm">Menú principal</a>
    </div>
</nav>

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h4 class="mb-0">Órdenes de venta</h4>
        <a href="registro_venta.jsp" class="btn btn-cekalix">+ Registrar orden</a>
    </div>

    <% if (request.getParameter("correcto") != null) { %>
        <div class="alert alert-success py-2">Orden registrada en estado Pendiente.</div>
    <% } else if (request.getParameter("confirmada") != null) { %>
        <div class="alert alert-success py-2">Orden confirmada y cajas reservadas correctamente.</div>
    <% } else if (request.getParameter("despachada") != null) { %>
        <div class="alert alert-success py-2">Despacho registrado y stock físico actualizado.</div>
    <% } else if (request.getParameter("anulada") != null) { %>
        <div class="alert alert-success py-2">Orden anulada correctamente.</div>
    <% } else if (request.getParameter("stock") != null) { %>
        <div class="alert alert-warning py-2">No se pudo procesar: revisa el stock disponible o el estado de la orden.</div>
    <% } else if (request.getParameter("anulacion") != null) { %>
        <div class="alert alert-danger py-2">No se pudo anular. Verifica el motivo y el estado de la orden.</div>
    <% } else if (request.getParameter("error") != null) { %>
        <div class="alert alert-danger py-2">No se pudo procesar la orden.</div>
    <% } %>

    <div class="card shadow">
        <div class="table-responsive">
            <table class="table table-striped table-hover align-middle mb-0 tabla-cekalix">
                <thead>
                    <tr>
                        <th>N.º orden</th>
                        <th>RUC</th>
                        <th>Razón social</th>
                        <th>Fecha</th>
                        <th class="text-end">Total</th>
                        <th>Estado</th>
                        <th>Motivo de anulación</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                <%
                    List<Venta> ventas = (List<Venta>) request.getAttribute("ventas");
                    if (ventas != null && !ventas.isEmpty()) {
                        for (Venta v : ventas) {
                            String estado = v.getEstado() == null ? "" : v.getEstado();
                            String color = "Pendiente".equalsIgnoreCase(estado) ? "warning text-dark"
                                : "Confirmada".equalsIgnoreCase(estado) ? "success"
                                : "Despachado".equalsIgnoreCase(estado) ? "primary" : "secondary";
                %>
                    <tr>
                        <td><%= v.getId_venta() %></td>
                        <td><%= v.getRuc() %></td>
                        <td><%= v.getRazon_social() %></td>
                        <td><%= v.getFecha() %></td>
                        <td class="text-end fw-semibold">S/ <%= v.getTotal() %></td>
                        <td><span class="badge bg-<%= color %>"><%= estado %></span></td>
                        <td><%= v.getMotivo_anulacion() == null ? "—" : v.getMotivo_anulacion() %></td>
                        <td>
                            <% if ("Pendiente".equalsIgnoreCase(estado)) { %>
                                <form action="Proceso_Venta" method="post" class="mb-2">
                                    <input type="hidden" name="accion" value="confirmar">
                                    <input type="hidden" name="id_venta" value="<%= v.getId_venta() %>">
                                    <button type="submit" class="btn btn-sm btn-cekalix">Confirmar y reservar</button>
                                </form>
                            <% } else if ("Confirmada".equalsIgnoreCase(estado)) { %>
                                <form action="Proceso_Venta" method="post" class="mb-2">
                                    <input type="hidden" name="accion" value="despachar">
                                    <input type="hidden" name="id_venta" value="<%= v.getId_venta() %>">
                                    <button type="submit" class="btn btn-sm btn-primary">Confirmar despacho</button>
                                </form>
                            <% } %>

                            <% if ("Pendiente".equalsIgnoreCase(estado) || "Confirmada".equalsIgnoreCase(estado)) { %>
                                <form action="Proceso_Venta" method="post" class="mt-2">
                                    <input type="hidden" name="accion" value="anular">
                                    <input type="hidden" name="id_venta" value="<%= v.getId_venta() %>">
                                    <input type="text" name="motivo" class="form-control form-control-sm mb-1"
                                           placeholder="Motivo de anulación" maxlength="500" required>
                                    <button type="submit" class="btn btn-sm btn-outline-danger">Anular orden</button>
                                </form>
                            <% } else { %>
                                <span class="text-muted">Sin acciones</span>
                            <% } %>
                        </td>
                    </tr>
                <%
                        }
                    } else {
                %>
                    <tr>
                        <td colspan="8" class="text-center text-muted py-4">No existen órdenes registradas.</td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>
